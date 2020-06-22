package p0nki.assistant;

import p0nki.javashit.ast.JSASTCreator;
import p0nki.javashit.ast.JSParseException;
import p0nki.javashit.ast.nodes.JSASTNode;
import p0nki.javashit.object.JSObject;
import p0nki.javashit.run.JSContext;
import p0nki.javashit.run.JSEvalException;
import p0nki.javashit.token.JSTokenList;
import p0nki.javashit.token.JSTokenizeException;
import p0nki.javashit.token.JSTokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class JSEvaluator {

    private final static ExecutorService service = Executors.newSingleThreadExecutor();

    public static void evaluate(long timeout,
                                String code,
                                JSContext context,
                                Runnable timeoutRunnable,
                                Consumer<JSTokenizeException> tokenizeExceptionConsumer,
                                Consumer<JSParseException> parseExceptionConsumer,
                                Consumer<JSEvalException> evalExceptionConsumer,
                                Consumer<List<JSObject>> objectConsumer) {
        boolean[] anythingSent = new boolean[]{false};
        try {
            service.submit(() -> {
                JSTokenizer tokenizer = new JSTokenizer();
                JSTokenList tokens;
                try {
                    tokens = tokenizer.tokenize(code);
                } catch (JSTokenizeException e) {
                    anythingSent[0] = true;
                    tokenizeExceptionConsumer.accept(e);
                    return;
                }
                System.out.println("JSEVAL tokenize");
                JSASTCreator astCreator = new JSASTCreator();
                List<JSObject> objects = new ArrayList<>();
                try {
                    while (tokens.hasAny()) {
                        JSASTNode node = astCreator.parseExpression(tokens);
                        objects.add(node.evaluate(context));
                    }
                } catch (JSParseException e) {
                    anythingSent[0] = true;
                    parseExceptionConsumer.accept(e);
                } catch (JSEvalException e) {
                    anythingSent[0] = true;
                    evalExceptionConsumer.accept(e);
                }
                anythingSent[0] = true;
                System.out.println("JSEVAL parse and eval");
                objectConsumer.accept(objects);
            }).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | TimeoutException e) {
            if (!anythingSent[0]) {
                System.out.println("JSEVAL timeout");
                timeoutRunnable.run();
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
