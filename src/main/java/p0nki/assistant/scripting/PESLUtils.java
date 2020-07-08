package p0nki.assistant.scripting;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import p0nki.pesl.api.PESLEvalException;
import p0nki.pesl.api.object.FunctionObject;
import p0nki.pesl.api.object.PESLObject;
import p0nki.pesl.api.object.StringObject;
import p0nki.pesl.api.object.UndefinedObject;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class PESLUtils {

    public static Message parseMessage(PESLObject object) {
        if (object instanceof StringObject) {
            return new MessageBuilder().setContent(object.castToString()).build();
        } else if (object.getType().equals("embed")) {
            return new MessageBuilder().setContent("[todo]").setEmbed(new EmbedBuilder().setTitle("[todo]").setDescription("[todo]").build()).build();
        } else {
            return new MessageBuilder().setContent(object.castToString()).build();
        }
    }

    public static PESLObject wrap(FunctionWhichThrows function) {
        return FunctionObject.of(false, arguments -> {
            try {
                return function.operate(arguments);
            } catch (PESLEvalException e) {
                throw e;
            } catch (Throwable t) {
                throw new PESLEvalException(t.getMessage());
            }
        });
    }

    public static PESLObject simpleFunc(PESLObject value) {
        return FunctionObject.of(false, arguments -> {
            PESLEvalException.validArgumentListLength(arguments, 0);
            return value;
        });
    }

    public static <T> PESLObject nullable(@Nullable T str, Function<T, PESLObject> function) {
        return Optional.ofNullable(str).map(function).orElse(UndefinedObject.INSTANCE);
    }

    public static PESLObject nullableString(@Nullable String str) {
        if (str == null) return UndefinedObject.INSTANCE;
        return new StringObject(str);
    }

    public interface FunctionWhichThrows {

        PESLObject operate(List<PESLObject> arguments) throws Throwable;

    }

}
