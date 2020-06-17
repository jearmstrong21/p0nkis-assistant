package p0nki.easycommandtestbot.cogs;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import p0nki.easycommand.annotations.Argument;
import p0nki.easycommand.annotations.Command;
import p0nki.easycommand.annotations.CommandCog;
import p0nki.easycommand.annotations.Source;
import p0nki.easycommandtestbot.lib.requirements.RequireGuild;
import p0nki.easycommandtestbot.lib.utils.DiscordSource;
import p0nki.easycommandtestbot.lib.utils.DiscordUtils;
import p0nki.easycommandtestbot.lib.utils.Holder;

import java.util.Date;
import java.util.stream.Collectors;

@CommandCog(name = "info")
public class InfoCog extends ListenerAdapter implements Holder {

    @Command(names = "guildinfo", requirements = RequireGuild.class)
    public void guildinfo(@Source DiscordSource source) {
        source.send(source.guild().getName() + ": " + source.guild().getIconUrl());
    }

    @Command(names = "channelinfo")
    public void channelinfo(@Source DiscordSource source, @Argument(name = "channels") TextChannel[] channels) {
        StringBuilder result = new StringBuilder(channels.length + " channels\n");
        for (TextChannel channel : channels) {
            result.append(channel.getName()).append("\n");
        }
        source.send(result);
    }

    @Command(names = "emoteinfo")
    public void emoteinfo(@Source DiscordSource source, @Argument(name = "emote") Emote[] emotes) {
        StringBuilder result = new StringBuilder(emotes.length + " emotes\n");
        for (Emote emote : emotes) {
            result.append(emote.getName()).append(": ").append(emote.getId()).append(" ").append(emote.getImageUrl()).append("\n");
        }
        source.send(result);
    }

    @Command(names = "userinfo")
    public void userinfo(@Source DiscordSource source) {
        userinfo(source, new User[]{source.user()});
    }

    @Command(names = "userinfo")
    public void userinfo(@Source DiscordSource source, @Argument(name = "user") User[] users) {
        StringBuilder result = new StringBuilder(users.length + " users\n");
        for (User user : users) {
            result.append(user.getAsTag()).append(": ").append(user.getEffectiveAvatarUrl()).append("\n");
        }
        source.send(result);
    }

    @Command(names = "memberinfo", requirements = RequireGuild.class)
    public void memberinfo(@Source DiscordSource source) {
        memberinfo(source, new Member[]{source.member()});
    }

    @Command(names = "memberinfo", requirements = RequireGuild.class)
    public void memberinfo(@Source DiscordSource source, @Argument(name = "member") Member[] members) {
        StringBuilder result = new StringBuilder(members.length + " members\n");
        for (Member member : members) {
            result.append(member.getUser().getAsTag()).append(": ");
            result.append(member.getRoles().stream().map(Role::getName).collect(Collectors.joining(", ")));
            result.append("\n");
        }
        source.send(result);
    }

    @Command(names = "roleinfo")
    public void roleinfo(@Source DiscordSource source, @Argument(name = "role") Role[] roles) {
        StringBuilder result = new StringBuilder(roles.length + " roles\n");
        for (Role role : roles) {
            result.append(role.getName()).append(" ").append(role.getColor()).append("\n");
        }
        source.send(result);
    }

    @Command(names = {"snowflakeinfo", "sf", "sfi", "snowflake"})
    public void snowflakeinfo(@Source DiscordSource source, @Argument(name = "snowflake") ISnowflake[] snowflakes) {
        StringBuilder result = new StringBuilder(snowflakes.length + " snowflakes\n");
        for (ISnowflake snowflake : snowflakes) {
            long date = snowflake.getTimeCreated().toInstant().toEpochMilli();
            // TODO: abstract this to DiscordUtils.timeDifferenceToString(long start, long end) and pass in `date` and `System.currentTimeMillis()`
            result.append(String.format("Snowflake (type %s): created at %s at unix time %s. That's %s ago.\n",
                    DiscordUtils.getEntityType(snowflake), new Date(date), date, DiscordUtils.formatTimeDifference(date, System.currentTimeMillis())));
        }
        source.send(result);
    }

}