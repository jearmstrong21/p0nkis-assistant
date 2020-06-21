package p0nki.assistant.lib.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import p0nki.assistant.lib.utils.DiscordUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

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

    String dir() {
        return dir;
    }

    protected final File getDir() {
        return new File(DiscordUtils.data(dir));
    }

    void setDir(String dir) {
        this.dir = dir;
    }

    protected final File getFile() {
        return new File(DiscordUtils.data(String.format("%s/%s.json", dir, name)));
    }

    protected final void read() {
        try {
            System.out.println("READ (mutable) " + getFile().toString());
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
                System.out.println("WRITE " + file.toString());
                PrintWriter printWriter = new PrintWriter(getFile());
                EasyJackson.OBJECT_WRITER.writeValue(printWriter, this);
                printWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

}
