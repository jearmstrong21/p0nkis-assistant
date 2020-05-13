package p0nki.p0nkisassistant.commands;

import com.mojang.brigadier.CommandDispatcher;
import p0nki.p0nkisassistant.P0nkisAssistant;
import p0nki.p0nkisassistant.listeners.CommandListener;
import p0nki.p0nkisassistant.utils.CommandSource;

import static p0nki.p0nkisassistant.utils.BrigadierUtils.literal;

public class HelpCommand {

    public static int help(CommandSource source) {
        source.to.sendMessage(
                "My prefix here is `" + CommandListener.INSTANCE.getPrefix(source) + "`.\n" +
                        "Invite link: " + P0nkisAssistant.jda.getInviteUrl() + "\n" +
                        "Commands:\n" +
                        "```\n" + CommandListener.INSTANCE.dumpTree() + "```")
                .queue(message -> message.suppressEmbeds(true).queue());
        return CommandListener.SUCCESS;
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(literal("help")
                .executes(context -> help(context.getSource()))
        );
    }

}
