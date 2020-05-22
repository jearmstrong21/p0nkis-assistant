package p0nki.p0nkisassistant.utils;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Utils {

    public static long snowflakeToUnixTime(long id) {
        return ((id >> 22) + 1420070400000L);
    }

    public static String formatUnixTime(long time) {
        return new Date(time).toString();
    }

    public static String formatDiscordID(String id) {
        return formatUnixTime(snowflakeToUnixTime(Long.parseLong(id)));
    }

    public static <T> void serialize(String filename, T value, boolean prettyprint) {
        try {
            PrintWriter writer = new PrintWriter("data/" + instanceName() + "/" + filename + ".json");
            if (prettyprint) writer.println(new GsonBuilder().setPrettyPrinting().create().toJson(value));
            else writer.println(new Gson().toJson(value));
            writer.close();
        } catch (FileNotFoundException e) {
            report(e, null, null);
        }
    }

    @Nonnull
    public static <T> T deserialize(String filename, Class<T> clazz) {
        try {
            return new Gson().fromJson(new FileReader("data/" + instanceName() + "/" + filename + ".json"), clazz);
        } catch (FileNotFoundException e) {
            try {
                T value = clazz.getConstructor().newInstance();
                serialize(filename, value, true);
                return value;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                report(ex, null, null);
                System.exit(1);
                return null;
            }
        }
    }

    public static String prettyprint(JsonElement element) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(element);
    }

    public static String instanceName() {
//        return "prod";
        return System.getProperty("os.name").toLowerCase().contains("mac") ? "debug" : "prod";
    }

//    public static URL resource(String path) {
//        return ClassLoader.getSystemClassLoader().getResource(instanceName() + "/" + path);
//    }

    public static void silenceExceptions(RunnableWithException runnable) {
        try {
            runnable.run();
        } catch (Throwable ignored) {
        }
    }

    public interface RunnableWithException {
        void run() throws Throwable;
    }

    public static String load(String path) throws IOException {
        return Files.readString(Path.of("data/" + instanceName() + "/" + path));
    }

    public static <T> T ifnull(T val, T ifnull) {
        return val == null ? ifnull : val;
    }

    public static String truncate(String value, int length) {
        if (value.length() > length) {
            return value.substring(0, length - 3) + "...";
        }
        return value;
    }

    public static String lengthLimit(String value, int length) {
        if (value.length() >= length) {
            String res = "--- too long to fit in " + length + " characters ---";
            if (res.length() >= length) {
                return "-".repeat(length - 1);
            }
            return res;
        }
        return value;
    }

    public static String toString(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public static String colorToHex(Color color) {
        return String.format("%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    public static String image(int width, int height, Color color, Color textColor, String format, String text) {
        return "https://dummyimage.com/" + width + "x" + height + "/" + colorToHex(color) + "/" + colorToHex(textColor) + "." + format + "&" + text;
    }

    public static String image(int width, int height, Color color) {
        return image(width, height, color, color, "png", "");
    }

    public static String image(Color color) {
        return image(500, 500, color);
    }

    public static void report(Throwable t, String command, CommandSource source) {
        String trace = toString(t);
        String out = "";
        out += "COMMAND: " + command + "\n";
        out += "SOURCE: source=" + (source == null ? "null" : source.user() + "," + source.channel() + "," + source.guild()) + "\n";
        out += "TRACE:\n\n";
        out += trace;
        if (out.length() > 2000 - 6) {
            Webhook.get("bot").accept(null, new WebhookMessageBuilder().addFile("STACKTRACE " + new Date().toString(), out.getBytes()));
        } else {
            Webhook.get("bot").accept(null, new WebhookMessageBuilder().append("```" + out + "```"));
        }
    }

    public static <T> Predicate<T> alwaysTrue() {
        return __ -> true;
    }

    public static <T> Predicate<T> alwaysFalse() {
        return __ -> false;
    }

    public static <T> Supplier<T> alwaysSupply(T value) {
        return () -> value;
    }
//
//    public static Predicate<CommandSource> isAdmin() {
//        return source -> {
//            if (source.source.equals(P0nkisAssistant.P0NKI.get())) return true;
//            if (source.from instanceof TextChannel) {
//                return Objects.requireNonNull(((TextChannel) source.from).getGuild().getMember(source.source)).hasPermission(Permission.ADMINISTRATOR);
//            }
//            return false;
//        };
//    }
//
//    public static Predicate<CommandSource> isFromGuild() {
//        return source -> source.from instanceof TextChannel;
//    }

//    public static <T> Predicate<T> or(Predicate<T>... predicates) {
//        return test -> Arrays.stream(predicates).anyMatch(predicate -> predicate.test(test));
//    }
//
//    public static <T> Predicate<T> and(Predicate<T>... predicates) {
//        return test -> Arrays.stream(predicates).allMatch(predicate -> predicate.test(test));
//    }

//    public static void safeSleep(long millis) {
//        long start = System.currentTimeMillis();
//        while (System.currentTimeMillis() < start + millis) {
//            try {
//                Thread.sleep(start + millis - System.currentTimeMillis());
//            } catch (InterruptedException ignored) {
//
//            }
//        }
//    }

    public static String format(OffsetDateTime time) {
        return Date.from(time.toInstant()).toString();
    }

}
