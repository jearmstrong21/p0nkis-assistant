package p0nki.p0nkisassistant.arguments;

import net.dv8tion.jda.api.entities.Role;
import p0nki.p0nkisassistant.P0nkisAssistant;

public class RoleArgumentType extends GenericArgumentType<Role> {

    @Override
    protected String getIdByMention(String str) {
        return str.substring(3, str.length() - 1);
    }

    @Override
    protected Role parseById(String str) {
        return P0nkisAssistant.jda.getRoleById(str);
    }

    @Override
    protected String formatException(String str) {
        return "Unable to parse role `" + str + "`";
    }

    @Override
    public String toString() {
        return "role()";
    }

}
