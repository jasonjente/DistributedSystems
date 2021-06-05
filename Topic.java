public class Topic {

    private String topic; //This is the LineCode

    Topic(){
        this.topic = null;
    }

    public Topic(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

}
