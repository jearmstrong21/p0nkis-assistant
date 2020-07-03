package p0nki.assistant.lib;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import p0nki.assistant.data.BotConfig;
import p0nki.assistant.lib.utils.CogInitializer;
import p0nki.assistant.lib.utils.DiscordParsers;
import p0nki.assistant.lib.utils.DiscordSource;
import p0nki.easycommand.CommandDispatcher;
import p0nki.easycommand.utils.Optional;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class EasyListener extends ListenerAdapter {

    public static EasyListener INSTANCE = new EasyListener();

    private final CommandDispatcher dispatcher;
    private final List<CogInitializer> cogInitializers = new ArrayList<>();
    private JDA jda;
    private String token;
    private Activity activity;

    private EasyListener() {
        dispatcher = new CommandDispatcher();
        dispatcher.addPrimitives();
        dispatcher.addParser(DiscordParsers.TEXT_CHANNEL);
        dispatcher.addParser(DiscordParsers.EMOTE);
        dispatcher.addParser(DiscordParsers.MEMBER);
        dispatcher.addParser(DiscordParsers.USER);
        dispatcher.addParser(DiscordParsers.ROLE);
        dispatcher.addParser(DiscordParsers.SNOWFLAKE);
        dispatcher.addParser(DiscordParsers.DURATION);
    }

    public EasyListener createJda() throws LoginException, InterruptedException {
        jda = JDABuilder.create(GatewayIntent.getIntents(GatewayIntent.DEFAULT))
                .setBulkDeleteSplittingEnabled(true)
                .setToken(token.trim())
                .setActivity(activity)
                .addEventListeners(this)
                .build()
                .awaitReady();
        return this;
    }

    public JDA getJda() {
        return jda;
    }

    public CommandDispatcher getDispatcher() {
        return dispatcher;
    }

    public EasyListener setToken(String token) {
        this.token = token;
        return this;
    }

    public EasyListener setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    // TODO: EasyListenerBuilder

    public EasyListener addCog(Class<?>... classes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        for (Class<?> clazz : classes) {
            addCog(clazz.getConstructor().newInstance());
        }
        return this;
    }

    public EasyListener addCog(Object... objects) {
        for (Object object : objects) {
            if (object instanceof CogInitializer) cogInitializers.add((CogInitializer) object);
            if (object instanceof EventListener) jda.addEventListener(object);
            dispatcher.createCog(object, object.getClass());
        }
        return this;
    }

    public EasyListener initializeCogs() {
        cogInitializers.forEach(CogInitializer::initialize);
        return this;
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        String msg = event.getMessage().getContentRaw();
        if (msg.startsWith(BotConfig.VALUE.getPrefix())) {
            DiscordSource source = new DiscordSource(event.getMessage());
            String command = msg.substring(BotConfig.VALUE.getPrefix().length());
            try {
                Optional<String> result = dispatcher.run(source, command);
                if (result.isPresent() && result.get().equals("No command found")) return;
                System.out.println("COMMAND " + command);
                if (result.isPresent())
                    source.send("Error running command.\n" + result.get());
            } catch (Throwable t) {
                source.send("Error running command.\n" + t.getMessage());
                System.out.println("COMMAND " + command);
                System.out.println("ERROR: " + t.getMessage());
                System.out.println();
                t.printStackTrace();
            }
        }
    }
}
