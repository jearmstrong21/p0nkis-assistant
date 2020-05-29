package p0nki.p0nkisassistant.commands;

import p0nki.commandparser.argument.GreedyStringArgumentType;
import p0nki.commandparser.command.CommandDispatcher;
import p0nki.p0nkisassistant.P0nkisAssistant;
import p0nki.p0nkisassistant.listeners.CommandListener;
import p0nki.p0nkisassistant.utils.CommandResult;
import p0nki.p0nkisassistant.utils.CommandSource;
import p0nki.p0nkisassistant.utils.Nodes;

import java.util.Optional;

public class HelpCommands {

    public static void register(CommandDispatcher<CommandSource, CommandResult> dispatcher) {
        dispatcher.register(Nodes.literal("help")
                .documentation("Lists categories")
                .category("misc")
                .then(Nodes.greedyString("category")
                        .executes(context -> {
                            String category = GreedyStringArgumentType.get(context, "category");
                            if (CommandListener.INSTANCE.getCategories().contains(category)) {
                                Optional<String> optional = category.equals("none") ? Optional.empty() : Optional.of(category);
                                context.source().channel().sendMessage("Help for category `" + category + "`:\n```\n" + CommandListener.INSTANCE.specificHelp(optional) + "```").queue();
                                return CommandResult.SUCCESS;
                            } else {
                                context.source().channel().sendMessage("No category with that name.").queue();
                                return CommandResult.FAILURE;
                            }
                        })
                )
                .executes(context -> {
                    context.source().channel().sendMessage("My prefix here is `" + CommandListener.INSTANCE.getPrefix(context.source()) + "`.\nInvite link: <" + P0nkisAssistant.jda.getInviteUrl() + ">" +
                            "\nCategories:\n```\n"
                            + CommandListener.INSTANCE.genericHelp() + "```").queue();
                    return CommandResult.SUCCESS;
                })
        );
    }


}
