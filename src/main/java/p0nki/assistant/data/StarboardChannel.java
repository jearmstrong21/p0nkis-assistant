package p0nki.assistant.data;

import java.util.Map;

public class StarboardChannel {

    private String channelID;
    private int countRequired;
    private StarboardChannelSet channelSet;
    private StarboardReaction reaction;
    private Map<String, StarredMessage> starred2original;
    private Map<String, String> original2starred;

    public StarboardChannel() {

    }

    public StarboardChannel(String channelID, int countRequired, StarboardChannelSet channelSet, StarboardReaction reaction, Map<String, StarredMessage> starred2original, Map<String, String> original2starred) {
        this.channelID = channelID;
        this.countRequired = countRequired;
        this.channelSet = channelSet;
        this.reaction = reaction;
        this.starred2original = starred2original;
        this.original2starred = original2starred;
    }

    public String getChannelID() {
        return channelID;
    }

    public int getCountRequired() {
        return countRequired;
    }

    public StarboardChannelSet getChannelSet() {
        return channelSet;
    }

    public StarboardReaction getReaction() {
        return reaction;
    }

    public Map<String, StarredMessage> getStarred2original() {
        return starred2original;
    }

    public Map<String, String> getOriginal2starred() {
        return original2starred;
    }

    public void print() {
        System.out.println();
        System.out.println(channelID);
        System.out.println(countRequired);
        channelSet.print();
        reaction.print();
    }
}
