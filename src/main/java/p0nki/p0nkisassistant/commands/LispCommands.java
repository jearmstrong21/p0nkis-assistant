package p0nki.p0nkisassistant.commands;

import net.dv8tion.jda.api.entities.User;
import p0nki.commandparser.argument.GreedyStringArgumentType;
import p0nki.commandparser.command.CommandDispatcher;
import p0nki.espressolisp.exceptions.LispException;
import p0nki.espressolisp.library.LispStandardLibrary;
import p0nki.espressolisp.object.LispObject;
import p0nki.espressolisp.run.LispContext;
import p0nki.espressolisp.token.LispToken;
import p0nki.espressolisp.token.LispTokenizer;
import p0nki.espressolisp.tree.LispASTCreator;
import p0nki.espressolisp.tree.LispTreeNode;
import p0nki.espressolisp.utils.LispLogger;
import p0nki.p0nkisassistant.utils.CommandResult;
import p0nki.p0nkisassistant.utils.CommandSource;
import p0nki.p0nkisassistant.utils.Constants;
import p0nki.p0nkisassistant.utils.Nodes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LispCommands {

    private static final Map<String, LispContext> contexts = new HashMap<>();

    private static LispContext create() throws LispException {
        LispContext ctx = new LispContext(new Logger(), null);
        ctx.potentialLibrary(LispStandardLibrary.INSTANCE);
        ctx.evaluate("(import 'std')");
        ctx.evaluate("(= println (. std 'println'))");
        ctx.evaluate("(= randf (. std 'randf'))");
        return ctx;
    }

    private static LispContext get(User user) throws LispException {
        if (!contexts.containsKey(user.getId())) contexts.put(user.getId(), create());
        return contexts.get(user.getId());
    }

    public static void register(CommandDispatcher<CommandSource, CommandResult> dispatcher) {
        dispatcher.register(Nodes.literal("lisp")
                .category("lisp")
                .then(Nodes.literal("reset")
                        .executes(context -> {
                            try {
                                contexts.put(context.source().user().getId(), new LispContext(new Logger(), null));
                                context.source().channel().sendMessage("Context reset").queue();
                                return CommandResult.SUCCESS;
                            } catch (LispException e) {
                                context.source().channel().sendMessage("Unable to reset context").queue();
                                return CommandResult.FAILURE;
                            }
                        })
                )
                .then(Nodes.literal("tokenize", "tokens", "token", "to")
                        .then(Nodes.greedyString("code")
                                .executes(context -> {
                                    String code = GreedyStringArgumentType.get(context, "code");
                                    List<LispToken> tokens = LispTokenizer.tokenize(code);
                                    String result = tokens.stream().map(LispToken::toString).collect(Collectors.joining("\n"));
                                    if (result.length() > Constants.MESSAGE_SIZE - 7) {
                                        context.source().channel().sendFile(result.getBytes(), "Tokens.txt").queue();
                                    } else {
                                        context.source().channel().sendMessage("```\n" + result + "```").queue();
                                    }
                                    return CommandResult.SUCCESS;
                                })
                        )
                )
                .then(Nodes.literal("tree", "tr")
                        .then(Nodes.greedyString("code")
                                .executes(context -> {
                                    String code = GreedyStringArgumentType.get(context, "code");
                                    List<LispToken> tokens = LispTokenizer.tokenize(code);
                                    try {
                                        StringBuilder result = new StringBuilder();
                                        for (int i = 0; i < 100 && tokens.size() > 0; i++) {
                                            LispTreeNode node = LispASTCreator.parse(tokens);
                                            result.append(node.toDebugJSON().toString(2));
                                        }
                                        if (tokens.size() > 0) {
                                            context.source().channel().sendMessage("Extra tokens after 100 node parses\n```\n" + tokens.get(0).toString() + "```").queue();
                                            return CommandResult.FAILURE;
                                        }
                                        if (result.length() > Constants.MESSAGE_SIZE - 7) {
                                            context.source().channel().sendFile(result.toString().getBytes(), "Tree.txt").queue();
                                        } else {
                                            context.source().channel().sendMessage("```\n" + result + "```").queue();
                                        }
                                        return CommandResult.SUCCESS;
                                    } catch (LispException e) {
                                        return CommandResult.FAILURE;
                                    }
                                })
                        )
                )
                .then(Nodes.literal("eval", "e")
                        .then(Nodes.greedyString("code")
                                .executes(context -> {
                                    String code = GreedyStringArgumentType.get(context, "code");
                                    List<LispToken> tokens = LispTokenizer.tokenize(code);
                                    try {
                                        LispContext lispCtx = get(context.source().user());
                                        StringBuilder result = new StringBuilder();
                                        ((Logger) lispCtx.getLogger()).setBuffer(result);
                                        for (int i = 0; i < 100 && tokens.size() > 0; i++) {
                                            LispTreeNode node = LispASTCreator.parse(tokens);
                                            LispObject obj = node.evaluate(lispCtx);
                                            result.append("=> ").append(obj.lispStr());
                                            while (obj.isLValue()) {
                                                obj = obj.get();
                                                result.append(" = ").append(obj.lispStr());
                                            }
                                            result.append("\n");
                                        }
                                        ((Logger) lispCtx.getLogger()).setBuffer(null);
                                        if (tokens.size() > 0) {
                                            context.source().channel().sendMessage("Extra tokens after 100 node parses\n```\n" + tokens.get(0).toString() + "```").queue();
                                            return CommandResult.FAILURE;
                                        }
                                        if (result.length() > Constants.MESSAGE_SIZE - 7) {
                                            context.source().channel().sendFile(result.toString().getBytes(), "Tree.txt").queue();
                                        } else {
                                            context.source().channel().sendMessage("```\n" + result + "```").queue();
                                        }
                                        return CommandResult.SUCCESS;
                                    } catch (LispException e) {
                                        if (e.getToken() != null) {
                                            context.source().channel().sendMessage("Token error\n```\n" + code + "\n" + e.getMessage() + "\n\n" + e.getToken().toString() + "```").queue();
                                        } else {
                                            context.source().channel().sendMessage("Runtime error\n```\n" + e.getMessage() + "```").queue();
                                        }
                                        return CommandResult.FAILURE;
                                    }
                                })
                        )
                )
                .then(Nodes.literal("keys", "k")
                        .executes(context -> {
                            try {
                                LispContext ctx = get(context.source().user());
                                StringBuilder builder = new StringBuilder();
                                ctx.keys().forEach(key -> {
                                    builder.append(key).append(" => ");
                                    LispObject obj = ctx.get(key);
                                    builder.append(obj.lispStr());
                                    while (obj.isLValue()) {
                                        obj = obj.get();
                                        builder.append(" = ").append(obj.lispStr());
                                    }
                                    builder.append("\n");
                                });
                                if (builder.length() > Constants.MESSAGE_SIZE - 7) {
                                    context.source().channel().sendFile(builder.toString().getBytes(), "Keys.txt").queue();
                                } else {
                                    context.source().channel().sendMessage("```\n" + builder + "```").queue();
                                }
                                return CommandResult.SUCCESS;
                            } catch (LispException e) {
                                context.source().channel().sendMessage("Unable to list context keys").queue();
                                return CommandResult.FAILURE;
                            }
                        })
                )
        );
    }

    private static class Logger implements LispLogger {

        private StringBuilder builder = null;

        public void setBuffer(StringBuilder builder) {
            this.builder = builder;
        }

        private void send(String streamName, CharSequence text) {
            builder.append(streamName).append(" => ").append(text).append("\n");
        }

        private StringBuilder format(LispObject object) {
            StringBuilder str = new StringBuilder(object.lispStr());
            while (object.isLValue()) {
                object = object.get();
                str.append(" = ").append(object.lispStr());
            }
            return str;
        }

        @Override
        public void out(LispObject object) {
            send("out", format(object));
        }

        @Override
        public void out(String message) {
            send("out", message);
        }

        @Override
        public void warn(LispObject object) {
            send("warn", format(object));
        }

        @Override
        public void warn(String message) {
            send("warn", message);
        }
    }

}
