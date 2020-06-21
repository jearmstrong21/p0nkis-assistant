package p0nki.assistant.data;

import p0nki.assistant.lib.data.PerGuildDataCache;
import p0nki.assistant.lib.data.ReadWriteData;

import java.util.HashMap;
import java.util.Map;

public class StarboardData extends ReadWriteData {

    public static final PerGuildDataCache<StarboardData> CACHE = new PerGuildDataCache<>("starboard", StarboardData::new);

    private Map<String,StarboardChannel> channels = new HashMap<>();

    private StarboardData(String dir) {
        super(dir);
    }

    public void write_(){
        write();
    }

    public Map<String, StarboardChannel> getChannels() {
        return channels;
    }

}
