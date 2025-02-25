/*
 
 * Copyright (c) 2015 University of Illinois Board of Trustees, All rights reserved.   
 * Developed at GSLIS/ the iSchool, by Dr. Jana Diesner, Amirhossein Aleyasen,    
 * Chieh-Li Chin, Shubhanshu Mishra, Kiumars Soltani, and Liang Tao.     
 *   
 * This program is free software; you can redistribute it and/or modify it under   
 * the terms of the GNU General Public License as published by the Free Software   
 * Foundation; either version 2 of the License, or any later version.   
 *    
 * This program is distributed in the hope that it will be useful, but WITHOUT   
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or    
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for   
 * more details.   
 *    
 * You should have received a copy of the GNU General Public License along with   
 * this program; if not, see <http://www.gnu.org/licenses>.   
 *
 
 
 */
package context.core.task.stemming;

import context.app.AppConfig;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.WordLemmaTag;
import edu.stanford.nlp.ling.WordTag;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author Aale
 */
public class LemmaTagger {

    private static StanfordCoreNLP pipeline;
    private static Map<String, String> tagger_models_str = new HashMap<String, String>();
    private static Map<String, MaxentTagger> tagger_models = new HashMap<String, MaxentTagger>();

    static {
        Properties props = new Properties();
        props.put("annotators", "tokenize");
        pipeline = new StanfordCoreNLP(props);

        tagger_models_str.put("en", AppConfig.getUserDirLoc() + "/data/pos/models/wsj-0-18-left3words-distsim.tagger");
        tagger_models_str.put("ar", AppConfig.getUserDirLoc() + "/data/pos/models/arabic-fast.tagger");
        tagger_models_str.put("ch", AppConfig.getUserDirLoc() + "/data/pos/models/chinese-distsim.tagger");
        tagger_models_str.put("fr", AppConfig.getUserDirLoc() + "/data/pos/models/french.tagger");
        tagger_models_str.put("de", AppConfig.getUserDirLoc() + "/data/pos/models/german-fast.tagger");

    }

    /**
     *
     * @param args
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static void main(String[] args) throws ClassNotFoundException, IOException {

        // Initialize the tagger
        MaxentTagger tagger = getTagger("en");

        // The sample string
//        String sample = "بعد إعلان الدستور العثماني سنة";
//        String sample = "This question appears to be off-topic. The users who voted to close gave this specific reason.";
        // The tagged string
//        String tagged = tagger.tagString(sample);
        // Output the result
//        System.out.println(tagged);
        List<CoreLabel> sent = Sentence.toCoreLabelList("These", "are", "some", "questions");
        final List<TaggedWord> lemmatize = lemmatize(sent, "en");
        System.out.println("Lemmatize::");
        System.out.println(lemmatize);
        for (TaggedWord c : lemmatize) {
            System.out.println(c.word() + "\t" + c.tag());
        }
    }

    static Morphology morphology = new Morphology();

    /**
     *
     * @param sent
     * @param language
     * @return
     */
    public static List<TaggedWord> lemmatize(List<CoreLabel> sent, String language) {
        MaxentTagger tagger = getTagger(language);
//        List<HasWord> sent = Sentence.toWordList("This is a sample text");
        List<TaggedWord> taggedSent = tagger.tagSentence(sent);
        for (TaggedWord token : taggedSent) {
            String word = token.word();
            String pos = token.tag();
            String lemma = morphology.lemmatize(new WordTag(word, pos)).lemma();
            token.setTag(lemma);
        }
//        final List<WordLemmaTag> tagged = (List<WordLemmaTag>) tagger.tagCoreLabelsOrHasWords(sent, morphology, true);
//        for (TaggedWord tw : taggedSent) {
//            System.out.println(tw.word() + "\t" + tw.tag());
//        }
        return taggedSent;
    }

    private static MaxentTagger getTagger(String language) {
        MaxentTagger tagger = tagger_models.get(language);
        if (tagger == null) {
//            try {
            tagger_models.put(language, new MaxentTagger(tagger_models_str.get(language)));
            return tagger_models.get(language);
//            } catch (IOException | ClassNotFoundException ex) {
//                Exceptions.printStackTrace(ex);
//            }
        }
        return tagger;
    }
}
