package p0nki.p0nkisassistant.arguments;

import net.dv8tion.jda.api.entities.Guild;
import p0nki.commandparser.command.CommandContext;
import p0nki.p0nkisassistant.P0nkisAssistant;
import p0nki.p0nkisassistant.utils.CommandSource;

public class GuildArgumentType extends GenericArgumentType<Guild> {
    public static Guild get(CommandContext<?> context, String name) {
        return context.get(name, Guild.class);
    }

    @Override
    protected String getIdByMention(CommandSource source, String str) {
        return str;
    }

    @Override
    protected Guild parseById(CommandSource source, String str) {
        return P0nkisAssistant.jda.getGuildById(str);
    }

    @Override
    public String getName() {
        return "guild()";
    }

}
