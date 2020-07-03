package p0nki.assistant.cogs;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import p0nki.assistant.data.CounterData;
import p0nki.assistant.lib.requirements.RequireGuild;
import p0nki.assistant.lib.requirements.RequireManageServer;
import p0nki.assistant.lib.requirements.RequireMessageManage;
import p0nki.assistant.lib.utils.DiscordSource;
import p0nki.assistant.lib.utils.DiscordUtils;
import p0nki.assistant.lib.utils.Holder;
import p0nki.easycommand.annotations.*;
import p0nki.easycommand.arguments.Parsers;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CommandCog(name = "counter", requirements = RequireGuild.class)
public class CounterCog extends ListenerAdapter implements Holder {

    @Command(literals = @Literal("counter"), names = {"enable", "e"}, requirements = RequireManageServer.class)
    public void enable(@Source DiscordSource source) {
        CounterData data = CounterData.CACHE.of(source);
        if (data.isEnabled()) {
            source.send("Counters are already enabled");
        } else {
            data.setEnabled(true);
            source.send("Counters enabled");
        }
    }

    @Command(literals = @Literal("counter"), names = {"disable", "d"}, requirements = RequireManageServer.class)
    public void disable(@Source DiscordSource source) {
        CounterData data = CounterData.CACHE.of(source);
        if (data.isEnabled()) {
            data.setEnabled(false);
            source.send("Counters disabled");
        } else {
            source.send("Counters are already disabled");
        }
    }

    @Command(literals = @Literal("counter"), names = {"remove", "r"}, requirements = RequireMessageManage.class)
    public void remove(@Source DiscordSource source, @Argument(name = "name", modifiers = Parsers.GREEDY_STRING) String name) {
        CounterData data = CounterData.CACHE.of(source);
        if (data.has(name)) {
            data.remove(name);
            source.send("Specified counter deleted");
        } else {
            source.send("No such counter");
        }
    }

    @Command(literals = @Literal("counter"), names = {"list", "l"})
    public void list(@Source DiscordSource source) {
        CounterData data = CounterData.CACHE.of(source);
        if (data.isEnabled()) {
            List<String> keys = new ArrayList<>(data.keys());
            Collections.sort(keys);
            DiscordUtils.paginateList(source, keys.size(), 0, value -> keys.get(value) + " = " + data.get(keys.get(value)));
        } else {
            source.send("Counters disabled in this guild");
        }
    }

    @Command(literals = @Literal("counter"), names = {"info", "i"})
    public void info(@Source DiscordSource source, @Argument(name = "name") String name) {
        CounterData data = CounterData.CACHE.of(source);
        if (data.isEnabled()) {
            source.send(name + " = " + data.get(name));
        } else {
            source.send("Counters disabled in this guild");
        }
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        CounterData data = CounterData.CACHE.of(event.getGuild());
        if (data.isEnabled()) {
            String content = event.getMessage().getContentRaw().toLowerCase();
            int change = 0;
            boolean start = content.startsWith("++") || content.startsWith("--");
            boolean end = content.endsWith("++") || content.endsWith("--");
            if (start && end) return;
            if (content.startsWith("++") || content.endsWith("++")) change = 1;
            if (content.startsWith("--") || content.endsWith("--")) change = -1;
            if (change == 0) return;
            if (content.startsWith("++") || content.startsWith("--")) content = content.substring(2);
            if (content.endsWith("++") || content.endsWith("--")) content = content.substring(0, content.length() - 2);
            if (content.length() == 0) return;
            int newValue = data.add(content, change);
            event.getChannel().sendMessage(content + " = " + newValue).allowedMentions(Collections.emptyList()).queue();
        }
    }

}
