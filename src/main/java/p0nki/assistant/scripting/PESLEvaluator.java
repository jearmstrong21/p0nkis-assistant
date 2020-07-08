package p0nki.assistant.scripting;

import p0nki.pesl.api.PESLContext;
import p0nki.pesl.api.PESLEvalException;
import p0nki.pesl.api.object.PESLObject;
import p0nki.pesl.api.parse.ASTNode;
import p0nki.pesl.api.parse.PESLParseException;
import p0nki.pesl.api.parse.PESLParser;
import p0nki.pesl.api.token.PESLTokenList;
import p0nki.pesl.api.token.PESLTokenizeException;
import p0nki.pesl.api.token.PESLTokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class PESLEvaluator {

    private final static ExecutorService service = Executors.newSingleThreadExecutor();

    public static void evaluate(long timeout,
                                String code,
                                PESLContext context,
                                Runnable timeoutRunnable,
                                Consumer<PESLTokenizeException> tokenizeExceptionConsumer,
                                Consumer<PESLParseException> parseExceptionConsumer,
                                Consumer<PESLEvalException> evalExceptionConsumer,
                                Consumer<List<PESLObject>> objectConsumer) {
        boolean[] anythingSent = new boolean[]{false};
        try {
            service.submit(() -> {
                PESLTokenizer tokenizer = new PESLTokenizer();
                PESLTokenList tokens;
                try {
                    tokens = tokenizer.tokenize(code);
                } catch (PESLTokenizeException e) {
                    anythingSent[0] = true;
                    tokenizeExceptionConsumer.accept(e);
                    return;
                }
                System.out.println("PESLEVAL tokenize");
                PESLParser parser = new PESLParser();
                List<PESLObject> objects = new ArrayList<>();
                try {
                    while (tokens.hasAny()) {
                        ASTNode node = parser.parseExpression(tokens);
                        objects.add(node.evaluate(context));
                    }
                } catch (PESLParseException e) {
                    anythingSent[0] = true;
                    parseExceptionConsumer.accept(e);
                } catch (PESLEvalException e) {
                    anythingSent[0] = true;
                    evalExceptionConsumer.accept(e);
                }
                anythingSent[0] = true;
                System.out.println("PESLEVAL parse and eval");
                objectConsumer.accept(objects);
            }).get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | TimeoutException e) {
            if (!anythingSent[0]) {
                System.out.println("PESLEVAL timeout");
                timeoutRunnable.run();
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
