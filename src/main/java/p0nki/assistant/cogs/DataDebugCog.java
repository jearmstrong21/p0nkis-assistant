package p0nki.assistant.cogs;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import p0nki.assistant.lib.requirements.RequireOwner;
import p0nki.assistant.lib.utils.DiscordSource;
import p0nki.assistant.lib.utils.DiscordUtils;
import p0nki.assistant.lib.utils.Holder;
import p0nki.easycommand.annotations.*;
import p0nki.easycommand.arguments.Parsers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@CommandCog(name = "data", requirements = RequireOwner.class)
public class DataDebugCog implements Holder {

    @Command(names = "guilds")
    public void guilds(@Source DiscordSource source) {
        List<Guild> guilds = new ArrayList<>(jda().getGuilds());
        guilds.sort(Comparator.comparing(Guild::getName));
        DiscordUtils.paginateList(source, guilds.size(), 0,
                index -> guilds.get(index).getName() + ": " +
                        guilds.get(index).getId());
    }

    @Command(names = "channels")
    public void channels(@Source DiscordSource source, @Argument(name = "guild") String guildId) {
        Guild guild = jda().getGuildById(guildId);
        if (guild != null) {
            List<GuildChannel> channels = new ArrayList<>(guild.getChannels());
            channels.sort(Comparator.comparing(GuildChannel::getPosition));
            DiscordUtils.paginateList(source, channels.size(), 0,
                    index -> channels.get(index).getType() + " " +
                            channels.get(index).getName() + ": " +
                            channels.get(index).getId());
        } else {
            source.send("No guild with ID");
        }
    }

    private void list(StringBuilder builder, String folder, String file, String indent, boolean printFull) {
        builder.append(indent).append(printFull ? (folder + "/") : "").append(file).append("\n");
        String[] sub = new File(folder + "/" + file).list();
        if (sub == null) return;
        for (String s : sub) {
            list(builder, folder + "/" + file, s, indent + "   ", printFull);
        }
    }

    @Command(literals = @Literal("data"), names = {"ls", "l"})
    public void ls(@Source DiscordSource source) {
        ls(source, false);
    }

    @Command(literals = @Literal("data"), names = {"ls", "l"})
    public void ls(@Source DiscordSource source, @Argument(name = "printFull") boolean printFull) {
        StringBuilder builder = new StringBuilder();
        list(builder, "data/.", ".", "", printFull);
        String res = builder.toString();
        if (res.length() > 2000 - 7) {
            source.channel().sendFile(res.getBytes(), "Data_ls.txt").queue();
        } else {
            source.send("```\n" + res + "```");
        }
    }

    @Command(literals = @Literal("data"), names = {"dump", "d"})
    public void dump(@Source DiscordSource source, @Argument(name = "path", modifiers = Parsers.GREEDY_STRING) String path) {
        try {
            String res = Files.readString(Path.of("data/" + path));
            if (res.length() > 2000 - 7) {
                source.channel().sendFile(res.getBytes(), "Data_dump.txt").queue();
            } else {
                source.send("```\n" + res + "```");
            }
        } catch (IOException ioException) {
            source.send("IOException: " + ioException.getMessage());
        }
    }

}
