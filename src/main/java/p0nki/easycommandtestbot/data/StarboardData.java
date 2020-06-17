package p0nki.easycommandtestbot.data;

import p0nki.easycommandtestbot.lib.data.PerGuildDataCache;
import p0nki.easycommandtestbot.lib.data.ReadWriteData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public void print(){
        System.out.println();
        channels.values().forEach(StarboardChannel::print);
    }
}
