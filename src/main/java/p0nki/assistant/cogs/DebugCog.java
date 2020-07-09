package p0nki.assistant.cogs;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import p0nki.assistant.P0nkisAssistant;
import p0nki.assistant.lib.requirements.RequireOwner;
import p0nki.assistant.lib.utils.DiscordSource;
import p0nki.assistant.lib.utils.DiscordUtils;
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

    @Command(names = "runinfo")
    public void runinfo(@Source DiscordSource source) {
        StringBuilder propertyDump = new StringBuilder();
        System.getProperties().forEach((a, b) -> propertyDump.append(a).append(": ").append(b).append("\n"));
        Runtime rt = Runtime.getRuntime();
        StringBuilder result = new StringBuilder("```");
        result.append("\n----- MISC -----\n");
        result.append(String.format("Free memory     %s\n", DiscordUtils.formatMemory(rt.freeMemory())));
        result.append(String.format("Total memory    %s\n", DiscordUtils.formatMemory(rt.totalMemory())));
        result.append(String.format("Max memory      %s\n", DiscordUtils.formatMemory(rt.maxMemory())));
        result.append(String.format("Elapsed time    %s\n", DiscordUtils.formatTimeDifference(P0nkisAssistant.START_TIME, System.currentTimeMillis())));
        result.append("\n----- SYS  -----\n");
        result.append(String.format("Java version    %s\n", System.getProperty("java.version")));
        result.append(String.format("OS name         %s\n", System.getProperty("os.name")));
        result.append("\n```");
        source.channel().sendMessage(result).addFile(propertyDump.toString().getBytes(), "PropertyDump.txt").queue();
    }

}
