package p0nki.p0nkisassistant.listeners;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import p0nki.p0nkisassistant.P0nkisAssistant;
import p0nki.p0nkisassistant.utils.Constants;
import p0nki.p0nkisassistant.utils.Utils;
import p0nki.p0nkisassistant.utils.Webhook;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class AdminCommandListener extends ListenerAdapter {

    public static AdminCommandListener INSTANCE = new AdminCommandListener();

    private final Set<String> logoutMessageIDs = new HashSet<>();

    private AdminCommandListener() {

    }

    @Override
    public void onPrivateMessageReactionAdd(@Nonnull PrivateMessageReactionAddEvent event) {
        if (P0nkisAssistant.P0NKI.get().equals(event.getUser())) {
            if (logoutMessageIDs.contains(event.getMessageId())) {
                if (event.getReactionEmote().getEmoji().equals(Constants.UNICODE_SUCCESS)) {
                    event.getChannel().sendMessage("Forced logout.").queue();
                    Webhook.get("bot").accept(null, new WebhookMessageBuilder().setContent("Forced logout"));
                    System.exit(1);
                } else {
                    event.getChannel().sendMessage("Cancelled forced logout.").queue();
                    logoutMessageIDs.remove(event.getMessageId());
                }
            }
        }
    }

    @Override
    public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {
        if (event.getAuthor().equals(P0nkisAssistant.P0NKI.get())) {
            if (event.getMessage().getContentRaw().equals("logout")) {
                event.getChannel().sendMessage("Verify logout").queue(message -> {
                    logoutMessageIDs.add(message.getId());
                    Utils.silenceExceptions(() -> message.addReaction(Constants.UNICODE_SUCCESS).queue());
                    Utils.silenceExceptions(() -> message.addReaction(Constants.UNICODE_FAILURE).queue());
                });
            }
        }
    }
}
