package p0nki.p0nkisassistant.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import p0nki.p0nkisassistant.P0nkisAssistant;
import p0nki.p0nkisassistant.utils.Constants;
import p0nki.p0nkisassistant.utils.Utils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ReactionListener extends ListenerAdapter {

    public static ReactionListener INSTANCE = new ReactionListener();
    private final List<Listener> events;

    public ReactionListener() {
        events = new ArrayList<>();
    }

    public void registerVerify(Message message, User user, Runnable yes, Runnable no) {
        Utils.silenceExceptions(() -> message.addReaction(Constants.UNICODE_SUCCESS).queue());
        Utils.silenceExceptions(() -> message.addReaction(Constants.UNICODE_FAILURE).queue());
        registerOnAdd(message, MessageReaction.ReactionEmote.fromUnicode(Constants.UNICODE_SUCCESS, P0nkisAssistant.jda), user, () -> {
            yes.run();
            return true;
        });
        registerOnAdd(message, MessageReaction.ReactionEmote.fromUnicode(Constants.UNICODE_FAILURE, P0nkisAssistant.jda), user, () -> {
            no.run();
            return true;
        });
    }

    public void registerOnAdd(Message message, MessageReaction.ReactionEmote emote, User user, Supplier<Boolean> onAdd) {
        events.add(new Listener(message.getId(), emote, user, onAdd, Utils.alwaysSupply(false)));
    }

    public void registerOnRemove(Message message, MessageReaction.ReactionEmote emote, User user, Supplier<Boolean> onRemove) {
        events.add(new Listener(message.getId(), emote, user, Utils.alwaysSupply(false), onRemove));
    }

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        List<Listener> finished = new ArrayList<>();
        for (Listener listener : events) {
            if (listener.messageId.equals(event.getMessageId()) && listener.emote.equals(event.getReactionEmote()) && listener.user.equals(event.getUser()) && listener.onAdd.get()) {
                finished.add(listener);
            }
        }
        events.removeAll(finished);
    }

    @Override
    public void onMessageReactionRemove(@Nonnull MessageReactionRemoveEvent event) {
        List<Listener> finished = new ArrayList<>();
        for (Listener listener : events) {
            if (listener.messageId.equals(event.getMessageId()) && listener.emote.equals(event.getReactionEmote()) && listener.user.equals(event.getUser()) && listener.onRemove.get()) {
                finished.add(listener);
            }
        }
        events.removeAll(finished);
    }

    private static class Listener {
        String messageId;
        MessageReaction.ReactionEmote emote;
        User user;
        Supplier<Boolean> onAdd;
        Supplier<Boolean> onRemove;

        public Listener(String messageId, MessageReaction.ReactionEmote emote, User user, Supplier<Boolean> onAdd, Supplier<Boolean> onRemove) {
            this.messageId = messageId;
            this.emote = emote;
            this.user = user;
            this.onAdd = onAdd;
            this.onRemove = onRemove;
        }
    }

}
