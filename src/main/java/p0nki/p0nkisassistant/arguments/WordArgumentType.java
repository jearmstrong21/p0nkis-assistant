package p0nki.p0nkisassistant.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class WordArgumentType implements ArgumentType<String> {

    public static WordArgumentType word() {
        return new WordArgumentType();
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        if (!reader.canRead()) {
            return "";
        }
        final char next = reader.peek();
        if (StringReader.isQuotedStringStart(next)) {
            reader.skip();
            final StringBuilder result = new StringBuilder();
            boolean escaped = false;
            while (reader.canRead()) {
                final char c = reader.read();
                if (escaped) {
                    if (c == next || c == '\\') {
                        result.append(c);
                        escaped = false;
                    } else {
                        reader.setCursor(reader.getCursor() - 1);
                        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidEscape().createWithContext(reader, String.valueOf(c));
                    }
                } else if (c == '\\') {
                    escaped = true;
                } else if (c == next) {
                    return result.toString();
                } else {
                    result.append(c);
                }
            }

            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedEndOfQuote().createWithContext(reader);
        }
        final int start = reader.getCursor();
        while (reader.canRead() && reader.peek() != ' ') {
            reader.skip();
        }
        return reader.getString().substring(start, reader.getCursor());
    }

    @Override
    public String toString() {
        return "word()";
    }

}
