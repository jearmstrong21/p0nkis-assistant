package p0nki.p0nkisassistant.arguments;

import net.dv8tion.jda.api.entities.Emote;
import p0nki.p0nkisassistant.P0nkisAssistant;

public class EmoteArgumentType extends GenericArgumentType<Emote> {

    @Override
    protected String getIdByMention(String str) {
        return str.substring(str.length() - 19, str.length() - 1);
    }

    @Override
    protected Emote parseById(String str) {
        return P0nkisAssistant.jda.getEmoteById(str);
    }

    @Override
    protected String formatException(String str) {
        return "Unable to parse emote `" + str + "`";
    }

    @Override
    public String toString() {
        return "emote()";
    }

}