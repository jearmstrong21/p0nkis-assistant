package p0nki.assistant.cogs;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import p0nki.assistant.lib.requirements.RequireOwner;
import p0nki.assistant.lib.utils.DiscordSource;
import p0nki.assistant.lib.utils.Holder;
import p0nki.easycommand.annotations.Command;
import p0nki.easycommand.annotations.CommandCog;
import p0nki.easycommand.annotations.Source;

@CommandCog(name = "debug", requirements = RequireOwner.class)
public class DebugCog extends ListenerAdapter implements Holder {

    @Command(names = "whomst")
    public void whomst(@Source DiscordSource source) {
        source.send("P0nki's Bot\nJava version: " + System.getProperty("java.version") + "\nOS name: " + System.getProperty("os.name"));
    }

    @Command(names = "listeners")
    public void listeners(@Source DiscordSource source) {
        StringBuilder str = new StringBuilder(jda().getRegisteredListeners().size() + " listeners\n");
        for (int i = 0; i < jda().getRegisteredListeners().size(); i++) {
            str.append(jda().getRegisteredListeners().get(i).toString()).append(": ").append(jda().getRegisteredListeners().get(i).hashCode()).append("\n");
        }
        source.send(str);
    }

}
