package p0nki.p0nkisassistant.commands;

import p0nki.commandparser.argument.GreedyStringArgumentType;
import p0nki.commandparser.command.CommandDispatcher;
import p0nki.p0nkisassistant.utils.*;

import java.util.stream.Collectors;

public class UnicodeInfoCommand {

    public static CommandResult unicodeInfo(CommandSource source, String string) {
        source.channel().sendMessage(new CustomEmbedBuilder()
                .source(source)
                .success()
                .title("Unicode info")
                .description(Utils.lengthLimit(string, Constants.EMBED_DESCRIPTION))
                .field("Codepoint names", Utils.lengthLimit("```" + string.chars().boxed().map(Character::getName).collect(Collectors.joining(", ")) + "```", Constants.EMBED_FIELD_VALUE), false)
                .field("Codepoint values", Utils.lengthLimit("```" + string.chars().boxed().map(x -> String.format("%04x", x)).collect(Collectors.joining(", ")) + "```", Constants.EMBED_FIELD_VALUE), false)
                .field("Copypaste to Jav️a", Utils.lengthLimit("```java\n\"" + string.chars().boxed().map(x -> "\\u" + String.format("%04x", x)).collect(Collectors.joining()) + "\"```", Constants.EMBED_FIELD_VALUE), false)
                .field("Length", string.length() + "", false)
                .build()).queue();
        return CommandResult.SUCCESS;
    }

    public static void register(CommandDispatcher<CommandSource, CommandResult> dispatcher) {
        dispatcher.register(Nodes.literal("unicodeinfo")
                .documentation("Codepoint information for a piece of text")
                .category("misc")
                .then(Nodes.greedyString("text")
                        .executes(context -> unicodeInfo(context.source(), GreedyStringArgumentType.get(context, "text")))
                )
        );
    }

}
