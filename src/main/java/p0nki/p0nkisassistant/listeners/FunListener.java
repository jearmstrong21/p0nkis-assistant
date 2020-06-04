package p0nki.p0nkisassistant.listeners;

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import p0nki.p0nkisassistant.P0nkisAssistant;
import p0nki.p0nkisassistant.utils.CommandSource;
import p0nki.p0nkisassistant.utils.Constants;
import p0nki.p0nkisassistant.utils.Webhook;

import javax.annotation.Nonnull;

public class FunListener extends ListenerAdapter {

    public static FunListener INSTANCE = new FunListener();

    private FunListener() {

    }

//    public Supplier<Map<String, Emote>> EMOTE_TRIGGERS = () -> Config.INSTANCE.get("fun.emote_triggers").getAsJsonObject().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> Objects.requireNonNull(P0nkisAssistant.jda.getEmoteById(entry.getValue().getAsString()))));

    public boolean isHMM(CommandSource source, String msg) {
        msg = msg.toUpperCase();
        String command = CommandListener.INSTANCE.stripPrefix(msg);
        if (command == null) return false;
        command = command.toUpperCase();
        if (command.length() < 3) return false;
        if (command.charAt(0) != 'H') return false;
        for (int i = 1; i < command.length(); i++) {
            if (command.charAt(i) != 'M') return false;
        }
        return true;
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (isHMM(CommandSource.of(event), event.getMessage().getContentRaw())) {
            boolean deleted = false;
            try {
                event.getMessage().delete().complete();
                deleted = true;
            } catch (InsufficientPermissionException ignored) {

            }
            event.getChannel().sendMessage("HMMMMM").complete();
            Webhook.get("bot").accept(null,
                    new WebhookEmbedBuilder()
                            .setTitle(new WebhookEmbed.EmbedTitle("HMMMM", null))
                            .addField(new WebhookEmbed.EmbedField(false, "Guild", event.isFromGuild() ? event.getGuild().toString() : "no guild"))
                            .addField(new WebhookEmbed.EmbedField(false, "Channel", event.getChannel().toString()))
                            .addField(new WebhookEmbed.EmbedField(false, "User", event.getAuthor().getAsTag()))
                            .addField(new WebhookEmbed.EmbedField(false, "Deleted", deleted ? "yes" : "no"))
                            .setFooter(new WebhookEmbed.EmbedFooter("Requested by " + event.getAuthor().getAsTag(), event.getAuthor().getEffectiveAvatarUrl()))
                            .setColor(Constants.SUCCESS.getRGB())
            );
        }
        if (event.getMessage().isMentioned(P0nkisAssistant.P0NKI.get(), Message.MentionType.USER, Message.MentionType.HERE, Message.MentionType.EVERYONE, Message.MentionType.ROLE)) {
            event.getMessage().addReaction(P0nkisAssistant.EMOTE_PINGSOCK.get()).queue();
        }
//        EMOTE_TRIGGERS.get().forEach((key, emote) -> {
//            if (event.getMessage().getContentRaw().toLowerCase().contains(key.toLowerCase())) {
//                event.getMessage().addReaction(emote).queue();
//            }
//        });
    }

}
