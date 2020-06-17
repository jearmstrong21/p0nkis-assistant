package p0nki.easycommandtestbot.data;

import p0nki.easycommandtestbot.lib.data.PerGuildDataCache;
import p0nki.easycommandtestbot.lib.data.ReadWriteData;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CounterData extends ReadWriteData {

    public static PerGuildDataCache<CounterData> CACHE = new PerGuildDataCache<>("counter", CounterData::new);

    private boolean enabled = true;
    private Map<String, Integer> data = new HashMap<>();

    private CounterData(String dir) {
        super(dir);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        write();
    }

    public Set<String> keys() {
        return Collections.unmodifiableSet(data.keySet());
    }

    public boolean has(String name) {
        return data.containsKey(name);
    }

    public void remove(String name) {
        data.remove(name);
        write();
    }

    public int get(String name) {
        return data.getOrDefault(name, 0);
    }

    public int add(String name, int amount) {
        int newValue = get(name) + amount;
        data.put(name, newValue);
        write();
        return newValue;
    }

}
