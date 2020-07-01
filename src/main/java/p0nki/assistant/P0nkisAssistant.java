package p0nki.assistant;

import p0nki.assistant.cogs.*;
import p0nki.assistant.data.BotConfig;
import p0nki.assistant.lib.EasyListener;
import p0nki.assistant.lib.utils.DiscordUtils;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;

public class P0nkisAssistant {

    public static void main(String[] args) throws IOException, LoginException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        EasyListener.INSTANCE
                .setToken(Files.readString(Path.of(DiscordUtils.resource(BotConfig.VALUE.getTokenFile()))))
                .createJda()
                .addCog(
                        UtilsCog.class,
                        InfoCog.class,
                        DebugCog.class,
                        CounterCog.class,
                        StarboardCog.class,
                        RolepollCog.class,
                        StatusCog.class,
                        EvalCog.class,
                        TrickCog.class,
                        NickCog.class,
                        LevelCog.class
                )
                .initializeCogs();
    }

}
