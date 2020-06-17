package p0nki.easycommandtestbot.lib;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import p0nki.easycommandtestbot.lib.page.Paginator;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.IntFunction;

public class DiscordUtils {

    private DiscordUtils() {

    }

    /**
     * Returns a TextChannel if `id` is a valid text channel, then if that fails a PrivateChannel with the user given by `id`, and if that fails then null
     */
    @Nullable
    public static MessageChannel getChannelById(String id) {
        TextChannel tc = EasyListener.INSTANCE.getJda().getTextChannelById(id);
        if (tc != null) return tc;
        User user = EasyListener.INSTANCE.getJda().getUserById(id);
        if (user == null) return null;
        return user.openPrivateChannel().complete();
    }

    public static String resource(String path) {
        System.out.println("RESOURCE " + ClassLoader.getSystemClassLoader().getResource(path));
        return ClassLoader.getSystemClassLoader().getResource(path).getFile();
    }

    public static String data(String path) {
        return "data/" + path;
    }

    public static void paginateList(DiscordSource source, int itemCount, int itemsPerPage, int startPage, IntFunction<String> function) {
        int totalPageCount = itemCount / itemsPerPage + (itemCount % itemsPerPage > 0 ? 1 : 0);
        new Paginator(index -> {
            EmbedBuilder builder = new EmbedBuilder().setTitle("Page " + (index + 1) + "/" + totalPageCount);
            for (int i = index * itemsPerPage; i < Math.min((index + 1) * itemsPerPage, itemCount); i++) {
                builder.getDescriptionBuilder().append(i + 1).append(") ").append(function.apply(i)).append("\n");
            }
            return builder;
        }, totalPageCount, startPage).start(source);
    }

    public static String formatTimeDifference(long start, long end) {
        LocalDate startDate = LocalDate.from(new Date(start).toInstant().atZone(ZoneId.systemDefault()));
        LocalDate endDate = LocalDate.from(new Date(end).toInstant().atZone(ZoneId.systemDefault()));
        Period period = Period.between(startDate, endDate);
        long years = period.getYears();
        long months = period.getMonths();
        long days = period.getDays();

        Instant startInstant = new Date(start).toInstant();
        Instant endInstant = new Date(end).toInstant();
        long hours = ChronoUnit.HOURS.between(startInstant, endInstant) % 24;
        long minutes = ChronoUnit.MINUTES.between(startInstant, endInstant) % 60;
        long seconds = ChronoUnit.SECONDS.between(startInstant, endInstant) % 60;
        long millis = ChronoUnit.MILLIS.between(startInstant, endInstant) % 1000;
        return String.format("%s years, %s months, %s days, %s hours, %s minutes, %s seconds, and %s milliseconds", years, months, days, hours, minutes, seconds, millis);
    }

    @CheckReturnValue
    public static String censorPings(@Nullable DiscordSource source, @Nonnull String message) {
        message = message.replaceAll("@everyone", "@\u0435veryone");
        message = message.replaceAll("@here", "@h\u0435re");
        if (source != null && source.isGuild()) {
            for (Role role : source.guild().getRoles()) {
                message = message.replaceAll(role.getAsMention(), "@" + role.getName());
            }
            for (Member member : source.guild().getMembers()) {
                message = message.replaceAll("<@" + member.getId() + ">", "@" + member.getEffectiveName());
                message = message.replaceAll("<@!" + member.getId() + ">", "@" + member.getEffectiveName());
            }
        }
        return message;
    }

    public static EntityType getEntityType(ISnowflake snowflake) {
        if (snowflake instanceof TextChannel) return EntityType.TEXT_CHANNEL;
        if (snowflake instanceof Emote) return EntityType.EMOTE;
        if (snowflake instanceof Member) return EntityType.MEMBER;
        if (snowflake instanceof User) return EntityType.USER;
        if (snowflake instanceof Role) return EntityType.ROLE;
        if (snowflake instanceof LimitedSnowflake) return EntityType.LIMITED_SNOWFLAKE;
        throw new UnsupportedOperationException(snowflake.getClass().toString());
    }

}
