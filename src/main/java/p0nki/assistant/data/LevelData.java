package p0nki.assistant.data;

import net.dv8tion.jda.api.entities.User;
import p0nki.assistant.lib.data.PerGuildDataCache;
import p0nki.assistant.lib.data.ReadWriteData;

import javax.annotation.CheckReturnValue;
import java.util.HashMap;
import java.util.Map;

public class LevelData extends ReadWriteData {

    // DELAY = 1 minute
    // between 15 and 25 xp

    public static final PerGuildDataCache<LevelData> CACHE = new PerGuildDataCache<>("level", LevelData::new);
    private final Map<String, Integer> xp = new HashMap<>();
    private final Map<String, Integer> levels = new HashMap<>();
    private final Map<String, Long> lastXpTime = new HashMap<>();
    private boolean enabled = true;
    private String levelUpMessage = "GG {MENTION}. You levelled up to {LEVEL}. Do `pa!rank` to see your progress. Chat on!";

    private LevelData(String dir) {
        super(dir);
    }

    public static int getXpForLevel(int level) {
        return 5 * level * level + 50 * level + 100;
    }

    private static int randomIncrease() {
        return 15 + (int) (10 * Math.random());
    }

    public String getLevelUpMessage() {
        return levelUpMessage;
    }

    public void setLevelUpMessage(String levelUpMessage) {
        this.levelUpMessage = levelUpMessage;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        write();
    }

    public int getXp(User user) {
        return xp.getOrDefault(user.getId(), 0);
    }

    public int getLevel(User user) {
        return levels.getOrDefault(user.getId(), 0);
    }

    public long getLastXpTime(User user) {
        return lastXpTime.getOrDefault(user.getId(), -1L);
    }

    @CheckReturnValue
    public boolean gainXp(User user) {
        xp.put(user.getId(), getXp(user) + randomIncrease());
        lastXpTime.put(user.getId(), System.currentTimeMillis());
        int level = getLevel(user);
        int xpForLevel = getXpForLevel(level);
        if (xp.get(user.getId()) > xpForLevel) {
            xp.put(user.getId(), xp.get(user.getId()) - xpForLevel);
            levels.put(user.getId(), level + 1);
            write();
            return true;
        }
        write();
        return false;
    }

}
