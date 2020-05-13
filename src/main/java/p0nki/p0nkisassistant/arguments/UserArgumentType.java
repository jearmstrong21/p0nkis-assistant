package p0nki.p0nkisassistant.arguments;

import net.dv8tion.jda.api.entities.User;
import p0nki.p0nkisassistant.P0nkisAssistant;

public class UserArgumentType extends GenericArgumentType<User> {

    @Override
    protected String getIdByMention(String str) {
        return str.substring(3, str.length() - 1);
    }

    @Override
    protected User parseById(String str) {
        return P0nkisAssistant.jda.getUserById(str);
    }

    @Override
    protected String formatException(String str) {
        return "Unable to parse user `" + str + "`";
    }

    @Override
    public String toString() {
        return "user()";
    }

}