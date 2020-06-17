package p0nki.easycommandtestbot.lib.data;

import net.dv8tion.jda.api.entities.Guild;
import p0nki.easycommandtestbot.lib.utils.DiscordSource;
import p0nki.easycommandtestbot.lib.utils.DiscordUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class PerGuildDataCache<T extends ReadWriteData> {

    private final String name;
    private final Map<String, T> cache = new HashMap<>();
    private final Function<String, T> supplier;

    public PerGuildDataCache(String name, Function<String, T> supplier) {
        this.name = name;
        this.supplier = supplier;
        File file = new File(DiscordUtils.data("guilds"));
        if (!file.exists()) DiscordUtils.verify(file.mkdirs());
        String[] files = file.list();
        for (String s : files) {
            File f = new File(DiscordUtils.data(String.format("guilds/%s/%s.json", s, name)));
            if (f.exists()) {
                T data = supplier.apply(s);
                data.setDir("guilds/" + data.dir());
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
        return of(source.guild());
    }

    public T of(Guild guild) {
        return of(guild.getId());
    }

    public T of(String guild) {
        if (!cache.containsKey(guild)) {
            T data = supplier.apply(guild);
            data.setDir("guilds/" + data.dir());
            data.setName(name);
            data.read();
            cache.put(guild, data);
        }
        return cache.get(guild);
    }

}
