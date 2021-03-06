package p0nki.assistant.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.dv8tion.jda.api.entities.Activity;
import p0nki.assistant.lib.data.ReadData;

public class BotConfig extends ReadData {

    public static BotConfig VALUE = new BotConfig();
    @JsonProperty("token_file")
    private String tokenFile;
    private String prefix;
    private String owner;

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
}
