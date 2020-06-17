package p0nki.easycommandtestbot.lib.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import p0nki.easycommandtestbot.lib.DiscordUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class ReadWriteData {

    @JsonIgnore
    private String dir;
    @JsonIgnore
    private String name;

    protected ReadWriteData(String dir) {
        this(dir, null);
    }

    protected ReadWriteData(String dir, String name) {
        this.dir = dir;
        this.name = name;
    }

    void setName(String name) {
        this.name = name;
    }

    void setDir(String dir) {
        this.dir = dir;
    }

    String dir() {
        return dir;
    }

    protected final File getDir() {
        return new File(DiscordUtils.data(dir));
    }

    protected final File getFile() {
        return new File(DiscordUtils.data(String.format("%s/%s.json", dir, name)));
    }

    protected final void read() {
        try {
            EasyJackson.OBJECT_MAPPER.readerForUpdating(this).readValue(getFile());
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected final void write() {
        new Thread(() -> {
            try {
                File file = getFile();
                if (!file.exists()) file.getParentFile().mkdirs();
                EasyJackson.OBJECT_WRITER.writeValue(getFile(), this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

}
