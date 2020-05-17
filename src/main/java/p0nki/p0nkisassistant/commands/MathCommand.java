package p0nki.p0nkisassistant.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import p0nki.p0nkisassistant.listeners.CommandListener;
import p0nki.p0nkisassistant.utils.CommandSource;

import static p0nki.p0nkisassistant.utils.BrigadierUtils.*;

public class MathCommand {

    public static int math(CommandSource source, String expr) {
        expr = expr.replace(" ", "");
        if (expr.equals("9+10")) source.to.sendMessage("21").queue();
        else source.to.sendMessage("Unable to parse. Try something simpler.").queue();
        return CommandListener.SUCCESS;
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(literal("math")
                .then(argument("expr", greedyString())
                        .executes(context -> math(context.getSource(), StringArgumentType.getString(context, "expr")))
                )
        );
    }

}
