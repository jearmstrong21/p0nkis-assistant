package p0nki.p0nkisassistant.commands;

import p0nki.commandparser.command.CommandDispatcher;
import p0nki.p0nkisassistant.P0nkisAssistant;
import p0nki.p0nkisassistant.utils.CommandResult;
import p0nki.p0nkisassistant.utils.CommandSource;
import p0nki.p0nkisassistant.utils.Constants;
import p0nki.p0nkisassistant.utils.Nodes;

public class PingCommand {

    public static CommandResult ping(CommandSource source) {
        long start = System.currentTimeMillis();
        source.channel().sendMessage("Ping!").queue(message -> {
            long end = System.currentTimeMillis();
            P0nkisAssistant.jda.getRestPing().queue(restPing -> {
                message.editMessage("Pong!\nRound-trip completion: " + (end - start) + "ms\nRest ping: " + restPing + "\nGateway ping: " + P0nkisAssistant.jda.getGatewayPing()).queue();
                source.message().addReaction(Constants.UNICODE_SUCCESS).queue();
            });
        });
        return CommandResult.IGNORE;
    }

    public static void register(CommandDispatcher<CommandSource, CommandResult> dispatcher) {
        dispatcher.register(Nodes.literal("ping")
                .documentation("Shows message send time, rest ping, and gateway ping")
                .executes(context -> ping(context.source()))
        );
    }

}
