package p0nki.easycommandtestbot.lib.data;

import net.dv8tion.jda.api.entities.User;
import p0nki.easycommandtestbot.lib.utils.DiscordSource;
import p0nki.easycommandtestbot.lib.utils.DiscordUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class PerUserDataCache<T extends ReadWriteData> {

    private final String name;
    private final Map<String, T> cache = new HashMap<>();
    private final Function<String, T> supplier;

    public PerUserDataCache(String name, Function<String, T> supplier) {
        this.name = name;
        this.supplier = supplier;
        File file = new File(DiscordUtils.data("users"));
        if (!file.exists()) DiscordUtils.verify(file.mkdirs());
        String[] files = file.list();
        for (String s : files) {
            File f = new File(DiscordUtils.data(String.format("users/%s/%s.json", s, name)));
            if (f.exists()) {
                T data = supplier.apply(s);
                data.setDir("users/" + data.dir());
                data.setName(name);
                data.read();
                cache.put(s, data);
            }
        }
    }

    public Set<String> getKeys() {
        return cache.keySet();
    }

    public T of(DiscordSource source) {
        return of(source.user());
    }

    public T of(User user) {
        return of(user.getId());
    }

    public T of(String user) {
        if (!cache.containsKey(user)) {
            T data = supplier.apply(user);
            data.setDir("users/" + data.dir());
            data.setName(name);
            data.read();
            cache.put(user, data);
        }
        return cache.get(user);
    }

}
