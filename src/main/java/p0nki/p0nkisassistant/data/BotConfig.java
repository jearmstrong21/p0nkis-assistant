package p0nki.p0nkisassistant.data;

import p0nki.p0nkisassistant.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class BotConfig {

    public Map<String, String> guildPrefixes = new HashMap<>();
    public String basePrefix = "";
    public String ownerID = "";
    public String pingsockEmoteID = "";
    public String notifRolePing = "";

    public static BotConfig get() {
        return Utils.deserialize("botconfig", BotConfig.class);
    }

    public void set() {
        Utils.serialize("botconfig", this, true);
    }

}
