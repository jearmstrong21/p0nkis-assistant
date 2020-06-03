package p0nki.p0nkisassistant.commands;

import net.dv8tion.jda.api.Permission;
import p0nki.commandparser.argument.GreedyStringArgumentType;
import p0nki.commandparser.argument.QuotedStringArgumentType;
import p0nki.commandparser.command.CommandDispatcher;
import p0nki.p0nkisassistant.data.TricksConfig;
import p0nki.p0nkisassistant.utils.*;

import java.util.Comparator;
import java.util.stream.Collectors;

public class TrickCommands {

    public static void register(CommandDispatcher<CommandSource, CommandResult> dispatcher) {
        dispatcher.register(Nodes.literal("trick", "t")
                .requires(Requirements.IN_GUILD)
                .category("trick")
                .documentation("Trick commands! Uses custom lisp interpreter, for more information run `lisp --help`")
                .then(Nodes.literal("--list", "-l")
                        .documentation("Lists tricks in the current guild")
                        .executes(context -> {
                            TricksConfig.Guild tricks = TricksConfig.get().guild(context.source().guild().getId());
                            String result = tricks.tricks().stream().sorted(Comparator.naturalOrder()).map(trick -> {
                                TricksConfig.Trick t = tricks.trick(trick);
                                return t.name() + ": " + (t.isLisp() ? "lisp" : "str") + ", owned by " + t.owner().getAsTag();
                            }).collect(Collectors.joining("\n"));
                            if (tricks.tricks().size() == 0) result = "No tricks.";
                            if (result.length() <= Constants.MESSAGE_SIZE - 7) {
                                context.source().channel().sendMessage("```\n" + result + "```").queue();
                            } else {
                                context.source().channel().sendFile(result.getBytes(), "Tricks.txt").queue();
                            }
                            return CommandResult.SUCCESS;
                        })
                )
                .then(Nodes.literal("--add", "-a")
                        .documentation("Adds a trick")
                        .then(Nodes.literal("lisp", "lsp", "l")
                                .then(Nodes.quotedString("name")
                                        .then(Nodes.greedyString("code")
                                                .executes(context -> {
                                                    TricksConfig config = TricksConfig.get();
                                                    TricksConfig.Guild tricks = config.guild(context.source().guild().getId());
                                                    String name = QuotedStringArgumentType.get(context, "name");
                                                    if (tricks.has(name)) {
                                                        context.source().channel().sendMessage("Trick with that name already exists").queue();
                                                        return CommandResult.FAILURE;
                                                    } else {
                                                        String code = GreedyStringArgumentType.get(context, "code");
                                                        tricks.set(new TricksConfig.Trick(name, context.source().user().getId(), true, code));
                                                        config.set();
                                                        context.source().channel().sendMessage("Trick added").queue();
                                                        return CommandResult.SUCCESS;
                                                    }
                                                })
                                        )
                                )
                        )
                        .then(Nodes.literal("str", "s")
                                .then(Nodes.quotedString("name")
                                        .then(Nodes.greedyString("code")
                                                .executes(context -> {
                                                    TricksConfig config = TricksConfig.get();
                                                    TricksConfig.Guild tricks = config.guild(context.source().guild().getId());
                                                    String name = QuotedStringArgumentType.get(context, "name");
                                                    if (tricks.has(name)) {
                                                        context.source().channel().sendMessage("Trick with that name already exists").queue();
                                                        return CommandResult.FAILURE;
                                                    } else {
                                                        String code = GreedyStringArgumentType.get(context, "code");
                                                        tricks.set(new TricksConfig.Trick(name, context.source().user().getId(), false, code));
                                                        config.set();
                                                        context.source().channel().sendMessage("Trick added").queue();
                                                        return CommandResult.SUCCESS;
                                                    }
                                                })
                                        )
                                )
                        )
                )
                .then(Nodes.literal("--update", "-u")
                        .documentation("Updates a preexisting trick")
                        .then(Nodes.quotedString("name")
                                .then(Nodes.greedyString("code")
                                        .executes(context -> {
                                            TricksConfig config = TricksConfig.get();
                                            TricksConfig.Guild tricks = config.guild(context.source().guild().getId());
                                            String name = QuotedStringArgumentType.get(context, "name");
                                            if (tricks.has(name)) {
                                                TricksConfig.Trick trick = tricks.trick(name);
                                                if (trick.owner().equals(context.source().user()) || context.source().member().hasPermission(Permission.MESSAGE_MANAGE)) {
                                                    trick.source = GreedyStringArgumentType.get(context, "code");
                                                    config.set();
                                                    context.source().channel().sendMessage("Updated trick").queue();
                                                    return CommandResult.SUCCESS;
                                                } else {
                                                    context.source().channel().sendMessage("You don't have permission to update this trick").queue();
                                                }
                                            } else {
                                                context.source().channel().sendMessage("No such trick").queue();
                                            }
                                            return CommandResult.FAILURE;
                                        })
                                )
                        )
                )
                .then(Nodes.literal("--remove", "-r")
                        .documentation("Removes a trick")
                        .then(Nodes.quotedString("name")
                                .executes(context -> {
                                    TricksConfig config = TricksConfig.get();
                                    TricksConfig.Guild tricks = config.guild(context.source().guild().getId());
                                    String name = QuotedStringArgumentType.get(context, "name");
                                    if (tricks.has(name)) {
                                        TricksConfig.Trick trick = tricks.trick(name);
                                        if (context.source().member().hasPermission(Permission.MESSAGE_MANAGE) || context.source().user().equals(trick.owner())) {
                                            tricks.remove(name);
                                            config.set();
                                            context.source().channel().sendMessage("Trick deleted").queue();
                                            return CommandResult.SUCCESS;
                                        } else {
                                            context.source().channel().sendMessage("You do not have MESSAGE_MANAGE or are owner").queue();
                                            return CommandResult.FAILURE;
                                        }
                                    } else {
                                        context.source().channel().sendMessage("No such trick").queue();
                                        return CommandResult.FAILURE;
                                    }
                                })
                        )
                )
                .then(Nodes.literal("--info", "-i")
                        .documentation("Returns info about a trick")
                        .then(Nodes.quotedString("name")
                                .executes(context -> {
                                    TricksConfig.Guild tricks = TricksConfig.get().guild(context.source().guild().getId());
                                    String name = QuotedStringArgumentType.get(context, "name");
                                    if (tricks.has(name)) {
                                        TricksConfig.Trick trick = tricks.trick(name);
                                        context.source().channel().sendMessage("Trick\nOwner: " + trick.owner().getAsTag() + "\nType: " + (trick.isLisp() ? "lisp" : "str")).queue();
                                        return CommandResult.SUCCESS;
                                    } else {
                                        context.source().channel().sendMessage("No such trick").queue();
                                        return CommandResult.FAILURE;
                                    }
                                })
                        )
                )
                .then(Nodes.literal("--source", "-s")
                        .documentation("Dumps source of a trick")
                        .then(Nodes.quotedString("name")
                                .executes(context -> {
                                    TricksConfig.Guild tricks = TricksConfig.get().guild(context.source().guild().getId());
                                    String name = QuotedStringArgumentType.get(context, "name");
                                    if (tricks.has(name)) {
                                        context.source().channel().sendMessage("```\n" + tricks.trick(name).source + "```").queue();
                                        return CommandResult.SUCCESS;
                                    } else {
                                        context.source().channel().sendMessage("No such trick").queue();
                                        return CommandResult.FAILURE;
                                    }
                                })
                        )
                )
        );
    }

}
