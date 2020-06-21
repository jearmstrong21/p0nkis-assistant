package p0nki.assistant.cogs;

import net.dv8tion.jda.api.entities.Activity;
import p0nki.easycommand.annotations.*;
import p0nki.easycommand.arguments.Parsers;
import p0nki.assistant.lib.requirements.RequireOwner;
import p0nki.assistant.lib.utils.DiscordSource;
import p0nki.assistant.lib.utils.Holder;

@CommandCog(name = "status", requirements = RequireOwner.class)
public class StatusCog implements Holder {

    @Command(literals = @Literal("status"), names = "play")
    public void play(@Source DiscordSource source, @Argument(name = "value", modifiers = Parsers.GREEDY_STRING) String value) {
        jda().getPresence().setActivity(Activity.playing(value));
        source.send("Status set");
    }

    @Command(literals = @Literal("status"), names = "listen")
    public void listen(@Source DiscordSource source, @Argument(name = "value", modifiers = Parsers.GREEDY_STRING) String value) {
        jda().getPresence().setActivity(Activity.listening(value));
        source.send("Status set");
    }

}
