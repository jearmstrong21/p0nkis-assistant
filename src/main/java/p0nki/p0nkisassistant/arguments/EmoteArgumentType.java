package p0nki.p0nkisassistant.arguments;

import net.dv8tion.jda.api.entities.Emote;
import p0nki.commandparser.command.CommandContext;
import p0nki.p0nkisassistant.P0nkisAssistant;
import p0nki.p0nkisassistant.utils.CommandSource;

public class EmoteArgumentType extends GenericArgumentType<Emote> {

    public static Emote get(CommandContext<?> context, String name) {
        return context.get(name, Emote.class);
    }

    @Override
    protected String getIdByMention(CommandSource source, String str) {
        return str.substring(str.length() - 19, str.length() - 1);
    }

    @Override
    protected Emote parseById(CommandSource source, String str) {
        return P0nkisAssistant.jda.getEmoteById(str);
    }

    @Override
    public String getName() {
        return "emote()";
    }

}