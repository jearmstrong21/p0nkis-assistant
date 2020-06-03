package p0nki.p0nkisassistant.listeners;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.events.ResumedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import p0nki.p0nkisassistant.P0nkisAssistant;
import p0nki.p0nkisassistant.data.RolepollConfig;
import p0nki.p0nkisassistant.utils.Constants;
import p0nki.p0nkisassistant.utils.CustomEmbedBuilder;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RolepollListener extends ListenerAdapter {

    public static RolepollListener INSTANCE = new RolepollListener();

    public static RolepollConfig CACHED = RolepollConfig.get();

    private void remRole(Guild guild, Member member, Role role) {
        guild.removeRoleFromMember(member, role).reason("Reaction role").queue();
        member.getUser().openPrivateChannel().queue(
                privateChannel -> privateChannel.sendMessageFormat("Removed role %s (%s) in guild %s (%s)", role.getName(), role.getId(), guild.getName(), guild.getId()).queue(),
                throwable -> {
                });
    }

    private void addRole(Guild guild, Member member, Role role) {
        guild.addRoleToMember(member, role).reason("Reaction role").queue();
        member.getUser().openPrivateChannel().queue(
                privateChannel -> privateChannel.sendMessageFormat("Added role %s (%s) in guild %s (%s)", role.getName(), role.getId(), guild.getName(), guild.getId()).queue(),
                throwable -> {
                });
    }

    private long updateRolepoll(RolepollConfig.Rolepoll poll) {
        long start = System.currentTimeMillis();
        Guild guild = Objects.requireNonNull(P0nkisAssistant.jda.getGuildById(poll.guildID));
        TextChannel channel = Objects.requireNonNull(guild.getTextChannelById(poll.channelID));
        Message message = channel.retrieveMessageById(poll.messageID).complete();
        CustomEmbedBuilder embed = new CustomEmbedBuilder()
                .success()
                .source(P0nkisAssistant.jda.getSelfUser())
                .title("Role poll");
        for (int i = 0; i < poll.roles.size(); i++) {
            embed.field(Constants.UNICODE_NUMBERS[i], Objects.requireNonNull(P0nkisAssistant.jda.getRoleById(poll.roles.get(i))).getAsMention(), false);
        }
        message.editMessage("_ _").embed(embed.build()).complete();
        List<String> reacts = Arrays.asList(Arrays.copyOf(Constants.UNICODE_NUMBERS, poll.roles.size()));
        for (String r : reacts) {
            message.addReaction(r).complete();
        }
        for (MessageReaction reaction : message.getReactions()) {
            if (reaction.getReactionEmote().isEmote()) {
                reaction.removeReaction().complete();
                continue;
            } else {
                if (!reacts.contains(reaction.getReactionEmote().getEmoji())) {
                    reaction.removeReaction().complete();
                    continue;
                }
            }
            List<User> users = reaction.retrieveUsers().complete();
            Role role = Objects.requireNonNull(guild.getRoleById(poll.roles.get(reacts.indexOf(reaction.getReactionEmote().getEmoji()))));
            for (Member member : guild.getMembers()) {
                if (member.getUser().getId().equals(P0nkisAssistant.jda.getSelfUser().getId())) continue;
                if (member.getRoles().contains(role) && !users.contains(member.getUser())) {
                    remRole(guild, member, role);
                } else if (!member.getRoles().contains(role) && users.contains(member.getUser())) {
                    addRole(guild, member, role);
                }
            }
        }
        return System.currentTimeMillis() - start;
    }

    private RolepollListener() {

    }

    @CheckReturnValue
    private List<Long> updateAllRolepolls() {
        CACHED = RolepollConfig.get();
        return CACHED.rolepolls.values().stream().map(this::updateRolepoll).collect(Collectors.toList());
    }

    public void updateAllRolepollsAndLog() {
        System.out.println("ROLEPOLL UPDATE. Total time: " + updateAllRolepolls().stream().reduce(0L, Long::sum));
    }

    private void updateRolepollAdd(Guild guild, String id, Member member, MessageReaction.ReactionEmote emote) {
        if (CACHED.rolepolls.containsKey(id)) {
            RolepollConfig.Rolepoll poll = CACHED.rolepolls.get(id);
            List<String> reacts = Arrays.asList(Arrays.copyOf(Constants.UNICODE_NUMBERS, poll.roles.size()));
            if (emote.isEmoji()) {
                if (reacts.contains(emote.getEmoji())) {
                    int index = reacts.indexOf(emote.getEmoji());
                    if (index < poll.roles.size()) {
                        Role role = Objects.requireNonNull(guild.getRoleById(poll.roles.get(index)));
                        addRole(guild, member, role);
                    }
                }
            }
        }
    }

    private void updateRolepollRem(Guild guild, String id, @Nonnull Member member, MessageReaction.ReactionEmote emote) {
        if (CACHED.rolepolls.containsKey(id)) {
            RolepollConfig.Rolepoll poll = CACHED.rolepolls.get(id);
            List<String> reacts = Arrays.asList(Arrays.copyOf(Constants.UNICODE_NUMBERS, poll.roles.size()));
            if (emote.isEmoji()) {
                if (reacts.contains(emote.getEmoji())) {
                    int index = reacts.indexOf(emote.getEmoji());
                    if (index < poll.roles.size()) {
                        Role role = Objects.requireNonNull(guild.getRoleById(poll.roles.get(index)));
                        remRole(guild, member, role);
                    }
                }
            }
        }
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        updateAllRolepollsAndLog();
    }

    @Override
    public void onResume(@Nonnull ResumedEvent event) {
        updateAllRolepollsAndLog();
    }

    @Override
    public void onReconnect(@Nonnull ReconnectedEvent event) {
        updateAllRolepollsAndLog();
    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        updateRolepollAdd(event.getGuild(), event.getMessageId(), event.getMember(), event.getReactionEmote());
    }

    @Override
    public void onGuildMessageReactionRemove(@Nonnull GuildMessageReactionRemoveEvent event) {
        if (event.getMember() != null) {
            updateRolepollRem(event.getGuild(), event.getMessageId(), event.getMember(), event.getReactionEmote());
        }
    }

}
