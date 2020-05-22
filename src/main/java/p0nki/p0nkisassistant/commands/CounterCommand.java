package p0nki.p0nkisassistant.commands;

import p0nki.commandparser.argument.QuotedStringArgumentType;
import p0nki.commandparser.command.CommandDispatcher;
import p0nki.p0nkisassistant.data.CounterConfig;
import p0nki.p0nkisassistant.utils.CommandResult;
import p0nki.p0nkisassistant.utils.CommandSource;
import p0nki.p0nkisassistant.utils.Nodes;
import p0nki.p0nkisassistant.utils.Requirements;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class CounterCommand {

    public static CommandResult modify(CommandSource source, String name, int value) {
        CounterConfig config = CounterConfig.get();
        if (!config.data.containsKey(source.guild().getId())) config.data.put(source.guild().getId(), new HashMap<>());
        if (!config.data.get(source.guild().getId()).containsKey(name))
            config.data.get(source.guild().getId()).put(name, 0);
        config.data.get(source.guild().getId()).put(name, Math.max(0, config.data.get(source.guild().getId()).get(name) + value));
        config.set();
        source.channel().sendMessage("New value: " + config.data.get(source.guild().getId()).get(name)).queue();
        return CommandResult.SUCCESS;
    }

    public static CommandResult show(CommandSource source, String name) {
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
            source.channel().sendMessage("No counter with that name exists in this guild.").queue();
            return CommandResult.FAILURE;
        }
        source.channel().sendMessage("Counter value: " + config.data.get(source.guild().getId()).get(name)).queue();
        return CommandResult.SUCCESS;
    }

    public static CommandResult dump(CommandSource source) {
        CounterConfig config = CounterConfig.get();
        if (!config.data.containsKey(source.guild().getId())) config.data.put(source.guild().getId(), new HashMap<>());
        config.set();
        if (config.data.get(source.guild().getId()).size() == 0) {
            source.channel().sendMessage("No counters in this guild.").queue();
            return CommandResult.FAILURE;
        }
        source.channel().sendMessage(config.data.get(source.guild().getId()).entrySet().stream().sorted(Map.Entry.comparingByKey()).map(stringIntegerEntry -> stringIntegerEntry.getKey() + ": " + stringIntegerEntry.getValue()).collect(Collectors.joining("\n"))).queue();
        return CommandResult.SUCCESS;
    }

    public static void register(CommandDispatcher<CommandSource, CommandResult> dispatcher) {
        dispatcher.register(Nodes.literal("counter")
                .requires(Requirements.IN_GUILD)
                .documentation("Counter to increment and decrement unsigned integers by name. Stored per-guild.")
                .then(Nodes.quotedString("name")
                        .documentation("`counter [name]` shows the value of that counter. `counter [name]++` and `counter [name]--` increment and decrement the counter, respectively. ")
                        .executes(context -> show(context.source(), QuotedStringArgumentType.get(context, "name")))
                )
                .executes(context -> dump(context.source()))
        );
        dispatcher.register(Nodes.literal("counters")
                .requires(Requirements.IN_GUILD)
                .documentation("Shows all counters for this guild")
                .executes(context -> dump(context.source()))
        );
    }

}
