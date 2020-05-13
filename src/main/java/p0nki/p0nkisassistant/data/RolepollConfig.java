package p0nki.p0nkisassistant.data;

import p0nki.p0nkisassistant.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RolepollConfig {

    public Map<String, Rolepoll> rolepolls = new HashMap<>();

    public static RolepollConfig get() {
        return Utils.deserialize("rolepoll", RolepollConfig.class);
    }

    public void set() {
        Utils.serialize("rolepoll", this, true);
    }

    public static class Rolepoll {
        public String messageID = "";
        public String channelID = "";
        public String guildID = "";
        public List<String> roles = new ArrayList<>();
    }

}
