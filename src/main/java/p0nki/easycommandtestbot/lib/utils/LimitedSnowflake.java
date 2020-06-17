package p0nki.easycommandtestbot.lib.utils;

import net.dv8tion.jda.api.entities.ISnowflake;

public class LimitedSnowflake implements ISnowflake {

    private final long id;

    public LimitedSnowflake(long id) {
        this.id = id;
    }

    @Override
    public long getIdLong() {
        return id;
    }
}
