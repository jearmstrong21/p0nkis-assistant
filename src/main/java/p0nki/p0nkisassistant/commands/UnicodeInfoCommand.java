package p0nki.p0nkisassistant.commands;

import com.mojang.brigadier.CommandDispatcher;
import p0nki.p0nkisassistant.listeners.CommandListener;
import p0nki.p0nkisassistant.utils.CommandSource;
import p0nki.p0nkisassistant.utils.Constants;
import p0nki.p0nkisassistant.utils.CustomEmbedBuilder;
import p0nki.p0nkisassistant.utils.Utils;

import java.util.stream.Collectors;

import static p0nki.p0nkisassistant.utils.BrigadierUtils.*;

public class UnicodeInfoCommand {

    public static int unicodeInfo(CommandSource source, String string) {
        source.to.sendMessage(new CustomEmbedBuilder()
                .source(source)
                .success()
                .title("Unicode info")
                .description(Utils.lengthLimit(string, Constants.EMBED_DESCRIPTION))
                .field("Codepoint names", Utils.lengthLimit("```" + string.chars().boxed().map(Character::getName).collect(Collectors.joining(", ")) + "```", Constants.EMBED_FIELD_VALUE), false)
                .field("Codepoint values", Utils.lengthLimit("```" + string.chars().boxed().map(x -> String.format("%04x", x)).collect(Collectors.joining(", ")) + "```", Constants.EMBED_FIELD_VALUE), false)
                .field("Copypaste to Jav️a", Utils.lengthLimit("```java\n\"" + string.chars().boxed().map(x -> "\\u" + String.format("%04x", x)).collect(Collectors.joining()) + "\"```", Constants.EMBED_FIELD_VALUE), false)
                .field("Length", string.length() + "", false)
                .build()).queue();
        return CommandListener.SUCCESS;
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(literal("unicodeinfo")
                .then(argument("text", greedyString())
                        .executes(context -> unicodeInfo(context.getSource(), context.getArgument("text", String.class)))
                )
        );
    }

}
