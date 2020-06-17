package p0nki.easycommandtestbot.data;

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
