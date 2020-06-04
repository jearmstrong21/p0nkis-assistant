package p0nki.p0nkisassistant.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import p0nki.commandparser.argument.GreedyStringArgumentType;
import p0nki.commandparser.command.CommandDispatcher;
import p0nki.espressolisp.adapter.LispMonadAdapter;
import p0nki.espressolisp.exceptions.LispException;
import p0nki.espressolisp.library.LispStandardLibrary;
import p0nki.espressolisp.object.LispObject;
import p0nki.espressolisp.object.literal.LispCompleteFunctionLiteral;
import p0nki.espressolisp.object.literal.LispStringLiteral;
import p0nki.espressolisp.object.reference.LispReference;
import p0nki.espressolisp.object.reference.LispStandardReferenceImpl;
import p0nki.espressolisp.run.LispContext;
import p0nki.espressolisp.token.LispToken;
import p0nki.espressolisp.token.LispTokenizer;
import p0nki.espressolisp.tree.LispASTCreator;
import p0nki.espressolisp.tree.LispTreeNode;
import p0nki.espressolisp.utils.LispLogger;
import p0nki.p0nkisassistant.utils.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
        //TODO put url_encode into LISP STANDARD LIBRARY
        //TODO make TrickListener parse node list and evaluate those instead of hardcoding specific parses to allow for strings and stuff with spaces
        ctx.set("url_encode", new LispReference("url_encode", true, new LispStandardReferenceImpl(new LispCompleteFunctionLiteral(List.of("arg1"), (LispMonadAdapter) (context, arg1) -> new LispStringLiteral(URLEncoder.encode(arg1.fullyDereference().asString().getValue(), StandardCharsets.UTF_8))))));
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
                if (e.getCause() instanceof LispException) onException.accept((LispException) e.getCause());
                e.printStackTrace();
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
                .documentation("EspressoLisp(tm) commands! Run `lisp --about` for more information")
                .then(Nodes.literal("--about", "-a")
                        .executes(context -> {
                            context.source().channel().sendMessage("Under construction").queue();
                            return CommandResult.SUCCESS;
                            // TODO fully doc language, link to public google doc in --about command?
                            // TODO number() cast just like str() cast in builtin
                            // TODO Math library in 2.1 espressolisp release along with whitespace fix + whitespace escapes in strings?
                        })
                )
                .then(Nodes.literal("--reset", "-r")
                        .documentation("Resets your REPL")
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
                .then(Nodes.literal("--tokens", "-to")
                        .documentation("Dumps tokenization of code")
                        .then(Nodes.greedyString("code")
                                .executes(context -> {
                                    String code = GreedyStringArgumentType.get(context, "code");
                                    List<LispToken> tokens = LispTokenizer.tokenize(code);
                                    String result = tokens.stream().map(LispToken::toString).collect(Collectors.joining("\n"));
                                    if (result.length() > Constants.MESSAGE_SIZE - 7) {
                                        context.source().channel().sendFile(result.getBytes(), "Tokens.txt").queue();
                                    } else {
                                        context.source().channel().sendMessage(Utils.censorPings(context.source(), "```\n" + result + "```")).queue();
                                    }
                                    return CommandResult.SUCCESS;
                                })
                        )
                )
                .then(Nodes.literal("--tree", "-tr")
                        .documentation("Dumps and verifies correctness of AST, but does not evaluate")
                        .then(Nodes.greedyString("code")
                                .executes(context -> {
                                    String code = GreedyStringArgumentType.get(context, "code");
                                    List<LispToken> tokens = LispTokenizer.tokenize(code);
                                    try {
                                        StringBuilder result = new StringBuilder();
                                        LispTreeNode node = LispASTCreator.parse(tokens);
                                        result.append(node.toDebugJSON().toString(2));
                                        if (tokens.size() > 0) {
                                            context.source().channel().sendMessage(Utils.censorPings(context.source(), "Extra tokens:\n```\n" + tokens.get(0).toString() + "```")).queue();
                                            return CommandResult.FAILURE;
                                        }
                                        if (result.length() > Constants.MESSAGE_SIZE - 7) {
                                            context.source().channel().sendFile(result.toString().getBytes(), "Tree.txt").queue();
                                        } else {
                                            context.source().channel().sendMessage(Utils.censorPings(context.source(), "```\n" + result + "```")).queue();
                                        }
                                        return CommandResult.SUCCESS;
                                    } catch (LispException e) {
                                        if (e.getToken() != null) {
                                            context.source().channel().sendMessage(Utils.censorPings(context.source(), "Token error\n```\n" + code + "\n" + e.getMessage() + "\n\n" + e.getToken().toString() + "```")).queue();
                                        } else {
                                            context.source().channel().sendMessage(Utils.censorPings(context.source(), "Runtime error\n```\n" + e.getMessage() + "```")).queue();
                                        }
                                        return CommandResult.FAILURE;
                                    }
                                })
                        )
                )
                .then(Nodes.literal("--eval", "-e")
                        .documentation("Evaluates AST of code")
                        .then(Nodes.greedyString("code")
                                .executes(context -> {
                                    String code = GreedyStringArgumentType.get(context, "code");
                                    LispContext ctx;
                                    try {
                                        ctx = get(context.source().user());
                                    } catch (LispException e) {
                                        context.source().channel().sendMessage(Utils.censorPings(context.source(), "Failed to obtain context. Message: " + e.getMessage())).queue();
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
                                                    msg.editMessage(Utils.censorPings(context.source(), str.append("\nRuntime exception\n```\n").append(e.getMessage()).append("```").toString())).queue();
                                                } else {
                                                    msg.editMessage(Utils.censorPings(context.source(), str.append("\nParse exception\n```\n").append(e.getMessage()).append("```").toString())).queue();
                                                }
                                            },
                                            (o) -> {
                                                str.append("\nObject:\n```=> ").append(o.lispStr());
                                                while (o.isLValue()) {
                                                    o = o.get();
                                                    str.append(" = ").append(o.lispStr());
                                                }
                                                msg.editMessage(Utils.censorPings(context.source(), str.append("```").toString())).queue();
                                            },
                                            2000);
                                    return CommandResult.IGNORE;
                                })
                        )
                )
                .then(Nodes.literal("--keys", "-k")
                        .documentation("Dumps all keys in your REPL")
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
                                    context.source().channel().sendMessage(Utils.censorPings(context.source(), "```\n" + builder + "```")).queue();
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
