package p0nki.p0nkisassistant.arguments;

import net.dv8tion.jda.api.entities.TextChannel;
import p0nki.p0nkisassistant.P0nkisAssistant;

public class ChannelArgumentType extends GenericArgumentType<TextChannel> {

    @Override
    protected String getIdByMention(String str) {
        return str.substring(2, str.length() - 1);
    }

    @Override
    protected TextChannel parseById(String str) {
        return P0nkisAssistant.jda.getTextChannelById(str);
    }

    @Override
    protected String formatException(String str) {
        return "Unable to parse channel `" + str + "`";
    }

    @Override
    public String toString() {
        return "channel()";
    }
}
