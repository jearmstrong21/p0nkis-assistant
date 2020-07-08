package p0nki.assistant.cogs;

import p0nki.assistant.lib.utils.DiscordSource;
import p0nki.assistant.lib.utils.Holder;
import p0nki.assistant.scripting.BaseScriptingContext;
import p0nki.assistant.scripting.GodScriptingContext;
import p0nki.assistant.scripting.PESLEvaluator;
import p0nki.easycommand.annotations.Argument;
import p0nki.easycommand.annotations.Command;
import p0nki.easycommand.annotations.CommandCog;
import p0nki.easycommand.annotations.Source;
import p0nki.easycommand.arguments.Parsers;

import java.util.HashMap;
import java.util.Map;

@CommandCog(name = "eval")
public class EvalCog implements Holder {

    private final Map<String, BaseScriptingContext> contextMap = new HashMap<>();

    private BaseScriptingContext get(DiscordSource source) {
        if (!contextMap.containsKey(source.user().getId())) {
            contextMap.put(source.user().getId(), createContext(source));
        }
        return contextMap.get(source.user().getId());
    }

    private BaseScriptingContext createContext(DiscordSource source) {
        if (source.isOwner()) {
            return new GodScriptingContext(source);
        } else {
            return new BaseScriptingContext(source);
        }
    }

    @Command(names = {"resetctx"})
    public void reset(@Source DiscordSource source) {
        contextMap.put(source.user().getId(), createContext(source));
    }

    @Command(names = {"eval", "pesl"})
    public void pesl(@Source DiscordSource source, @Argument(name = "code", modifiers = Parsers.GREEDY_STRING) String code) {
        if (code.startsWith("```") && code.endsWith("```") && code.length() > 6) {
            code = code.substring(3, code.length() - 3);
        }
        PESLEvaluator.evaluate(
                2000,
                code,
                get(source).getContext(),
                () -> source.send("Timeout while evaluating code"),
                tokenizeException -> source.send("Tokenize exception\n```\n" + tokenizeException.getMessage() + "\nat index " + tokenizeException.getIndex() + "\n```"),
                parseException -> source.send(String.format("Parse exception\n```\n%s\nat token %s [%d,%d]", parseException.getMessage(), parseException.getToken().toString(), parseException.getToken().getStart(), parseException.getToken().getEnd())),
                evalException -> source.send(String.format("Eval exception\n```\n%s\n```", evalException.getObject().toString())),
                objects -> {
                    if (objects.size() == 0) source.send("No objects result");
                    else source.send(objects.get(objects.size() - 1).castToString());
                }
        );
    }

}
