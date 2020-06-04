package p0nki.p0nkisassistant;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.User;
import p0nki.p0nkisassistant.data.BotConfig;
import p0nki.p0nkisassistant.listeners.*;
import p0nki.p0nkisassistant.utils.Lazy;
import p0nki.p0nkisassistant.utils.Utils;
import p0nki.p0nkisassistant.utils.Webhook;

public class P0nkisAssistant {

    public static JDA jda;

    public static Lazy<User> P0NKI = Lazy.fromSupplier(() -> jda.getUserById(BotConfig.CACHE.ownerID));
    public static Lazy<Emote> EMOTE_PINGSOCK = Lazy.fromSupplier(() -> jda.getEmoteById(BotConfig.CACHE.pingsockEmoteID));

    public static void main(String[] args) {
        try {
            Webhook.initializeClients();

            jda = new JDABuilder()
                    .setToken(Utils.load("token.txt"))
                    .setBulkDeleteSplittingEnabled(true)
                    .setRawEventsEnabled(true)
                    .build();

            jda.addEventListener(CommandListener.INSTANCE);
            jda.addEventListener(FunListener.INSTANCE);
            jda.addEventListener(GeneralEventListener.INSTANCE);
            jda.addEventListener(ReactionListener.INSTANCE);
            jda.addEventListener(AdminCommandListener.INSTANCE);
//            jda.addEventListener(LoggerListener.INSTANCE);
            jda.addEventListener(TrickListener.INSTANCE);
            jda.addEventListener(RolepollListener.INSTANCE);
            jda.addEventListener(StarboardListener.INSTANCE);
            jda.awaitReady();
            jda.getPresence().setActivity(Activity.listening("pings"));
            jda.getPresence().setStatus(OnlineStatus.ONLINE);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
