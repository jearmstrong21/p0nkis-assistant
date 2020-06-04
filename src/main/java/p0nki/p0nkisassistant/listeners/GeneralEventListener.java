package p0nki.p0nkisassistant.listeners;

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.events.ResumedEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import p0nki.p0nkisassistant.P0nkisAssistant;
import p0nki.p0nkisassistant.data.BotConfig;
import p0nki.p0nkisassistant.utils.CommandSource;
import p0nki.p0nkisassistant.utils.Constants;
import p0nki.p0nkisassistant.utils.CustomEmbedBuilder;
import p0nki.p0nkisassistant.utils.Webhook;

import javax.annotation.Nonnull;

public class GeneralEventListener extends ListenerAdapter {

    public static GeneralEventListener INSTANCE = new GeneralEventListener();

    private GeneralEventListener() {

    }

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        Webhook.get("bot").accept(null, new WebhookEmbedBuilder().setColor(Constants.SUCCESS.getRGB()).setTitle(new WebhookEmbed.EmbedTitle("Joined guild", null)).setDescription(event.getGuild().toString()));
        Webhook.get("bot").ping(null);
    }

    @Override
    public void onGuildLeave(@Nonnull GuildLeaveEvent event) {
        Webhook.get("bot").accept(null, new WebhookEmbedBuilder().setColor(Constants.FAILURE.getRGB()).setTitle(new WebhookEmbed.EmbedTitle("Left guild", null)).setDescription(event.getGuild().toString()));
        Webhook.get("bot").ping(null);
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        Webhook.get("bot").accept(null, new WebhookEmbedBuilder().setColor(Constants.SUCCESS.getRGB()).setTitle(new WebhookEmbed.EmbedTitle("Bot started", null)));
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (!event.getAuthor().isBot() && event.getMessage().isMentioned(P0nkisAssistant.jda.getSelfUser(), Message.MentionType.USER)) {
            CommandSource source = CommandSource.of(event);
            event.getChannel().sendMessage(new CustomEmbedBuilder()
                    .source(source)
                    .success()
                    .title("Who pinged? \uD83D\uDC40")
                    .description("My prefix here is " + BotConfig.CACHE.prefix)
                    .build()).queue();
        }
    }

    @Override
    public void onReconnect(@Nonnull ReconnectedEvent event) {
        System.out.println(event);
        Webhook.get("bot").accept(null, new WebhookEmbedBuilder().setColor(Constants.SUCCESS.getRGB()).setTitle(new WebhookEmbed.EmbedTitle("Bot reconnected", null)));
    }

    @Override
    public void onResume(@Nonnull ResumedEvent event) {
        System.out.println(event);
        Webhook.get("bot").accept(null, new WebhookEmbedBuilder().setColor(Constants.SUCCESS.getRGB()).setTitle(new WebhookEmbed.EmbedTitle("Bot resumed", null)));
    }
}
