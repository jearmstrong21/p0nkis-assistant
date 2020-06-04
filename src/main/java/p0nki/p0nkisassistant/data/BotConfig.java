package p0nki.p0nkisassistant.data;

import p0nki.p0nkisassistant.utils.Utils;

public class BotConfig {

    public final static BotConfig CACHE = Utils.deserialize("botconfig", BotConfig.class);

    public String prefix = "";
    public String ownerID = "";
    public String pingsockEmoteID = "";
    public String notifRolePing = "";

//    public static BotConfig get() {
//        return Utils.deserialize("botconfig", BotConfig.class);
//    }
//
//    public void set() {
//        Utils.serialize("botconfig", this, true);
//    }

}
