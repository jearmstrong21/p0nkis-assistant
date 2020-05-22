package p0nki.p0nkisassistant.utils;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandSource {

    private final Message message;

    public CommandSource(Message message) {
        this.message = message;
    }

    public static CommandSource of(MessageReceivedEvent event) {
        return new CommandSource(event.getMessage());
    }

    public Message message() {
        return message;
    }

    public MessageChannel channel() {
        return message.getChannel();
    }

    public boolean isGuild() {
        return channel() instanceof TextChannel;
    }

    public Member member() {
        return guild().getMember(user());
    }

    public User user() {
        return message.getAuthor();
    }

    public TextChannel textChannel() {
        return (TextChannel) channel();
    }

    public Guild guild() {
        return textChannel().getGuild();
    }

}
