package p0nki.p0nkisassistant.arguments;

import p0nki.commandparser.argument.ArgumentType;
import p0nki.commandparser.command.CommandContext;
import p0nki.commandparser.command.CommandReader;
import p0nki.p0nkisassistant.utils.CommandSource;

import java.util.Optional;

public abstract class GenericArgumentType<T> implements ArgumentType<CommandSource, T> {

    protected abstract String getIdByMention(CommandSource source, String str);

    protected abstract T parseById(CommandSource source, String str);

    @Override
    public Optional<T> parse(CommandContext<CommandSource> context, CommandReader reader) {
        String str = reader.readWhile(CommandReader.isNotSpace);
        try {
            T value = parseById(context.source(), str);
            if (value != null) return Optional.of(value);
        } catch (Throwable ignored) {

        }
        try {
            T value = parseById(context.source(), getIdByMention(context.source(), str));
            if (value != null) return Optional.of(value);
        } catch (Throwable ignored) {

        }
        return Optional.empty();
    }
}
