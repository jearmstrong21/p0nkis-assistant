package p0nki.p0nkisassistant.data;

import p0nki.p0nkisassistant.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class CounterConfig {

    public Map<String, Map<String, Integer>> data = new HashMap<>();

    public static CounterConfig get() {
        return Utils.deserialize("counter", CounterConfig.class);
    }

    public void set() {
        Utils.serialize("counter", this, true);
    }

}
