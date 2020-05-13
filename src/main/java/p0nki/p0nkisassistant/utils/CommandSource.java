package p0nki.p0nkisassistant.utils;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandSource {

    public User source;
    public Message message;
    public MessageChannel from;
    public MessageChannel to;

    public Guild guild() {
        return from instanceof TextChannel ? ((TextChannel) from).getGuild() : null;
    }

    public static CommandSource of(MessageReceivedEvent event) {
        return new CommandSource(event.getMessage().getAuthor(), event.getMessage(), event.getChannel(), event.getChannel());
    }

    public CommandSource(User source, Message message, MessageChannel from, MessageChannel to) {
        this.source = source;
        this.message = message;
        this.from = from;
        this.to = to;
    }

    public CommandSource from(MessageChannel from) {
        this.from = from;
        return this;
    }

    public CommandSource to(MessageChannel to) {
        this.to = to;
        return this;
    }

    public CommandSource source(User source) {
        this.source = source;
        return this;
    }

}
