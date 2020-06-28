package p0nki.assistant.cogs;

import p0nki.assistant.lib.requirements.RequireGuild;
import p0nki.assistant.lib.requirements.RequireOwner;
import p0nki.assistant.lib.utils.DiscordSource;
import p0nki.assistant.lib.utils.Holder;
import p0nki.easycommand.annotations.*;
import p0nki.easycommand.arguments.Parsers;

import java.util.Objects;

@CommandCog(name = "nick", requirements = {RequireOwner.class, RequireGuild.class})
public class NickCog implements Holder {

    private final static String ERROR = "Cannot change to that nick. Am I missing permissions or is it too long?";

    @Command(literals = @Literal("nick"), names = {"reset", "r"})
    public void reset(@Source DiscordSource source) {
        Objects.requireNonNull(source.guild().getMember(jda().getSelfUser())).modifyNickname("")
                .queue(aVoid -> {
                    source.send("Nickname reset");
                    System.out.println("NICK RESET " + source.guild().getName() + ":" + source.guild().getId());
                }, error -> source.send(ERROR));
    }

    @Command(literals = @Literal("nick"), names = {"set", "s"})
    public void set(@Source DiscordSource source, @Argument(name = "nick", modifiers = Parsers.GREEDY_STRING) String nick) {
        Objects.requireNonNull(source.guild().getMember(jda().getSelfUser())).modifyNickname(nick)
                .queue(aVoid -> {
                    System.out.println("NICK SET " + source.guild().getName() + ":" + source.guild().getId() + " " + nick);
                    source.send("Nickname changed");
                }, error -> source.send(ERROR));
    }

}
