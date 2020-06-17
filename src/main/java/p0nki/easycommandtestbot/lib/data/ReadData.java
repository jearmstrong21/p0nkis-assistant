package p0nki.easycommandtestbot.lib.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import p0nki.easycommandtestbot.lib.DiscordUtils;

import java.io.File;
import java.io.IOException;

public class ReadData {

    @JsonIgnore
    private final String name;

    protected ReadData(String name) {
        this.name = name;
    }

    protected final File getFile() {
        return new File(DiscordUtils.resource(name + ".json"));
    }

    protected final void read() {
        try {
            EasyJackson.OBJECT_MAPPER.readerForUpdating(this).readValue(getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
