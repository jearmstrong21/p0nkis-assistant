package p0nki.p0nkisassistant.listeners;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import p0nki.espressolisp.exceptions.LispException;
import p0nki.espressolisp.object.literal.LispFunctionLiteral;
import p0nki.espressolisp.object.literal.LispNumberLiteral;
import p0nki.espressolisp.object.reference.LispReference;
import p0nki.espressolisp.object.reference.LispStandardReferenceImpl;
import p0nki.espressolisp.run.LispContext;
import p0nki.espressolisp.token.LispToken;
import p0nki.espressolisp.token.LispTokenizer;
import p0nki.espressolisp.tree.LispASTCreator;
import p0nki.espressolisp.tree.LispTreeNode;
import p0nki.p0nkisassistant.commands.LispCommands;
import p0nki.p0nkisassistant.commands.TrickCommands;
import p0nki.p0nkisassistant.data.TricksConfig;
import p0nki.p0nkisassistant.utils.CommandSource;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrickListener extends ListenerAdapter {

    public static final TrickListener INSTANCE = new TrickListener();

    private TrickListener() {

    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        String prefix = CommandListener.INSTANCE.getPrefix(new CommandSource(event.getMessage())) + "!";
        String content = event.getMessage().getContentRaw();
        if (content.startsWith(prefix)) {
            String afterPrefix = content.substring(prefix.length());
            String name = afterPrefix;
            String argStr = "";
            if (afterPrefix.contains(" ")) {
                int index = afterPrefix.indexOf(" ");
                name = afterPrefix.substring(0, index);
                argStr = afterPrefix.substring(index + 1);
            }
            Optional<TricksConfig.Trick> trickOptional = TrickCommands.getTrick(new CommandSource(event.getMessage()), name);
            if (trickOptional.isPresent()) {
                TricksConfig.Trick trick = trickOptional.get();
                String[] argArray = argStr.split(" ");
                if (trick.source.isLisp) {
//                    List<LispObject> args = new ArrayList<>();
//                    for (String s : argArray) {
//                        if (s.trim().equals("")) continue;
//                        try {
//                            args.add(new LispNumberLiteral(Double.parseDouble(s)));
//                        } catch (NumberFormatException e) {
//                            args.add(new LispStringLiteral(s));
//                        }
//                    }
                    try {
                        List<LispToken> tokens = LispTokenizer.tokenize(argStr);
                        List<LispTreeNode> args = new ArrayList<>();
                        for (int i = 0; i < 1000 && tokens.size() > 0; i++) {
                            args.add(LispASTCreator.parse(tokens));
                        }
                        if (tokens.size() > 0) {
                            throw new LispException("Too many tokens to parse", tokens.get(0));
                        }
                        LispContext ctx = LispCommands.createFresh();
                        ctx.set("ARG_COUNT", new LispReference("ARG_COUNT", false, new LispStandardReferenceImpl(new LispNumberLiteral(args.size()))));
                        LispCommands.evaluate(ctx, trick.source.code,
                                () -> {
                                    // on tokenize
                                },
                                () -> {
                                    // on ast
                                },
                                () -> event.getChannel().sendMessage("Timeout while evaluating trick").queue(),
                                e -> event.getChannel().sendMessage("Exception " + e.getMessage() + " at token " + e.getToken() + " while evaluating trick").queue(),
                                obj -> {
                                    try {
                                        obj = obj.fullyDereference();
                                        if (obj instanceof LispFunctionLiteral) {
                                            LispFunctionLiteral func = (LispFunctionLiteral) obj;
                                            if (func.getArgNames().size() != args.size()) {
                                                event.getChannel().sendMessage("Expected " + func.getArgNames().size() + " arguments, received " + args.size() + " arguments").queue();
                                            } else {
                                                for (int i = 0; i < func.getArgNames().size(); i++) {
                                                    ctx.set(func.getArgNames().get(i), new LispReference(func.getArgNames().get(i), false, new LispStandardReferenceImpl(args.get(i).evaluate(ctx))));
                                                }
                                                event.getChannel().sendMessage(func.getTreeRoot().evaluate(ctx).fullyDereference().lispStr()).queue();
                                            }
                                        } else {
                                            event.getChannel().sendMessage(obj.lispStr()).queue();
                                        }
                                    } catch (LispException e) {
                                        event.getChannel().sendMessage("Exception " + e.getMessage() + " at token " + e.getToken() + " while evaluating trick").queue();
                                    }
                                }, 2000);
                    } catch (LispException e) {
                        event.getChannel().sendMessage("Exception " + e.getMessage() + " at token " + e.getToken() + " while initializing context").queue();
                    }
                } else {
                    event.getChannel().sendMessage(trick.source.code).queue();
                }
            }
        }
//        if (content.startsWith(prefix + "!")) {
//            String name = content.substring(prefix.length() + 1, content.contains(" ") ? content.indexOf(" ") : content.length());
//            TricksConfig.Guild tricks = TricksConfig.get().guild(event.getGuild().getId());
//            if (tricks.has(name)) {
//                TricksConfig.Trick trick = tricks.trick(name);
//                String argsStr = content.substring(prefix.length() + 1 + name.length()).trim();
//                String[] args = argsStr.split(" ");
//                if (trick.isLisp()) {
//                    List<LispObject> arguments = new ArrayList<>();
//                    for (String s : args) {
//                        if (s.trim().equals("")) continue;
//                        try {
//                            arguments.add(new LispNumberLiteral(Double.parseDouble(s)));
//                        } catch (NumberFormatException e) {
//                            arguments.add(new LispStringLiteral(s));
//                        }
//                    }
//                    try {
//                        LispCommands.evaluate(LispCommands.createFresh(), trick.source, () -> {
//                        }, () -> {
//                        }, () -> event.getChannel().sendMessage("Timed out while evaluating trick").queue(), (e) -> event.getChannel().sendMessage("Exception " + e.getMessage() + " at token " + e.getToken()).queue(), obj -> {
//                            try {
//                                if (obj instanceof LispFunctionLiteral) {
//                                    LispFunctionLiteral func = (LispFunctionLiteral) obj;
//                                    LispContext ctx = LispCommands.createFresh();
//                                    if (func.getArgNames().size() != arguments.size()) {
//                                        event.getChannel().sendMessage("Expected " + func.getArgNames().size() + " arguments, received " + arguments.size() + " arguments").queue();
//                                    } else {
//                                        for (int i = 0; i < arguments.size(); i++) {
//                                            ctx.set(func.getArgNames().get(i), new LispReference(func.getArgNames().get(i), false, new LispStandardReferenceImpl(arguments.get(i))));
//                                        }
//                                        event.getChannel().sendMessage(func.getTreeRoot().evaluate(ctx).fullyDereference().lispStr()).queue();
//                                    }
//                                } else {
//                                    if (arguments.size() > 0) {
//                                        event.getChannel().sendMessage("Expected 0 arguments, received " + arguments.size() + " arguments").queue();
//                                    } else {
//                                        event.getChannel().sendMessage(obj.fullyDereference().lispStr()).queue();
//                                    }
//                                }
//                            } catch (LispException e) {
//                                event.getChannel().sendMessage("Exception: " + e.getMessage()).queue();
//                            }
//                        }, 2000);
//                    } catch (LispException e) {
//                        event.getChannel().sendMessage("Error creating context with message " + e.getMessage() + " and token " + e.getToken()).queue();
//                    }
//                } else {
//                    // TODO string format with `source`
//                    event.getChannel().sendMessage(trick.source).queue();
//                }
//            }
//        }
    }
}
