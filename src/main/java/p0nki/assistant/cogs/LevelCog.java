package p0nki.assistant.cogs;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import p0nki.assistant.data.LevelData;
import p0nki.assistant.lib.requirements.RequireGuild;
import p0nki.assistant.lib.requirements.RequireManageServer;
import p0nki.assistant.lib.utils.DiscordSource;
import p0nki.assistant.lib.utils.DiscordUtils;
import p0nki.assistant.lib.utils.Holder;
import p0nki.easycommand.annotations.*;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@CommandCog(name = "level", requirements = RequireGuild.class)
public class LevelCog extends ListenerAdapter implements Holder {

    @Command(literals = @Literal("level"), names = {"enable", "e"}, requirements = RequireManageServer.class)
    public void enable(@Source DiscordSource source) {
        LevelData data = LevelData.CACHE.of(source);
        if (data.isEnabled()) {
            source.send("Levelling is already enabled");
        } else {
            data.setEnabled(true);
            source.send("Levelling enabled");
        }
    }

    @Command(literals = @Literal("level"), names = {"disable", "d"}, requirements = RequireManageServer.class)
    public void disable(@Source DiscordSource source) {
        LevelData data = LevelData.CACHE.of(source);
        if (data.isEnabled()) {
            data.setEnabled(false);
            source.send("Levelling disabled. Data will not be lost, merely frozen.");
        } else {
            source.send("Levelling is already disabled");
        }
    }

    @Command(names = "rank")
    public void rank(@Source DiscordSource source) {
        rank(source, source.user());
    }

    @Command(names = "rank")
    public void rank(@Source DiscordSource source, @Argument(name = "user") User user) {
        LevelData data = LevelData.CACHE.of(source);
        if (data.isEnabled()) {
            int level = data.getLevel(user);
            int xp = data.getXp(user);
            int xpRequired = LevelData.getXpForLevel(level);
            source.send(String.format("At level %s, with %s/%s XP to the next level. Chat on, товарищ!", level, xp, xpRequired));
        } else {
            source.send("Levelling is disabled in this guild");
        }
    }

    @Command(names = "levels")
    public void levels(@Source DiscordSource source) {
        LevelData data = LevelData.CACHE.of(source);
        if (data.isEnabled()) {
            List<User> users = data.getKeys().stream().map(jda()::getUserById).filter(Objects::nonNull).sorted((o1, o2) -> {
                int l1 = data.getLevel(o1);
                int l2 = data.getLevel(o2);
                int x1 = data.getXp(o1);
                int x2 = data.getXp(o2);
                if (l1 < l2) return 1;
                if (l1 > l2) return -1;
                if (x1 < x2) return 1;
                if (x1 > x2) return -1;
                return o1.getAsTag().compareTo(o2.getAsTag());
            }).collect(Collectors.toList());
            DiscordUtils.paginateList(source, users.size(), 10, 0, index -> {
                User user = users.get(index);
                int level = data.getLevel(user);
                return user.getAsMention() + " (" + user.getAsTag() + "): " + level + " (" + data.getXp(user) + " / " + LevelData.getXpForLevel(level) + ")";
            });
        } else {
            source.send("Levelling is disabled in this guild");
        }
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        LevelData data = LevelData.CACHE.of(event.getGuild());
        User user = event.getAuthor();
        if (user.isBot()) return;
        if (event.getMember() == null) return;
        if (data.getLastXpTime(user) + 60 * 1000 < System.currentTimeMillis()) {
            if (data.gainXp(user)) {
                String levelUpMessage = data.getLevelUpMessage();
                if (levelUpMessage.length() > 0 && data.isEnabled()) {
                    levelUpMessage = levelUpMessage.replace("{MENTION}", event.getAuthor().getAsMention());
                    levelUpMessage = levelUpMessage.replace("{LEVEL}", data.getLevel(event.getAuthor()) + "");
                    event.getChannel().sendMessage(levelUpMessage).queue();
                }
            }
        }

    }

}
