package p0nki.p0nkisassistant.commands;

import p0nki.commandparser.command.CommandDispatcher;
import p0nki.p0nkisassistant.utils.CommandResult;
import p0nki.p0nkisassistant.utils.CommandSource;
import p0nki.p0nkisassistant.utils.CustomEmbedBuilder;
import p0nki.p0nkisassistant.utils.Nodes;

public class SourceInfoCommand {

    public static CommandResult sourceInfo(CommandSource source) {
        source.channel().sendMessage(new CustomEmbedBuilder()
                .source(source)
                .success()
                .title("Source info")
                .field("Channel", source.channel().toString(), false)
                .field("Guild", source.guild() + "", false)
                .field("User", source.user().toString(), false)
                .build()).queue();
        return CommandResult.SUCCESS;
    }

    public static void register(CommandDispatcher<CommandSource, CommandResult> dispatcher) {
        dispatcher.register(Nodes.literal("sourceinfo")
                .documentation("Provides information about where this command was executed")
                .category("misc")
                .executes(context -> sourceInfo(context.source()))
        );
    }

}
