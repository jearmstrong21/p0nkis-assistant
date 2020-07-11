package p0nki.assistant.cogs;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import p0nki.assistant.lib.requirements.RequireOwner;
import p0nki.assistant.lib.utils.DiscordSource;
import p0nki.easycommand.annotations.Argument;
import p0nki.easycommand.annotations.Command;
import p0nki.easycommand.annotations.CommandCog;
import p0nki.easycommand.annotations.Source;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@CommandCog(name = "shadowban", requirements = RequireOwner.class)
public class ShadowBanCog extends ListenerAdapter {

    private final List<String> shadowbanned = new ArrayList<>();

    @Command(names = "shadowban")
    public void shadowban(@Source DiscordSource source, @Argument(name = "user") User user) {
        shadowbanned.add(user.getId());
        source.send("\uD83D\uDC4C");
    }

    @Command(names = "unshadowban")
    public void unshadowban(@Source DiscordSource source, @Argument(name = "user") User user) {
        shadowbanned.remove(user.getId());
        source.send("\uD83D\uDC4C");
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (shadowbanned.contains(event.getAuthor().getId())) {
            event.getMessage().delete().queue(aVoid -> {
            }, throwable -> {
            });
        }
    }
}
