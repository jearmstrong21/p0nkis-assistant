package p0nki.p0nkisassistant.commands;

import p0nki.commandparser.argument.GreedyStringArgumentType;
import p0nki.commandparser.command.CommandDispatcher;
import p0nki.p0nkisassistant.utils.CommandResult;
import p0nki.p0nkisassistant.utils.CommandSource;
import p0nki.p0nkisassistant.utils.Nodes;
import p0nki.simpleclojure.CLJContext;
import p0nki.simpleclojure.exceptions.ClojureException;
import p0nki.simpleclojure.objects.CLJObject;

import java.util.HashMap;
import java.util.Map;

public class ClojureCommands {

    private final static Map<String, CLJContext> userContexts = new HashMap<>();

    public static void register(CommandDispatcher<CommandSource, CommandResult> dispatcher) {
        dispatcher.register(Nodes.literal("clojure", "clj")
                .documentation("User-specific limited Clojure REPL. Not persistent between arbitrary bot restarts")
                .category("clojure")
                .then(Nodes.literal("reset")
                        .documentation("Resets the REPL of the calling user")
                        .executes(context -> {
                            if (userContexts.containsKey(context.source().user().getId())) {
                                context.source().channel().sendMessage("Re-initialized context").queue();
                            } else {
                                context.source().channel().sendMessage("No context initialized. Created new one.").queue();
                            }
                            userContexts.put(context.source().user().getId(), new CLJContext());
                            return CommandResult.SUCCESS;
                        })
                )
                .then(Nodes.greedyString("code")
                        .documentation("Execute code in your REPL")
                        .executes(context -> {
                            String result = "";
                            if (!userContexts.containsKey(context.source().user().getId())) {
                                userContexts.put(context.source().user().getId(), new CLJContext());
                                result += "No context existed. New one created.\n";
                            }
                            long start, end;
                            CLJObject obj;
                            String code = GreedyStringArgumentType.get(context, "code");
                            CLJContext cljCtx = userContexts.get(context.source().user().getId());
                            start = System.currentTimeMillis();
                            try {
                                obj = cljCtx.read(code);
                                end = System.currentTimeMillis();
                                long time = end - start;
                                result += "Completed in " + time + "ms.\n\n-------\n" + obj.getType() + "\n" + obj.debugString() + "";
                                context.source().channel().sendMessage(result).queue();
                                return CommandResult.SUCCESS;
                            } catch (ClojureException | IndexOutOfBoundsException e) {
                                end = System.currentTimeMillis();
                                long time = end - start;
                                result += "Completed in " + time + "ms.\n\n-------\nFAILURE\n" + e.getMessage();
                                context.source().channel().sendMessage(result).queue();
                                return CommandResult.FAILURE;
                            }
                        })
                )
        );
    }

}
