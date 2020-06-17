package p0nki.easycommandtestbot.lib.utils;

import p0nki.easycommand.utils.Optional;

import java.util.Collections;
import java.util.Set;

public class TimeDuration {

    private final int amount;
    private final Type type;

    public TimeDuration(int amount, Type type) {
        this.amount = amount;
        this.type = type;
    }

    public static Optional<TimeDuration> parse(String str) {
        for (Type type : Type.values()) {
            for (String ending : type.getNames()) {
                if (str.endsWith(ending)) {
                    String number = str.substring(0, str.length() - ending.length()).trim();
                    return Optional.emptyIfThrow(() -> new TimeDuration(Integer.parseInt(number), type));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return amount + type.getMainName();
    }

    public long getMilliseconds() {
        return amount * type.getMillis();
    }

    public int getAmount() {
        return amount;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        SECOND(1000, "s", "sec", "secs", "second", "seconds"),
        MINUTE(60 * 1000, "m", "min", "mins", "minute", "minutes"),
        HOUR(60 * 60 * 1000, "h", "hr", "hrs", "hour", "hours"),
        DAY(24 * 60 * 60 * 1000, "d", "day", "days"),
        WEEK(7 * 24 * 60 * 60 * 1000, "wk", "weeks");

        private final long millis;
        private final Set<String> names;
        private final String mainName;

        Type(long millis, String... names) {
            this.millis = millis;
            this.names = Collections.unmodifiableSet(Set.of(names));
            mainName = this.names.stream().reduce("", (a, b) -> (a.length() > b.length() ? a : b));
        }

        public long getMillis() {
            return millis;
        }

        public Set<String> getNames() {
            return names;
        }

        public String getMainName() {
            return mainName;
        }
    }

}
