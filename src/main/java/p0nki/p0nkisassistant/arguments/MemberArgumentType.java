package p0nki.p0nkisassistant.arguments;

import net.dv8tion.jda.api.entities.Member;
import p0nki.commandparser.command.CommandContext;
import p0nki.p0nkisassistant.utils.CommandSource;

public class MemberArgumentType extends GenericArgumentType<Member> {
    public static Member get(CommandContext<?> context, String name) {
        return context.get(name, Member.class);
    }

    @Override
    protected String getIdByMention(CommandSource source, String str) {
        return str.substring(3, str.length() - 1);
    }

    @Override
    protected Member parseById(CommandSource source, String str) {
        return source.guild().getMemberById(str);
    }

    @Override
    public String getName() {
        return "member";
    }
}
