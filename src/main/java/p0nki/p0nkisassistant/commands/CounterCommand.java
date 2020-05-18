package p0nki.p0nkisassistant.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import p0nki.p0nkisassistant.data.CounterConfig;
import p0nki.p0nkisassistant.listeners.CommandListener;
import p0nki.p0nkisassistant.utils.CommandSource;

import java.util.HashMap;

import static p0nki.p0nkisassistant.utils.BrigadierUtils.*;

public class CounterCommand {

    public static int modify(CommandSource source, String name, int value) {
        if (source.guild() == null) {
            source.to.sendMessage("Only in guild.").queue();
            return CommandListener.FAILURE;
        }
        CounterConfig config = CounterConfig.get();
        if (!config.data.containsKey(source.guild().getId())) config.data.put(source.guild().getId(), new HashMap<>());
        if (!config.data.get(source.guild().getId()).containsKey(name))
            config.data.get(source.guild().getId()).put(name, 0);
        config.data.get(source.guild().getId()).put(name, Math.max(0, config.data.get(source.guild().getId()).get(name) + value));
        config.set();
        source.to.sendMessage("New value: " + config.data.get(source.guild().getId()).get(name)).queue();
        return CommandListener.SUCCESS;
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(literal("++")
                .then(argument("name", string())
                        .executes(context -> modify(context.getSource(), StringArgumentType.getString(context, "name"), 1))
                )
        );
        dispatcher.register(literal("--")
                .then(argument("name", string())
                        .executes(context -> modify(context.getSource(), StringArgumentType.getString(context, "name"), -1))
                )
        );
    }

}
