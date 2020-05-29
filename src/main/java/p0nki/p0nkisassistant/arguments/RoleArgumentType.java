package p0nki.p0nkisassistant.arguments;

import net.dv8tion.jda.api.entities.Role;
import p0nki.commandparser.command.CommandContext;
import p0nki.p0nkisassistant.utils.CommandSource;

public class RoleArgumentType extends GenericArgumentType<Role> {

    public static Role get(CommandContext<?> context, String name) {
        return context.get(name, Role.class);
    }

    @Override
    protected String getIdByMention(CommandSource source, String str) {
        return str.substring(3, str.length() - 1);
    }

    @Override
    protected Role parseById(CommandSource source, String str) {
        return source.guild().getRoleById(str);
    }

    @Override
    public String getName() {
        return "role()";
    }

}
