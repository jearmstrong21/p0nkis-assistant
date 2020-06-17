package p0nki.easycommandtestbot.data;

import p0nki.easycommandtestbot.lib.data.PerGuildDataCache;
import p0nki.easycommandtestbot.lib.data.ReadWriteData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RolepollData extends ReadWriteData {

    public static final PerGuildDataCache<RolepollData> CACHE = new PerGuildDataCache<>("rolepoll", RolepollData::new);

    private Map<String, List<String>> rolepolls = new HashMap<>();

    private RolepollData(String dir) {
        super(dir);
    }

    public Map<String, List<String>> getRolepolls() {
        return rolepolls;
    }

    public void makeRolepoll(String messageID, List<String> roles) {
        rolepolls.put(messageID, roles);
        write();
    }

}
