package p0nki.assistant.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class StarboardChannelSet {

    private List<String> channels;
    @JsonProperty("isBlacklist")
    private boolean blacklist;

    public StarboardChannelSet() {

    }

    public StarboardChannelSet(List<String> channels, boolean blacklist) {
        this.channels = channels;
        this.blacklist = blacklist;
    }

    public boolean contains(String channelID) {
        if (blacklist) return !channels.contains(channelID);
        return channels.contains(channelID);
    }

    public List<String> getChannels() {
        return channels;
    }

    public boolean isBlacklist() {
        return blacklist;
    }

}
