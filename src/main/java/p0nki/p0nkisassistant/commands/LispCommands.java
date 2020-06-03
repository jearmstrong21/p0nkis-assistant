package p0nki.p0nkisassistant.commands;

import net.dv8tion.jda.api.entities.Message;
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
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class LispCommands {

    private static final Map<String, LispContext> contexts = new HashMap<>();

    public static LispContext createFresh() throws LispException {
        LispContext ctx = new LispContext(new Logger(), null);
        ctx.potentialLibrary(LispStandardLibrary.INSTANCE);
        ctx.evaluate("(import 'std')");
        ctx.evaluate("(= println (. std 'println'))");
        ctx.evaluate("(= randf (. std 'randf'))");
        return ctx;
    }

    private static LispContext get(User user) throws LispException {
        if (!contexts.containsKey(user.getId())) contexts.put(user.getId(), createFresh());
        return contexts.get(user.getId());
    }

    public static void evaluate(LispContext ctx, String code, Runnable onTokenize, Runnable onAST, Runnable onTimeout, Consumer<LispException> onException, Consumer<LispObject> onFinish, long timeout) {
        List<LispToken> tokens = LispTokenizer.tokenize(code);
        onTokenize.run();
        try {
            LispTreeNode node = LispASTCreator.parse(tokens);
            if (tokens.size() > 0)
                throw new LispException("Extra tokens found (limitation of bot)", tokens.get(0));
            onAST.run();
            ExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            Future<Void> future = executor.submit(() -> {
                onFinish.accept(node.evaluate(ctx));
                return null;
            });
            try {
                future.get(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                onTimeout.run();
            } finally {
                executor.shutdown();
            }
        } catch (LispException e) {
            onException.accept(e);
        }
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
                                                LispTreeNode node = LispASTCreator.parse(tokens);
                                                result.append(node.toDebugJSON().toString(2));
                                                if (tokens.size() > 0) {
                                                    context.source().channel().sendMessage("Extra tokens:\n```\n" + tokens.get(0).toString() + "```").queue();
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
                        .then(Nodes.literal("eval", "e")
                                .then(Nodes.greedyString("code")
                                        .executes(context -> {
                                            String code = GreedyStringArgumentType.get(context, "code");
                                            LispContext ctx;
                                            try {
                                                ctx = get(context.source().user());
                                            } catch (LispException e) {
                                                context.source().channel().sendMessage("Failed to obtain context. Message: " + e.getMessage()).queue();
                                                return CommandResult.FAILURE;
                                            }
                                            Message msg = context.source().channel().sendMessage("Evaluating ...").complete();
                                            StringBuilder str = new StringBuilder("Evaluating...");
                                            evaluate(ctx, code,
                                                    () -> {
                                                    },
                                                    () -> msg.editMessage(str.append("\nTokenized and created AST")).queue(),
                                                    () -> msg.editMessage(str.append("\nTimed out")).queue(),
                                                    (e) -> {
                                                        if (e.getToken() == null) {
                                                            msg.editMessage(str.append("\nRuntime exception\n```\n").append(e.getMessage()).append("```")).queue();
                                                        } else {
                                                            msg.editMessage(str.append("\nParse exception\n```\n").append(e.getMessage()).append("```")).queue();
                                                        }
                                                    },
                                                    (o) -> {
                                                        str.append("\nObject:\n```=> ").append(o.lispStr());
                                                        while (o.isLValue()) {
                                                            o = o.get();
                                                            str.append(" = ").append(o.lispStr());
                                                        }
                                                        msg.editMessage(str.append("```")).queue();
                                                    },
                                                    2000);
                                            return CommandResult.IGNORE;
                                        })
                                )
                        )
//                                    Message msg = context.source().channel().sendMessage("Evaluating...").complete();
//                                    List<LispToken> tokens = LispTokenizer.tokenize(code);
//                                    try {
//                                        LispTreeNode node = LispASTCreator.parse(tokens);
//                                        if (tokens.size() > 0) {
//                                            context.source().channel().sendMessage("Extra tokens:\n```\n" + tokens.get(0).toString() + "```").queue();
//                                            return CommandResult.FAILURE;
//                                        }
//                                        msg.editMessage(msg.getContentRaw() + "\nTokenized and AST created").queue();
//                                        StringBuilder result = new StringBuilder();
//                                        ExecutorService executor = Executors.newSingleThreadScheduledExecutor();
//                                        LispContext lispCtx = get(context.source().user());
//                                        ((Logger) lispCtx.getLogger()).setBuffer(result);
//                                        Future<Void> future = executor.submit(() -> {
//                                            LispObject obj = node.evaluate(lispCtx);
//                                            result.append("=> ").append(obj.lispStr());
//                                            while (obj.isLValue()) {
//                                                obj = obj.get();
//                                                result.append(" = ").append(obj.lispStr());
//                                            }
//                                            result.append("\n");
//                                            return null;
//                                        });
//                                        try {
//                                            future.get(2, TimeUnit.SECONDS);
//                                        } catch (InterruptedException | ExecutionException | TimeoutException e) {
//                                            msg.editMessage(msg.getContentRaw() + "\nTimed out").queue();
//                                            return CommandResult.FAILURE;
//                                        } finally {
//                                            executor.shutdown();
//                                        }
//                                        ((Logger) lispCtx.getLogger()).setBuffer(null);
//                                        if (tokens.size() > 0) {
//                                            context.source().channel().sendMessage("Extra tokens after 100 node parses\n```\n" + tokens.get(0).toString() + "```").queue();
//                                            return CommandResult.FAILURE;
//                                        }
//                                        if (result.length() > Constants.MESSAGE_SIZE - 7) {
//                                            msg.delete().queue();
//                                            context.source().channel().sendFile(result.toString().getBytes(), "Tree.txt").queue();
//                                        } else {
//                                            msg.editMessage("```\n" + result + "```").queue();
//                                        }
//                                        return CommandResult.SUCCESS;
//                                    } catch (LispException e) {
//                                        if (e.getToken() != null) {
//                                            msg.editMessage(msg.getContentRaw() + "\nToken error\n```\n" + code + "\n" + e.getMessage() + "\n\n" + e.getToken().toString() + "```").queue();
//                                        } else {
//                                            msg.editMessage(msg.getContentRaw() + "\nRuntime error\n```\n" + e.getMessage() + "```").queue();
//                                        }
//                                        return CommandResult.FAILURE;
//                                    }
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

    public static class Logger implements LispLogger {

        @Override
        public void out(LispObject object) {
        }

        @Override
        public void out(String message) {
        }

        @Override
        public void warn(LispObject object) {
        }

        @Override
        public void warn(String message) {
        }
    }

}
