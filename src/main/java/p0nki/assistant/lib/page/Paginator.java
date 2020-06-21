package p0nki.assistant.lib.page;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import p0nki.assistant.lib.task.DelayedTaskManager;
import p0nki.assistant.lib.utils.DiscordSource;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Paginator extends ListenerAdapter {

    private static final long LIFETIME = 60000;
    private static final boolean JUMP_TO = false;
    private final PageSupplier pageSupplier;
    private final int totalPageCount;
    private int currentPageIndex;
    private Message message = null;
    private User paginator = null;
    private long lastInteractionTime;

    public Paginator(PageSupplier pageSupplier, int totalPageCount, int currentPageIndex) {
        this.pageSupplier = pageSupplier;
        this.totalPageCount = totalPageCount;
        this.currentPageIndex = currentPageIndex;
    }

    private void checkInteractionTime() {
        lastInteractionTime = System.currentTimeMillis();
        new DelayedTaskManager(() -> {
            if (message != null && System.currentTimeMillis() - lastInteractionTime > LIFETIME) stop();
        }).schedule(LIFETIME);
    }

    public void assertRunning() {
        if (message == null) throw new IllegalStateException("Paginator isn't running");
    }

    public void assertNotRunning() {
        if (message != null) throw new IllegalStateException("Paginator is running");
    }

    public void setIndex(int index) {
        assertRunning();
        currentPageIndex = index;
        if (index < 0 || index >= totalPageCount) throw new IllegalStateException("Invalid index " + index);
        message.editMessage(pageSupplier.create(index).build()).queue();
    }

    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    public void start(DiscordSource source) {
        start(source.channel(), source.user());
    }

    private void start(MessageChannel channel, User paginator) {
        assertNotRunning();
        this.paginator = paginator;
        message = channel.sendMessage(pageSupplier.create(currentPageIndex).build()).complete();
        if (JUMP_TO) message.addReaction("\u23ea").queue();
        message.addReaction("\u2b05\ufe0f").queue();
        message.addReaction("\u23f9\ufe0f").queue();
        message.addReaction("\u27a1\ufe0f").queue();
        if (JUMP_TO) message.addReaction("\u23e9").queue();
        channel.getJDA().addEventListener(this);
        checkInteractionTime();
    }

    public void stop() {
        assertRunning();
        message.getJDA().removeEventListener(this);
        message.editMessage("Paginator stopped.").queue();
        message.clearReactions().queue(null, throwable -> {
        });
        message = null;
        paginator = null;
    }

    private boolean isGoToStart(MessageReaction.ReactionEmote emote) {
        return emote.isEmoji() && emote.getEmoji().equals("\u23ea");
    }

    private boolean isLeft(MessageReaction.ReactionEmote emote) {
        return emote.isEmoji() && emote.getEmoji().equals("\u2b05\ufe0f");
    }

    private boolean isStop(MessageReaction.ReactionEmote emote) {
        return emote.isEmoji() && emote.getEmoji().equals("\u23f9\ufe0f");
    }

    private boolean isRight(MessageReaction.ReactionEmote emote) {
        return emote.isEmoji() && emote.getEmoji().equals("\u27a1\ufe0f");
    }

    private boolean isGoToEnd(MessageReaction.ReactionEmote emote) {
        return emote.isEmoji() && emote.getEmoji().equals("\u23e9");
    }

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        if (message != null) {
            if (event.getMessageId().equals(message.getId())) {
                if (event.getUserId().equals(paginator.getId())) {
                    if (JUMP_TO && isGoToStart(event.getReactionEmote())) {
                        setIndex(0);
                        checkInteractionTime();
                    } else if (isLeft(event.getReactionEmote())) {
                        if (currentPageIndex > 0) {
                            setIndex(currentPageIndex - 1);
                            checkInteractionTime();
                        }
                    } else if (isStop(event.getReactionEmote())) {
                        stop();
                    } else if (isRight(event.getReactionEmote())) {
                        if (currentPageIndex < totalPageCount - 1) {
                            setIndex(currentPageIndex + 1);
                            checkInteractionTime();
                        }
                    } else if (JUMP_TO && isGoToEnd(event.getReactionEmote())) { // TODO: instead of hardcoding unicode emoji, make them a private static final field in this class
                        setIndex(totalPageCount - 1);
                        checkInteractionTime();
                    }
                }
                if (!event.getUserId().equals(event.getJDA().getSelfUser().getId())) {
                    event.getReaction().removeReaction(Objects.requireNonNull(event.getUser())).queue(null, throwable -> {
                    });
                }
            }
        }
    }
}
