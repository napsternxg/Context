package context.core.task.entitydetection;

import context.core.entity.CorpusData;
import context.core.entity.FileData;
import context.core.entity.TabularData;
import context.core.util.CorpusAggregator;
import context.core.util.ForAggregation;
import context.core.util.JavaIO;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Aale
 */
public class EntityDetectionBody {

    /**
     * @param args
     *
     */
    private EntityDetectionTaskInstance instance;
    private CorpusData input;
    private List<TabularData> tabularOutput;

    private AbstractSequenceClassifier<?> classifier3;
    private AbstractSequenceClassifier<?> classifier4;
    private AbstractSequenceClassifier<?> classifier7;
//    private File stopFile;

    List<String[]> entitiesWithCount;

    /**
     *
     * @param instance
     */
    public EntityDetectionBody(EntityDetectionTaskInstance instance) {
        // TODO Auto-generated method stub

        this.instance = instance;
        init();

    }

    private void init() {

        this.input = (CorpusData) instance.getInput();
        this.tabularOutput = instance.getTabularOutput();
        this.classifier3 = instance.get3Classifier();
        this.classifier4 = instance.get4Classifier();
        this.classifier7 = instance.get7Classifier();
        System.out.println("init done successfully");
    }

    /**
     *
     * @return
     */
    public boolean detectEntities() {
        System.out.println("start detectEntities...");
        List<FileData> files = input.getFiles();

        List<List<String[]>> toAggregate = new ArrayList<List<String[]>>();
        try {
            for (FileData ff : files) {

                File file = ff.getFile();
                String text;
                try {
                    text = JavaIO.readFile(file);
                    text = text.replaceAll("\\p{Cc}", " ");
                    text = text.replaceAll("[^A-Za-z0-9 :;!\\?\\.,\'\"-]", " ");

                    List<String[]> longEntities = new ArrayList<String[]>();

                    List<ForAggregation> longEntities3 = new ArrayList<ForAggregation>();
                    List<ForAggregation> longEntities4 = new ArrayList<ForAggregation>();
                    List<ForAggregation> longEntities7 = new ArrayList<ForAggregation>();
                    MultiWordEntities MWE3 = MultiWordEntityRecognition(classifier3, text);
                    MultiWordEntities MWE4 = MultiWordEntityRecognition(classifier4, text);
                    MultiWordEntities MWE7 = MultiWordEntityRecognition(classifier7, text);

                    longEntities3.addAll(MWE3.forAgg);
                    //longEntities4.addAll(MWE4.forAgg);
                    longEntities7.addAll(MWE7.forAgg);

                    HashMap<String, Integer[]> Entities = new HashMap<String, Integer[]>();

                    for (int entityIndex = 0; entityIndex < longEntities3.size(); entityIndex++) {
                        Integer[] offsetArray = {MWE3.startInd.get(entityIndex)};
                        Entities.put(longEntities3.get(entityIndex).toAggregate[0], offsetArray);
                        longEntities.add(longEntities3.get(entityIndex).toAggregate);
                    }
                    // The following code is to incorporate another NER library, but it was causing
                    // problems for large individual documents, so for now "longEntities4.addAll(MWE4.forAgg);" 
                    //has been commented out.  In the future,
                    // a check on file size may be done to switch between using the library or not.

                    for (int entityIndex = 0; entityIndex < longEntities4.size(); entityIndex++) {

                        if (Entities.containsKey(longEntities4.get(entityIndex).toAggregate[0]) && Arrays.asList(Entities.get(longEntities4.get(entityIndex).toAggregate[0])).contains(MWE4.startInd.get(entityIndex))) {
                            continue;
                        } else if (Entities.containsKey(longEntities4.get(entityIndex).toAggregate[0])) {
                            Integer[] numOfOcc = Entities.get(longEntities4.get(entityIndex).toAggregate[0]);
                            Integer[] offsetArray4 = new Integer[numOfOcc.length + 1];
                            offsetArray4 = Arrays.copyOf(Entities.get(longEntities4.get(entityIndex).toAggregate[0]), offsetArray4.length);
                            offsetArray4[offsetArray4.length - 1] = MWE4.startInd.get(entityIndex);
                            Entities.put(longEntities4.get(entityIndex).toAggregate[0], offsetArray4);
                            longEntities.add(longEntities4.get(entityIndex).toAggregate);
                        } else {
                            Integer[] offsetArray = {MWE4.startInd.get(entityIndex)};
                            Entities.put(longEntities4.get(entityIndex).toAggregate[0], offsetArray);
                            longEntities.add(longEntities4.get(entityIndex).toAggregate);
                        }

                    }

                    for (int entityIndex = 0; entityIndex < longEntities7.size(); entityIndex++) {

                        if (Entities.containsKey(longEntities7.get(entityIndex).toAggregate[0]) && Arrays.asList(Entities.get(longEntities7.get(entityIndex).toAggregate[0])).contains(MWE7.startInd.get(entityIndex))) {
                            continue;
                        } else if (Entities.containsKey(longEntities7.get(entityIndex).toAggregate[0])) {
                            Integer[] numOfOcc = Entities.get(longEntities7.get(entityIndex).toAggregate[0]);
                            Integer[] offsetArray7 = new Integer[numOfOcc.length + 1];
                            offsetArray7 = Arrays.copyOf(Entities.get(longEntities7.get(entityIndex).toAggregate[0]), offsetArray7.length);
                            offsetArray7[offsetArray7.length - 1] = MWE7.startInd.get(entityIndex);
                            Entities.put(longEntities7.get(entityIndex).toAggregate[0], offsetArray7);
                            longEntities.add(longEntities7.get(entityIndex).toAggregate);
                        } else {
                            Integer[] offsetArray = {MWE7.startInd.get(entityIndex)};
                            Entities.put(longEntities7.get(entityIndex).toAggregate[0], offsetArray);
                            longEntities.add(longEntities7.get(entityIndex).toAggregate);
                        }
                    }

                    toAggregate.add(longEntities);

                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            
            //List<String[]> EntitiesToBeRemoved = new ArrayList<String[]>();
            List<String[]> entitiesWithCount = new CorpusAggregator().CorpusAggregate(toAggregate);
            /*
            for (String[] entityWithCount : entitiesWithCount) {
//                try {
                    if (entityWithCount[0].split(" ").length == 1 // && JavaIO.readFile(stopFile).contains(entityWithCount[0].toLowerCase()) ) {
                        EntitiesToBeRemoved.add(entityWithCount);
                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    return false;
//                }
            }
            for (String[] entityWithCount : EntitiesToBeRemoved) {
                entitiesWithCount.remove(entityWithCount);
            }

            */
            this.entitiesWithCount = entitiesWithCount;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private MultiWordEntities MultiWordEntityRecognition(AbstractSequenceClassifier<?> classifier, String inText) {

        List<ForAggregation> NamedEntities = new ArrayList<ForAggregation>();
        String htmlString = classifier.classifyToString(inText, "inlineXML", true);
        Pattern tags = Pattern.compile("<.+?>.+?</.+?>");
        Pattern tempTags = null;
        Matcher matcher = tags.matcher(htmlString);
        Matcher tempMatcher = null;
        List<Integer> startIndicies = new ArrayList<Integer>();
        HashMap<String, Integer> hashedNumOcc = new HashMap<String, Integer>();

        while (matcher.find()) {
            String name = (matcher.group().replaceAll("<.+?>", ""));
            /*
             if (name.split("\\s+").length<2){
             continue;
             }
             */
            String[] NamedEntity_array = {name.trim().replaceAll(" +", " "), matcher.group().replaceAll("<", "").replaceAll(">.+", "")};

            if (hashedNumOcc.containsKey(name)) {
                hashedNumOcc.put(name, hashedNumOcc.get(name) + 1);
            } else {
                hashedNumOcc.put(name, 1);
            }
            ForAggregation NamedEntity = new ForAggregation(NamedEntity_array);
            startIndicies.add(findNthIndexOf(inText, name, hashedNumOcc.get(name)));
            if (null != NamedEntity) {
                NamedEntities.add(NamedEntity);
            }

        }
        MultiWordEntities toReturn = new MultiWordEntities(NamedEntities, startIndicies);
        return toReturn;
    }

    private int findNthIndexOf(String str, String needle, int occurence)
            throws IndexOutOfBoundsException {
        int index = -1;
        Pattern p = Pattern.compile(needle, Pattern.MULTILINE);
        Matcher m = p.matcher(str);
        while (m.find()) {
            if (--occurence == 0) {
                index = m.start();
                break;
            }
        }
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }
        return index;
    }

    /**
     *
     * @param filepath
     */
    public void writeOutput(String filepath) {
        //Write CSV
        this.writeCsv(entitiesWithCount, filepath);
    }

    /**
     *
     * @param entitiesWithCount
     * @param filePath
     */
    public static void writeCsv(List<String[]> entitiesWithCount, String filePath) {

        System.out.println("size of entitiesWithCount=" + entitiesWithCount.size());
        StringBuffer sb = new StringBuffer();
        sb.append("Term, Entity, Frequency\n");
        String toWrite = "";
        for (int i1 = 0; i1 < entitiesWithCount.size(); i1++) {
            toWrite = entitiesWithCount.get(i1)[0].replaceAll("[^A-Za-z0-9\\. ]", "_") + "," + entitiesWithCount.get(i1)[1] + "," + (entitiesWithCount.get(i1)[2]) + "\n";
            sb.append(toWrite);
        }
        FileData.writeDataIntoFile(sb.toString(), filePath);
    }
}
