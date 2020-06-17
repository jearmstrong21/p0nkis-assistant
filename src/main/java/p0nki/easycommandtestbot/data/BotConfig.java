package p0nki.easycommandtestbot.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.dv8tion.jda.api.entities.Activity;
import p0nki.easycommandtestbot.lib.data.ReadData;

public class BotConfig extends ReadData {

    @JsonProperty("token_file")
    private String tokenFile;
    private String prefix;
    private String owner;
    private Activity activity;

    public static BotConfig VALUE = new BotConfig();

    protected BotConfig() {
        super("botconfig");
        read();
    }

    public String getTokenFile() {
        return tokenFile;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getOwner() {
        return owner;
    }

    public Activity getActivity() {
        return activity;
    }
}
