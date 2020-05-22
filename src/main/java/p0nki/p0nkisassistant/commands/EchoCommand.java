package p0nki.p0nkisassistant.commands;

import p0nki.commandparser.argument.GreedyStringArgumentType;
import p0nki.commandparser.command.CommandDispatcher;
import p0nki.p0nkisassistant.utils.CommandResult;
import p0nki.p0nkisassistant.utils.CommandSource;
import p0nki.p0nkisassistant.utils.Nodes;

public class EchoCommand {

    public static CommandResult echo(CommandSource source, String text) {
        source.channel().sendMessage(text).queue();
        return CommandResult.SUCCESS;
    }

    public static void register(CommandDispatcher<CommandSource, CommandResult> dispatcher) {
        dispatcher.register(Nodes.literal("echo")
                .then(Nodes.greedyString("text")
                        .executes(context -> echo(context.source(), GreedyStringArgumentType.get(context, "text")))
                )
        );
    }

}
