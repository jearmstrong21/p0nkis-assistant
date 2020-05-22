package p0nki.p0nkisassistant.arguments;

import net.dv8tion.jda.api.entities.TextChannel;
import p0nki.commandparser.command.CommandContext;
import p0nki.p0nkisassistant.P0nkisAssistant;
import p0nki.p0nkisassistant.utils.CommandSource;

public class TextChannelArgumentType extends GenericArgumentType<TextChannel> {

    public static TextChannel get(CommandContext<?> context, String name) {
        return context.get(name, TextChannel.class);
    }

    @Override
    protected String getIdByMention(CommandSource source, String str) {
        return str.substring(2, str.length() - 1);
    }

    @Override
    protected TextChannel parseById(CommandSource source, String str) {
        return P0nkisAssistant.jda.getTextChannelById(str);
    }

    @Override
    public String getName() {
        return "textChannel";
    }
}
