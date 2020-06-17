package p0nki.easycommandtestbot.cogs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import p0nki.easycommand.annotations.CommandCog;
import p0nki.easycommandtestbot.data.StarboardChannel;
import p0nki.easycommandtestbot.data.StarboardData;
import p0nki.easycommandtestbot.data.StarboardReaction;
import p0nki.easycommandtestbot.data.StarredMessage;
import p0nki.easycommandtestbot.lib.utils.Holder;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@CommandCog(name = "starboard")
public class StarboardCog extends ListenerAdapter implements Holder {

    private boolean matches(MessageReaction r, StarboardReaction reaction) {
        if (reaction.isEmoji()) {
            return r.getReactionEmote().isEmoji() && r.getReactionEmote().getEmoji().equals(reaction.getReaction());
        } else {
            return r.getReactionEmote().isEmote() && r.getReactionEmote().getEmote().getId().equals(reaction.getReaction());
        }
    }

    private Set<User> getUsers(Message message, User author, StarboardReaction reaction) {
        List<MessageReaction> list = message.getReactions();
        for (MessageReaction r : list) {
            if (matches(r, reaction)) {
                return r.retrieveUsers().complete().stream().filter(Predicate.not(User::isBot)).filter(Predicate.not(author::equals)).collect(Collectors.toSet());
            }
        }
        return new HashSet<>();
    }

    private Message embed(TextChannel to, Message message, StarboardReaction reaction, Set<User> starrers, Color color, int starCount) {
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(color.getRGB())
                .setDescription(message.getContentRaw());
        String text = starCount + " " + (reaction.isEmoji() ? reaction.getReaction() : Objects.requireNonNull(jda().getEmoteById(reaction.getReaction())).getAsMention()) + " " + ((TextChannel) message.getChannel()).getAsMention();
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
        if (reaction.isEmoji()) m.addReaction(reaction.getReaction()).complete();
        else m.addReaction(Objects.requireNonNull(jda().getEmoteById(reaction.getReaction()))).complete();
//        Webhook.get("starboard").accept(to.getGuild().getId(), new WebhookEmbedBuilder()
//                .setTitle(new WebhookEmbed.EmbedTitle("Message posted in starboard", null))
//                .setDescription(starrers.stream().map(IMentionable::getAsMention).collect(Collectors.joining(", ")))
//                .addField(new WebhookEmbed.EmbedField(false, "Guild", to.getGuild().getName()))
//                .addField(new WebhookEmbed.EmbedField(false, "Starboard channel", to.toString()))
//                .addField(new WebhookEmbed.EmbedField(false, "Message channel", message.getChannel().toString()))
//                .addField(new WebhookEmbed.EmbedField(false, "Original message", "[Jump](" + message.getJumpUrl() + ")"))
//                .addField(new WebhookEmbed.EmbedField(false, "Starboard message", "[Jump](" + m.getJumpUrl() + ")"))
//        );
        return m;
    }

    private void processUnstarredMessage(Message original, StarboardChannel channel) {
        Set<User> starrers = getUsers(original, original.getAuthor(), channel.getReaction());
        if (starrers.size() >= channel.getCountRequired()) {
            Message starred = embed(jda().getTextChannelById(channel.getChannelID()), original, channel.getReaction(), starrers, channel.getColor(), starrers.size());
            channel.getOriginal2starred().put(original.getId(), starred.getId());
            channel.getStarred2original().put(starred.getId(), new StarredMessage(original.getChannel().getId(), original.getId()));
        }
    }

    private void processStarredMessage(Message original, Message starred, StarboardChannel channel) {
        Set<User> starrers = getUsers(original, original.getAuthor(), channel.getReaction());
        starrers.addAll(getUsers(starred, original.getAuthor(), channel.getReaction()));
        String firstLine =
                starrers.size()
                        + " "
                        + (channel.getReaction().isEmoji() ? channel.getReaction().getReaction() : Objects.requireNonNull(jda().getEmoteById(channel.getReaction().getReaction())).getAsMention())
                        + " "
                        + ((TextChannel) original.getChannel()).getAsMention();
        List<String> lines = new ArrayList<>(Arrays.asList(starred.getContentRaw().split("\n")));
        lines.remove(0);
        firstLine += "\n" + String.join("\n", lines);
        starred.editMessage(firstLine).embed(starred.getEmbeds().get(0)).queue();
    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        StarboardData starboardConfig = StarboardData.CACHE.of(event.getGuild());
        Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
        for (StarboardChannel channel : starboardConfig.getChannels().values()) {
            if (!channel.getChannelSet().contains(message.getChannel().getId())) continue;
            if (!matches(event.getReaction(), channel.getReaction())) continue;
            if (channel.getStarred2original().containsKey(message.getId())) {
                processStarredMessage(Objects.requireNonNull(jda().getTextChannelById(channel.getStarred2original().get(message.getId()).getChannel())).retrieveMessageById(channel.getStarred2original().get(message.getId()).getId()).complete(), message, channel);
            } else if (channel.getOriginal2starred().containsKey(message.getId())) {
                processStarredMessage(message, Objects.requireNonNull(jda().getTextChannelById(channel.getChannelID())).retrieveMessageById(channel.getOriginal2starred().get(message.getId())).complete(), channel);
            } else {
                processUnstarredMessage(message, channel);
            }
        }
        starboardConfig.write_();
    }
}
