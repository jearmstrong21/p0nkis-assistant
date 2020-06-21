package p0nki.assistant.data;

public class StarredMessage {

    private String channel;
    private String id;

    public StarredMessage() {

    }

    public StarredMessage(String channel, String id) {
        this.channel = channel;
        this.id = id;
    }

    public String getChannel() {
        return channel;
    }

    public String getId() {
        return id;
    }
}
