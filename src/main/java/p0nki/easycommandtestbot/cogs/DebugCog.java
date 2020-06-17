package p0nki.easycommandtestbot.cogs;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import p0nki.easycommand.annotations.Command;
import p0nki.easycommand.annotations.CommandCog;
import p0nki.easycommand.annotations.Source;
import p0nki.easycommandtestbot.lib.DiscordSource;
import p0nki.easycommandtestbot.lib.Holder;
import p0nki.easycommandtestbot.lib.requirements.RequireOwner;

@CommandCog(name = "debug", requirements = RequireOwner.class)
public class DebugCog extends ListenerAdapter implements Holder {


    @Command(names = "whomst", requirements = RequireOwner.class)
    public void whomst(@Source DiscordSource source) {
        source.send("Tester bot\nJava version: " + System.getProperty("java.version") + "\nOS name: " + System.getProperty("os.name"));
    }

    @Command(names = "listeners", requirements = RequireOwner.class)
    public void listeners(@Source DiscordSource source) {
        StringBuilder str = new StringBuilder(jda().getRegisteredListeners().size() + " listeners\n");
        for (int i = 0; i < jda().getRegisteredListeners().size(); i++) {
            str.append(jda().getRegisteredListeners().get(i).toString()).append(": ").append(jda().getRegisteredListeners().get(i).hashCode()).append("\n");
        }
        source.send(str);
    }

}
