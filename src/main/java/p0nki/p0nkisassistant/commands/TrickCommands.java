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
                .then(Nodes.literal("--list", "-l")
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
                .then(Nodes.literal("--remove", "-r")
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
