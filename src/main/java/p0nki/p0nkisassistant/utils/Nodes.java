package p0nki.p0nkisassistant.utils;

import net.dv8tion.jda.api.entities.*;
import p0nki.commandparser.argument.GreedyStringArgumentType;
import p0nki.commandparser.argument.IntegerArgumentType;
import p0nki.commandparser.argument.QuotedStringArgumentType;
import p0nki.commandparser.node.ArgumentCommandNode;
import p0nki.commandparser.node.LiteralCommandNode;
import p0nki.p0nkisassistant.arguments.*;

public class Nodes {

    public static LiteralCommandNode<CommandSource, CommandResult> literal(String... names) {
        return new LiteralCommandNode<>(names);
    }

    public static ArgumentCommandNode<CommandSource, CommandResult, Integer> integer(String name) {
        return new ArgumentCommandNode<>(name, new IntegerArgumentType<>());
    }

    public static ArgumentCommandNode<CommandSource, CommandResult, Integer> integer(String name, int minimum) {
        return new ArgumentCommandNode<>(name, new IntegerArgumentType<>(minimum));
    }

    public static ArgumentCommandNode<CommandSource, CommandResult, Integer> integer(String name, int minimum, int maximum) {
        return new ArgumentCommandNode<>(name, new IntegerArgumentType<>(minimum, maximum));
    }

    public static ArgumentCommandNode<CommandSource, CommandResult, String> greedyString(String name) {
        return new ArgumentCommandNode<>(name, new GreedyStringArgumentType<>());
    }

    public static ArgumentCommandNode<CommandSource, CommandResult, String> quotedString(String name) {
        return new ArgumentCommandNode<>(name, new QuotedStringArgumentType<>());
    }

    public static ArgumentCommandNode<CommandSource, CommandResult, Emote> emote(String name) {
        return new ArgumentCommandNode<>(name, new EmoteArgumentType());
    }

    public static ArgumentCommandNode<CommandSource, CommandResult, Guild> guild(String name) {
        return new ArgumentCommandNode<>(name, new GuildArgumentType());
    }

    public static ArgumentCommandNode<CommandSource, CommandResult, Member> member(String name) {
        return new ArgumentCommandNode<>(name, new MemberArgumentType());
    }

    public static ArgumentCommandNode<CommandSource, CommandResult, Role> role(String name) {
        return new ArgumentCommandNode<>(name, new RoleArgumentType());
    }

    public static ArgumentCommandNode<CommandSource, CommandResult, TextChannel> textChannel(String name) {
        return new ArgumentCommandNode<>(name, new TextChannelArgumentType());
    }

    public static ArgumentCommandNode<CommandSource, CommandResult, User> user(String name) {
        return new ArgumentCommandNode<>(name, new UserArgumentType());
    }
}
