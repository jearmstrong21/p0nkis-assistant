package p0nki.assistant.cogs;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.Fitzpatrick;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import p0nki.assistant.lib.requirements.RequireGuild;
import p0nki.assistant.lib.utils.DiscordSource;
import p0nki.assistant.lib.utils.DiscordUtils;
import p0nki.assistant.lib.utils.Holder;
import p0nki.easycommand.annotations.Argument;
import p0nki.easycommand.annotations.Command;
import p0nki.easycommand.annotations.CommandCog;
import p0nki.easycommand.annotations.Source;

import java.util.Date;
import java.util.stream.Collectors;

@CommandCog(name = "info")
public class InfoCog extends ListenerAdapter implements Holder {

    @Command(names = {"guild", "guildinfo"}, requirements = RequireGuild.class)
    public void guildinfo(@Source DiscordSource source) {
        source.send(source.guild().getName() + ": " + source.guild().getIconUrl());
    }

    @Command(names = {"channel", "channelinfo"})
    public void channelinfo(@Source DiscordSource source, @Argument(name = "channels") TextChannel[] channels) {
        StringBuilder result = new StringBuilder(channels.length + " channels\n");
        for (TextChannel channel : channels) {
            result.append(channel.getName()).append("\n");
        }
        source.send(result);
    }

    @Command(names = {"emote", "emoteinfo"})
    public void emoteinfo(@Source DiscordSource source, @Argument(name = "emote") Emote[] emotes) {
        StringBuilder result = new StringBuilder(emotes.length + " emotes\n");
        for (Emote emote : emotes) {
            result.append(emote.getName()).append(": ").append(emote.getId()).append(" ").append(emote.getImageUrl()).append("\n");
        }
        source.send(result);
    }

    @Command(names = {"user", "userinfo"})
    public void userinfo(@Source DiscordSource source) {
        userinfo(source, new User[]{source.user()});
    }

    @Command(names = {"user", "userinfo"})
    public void userinfo(@Source DiscordSource source, @Argument(name = "user") User[] users) {
        StringBuilder result = new StringBuilder(users.length + " users\n");
        for (User user : users) {
            result.append(user.getAsTag()).append(" - ").append(user.getAsMention()).append(": ").append(user.getEffectiveAvatarUrl()).append("\n");
        }
        source.send(result);
    }

    @Command(names = {"member", "memberinfo"}, requirements = RequireGuild.class)
    public void memberinfo(@Source DiscordSource source) {
        memberinfo(source, new Member[]{source.member()});
    }

    @Command(names = {"member", "memberinfo"}, requirements = RequireGuild.class)
    public void memberinfo(@Source DiscordSource source, @Argument(name = "member") Member[] members) {
        StringBuilder result = new StringBuilder(members.length + " members\n");
        for (Member member : members) {
            result.append(member.getUser().getAsTag()).append(" - ").append(member.getAsMention()).append(": ").append(member.getRoles().stream().map(Role::getAsMention).collect(Collectors.joining(", "))).append("\n");
        }
        source.send(result);
    }

    @Command(names = {"role", "roleinfo"})
    public void roleinfo(@Source DiscordSource source, @Argument(name = "role") Role[] roles) {
        StringBuilder result = new StringBuilder(roles.length + " roles\n");
        for (Role role : roles) {
            result.append(role.getName()).append(" ").append(role.getColor()).append("\n");
        }
        source.send(result);
    }

    @Command(names = {"snowflake", "snowflakeinfo"})
    public void snowflakeinfo(@Source DiscordSource source, @Argument(name = "snowflake") ISnowflake[] snowflakes) {
        StringBuilder result = new StringBuilder(snowflakes.length + " snowflakes\n");
        for (ISnowflake snowflake : snowflakes) {
            long date = snowflake.getTimeCreated().toInstant().toEpochMilli();
            result.append(String.format("Snowflake (type %s): created at %s at unix time %s. That's %s ago.\n",
                    DiscordUtils.getEntityType(snowflake), new Date(date), date, DiscordUtils.formatTimeDifference(date, System.currentTimeMillis())));
        }
        source.send(result);
    }

    @Command(names = {"unicode", "unicodeinfo"})
    public void unicodeinfo(@Source DiscordSource source, @Argument(name = "text") String text) {
        source.send(text.chars().boxed().map(Character::getName).collect(Collectors.joining(", ")));
    }

    @Command(names = {"emoji", "emojiinfo"})
    public void emojiinfo(@Source DiscordSource source, @Argument(name = "unicodeEmoji") String unicodeEmoji) {
        Emoji emoji = EmojiManager.getByUnicode(unicodeEmoji);
        if (emoji == null) {
            source.send("No such emoji");
        } else {
            StringBuilder result = new StringBuilder(String.format("Unicode: %s\n", emoji.getUnicode()));
            if (emoji.supportsFitzpatrick()) {
                for (Fitzpatrick fitzpatrick : Fitzpatrick.values()) {
                    result.append(String.format("Fitzpatrick %s: %s\n", fitzpatrick, emoji.getUnicode(fitzpatrick)));
                }
            } else {
                result.append("Emoji does not support fitzpatrick modifiers\n");
            }
            result.append(String.format("Aliases: %s\n", String.join(", ", emoji.getAliases())));
            result.append(String.format("Tags: %s\n", String.join(", ", emoji.getTags())));
            result.append(String.format("Description: %s\n", emoji.getDescription()));
            result.append(String.format("HTML Decimal: %s\n", emoji.getHtmlDecimal()));
            result.append(String.format("HTML Hexadecimal: %s", emoji.getHtmlHexadecimal()));
            source.send(result);
        }
    }

}
