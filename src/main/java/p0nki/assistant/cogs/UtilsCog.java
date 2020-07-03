package p0nki.assistant.cogs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import p0nki.assistant.lib.page.Paginator;
import p0nki.assistant.lib.utils.DiscordSource;
import p0nki.assistant.lib.utils.Holder;
import p0nki.easycommand.RealCommandCog;
import p0nki.easycommand.annotations.Argument;
import p0nki.easycommand.annotations.Command;
import p0nki.easycommand.annotations.CommandCog;
import p0nki.easycommand.annotations.Source;
import p0nki.easycommand.arguments.Parsers;

import java.util.stream.Collectors;

@CommandCog(name = "utils")
public class UtilsCog extends ListenerAdapter implements Holder {

    private static String longest(String[] array) {
        int bestIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i].length() > array[bestIndex].length()) bestIndex = i;
        }
        return array[bestIndex];
    }

    private EmbedBuilder firstPageHelp() {
        EmbedBuilder builder = new EmbedBuilder().setTitle("All cogs");
        dispatcher().getCogs().forEach(cog -> builder.getDescriptionBuilder().append("`").append(cog.getName()).append("`\n"));
        return builder;
    }

    private void startHelpPaginator(DiscordSource source, int page) {
        new Paginator(pageNumber -> {
            if (pageNumber == 0) {
                return firstPageHelp();
            } else {
                RealCommandCog cog = dispatcher().getCogs().get(pageNumber - 1);
                EmbedBuilder builder = new EmbedBuilder().setTitle("Cog " + pageNumber + "/" + dispatcher().getCogs().size()).setDescription("`" + cog.getName() + "` cog\n\n");
                // TODO add requirements
                cog.getCommands().forEach(realCommand -> builder.getDescriptionBuilder()
                        .append(realCommand.getLiterals().stream().map(UtilsCog::longest).collect(Collectors.joining(" ")))
                        .append(" ")
                        .append(longest(realCommand.getNames().toArray(new String[0])))
                        .append(" ")
                        .append(realCommand.getArguments().stream().map(argument -> {
                            String str = argument.getName();
                            if (argument.getModifiers().size() > 0) str += ":";
                            str += String.join(",", argument.getModifiers());
                            return "[" + str + "]";
                        }).collect(Collectors.joining(" ")))
                        .append("\n"));
                return builder;
            }
        }, dispatcher().getCogs().size() + 1, page).start(source);
    }

    @Command(names = "help")
    public void help(@Source DiscordSource source) {
        startHelpPaginator(source, 0);
    }

    @Command(names = "help")
    public void help(@Source DiscordSource source, @Argument(name = "cog", modifiers = Parsers.GREEDY_STRING) String cog) {
        for (int i = 0; i < dispatcher().getCogs().size(); i++) {
            if (dispatcher().getCogs().get(i).getName().equals(cog)) {
                startHelpPaginator(source, i + 1);
                // TODO: why is `!help utils` triggering both help commands? wtf
                // TODO: instead of dead eventlistener spam which doesn't know to remove itself, have a single eventlistener? is this a good idea? ask jda what the performance costs of a fuckton of event listeners are
                return;
            }
        }
        source.channel().sendMessage("No cog with that name.").embed(firstPageHelp().build()).queue();
    }

    @Command(names = "ping")
    public void ping(@Source DiscordSource source) {
        long start = System.currentTimeMillis();
        source.channel().sendMessage("Ping...").queue(message -> {
            long end = System.currentTimeMillis();
            jda().getRestPing().queue(restPing -> message.editMessage(String.format("Pong!\nGateway ping: %s\nRest ping: %s\nMessage send time: %s", jda().getGatewayPing(), restPing, end - start)).queue());
        });
    }

    @Command(names = "echo")
    public void echo(@Source DiscordSource source, @Argument(name = "value", modifiers = Parsers.GREEDY_STRING) String value) {
        source.send(value);
    }

}
