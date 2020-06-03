package p0nki.p0nkisassistant.data;

import net.dv8tion.jda.api.entities.User;
import p0nki.p0nkisassistant.P0nkisAssistant;
import p0nki.p0nkisassistant.utils.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TricksConfig {

    private final Map<String, Guild> guilds = new HashMap<>();

    public static TricksConfig get() {
        return Utils.deserialize("tricks", TricksConfig.class);
    }

    public Set<String> guilds() {
        return new HashSet<>(guilds.keySet());
    }

    public Guild guild(String id) {
        if (!guilds.containsKey(id)) {
            guilds.put(id, new Guild());
            set();
        }
        return guilds.get(id);
    }

    public void set() {
        Utils.serialize("tricks", this, true);
    }

    public static class Trick {

        private final String name;
        private final String owner;
        private final boolean isLisp;
        public String source;

        public Trick(String name, String owner, boolean isLisp, String source) {
            this.name = name;
            this.owner = owner;
            this.isLisp = isLisp;
            this.source = source;
        }

        public String name() {
            return name;
        }

        public boolean isLisp() {
            return isLisp;
        }

        public User owner() {
            return P0nkisAssistant.jda.getUserById(owner);
        }

    }

    public static class Guild {

        private final Map<String, Trick> tricks = new HashMap<>();

        public Trick trick(String name) {
            return tricks.get(name);
        }

        public boolean has(String name) {
            return tricks.containsKey(name);
        }

        public void set(Trick trick) {
            tricks.put(trick.name(), trick);
        }

        public void remove(String name) {
            tricks.remove(name);
        }

        public Set<String> tricks() {
            return new HashSet<>(tricks.keySet());
        }

    }

}
