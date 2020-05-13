package p0nki.p0nkisassistant.arguments;

import net.dv8tion.jda.api.entities.Guild;
import p0nki.p0nkisassistant.P0nkisAssistant;

public class GuildArgumentType extends GenericArgumentType<Guild> {
    @Override
    protected String getIdByMention(String str) {
        return str;
    }

    @Override
    protected Guild parseById(String str) {
        return P0nkisAssistant.jda.getGuildById(str);
    }

    @Override
    protected String formatException(String str) {
        return "Unable to parse guild ID `" + str + "`";
    }

    @Override
    public String toString() {
        return "guild()";
    }

}
