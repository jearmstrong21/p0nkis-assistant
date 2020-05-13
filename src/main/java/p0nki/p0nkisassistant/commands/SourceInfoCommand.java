package p0nki.p0nkisassistant.commands;

import com.mojang.brigadier.CommandDispatcher;
import p0nki.p0nkisassistant.listeners.CommandListener;
import p0nki.p0nkisassistant.utils.CommandSource;
import p0nki.p0nkisassistant.utils.CustomEmbedBuilder;

import static p0nki.p0nkisassistant.utils.BrigadierUtils.literal;

public class SourceInfoCommand {

    public static int sourceInfo(CommandSource source) {
        source.to.sendMessage(new CustomEmbedBuilder()
                .source(source)
                .success()
                .title("Source info")
                .field("From channel", source.from.toString(), false)
                .field("To channel", source.to.toString(), false)
                .field("Guild", source.guild() + "", false)
                .field("From user", source.source.toString(), false)
                .field("Actually sent by", source.message.getAuthor().toString(), false)
                .build()).queue();
        return CommandListener.SUCCESS;
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(literal("sourceinfo")
                .executes(context -> sourceInfo(context.getSource()))
        );
    }

}
