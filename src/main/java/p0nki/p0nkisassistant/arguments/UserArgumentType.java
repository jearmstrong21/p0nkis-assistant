package p0nki.p0nkisassistant.arguments;

import net.dv8tion.jda.api.entities.User;
import p0nki.commandparser.command.CommandContext;
import p0nki.p0nkisassistant.P0nkisAssistant;
import p0nki.p0nkisassistant.utils.CommandSource;

public class UserArgumentType extends GenericArgumentType<User> {

    public static User get(CommandContext<?> context, String name) {
        return context.get(name, User.class);
    }

    @Override
    protected String getIdByMention(CommandSource source, String str) {
        return str.substring(3, str.length() - 1);
    }

    @Override
    protected User parseById(CommandSource source, String str) {
        return P0nkisAssistant.jda.getUserById(str);
    }

    @Override
    public String getName() {
        return "user()";
    }

}