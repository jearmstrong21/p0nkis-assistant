package p0nki.assistant.cogs;

import p0nki.assistant.JSEvaluator;
import p0nki.assistant.lib.utils.DiscordSource;
import p0nki.assistant.lib.utils.Holder;
import p0nki.easycommand.annotations.*;
import p0nki.easycommand.arguments.Parsers;
import p0nki.javashit.builtins.Builtins;
import p0nki.javashit.run.JSContext;

import java.util.HashMap;
import java.util.Map;

@CommandCog(name = "eval")
public class EvalCog implements Holder {

    private final Map<String, JSContext> contextMap = new HashMap<>();

    private JSContext get(DiscordSource source) {
        if (!contextMap.containsKey(source.user().getId())) {
            JSContext context = new JSContext(null, new HashMap<>());
            context.set("println", Builtins.PRINTLN);
            context.set("dir", Builtins.DIR);
            context.set("Math", Builtins.MATH);
            context.set("Data", Builtins.DATA);
            context.set("System", Builtins.SYSTEM);
            contextMap.put(source.user().getId(), context);
        }
        return contextMap.get(source.user().getId());
    }

    @Command(literals = @Literal({"javascript", "js"}), names = "eval")
    public void eval(@Source DiscordSource source, @Argument(name = "code", modifiers = Parsers.GREEDY_STRING) String code) {
        if (code.startsWith("```") && code.endsWith("```") && code.length() > 6) {
            code = code.substring(3, code.length() - 3);
        }
        JSEvaluator.evaluate(
                2000,
                code,
                get(source),
                () -> source.send("Timeout while evaluating code"),
                tokenizeException -> source.send("Tokenize exception\n```\n" + tokenizeException.getMessage() + "\nat index " + tokenizeException.getIndex() + "\n```"),
                parseException -> source.send(String.format("Parse exception\n```\n%s\nat token %s [%d,%d]", parseException.getMessage(), parseException.getToken().toString(), parseException.getToken().getStart(), parseException.getToken().getEnd())),
                evalException -> source.send(String.format("Eval exception\n```\n%s\n```", evalException.getObject().toString())),
                jsObjects -> {
                    if (jsObjects.size() == 0) source.send("No objects result");
                    else
                        source.send(String.format("%s objects returned. Last:\n```\n%s\n```", jsObjects.size(), jsObjects.get(jsObjects.size() - 1)));
//                    StringBuilder builder = new StringBuilder("Result\n```\n");
//                    jsObjects.forEach(object -> {
//                        builder.append("-> ").append(object.toString()).append("\n");
//                    });
//                    builder.append("```");
//                    source.sendCensored(builder);
                }
        );
    }

}
