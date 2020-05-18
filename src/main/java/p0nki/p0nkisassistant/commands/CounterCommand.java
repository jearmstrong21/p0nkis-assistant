package p0nki.p0nkisassistant.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import p0nki.p0nkisassistant.data.CounterConfig;
import p0nki.p0nkisassistant.listeners.CommandListener;
import p0nki.p0nkisassistant.utils.CommandSource;
import p0nki.p0nkisassistant.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static p0nki.p0nkisassistant.utils.BrigadierUtils.*;

public class CounterCommand {

    public static int modify(CommandSource source, String name, int value) {
        CounterConfig config = CounterConfig.get();
        if (!config.data.containsKey(source.guild().getId())) config.data.put(source.guild().getId(), new HashMap<>());
        if (!config.data.get(source.guild().getId()).containsKey(name))
            config.data.get(source.guild().getId()).put(name, 0);
        config.data.get(source.guild().getId()).put(name, Math.max(0, config.data.get(source.guild().getId()).get(name) + value));
        config.set();
        source.to.sendMessage("New value: " + config.data.get(source.guild().getId()).get(name)).queue();
        return CommandListener.SUCCESS;
    }

    public static int show(CommandSource source, String name) {
        if (name.length() >= 2) {
            String realName = name.substring(0, name.length() - 2);
            if (name.endsWith("++")) {
                return modify(source, realName, 1);
            }
            if (name.endsWith("--")) {
                return modify(source, realName, -1);
            }
        }
        CounterConfig config = CounterConfig.get();
        if (!config.data.containsKey(source.guild().getId())) config.data.put(source.guild().getId(), new HashMap<>());
        config.set();
        if (!config.data.get(source.guild().getId()).containsKey(name)) {
            source.to.sendMessage("No counter with that name exists in this guild.").queue();
            return CommandListener.FAILURE;
        }
        source.to.sendMessage("Counter value: " + config.data.get(source.guild().getId()).get(name)).queue();
        return CommandListener.SUCCESS;
    }

    public static int dump(CommandSource source) {
        CounterConfig config = CounterConfig.get();
        if (!config.data.containsKey(source.guild().getId())) config.data.put(source.guild().getId(), new HashMap<>());
        config.set();
        if (config.data.get(source.guild().getId()).size() == 0) {
            source.to.sendMessage("No counters in this guild.").queue();
            return CommandListener.FAILURE;
        }
        source.to.sendMessage(config.data.get(source.guild().getId()).entrySet().stream().sorted(Map.Entry.comparingByKey()).map(stringIntegerEntry -> stringIntegerEntry.getKey() + ": " + stringIntegerEntry.getValue()).collect(Collectors.joining("\n"))).queue();
        return CommandListener.SUCCESS;
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(literal("counter")
                .requires(Utils.isFromGuild())
                .then(argument("name", string())
                        .executes(context -> show(context.getSource(), StringArgumentType.getString(context, "name")))
                )
                .executes(context -> dump(context.getSource()))
        );
        dispatcher.register(literal("counters")
                .requires(Utils.isFromGuild())
                .executes(context -> dump(context.getSource()))
        );
    }

}
