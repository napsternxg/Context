package context.core.task.topicmodeling;

import context.core.entity.TaskInstance;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Aale
 */
public class TopicModelingTaskInstance extends TaskInstance {

    /**
     *
     */
    protected int numTopics;

    /**
     *
     */
    protected int numWordsPerTopic;

    /**
     *
     */
    protected int numIterations;

    /**
     *
     */
    protected String stopListLoc;

    /**
     *
     * @param name
     */
    protected Boolean isLowercase;
   
    /**
    *
    * @param name
    */
	public TopicModelingTaskInstance(StringProperty name) {
        super(name);
    }

    /**
     *
     * @param numTopics
     */
    public void setNumTopics(int numTopics) {
        this.numTopics = numTopics;
    }

    /**
     *
     * @param numWordsPerTopic
     */
    public void setNumWordsPerTopic(int numWordsPerTopic) {
        this.numWordsPerTopic = numWordsPerTopic;
    }

    /**
     *
     * @param numIterations
     */
    public void setNumIterations(int numIterations) {
        this.numIterations = numIterations;
    }

    /**
     *
     * @param stopListLoc
     */
    public void setStopListLoc(String stopListLoc) {
        this.stopListLoc = stopListLoc;
    }

    /**
     *
     * @return
     */
    public int getNumTopics() {
        return numTopics;
    }

    /**
     *
     * @return
     */
    public int getNumWordsPerTopic() {
        return numWordsPerTopic;
    }

    /**
     *
     * @return
     */
    public int getNumIterations() {
        return numIterations;
    }

    /**
     *
     * @return
     */
    public String getStopListLoc() {
        return stopListLoc;
    }
    /**
    *
    * @return
    */
    public Boolean getIsLowercase() {
		return isLowercase;
	}
    /**
    *
    * @return
    */
	public void setIsLowercase(Boolean isLowercase) {
		this.isLowercase = isLowercase;
	}
}
