package p0nki.assistant.lib.utils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import p0nki.assistant.data.BotConfig;
import p0nki.easycommand.utils.Optional;

import java.util.Collections;

public class DiscordSource {

    private final Message message;

    public DiscordSource(Message message) {
        this.message = message;
    }

    public void send(CharSequence result) {
        channel().sendMessage(result).allowedMentions(Collections.emptyList()).queue();
    }

    public boolean isOwner() {
        return user().getId().equals(BotConfig.VALUE.getOwner());
    }

    public JDA jda() {
        return message.getJDA();
    }

    public boolean isGuild() {
        return message.isFromGuild();
    }

    public Guild guild() {
        return message.getGuild();
    }

    public Optional<Guild> guildOpt() {
        return isGuild() ? Optional.of(guild()) : Optional.empty();
    }

    public MessageChannel channel() {
        return message.getChannel();
    }

    public PrivateChannel privateChannel() {
        return message.getPrivateChannel();
    }

    public boolean isPrivateChannel() {
        return message.isFromType(ChannelType.PRIVATE);
    }

    public Optional<PrivateChannel> privateChannelOpt() {
        return isPrivateChannel() ? Optional.of(privateChannel()) : Optional.empty();
    }

    public TextChannel textChannel() {
        return message.getTextChannel();
    }

    public boolean isTextChannel() {
        return message.isFromType(ChannelType.TEXT);
    }

    public Optional<TextChannel> textChannelOpt() {
        return isTextChannel() ? Optional.of(textChannel()) : Optional.empty();
    }

    public User user() {
        return message.getAuthor();
    }

    public Member member() {
        return message.getMember();
    }

    public boolean isMember() {
        return member() != null;
    }

    public Message message() {
        return message;
    }

}
