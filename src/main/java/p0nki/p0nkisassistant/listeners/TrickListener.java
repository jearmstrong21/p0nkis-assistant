package p0nki.p0nkisassistant.listeners;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import p0nki.espressolisp.exceptions.LispException;
import p0nki.p0nkisassistant.commands.LispCommands;
import p0nki.p0nkisassistant.data.TricksConfig;
import p0nki.p0nkisassistant.utils.CommandSource;

import javax.annotation.Nonnull;

public class TrickListener extends ListenerAdapter {

    public static final TrickListener INSTANCE = new TrickListener();

    private TrickListener() {

    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        String prefix = CommandListener.INSTANCE.getPrefix(new CommandSource(event.getMessage()));
        String content = event.getMessage().getContentRaw();
        if (content.startsWith(prefix + "!")) {
            String name = content.substring(prefix.length() + 1);
            TricksConfig.Guild tricks = TricksConfig.get().guild(event.getGuild().getId());
            if (tricks.has(name)) {
                TricksConfig.Trick trick = tricks.trick(name);
                if (trick.isLisp()) {
                    try {
                        LispCommands.evaluate(LispCommands.createFresh(), trick.source, () -> {
                        }, () -> {
                        }, () -> event.getChannel().sendMessage("Timed out while evaluating trick").queue(), (e) -> event.getChannel().sendMessage("Exception " + e.getMessage() + " at token " + e.getToken()).queue(), obj -> {
                            try {
                                event.getChannel().sendMessage(obj.fullyDereference().lispStr()).queue();
                            } catch (LispException e) {
                                event.getChannel().sendMessage("Issue dereferencing object").queue();
                            }
                        }, 2000);
                    } catch (LispException e) {
                        event.getChannel().sendMessage("Error creating context with message " + e.getMessage() + " and token " + e.getToken()).queue();
                    }
                } else {
                    event.getChannel().sendMessage(trick.source).queue();
                }
            }
        }
    }
}
