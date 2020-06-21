package p0nki.assistant.cogs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import p0nki.easycommand.annotations.*;
import p0nki.easycommand.utils.Optional;
import p0nki.assistant.Colors;
import p0nki.assistant.data.RolepollData;
import p0nki.assistant.lib.requirements.RequireGuild;
import p0nki.assistant.lib.requirements.RequireManageRoles;
import p0nki.assistant.lib.requirements.RequireManageServer;
import p0nki.assistant.lib.utils.DiscordSource;
import p0nki.assistant.lib.utils.DiscordUtils;
import p0nki.assistant.lib.utils.Holder;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@CommandCog(name = "rolepoll", requirements = RequireGuild.class)
public class RolepollCog extends ListenerAdapter implements Holder {

    @Command(literals = @Literal("rolepoll"), names = {"add", "a"}, requirements = {RequireManageServer.class, RequireManageRoles.class})
    public void add(@Source DiscordSource source, @Argument(name = "roles", maximumCount = 10) Role[] roles) {
        for (Role role : roles) {
            if (!source.member().canInteract(role)) {
                source.send("You cannot interact with the role " + role.getName() + " (" + role.getId() + ").");
                return;
            }
        }
        EmbedBuilder embed = new EmbedBuilder().setTitle("Rolepoll").setColor(Colors.PRIMARY);
        for (int i = 0; i < roles.length; i++) {
            embed.getDescriptionBuilder().append(DiscordUtils.UNICODE_NUMBERS[i]).append(" ").append(roles[i].getAsMention()).append("\n");
        }
        source.channel().sendMessage(embed.build()).queue(message -> {
            for (int i = 0; i < roles.length; i++) message.addReaction(DiscordUtils.UNICODE_NUMBERS[i]).queue();
            RolepollData.CACHE.of(source.guild()).makeRolepoll(message.getId(), Arrays.stream(roles).map(Role::getId).collect(Collectors.toList()));
        });
    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if (event.getUser().getId().equals(jda().getSelfUser().getId())) return;
        RolepollData data = RolepollData.CACHE.of(event.getGuild());
        if (data.getRolepolls().containsKey(event.getMessageId())) {
            List<String> roles = data.getRolepolls().get(event.getMessageId());
            if (event.getReactionEmote().isEmoji()) {
                Optional<Integer> index = DiscordUtils.unicodeEmojiToIndex(event.getReactionEmote().getEmoji());
                if (index.isPresent()) {
                    if (index.get() >= roles.size()) return;
                    String roleID = roles.get(index.get());
                    Role role = event.getGuild().getRoleById(roleID);
                    if (role != null) {
                        event.getGuild().addRoleToMember(event.getMember(), role).queue();
                        event.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(String.format("Added role %s (%s) in guild %s (%s)", role.getName(), role.getId(), event.getGuild().getName(), event.getGuild().getId())).queue());
                    } else {
                        System.out.println(String.format("ROLEPOLL null role %s in rolepoll %s", roleID, event.getMessageId()));
                    }
                }
            }
        }
    }

    @Override
    public void onGuildMessageReactionRemove(@Nonnull GuildMessageReactionRemoveEvent event) {
        if (Objects.requireNonNull(event.getUser()).getId().equals(jda().getSelfUser().getId())) return;
        RolepollData data = RolepollData.CACHE.of(event.getGuild());
        if (data.getRolepolls().containsKey(event.getMessageId())) {
            List<String> roles = data.getRolepolls().get(event.getMessageId());
            if (event.getReactionEmote().isEmoji()) {
                Optional<Integer> index = DiscordUtils.unicodeEmojiToIndex(event.getReactionEmote().getEmoji());
                if (index.isPresent()) {
                    if (index.get() >= roles.size()) return;
                    String roleID = roles.get(index.get());
                    Role role = event.getGuild().getRoleById(roleID);
                    if (role != null) {
                        Member member = event.getMember();
                        if (member != null) {
                            event.getGuild().removeRoleFromMember(event.getMember(), role).queue();
                            Objects.requireNonNull(event.getUser()).openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(String.format("Removed role %s (%s) in guild %s (%s)", role.getName(), role.getId(), event.getGuild().getName(), event.getGuild().getId())).queue());
                        } else {
                            System.out.println(String.format("ROLEPOLL null member %s in role %s in rolepoll %s", event.getUserId(), roleID, event.getMessageId()));
                        }
                    } else {
                        System.out.println(String.format("ROLEPOLL null role %s in rolepoll %s", roleID, event.getMessageId()));
                    }
                }
            }
        }
    }
}
