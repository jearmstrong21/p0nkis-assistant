package p0nki.assistant.cogs;

import net.dv8tion.jda.api.entities.Activity;
import p0nki.assistant.data.BotConfig;
import p0nki.assistant.lib.requirements.RequireOwner;
import p0nki.assistant.lib.utils.DiscordSource;
import p0nki.assistant.lib.utils.Holder;
import p0nki.easycommand.annotations.*;
import p0nki.easycommand.arguments.Parsers;

@CommandCog(name = "status", requirements = RequireOwner.class)
public class StatusCog implements Holder {

    @Command(literals = @Literal("status"), names = {"play", "p"})
    public void play(@Source DiscordSource source, @Argument(name = "value", modifiers = Parsers.GREEDY_STRING) String value) {
        jda().getPresence().setActivity(Activity.playing(value));
        source.send("Status updated");
    }

    @Command(literals = @Literal("status"), names = {"listen", "l"})
    public void listen(@Source DiscordSource source, @Argument(name = "value", modifiers = Parsers.GREEDY_STRING) String value) {
        jda().getPresence().setActivity(Activity.listening(value));
        source.send("Status updated");
    }

    @Command(literals = @Literal("status"), names = {"reset", "r"})
    public void reset(@Source DiscordSource source) {
        jda().getPresence().setActivity(BotConfig.VALUE.getActivity());
        source.send("Status reset");
    }

}
