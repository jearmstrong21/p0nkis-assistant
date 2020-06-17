package p0nki.easycommandtestbot;

import p0nki.easycommandtestbot.cogs.*;
import p0nki.easycommandtestbot.data.BotConfig;
import p0nki.easycommandtestbot.data.StarboardData;
import p0nki.easycommandtestbot.lib.EasyListener;
import p0nki.easycommandtestbot.lib.utils.DiscordUtils;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;

public class EasyCommandTestBot {

    public static void main(String[] args) throws IOException, LoginException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        StarboardData.CACHE.getKeys().forEach(key -> StarboardData.CACHE.of(key).print());
        EasyListener.INSTANCE
                .setToken(Files.readString(Path.of(DiscordUtils.resource(BotConfig.VALUE.getTokenFile()))))
                .setActivity(BotConfig.VALUE.getActivity())
                .createJda()
                .setPrefix(BotConfig.VALUE.getPrefix())
                .setOwner(BotConfig.VALUE.getOwner())
                .addCog(
                        UtilsCog.class,
                        InfoCog.class,
                        DebugCog.class,
                        CounterCog.class,
                        ReminderCog.class,
                        StarboardCog.class,
                        RolepollCog.class
                )
                .initializeCogs();
    }

}
