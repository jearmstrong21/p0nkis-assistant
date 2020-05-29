package p0nki.p0nkisassistant.commands;

import p0nki.commandparser.argument.GreedyStringArgumentType;
import p0nki.commandparser.command.CommandDispatcher;
import p0nki.p0nkisassistant.utils.CommandResult;
import p0nki.p0nkisassistant.utils.CommandSource;
import p0nki.p0nkisassistant.utils.Nodes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class SmartAssCommands {

    public static void register(CommandDispatcher<CommandSource, CommandResult> dispatcher) {
        dispatcher.register(Nodes.literal("xy", "xyprob", "xyproblem")
                .category("smartass")
                .documentation("XY Problem link")
                .executes(context -> {
                    context.source().channel().sendMessage("http://xyproblem.info/").queue();
                    return CommandResult.SUCCESS;
                }));
        dispatcher.register(Nodes.literal("lmgt", "lmgtfy")
                .category("smartass")
                .documentation("Let me google that (for you)")
                .then(Nodes.greedyString("text").executes(context -> {
                    String msg = "<http://lmgtfy.com/?q=" + URLEncoder.encode(GreedyStringArgumentType.get(context, "text"), StandardCharsets.UTF_8) + ">";
                    if (msg.length() > 2000) msg = msg.substring(0, 2000); // cut off at 2k chars
                    context.source().channel().sendMessage(msg).queue();
                    return CommandResult.SUCCESS;
                })));
    }

}
