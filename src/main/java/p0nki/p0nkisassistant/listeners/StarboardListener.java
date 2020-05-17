package p0nki.p0nkisassistant.listeners;

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import p0nki.p0nkisassistant.P0nkisAssistant;
import p0nki.p0nkisassistant.data.StarboardConfig;
import p0nki.p0nkisassistant.utils.Webhook;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StarboardListener extends ListenerAdapter {

    public static StarboardListener INSTANCE = new StarboardListener();

    public StarboardListener() {

    }

    private boolean matches(MessageReaction r, StarboardConfig.Reaction reaction) {
        if (reaction.isEmoji) {
            return r.getReactionEmote().isEmoji() && r.getReactionEmote().getEmoji().equals(reaction.reaction);
        } else {
            return r.getReactionEmote().isEmote() && r.getReactionEmote().getEmote().getId().equals(reaction.reaction);
        }
    }

    private Set<User> getUsers(Message message, User author, StarboardConfig.Reaction reaction) {
        List<MessageReaction> list = message.getReactions();
        for (MessageReaction r : list) {
            if (matches(r, reaction)) {
                return r.retrieveUsers().complete().stream().filter(Predicate.not(User::isBot)).filter(Predicate.not(author::equals)).collect(Collectors.toSet());
            }
        }
        return new HashSet<>();
    }

    private Message embed(TextChannel to, Message message, StarboardConfig.Reaction reaction, Set<User> starrers, StarboardConfig.Color color, int starCount) {
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(new Color(color.r, color.g, color.b).getRGB())
                .setDescription(message.getContentRaw());
        String text = starCount + " " + (reaction.isEmoji ? reaction.reaction : Objects.requireNonNull(P0nkisAssistant.jda.getEmoteById(reaction.reaction)).getAsMention()) + " " + ((TextChannel) message.getChannel()).getAsMention();
        if (message.getAttachments().size() == 1) {
            embed.setImage(message.getAttachments().get(0).getUrl());
        } else if (message.getAttachments().size() > 1) {
            embed.addField("Attachments", "" + message.getAttachments().size(), false);
            text += "\n" + message.getAttachments().stream().map(Message.Attachment::getUrl).collect(Collectors.joining("\n"));
        }
        if (message.getEmbeds().size() > 0) {
            embed.addField("Embeds", message.getEmbeds().size() + "", false);
        }
        embed.addField("Link", "[Jump](" + message.getJumpUrl() + ")", false);
        embed.setAuthor(message.getAuthor().getAsTag(), null, message.getAuthor().getEffectiveAvatarUrl());
        Message m = to.sendMessage(text).embed(embed.build()).complete();
        if (reaction.isEmoji) m.addReaction(reaction.reaction).complete();
        else m.addReaction(Objects.requireNonNull(P0nkisAssistant.jda.getEmoteById(reaction.reaction))).complete();
        Webhook.get("starboard").accept(to.getGuild().getId(), new WebhookEmbedBuilder()
                .setTitle(new WebhookEmbed.EmbedTitle("Message posted in starboard", null))
                .setDescription(starrers.stream().map(IMentionable::getAsMention).collect(Collectors.joining(", ")))
                .addField(new WebhookEmbed.EmbedField(false, "Guild", to.getGuild().getName()))
                .addField(new WebhookEmbed.EmbedField(false, "Starboard channel", to.toString()))
                .addField(new WebhookEmbed.EmbedField(false, "Message channel", message.getChannel().toString()))
                .addField(new WebhookEmbed.EmbedField(false, "Original message", "[Jump](" + message.getJumpUrl() + ")"))
                .addField(new WebhookEmbed.EmbedField(false, "Starboard message", "[Jump](" + m.getJumpUrl() + ")"))
        );
        return m;
    }

    private void processUnstarredMessage(Message original, StarboardConfig.Channel channel) {
        Set<User> starrers = getUsers(original, original.getAuthor(), channel.reaction);
        if (starrers.size() >= channel.countRequired) {
            Message starred = embed(P0nkisAssistant.jda.getTextChannelById(channel.channelID), original, channel.reaction, starrers, channel.color, starrers.size());
            channel.original2starred.put(original.getId(), starred.getId());
            channel.starred2original.put(starred.getId(), new StarboardConfig.Message(original.getChannel().getId(), original.getId()));
        }
    }

    private void processStarredMessage(Message original, Message starred, StarboardConfig.Channel channel) {
        Set<User> starrers = getUsers(original, original.getAuthor(), channel.reaction);
        starrers.addAll(getUsers(starred, original.getAuthor(), channel.reaction));
        String firstLine =
                starrers.size()
                        + " "
                        + (channel.reaction.isEmoji ? channel.reaction.reaction : Objects.requireNonNull(P0nkisAssistant.jda.getEmoteById(channel.reaction.reaction)).getAsMention())
                        + " "
                        + ((TextChannel) original.getChannel()).getAsMention();
        List<String> lines = new ArrayList<>(Arrays.asList(starred.getContentRaw().split("\n")));
        lines.remove(0);
        firstLine += "\n" + String.join("\n", lines);
        starred.editMessage(firstLine).embed(starred.getEmbeds().get(0)).queue();
    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        StarboardConfig starboardConfig = StarboardConfig.get(event.getGuild().getId());
        Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
        for (StarboardConfig.Channel channel : starboardConfig.channels.values()) {
            if (!channel.channelSet.contains(message.getChannel().getId())) continue;
            if (!matches(event.getReaction(), channel.reaction)) continue;
            if (channel.starred2original.containsKey(message.getId())) {
                processStarredMessage(Objects.requireNonNull(P0nkisAssistant.jda.getTextChannelById(channel.starred2original.get(message.getId()).channel)).retrieveMessageById(channel.starred2original.get(message.getId()).id).complete(), message, channel);
            } else if (channel.original2starred.containsKey(message.getId())) {
                processStarredMessage(message, Objects.requireNonNull(P0nkisAssistant.jda.getTextChannelById(channel.channelID)).retrieveMessageById(channel.original2starred.get(message.getId())).complete(), channel);
            } else {
                processUnstarredMessage(message, channel);
            }
        }
        starboardConfig.set();
    }
}
