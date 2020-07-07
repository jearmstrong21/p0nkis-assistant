package p0nki.assistant.cogs;

import p0nki.assistant.PESLEvaluator;
import p0nki.assistant.lib.utils.DiscordSource;
import p0nki.assistant.lib.utils.Holder;
import p0nki.easycommand.annotations.Argument;
import p0nki.easycommand.annotations.Command;
import p0nki.easycommand.annotations.CommandCog;
import p0nki.easycommand.annotations.Source;
import p0nki.easycommand.arguments.Parsers;
import p0nki.pesl.api.PESLContext;

import java.util.HashMap;
import java.util.Map;

@CommandCog(name = "eval")
public class EvalCog implements Holder {

    private final Map<String, PESLContext> contextMap = new HashMap<>();

    private PESLContext get(DiscordSource source) {
        if (!contextMap.containsKey(source.user().getId())) {
            contextMap.put(source.user().getId(), PESLEvaluator.newContext());
        }
        return contextMap.get(source.user().getId());
    }

    @Command(names = {"eval", "pesl"})
    public void eval(@Source DiscordSource source, @Argument(name = "code", modifiers = Parsers.GREEDY_STRING) String code) {
        if (code.startsWith("```") && code.endsWith("```") && code.length() > 6) {
            code = code.substring(3, code.length() - 3);
        }
        PESLEvaluator.evaluate(
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
                }
        );
    }

}
