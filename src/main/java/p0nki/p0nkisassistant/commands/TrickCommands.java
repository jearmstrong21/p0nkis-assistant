package p0nki.p0nkisassistant.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import p0nki.commandparser.argument.GreedyStringArgumentType;
import p0nki.commandparser.argument.IntegerArgumentType;
import p0nki.commandparser.argument.QuotedStringArgumentType;
import p0nki.commandparser.command.CommandDispatcher;
import p0nki.p0nkisassistant.P0nkisAssistant;
import p0nki.p0nkisassistant.data.TricksConfig;
import p0nki.p0nkisassistant.utils.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class TrickCommands {

    private static final int PER_PAGE = 10;

    public static boolean canBeUsed(CommandSource source, TricksConfig.Trick trick) {
        if (trick.owner.isGlobal) return true;
        return trick.owner.guild.equals(source.guild().getId());
    }

    private static void serialize() {
        Utils.serialize("tricks", TricksConfig.CACHE, true);
    }

    public static Optional<TricksConfig.Trick> getTrick(CommandSource source, String name) {
        List<TricksConfig.Trick> possibilities = TricksConfig.CACHE.tricks.stream().filter(trick -> canBeUsed(source, trick) && trick.name.equals(name)).collect(Collectors.toList());
        if (possibilities.size() == 0) return Optional.empty();
        if (possibilities.size() > 1) {
            System.out.println("wtf");
            return Optional.empty();
        }
        return Optional.of(possibilities.get(0));
    }

    private static int totalPages(int trickCount) {
        int i = trickCount / PER_PAGE;
        if (trickCount % PER_PAGE > 0) i++;
        return i;
    }

    private static CommandResult paginateList(CommandSource source, int page) {
        List<TricksConfig.Trick> tricks = TricksConfig.CACHE.tricks.stream().filter(trick -> canBeUsed(source, trick)).collect(Collectors.toList());
        if (tricks.size() == 0) {
            source.channel().sendMessage("No tricks available").queue();
            return CommandResult.FAILURE;
        } else {
            page--;
            int startIndex = PER_PAGE * page;
            if (tricks.size() <= startIndex) {
                source.channel().sendMessage("No tricks for this page").queue();
                return CommandResult.FAILURE;
            } else {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(Constants.SUCCESS);
                embed.setTitle("List of tricks " + (page + 1) + "/" + totalPages(tricks.size()));
                for (int i = page * PER_PAGE; i < Math.min(page * PER_PAGE + PER_PAGE, tricks.size()); i++) {
                    embed.getDescriptionBuilder().append(i + 1).append(") ").append(tricks.get(i).name).append("\n");
                }
                source.channel().sendMessage(embed.build()).queue();
                return CommandResult.SUCCESS;
            }
        }
    }

    private static EmbedBuilder embed(TricksConfig.Trick trick) {
        return new EmbedBuilder()
                .setTitle(trick.name)
                .setColor(Constants.SUCCESS)
                .addField("Owner", Objects.requireNonNull(P0nkisAssistant.jda.getUserById(trick.owner.owner)).getAsMention(), false)
                .addField("Global", trick.owner.isGlobal ? "yes" : "no", false)
                .addField("Type", trick.source.isLisp ? "lisp" : "str", false)
                .addField("Created at", trick.created.toString(), false)
                .addField("Modified at", trick.modified.toString(), false);
    }

    public static void register(CommandDispatcher<CommandSource, CommandResult> dispatcher) {
        dispatcher.register(Nodes.literal("trick", "t")
                .requires(Requirements.IN_GUILD)
                .category("trick")
                .documentation("Trick commands! Uses custom lisp interpreter, for more information run `lisp --about`")
                .then(Nodes.literal("--list", "-l")
                        .documentation("Lists tricks in the current guild")
                        .then(Nodes.integer("page", 0)
                                .executes(context -> paginateList(context.source(), IntegerArgumentType.get(context, "page")))
                        )
                        .executes(context -> paginateList(context.source(), 1))
                )
                .then(Nodes.literal("--add", "-a")
                        .then(Nodes.literal("--type=lisp", "-t=lisp")
                                .then(Nodes.quotedString("name")
                                        .then(Nodes.greedyString("code")
                                                .executes(context -> {
                                                    String name = QuotedStringArgumentType.get(context, "name");
                                                    Optional<TricksConfig.Trick> trick = getTrick(context.source(), name);
                                                    if (trick.isPresent()) {
                                                        context.source().channel().sendMessage("Trick already exists")
                                                                .embed(embed(trick.get()).build())
                                                                .queue();
                                                        return CommandResult.FAILURE;
                                                    } else {
                                                        String code = GreedyStringArgumentType.get(context, "code");
                                                        TricksConfig.Trick newTrick = new TricksConfig.Trick(name,
                                                                new TricksConfig.Owner(context.source().user().getId(),
                                                                        context.source().guild().getId(), false),
                                                                new TricksConfig.Source(code, true),
                                                                new Date(), new Date());
                                                        TricksConfig.CACHE.tricks.add(newTrick);
                                                        serialize();
                                                        context.source().channel().sendMessage("Added trick")
                                                                .embed(embed(newTrick).build())
                                                                .queue();
                                                        return CommandResult.SUCCESS;
                                                    }
                                                })
                                        )
                                )
                        )
                        .then(Nodes.quotedString("name")
                                .then(Nodes.greedyString("code")
                                        .executes(context -> {
                                            String name = QuotedStringArgumentType.get(context, "name");
                                            Optional<TricksConfig.Trick> trick = getTrick(context.source(), name);
                                            if (trick.isPresent()) {
                                                context.source().channel().sendMessage("Trick already exists")
                                                        .embed(embed(trick.get()).build())
                                                        .queue();
                                                return CommandResult.FAILURE;
                                            } else {
                                                String code = GreedyStringArgumentType.get(context, "code");
                                                TricksConfig.Trick newTrick = new TricksConfig.Trick(name,
                                                        new TricksConfig.Owner(context.source().user().getId(),
                                                                context.source().guild().getId(), false),
                                                        new TricksConfig.Source(code, false),
                                                        new Date(), new Date());
                                                TricksConfig.CACHE.tricks.add(newTrick);
                                                serialize();
                                                context.source().channel().sendMessage("Added trick")
                                                        .embed(embed(newTrick).build())
                                                        .queue();
                                                return CommandResult.SUCCESS;
                                            }
                                        })
                                )
                        )
                )
                .then(Nodes.literal("--reload", "-R")
                        .requires(Requirements.IS_OWNER)
                        .documentation("Reloads trick cache")
                        .executes(context -> {
                            TricksConfig.CACHE = Utils.deserialize("tricks", TricksConfig.class);
                            context.source().channel().sendMessage("Reloaded cache").queue();
                            return CommandResult.SUCCESS;
                        })
                )
                .then(Nodes.literal("--info", "-i")
                        .then(Nodes.quotedString("name")
                                .executes(context -> {
                                    String name = QuotedStringArgumentType.get(context, "name");
                                    Optional<TricksConfig.Trick> trick = getTrick(context.source(), name);
                                    if (trick.isPresent()) {
                                        context.source().channel().sendMessage(embed(trick.get()).build()).queue();
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
                                    String name = QuotedStringArgumentType.get(context, "name");
                                    Optional<TricksConfig.Trick> trick = getTrick(context.source(), name);
                                    if (trick.isPresent()) {
                                        context.source().channel().sendMessage("```\n" + trick.get().source.code + "```").queue();
                                        return CommandResult.SUCCESS;
                                    } else {
                                        context.source().channel().sendMessage("No such trick").queue();
                                        return CommandResult.FAILURE;
                                    }
                                })
                        )
                )
                .then(Nodes.literal("--update", "-u")
                        .then(Nodes.quotedString("name")
                                .then(Nodes.greedyString("code")
                                        .executes(context -> {
                                            String name = QuotedStringArgumentType.get(context, "name");
                                            Optional<TricksConfig.Trick> trick = getTrick(context.source(), name);
                                            if (trick.isPresent()) {
                                                if (context.source().user().equals(P0nkisAssistant.P0NKI.get()) || context.source().user().getId().equals(trick.get().owner.owner)) {
                                                    trick.get().source.code = GreedyStringArgumentType.get(context, "code");
                                                    trick.get().modified = new Date();
                                                    serialize();
                                                    context.source().channel().sendMessage("Trick updated").queue();
                                                    return CommandResult.SUCCESS;
                                                } else {
                                                    context.source().channel().sendMessage("You cannot update this trick").queue();
                                                    return CommandResult.FAILURE;
                                                }
                                            } else {
                                                context.source().channel().sendMessage("No such trick").queue();
                                                return CommandResult.FAILURE;
                                            }
                                        })
                                )
                        )
                )
                .then(Nodes.literal("--global", "-g")
                        .requires(Requirements.IS_OWNER)
                        .then(Nodes.quotedString("name")
                                .executes(context -> {
                                    String name = QuotedStringArgumentType.get(context, "name");
                                    Optional<TricksConfig.Trick> trick = getTrick(context.source(), name);
                                    if (trick.isPresent()) {
                                        trick.get().owner.isGlobal = !trick.get().owner.isGlobal;
                                        trick.get().modified = new Date();
                                        serialize();
                                        context.source().channel().sendMessage("Trick updated").queue();
                                        return CommandResult.SUCCESS;
                                    } else {
                                        context.source().channel().sendMessage("No such trick").queue();
                                        return CommandResult.FAILURE;
                                    }
                                })
                        )
                )
                .then(Nodes.literal("--remove", "-r")
                        .then(Nodes.quotedString("name")
                                .executes(context -> {
                                    String name = QuotedStringArgumentType.get(context, "name");
                                    Optional<TricksConfig.Trick> trick = getTrick(context.source(), name);
                                    if (trick.isPresent()) {
                                        if (trick.get().owner.isGlobal && !P0nkisAssistant.P0NKI.get().getId().equals(trick.get().owner.owner)) {
                                            context.source().channel().sendMessage("Cannot remove global trick").queue();
                                            return CommandResult.FAILURE;
                                        } else {
                                            if (context.source().member().hasPermission(Permission.MESSAGE_MANAGE) || context.source().user().getId().equals(trick.get().owner.owner) || context.source().user().equals(P0nkisAssistant.P0NKI.get())) {
                                                TricksConfig.CACHE.tricks.remove(trick.get());
                                                serialize();
                                                context.source().channel().sendMessage("Trick removed").queue();
                                                return CommandResult.SUCCESS;
                                            } else {
                                                context.source().channel().sendMessage("You cannot remove this trick").queue();
                                                return CommandResult.FAILURE;
                                            }
                                        }
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
