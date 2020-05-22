package p0nki.p0nkisassistant.commands;

import p0nki.commandparser.argument.GreedyStringArgumentType;
import p0nki.commandparser.command.CommandDispatcher;
import p0nki.p0nkisassistant.utils.CommandResult;
import p0nki.p0nkisassistant.utils.CommandSource;
import p0nki.p0nkisassistant.utils.Nodes;

public class MathCommand {

    public static CommandResult math(CommandSource source, String expr) {
        expr = expr.replace(" ", "");
        if (expr.equals("9+10")) source.channel().sendMessage("21").queue();
        else if (expr.equals("10+9")) source.channel().sendMessage("19").queue();
        else {
            source.channel().sendMessage("Unable to parse. Try something simpler.").queue();
            return CommandResult.FAILURE;
        }
        return CommandResult.SUCCESS;
    }

    public static void register(CommandDispatcher<CommandSource, CommandResult> dispatcher) {
        dispatcher.register(Nodes.literal("math")
                .documentation("The one and only math evaluate command. Example usage: `math 9+10`")
                .then(Nodes.greedyString("expr")
                        .executes(context -> math(context.source(), GreedyStringArgumentType.get(context, "expr")))
                )
        );
    }

}
