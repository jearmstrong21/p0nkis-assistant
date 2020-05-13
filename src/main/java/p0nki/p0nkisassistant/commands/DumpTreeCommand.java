package p0nki.p0nkisassistant.commands;

import com.mojang.brigadier.CommandDispatcher;
import p0nki.p0nkisassistant.listeners.CommandListener;
import p0nki.p0nkisassistant.utils.CommandSource;
import p0nki.p0nkisassistant.utils.Constants;

import java.util.Date;

import static p0nki.p0nkisassistant.utils.BrigadierUtils.literal;

public class DumpTreeCommand {

    public static int dumpTree(CommandSource source) {
        String str = CommandListener.INSTANCE.dumpTree();
        if (str.length() > Constants.MESSAGE_SIZE - 7) {
            source.to.sendFile(str.getBytes(), "Tree dump " + new Date().toString() + ".txt").queue();
        } else {
            source.to.sendMessage("```\n" + str + "```").queue();
        }
        return CommandListener.SUCCESS;
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(literal("dumptree")
                .executes(context -> dumpTree(context.getSource()))
        );
    }

}
