package p0nki.p0nkisassistant.utils;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import p0nki.p0nkisassistant.arguments.*;
import p0nki.p0nkisassistant.listeners.CommandListener;

public class BrigadierUtils {

    public static LiteralArgumentBuilder<CommandSource> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public static <T> RequiredArgumentBuilder<CommandSource, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    public static Command<CommandSource> wrap(RuntimeException t) {
        return context -> {
            throw t;
        };
    }

    public static Command<CommandSource> success() {
        return context -> CommandListener.SUCCESS;
    }

    public static Command<CommandSource> failure() {
        return context -> CommandListener.FAILURE;
    }

    public static UserArgumentType user() {
        return new UserArgumentType();
    }

    public static ChannelArgumentType channel() {
        return new ChannelArgumentType();
    }

    public static EmoteArgumentType emote() {
        return new EmoteArgumentType();
    }

    public static RoleArgumentType role() {
        return new RoleArgumentType();
    }

    public static GuildArgumentType guild() {
        return new GuildArgumentType();
    }

    public static WordArgumentType string() {
        return WordArgumentType.word();
    }

    public static StringArgumentType greedyString() {
        return StringArgumentType.greedyString();
    }

    public static IntegerArgumentType integer() {
        return IntegerArgumentType.integer();
    }

    public static IntegerArgumentType integer(int min) {
        return IntegerArgumentType.integer(min);
    }

    public static IntegerArgumentType integer(int min, int max) {
        return IntegerArgumentType.integer(min, max);
    }

}
