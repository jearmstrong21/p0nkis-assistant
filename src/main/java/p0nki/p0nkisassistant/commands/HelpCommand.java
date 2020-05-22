package p0nki.p0nkisassistant.commands;

import p0nki.commandparser.command.CommandDispatcher;
import p0nki.p0nkisassistant.P0nkisAssistant;
import p0nki.p0nkisassistant.listeners.CommandListener;
import p0nki.p0nkisassistant.utils.CommandResult;
import p0nki.p0nkisassistant.utils.CommandSource;
import p0nki.p0nkisassistant.utils.Nodes;

public class HelpCommand {

    public static CommandResult help(CommandSource source) {
        source.channel().sendMessage(
                "My prefix here is `" + CommandListener.INSTANCE.getPrefix(source) + "`.\n" +
                        "Invite link: " + P0nkisAssistant.jda.getInviteUrl() + "\n" +
                        "Commands:\n" +
                        "```\n" + CommandListener.INSTANCE.generateHelp() + "```")
                .queue(message -> message.suppressEmbeds(true).queue());
        return CommandResult.SUCCESS;
    }

    public static void register(CommandDispatcher<CommandSource, CommandResult> dispatcher) {
        dispatcher.register(Nodes.literal("help")
                .executes(context -> help(context.source()))
        );
    }

}
