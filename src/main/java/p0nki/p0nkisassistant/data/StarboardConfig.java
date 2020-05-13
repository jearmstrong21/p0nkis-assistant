package p0nki.p0nkisassistant.data;

import p0nki.p0nkisassistant.utils.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StarboardConfig {

    private String guildID;
    public Map<String, Channel> channels = new HashMap<>();

    public static StarboardConfig get(String id) {
        StarboardConfig config = Utils.deserialize("starboard:" + id, StarboardConfig.class);
        config.guildID = id;
        return config;
    }

    public void set() {
        Utils.serialize("starboard:" + guildID, this, true);
    }

    public static class Color {
        public int r = 255;
        public int g = 255;
        public int b = 0;
    }

    public static class Reaction {
        public String reaction = "⭐";
        public boolean isEmoji = true;
    }

    public static class ChannelSet {
        public Set<String> channels = new HashSet<>();
        public boolean isBlacklist = true;

        public boolean contains(String channelID) {
            if (isBlacklist) return !channels.contains(channelID);
            return channels.contains(channelID);
        }
    }

    public static class Message {
        public String channel;
        public String id;

        public Message(String channel, String id) {
            this.channel = channel;
            this.id = id;
        }
    }

    public static class Channel {
        public Map<String, Message> starred2original = new HashMap<>();
        public Map<String, String> original2starred = new HashMap<>();
        public ChannelSet channelSet = new ChannelSet();
        public String channelID = "";
        public int countRequired = 2;
        public Reaction reaction = new Reaction();
        public Color color = new Color();
    }

}
