package p0nki.p0nkisassistant.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public abstract class GenericArgumentType<T> implements ArgumentType<T> {

    protected abstract String getIdByMention(String str);

    protected abstract T parseById(String str);

    protected abstract String formatException(String str);

    @Override
    public T parse(StringReader reader) throws CommandSyntaxException {
        int begin = reader.getCursor();
        if (!reader.canRead()) reader.skip();
        while (reader.canRead() && reader.peek() != ' ') reader.skip();
        String str = reader.getString().substring(begin, reader.getCursor());
        try {
            T value = parseById(str);
            if (value != null) return value;
        } catch (Throwable ignored) {

        }
        try {
            T value = parseById(getIdByMention(str));
            if (value != null) return value;
        } catch (Throwable ignored) {

        }
        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create(formatException(str));
    }
}
