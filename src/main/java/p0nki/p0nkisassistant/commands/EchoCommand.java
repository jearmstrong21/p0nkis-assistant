package p0nki.p0nkisassistant.commands;

import com.mojang.brigadier.CommandDispatcher;
import p0nki.p0nkisassistant.listeners.CommandListener;
import p0nki.p0nkisassistant.utils.CommandSource;

import static p0nki.p0nkisassistant.utils.BrigadierUtils.*;

public class EchoCommand {

    public static int echo(CommandSource source, String text) {
        source.to.sendMessage(text).queue();
        return CommandListener.SUCCESS;
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(literal("echo")
                .then(argument("text", greedyString())
                        .executes(context -> echo(context.getSource(), context.getArgument("text", String.class)))
                )
        );
    }

}
