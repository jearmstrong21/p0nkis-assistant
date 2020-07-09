package p0nki.assistant.lib.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import p0nki.assistant.lib.EasyListener;
import p0nki.assistant.lib.page.Paginator;
import p0nki.easycommand.utils.Optional;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.function.IntFunction;

public class DiscordUtils {

    private DiscordUtils() {

    }

    public static final String[] UNICODE_NUMBERS = new String[]{
            "\u0031\ufe0f\u20e3",
            "\u0032\ufe0f\u20e3",
            "\u0033\ufe0f\u20e3",
            "\u0034\ufe0f\u20e3",
            "\u0035\ufe0f\u20e3",
            "\u0036\ufe0f\u20e3",
            "\u0037\ufe0f\u20e3",
            "\u0038\ufe0f\u20e3",
            "\u0039\ufe0f\u20e3",
            "\ud83d\udd1f"
    };

    // https://stackoverflow.com/a/5599842/9609025
    public static String formatMemory(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB", "EB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static Optional<Integer> unicodeEmojiToIndex(String emoji) {
        for (int i = 0; i < UNICODE_NUMBERS.length; i++) {
            if (UNICODE_NUMBERS[i].equals(emoji)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    public static void verify(boolean b) {
        if (!b) throw new AssertionError("oopsie");
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
        return Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource(path)).getFile();
    }

    public static String data(String path) {
        return "data/" + path;
    }

    public static void paginateList(DiscordSource source, int itemCount, int startPage, IntFunction<String> function) {
        final int itemsPerPage = 10;
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
        StringBuilder result = new StringBuilder();
        if (years > 0) result.append(years).append(" years, ");
        if (months > 0) result.append(months).append(" months, ");
        if (days > 0) result.append(days).append(" days, ");
        if (minutes > 0) result.append(minutes).append(" minutes, ");
        if (seconds > 0) result.append(seconds).append(" seconds, ");
        if (millis > 0) result.append(millis).append(" milliseconds, ");
        String str = result.toString();
        if (str.endsWith(", ")) return str.substring(0, str.length() - 2);
        return str;
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
