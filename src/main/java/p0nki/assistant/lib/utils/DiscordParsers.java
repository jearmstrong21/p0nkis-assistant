package p0nki.assistant.lib.utils;

import net.dv8tion.jda.api.entities.*;
import p0nki.easycommand.arguments.ArgumentParser;
import p0nki.easycommand.arguments.ParserFactory;
import p0nki.easycommand.utils.Optional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DiscordParsers {

    // --- MODIFIERS ---
    public static final String REQUIRE_ENTITY = "entity";

    // --- PARSERS ---
    public static final ArgumentParser TEXT_CHANNEL_PARSER = (source, reader) -> {
        DiscordSource ds = (DiscordSource) source;
        if (!ds.isGuild()) return Optional.empty();
        String str = reader.readWord();
        if (str.startsWith("<#") && str.endsWith(">")) str = str.substring(2, str.length() - 1);
        Set<TextChannel> channels = new HashSet<>(ds.guild().getTextChannels());
        for (TextChannel channel : channels) {
            if (channel.getId().equals(str)) return Optional.of(channel);
        }
        return Optional.empty();
    };
    public static final ArgumentParser EMOTE_PARSER = (source, reader) -> {
        DiscordSource ds = (DiscordSource) source;
        String str = reader.readWord();
        try {
            if (str.startsWith("<:") && str.endsWith(">")) str = str.substring(str.length() - 19, str.length() - 1);
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
        Set<Emote> emotes = new HashSet<>(ds.message().getEmotes());
        if (ds.isGuild()) emotes.addAll(ds.guild().getEmotes());
        for (Emote emote : emotes) {
            if (emote.getId().equals(str)) return Optional.of(emote);
        }
        return Optional.empty();
    };
    public static final ArgumentParser MEMBER_PARSER = (source, reader) -> {
        DiscordSource ds = (DiscordSource) source;
        if (!ds.isGuild()) return Optional.empty();
        String str = reader.readWord();
        if (str.startsWith("<@!") && str.endsWith(">")) str = str.substring(3, str.length() - 1);
        else if (str.startsWith("<@") && str.endsWith(">")) str = str.substring(2, str.length() - 1);
        Set<Member> members = new HashSet<>(ds.guild().getMembers());
        for (Member member : members) {
            if (member.getId().equals(str)) return Optional.of(member);
        }
        return Optional.empty();
    };
    public static final ArgumentParser USER_PARSER = (source, reader) -> {
        DiscordSource ds = (DiscordSource) source;
        String str = reader.readWord();
        if (str.startsWith("<@!") && str.endsWith(">")) str = str.substring(3, str.length() - 1);
        else if (str.startsWith("<@") && str.endsWith(">")) str = str.substring(2, str.length() - 1);
        Set<User> users = new HashSet<>();
        users.add(ds.jda().getSelfUser());
        if (ds.isPrivateChannel()) users.add(ds.privateChannel().getUser());
        if (ds.isGuild())
            users.addAll(ds.guild().getMembers().stream().map(Member::getUser).collect(Collectors.toSet()));
        if (ds.isFromOwner()) users.addAll(ds.jda().getUsers());
        for (User user : users) {
            if (user.getId().equals(str)) return Optional.of(user);
        }
        return Optional.empty();
    };
    public static final ArgumentParser ROLE_PARSER = (source, reader) -> {
        DiscordSource ds = (DiscordSource) source;
        if (!ds.isGuild()) return Optional.empty();
        String str = reader.readWord();
        if (str.startsWith("<@&") && str.endsWith(">")) str = str.substring(3, str.length() - 1);
        Set<Role> roles = new HashSet<>(ds.guild().getRoles());
        for (Role role : roles) {
            if (role.getId().equals(str)) return Optional.of(role);
        }
        return Optional.empty();
    };
    public static final ArgumentParser SNOWFLAKE_PARSER = (source, reader) -> {
        int index = reader.getIndex();

        Optional<?> textChannel = TEXT_CHANNEL_PARSER.parse(source, reader);
        if (textChannel.isPresent()) return textChannel;
        reader.setIndex(index);

        Optional<?> emote = EMOTE_PARSER.parse(source, reader);
        if (emote.isPresent()) return emote;
        reader.setIndex(index);

        Optional<?> member = MEMBER_PARSER.parse(source, reader);
        if (member.isPresent()) return member;
        reader.setIndex(index);

        Optional<?> user = USER_PARSER.parse(source, reader);
        if (user.isPresent()) return user;
        reader.setIndex(index);

        Optional<?> role = ROLE_PARSER.parse(source, reader);
        if (role.isPresent()) return role;
        reader.setIndex(index);

        String str = reader.readWord();
        Optional<Long> id = Optional.emptyIfThrow(() -> Long.parseLong(str));
        if (id.isPresent()) return Optional.of(new LimitedSnowflake(id.get()));

        return Optional.empty();
    };
    public static final ArgumentParser SNOWFLAKE_PARSER_REQUIRE_ENTITY = (source, reader) -> {
        int index = reader.getIndex();

        Optional<?> textChannel = TEXT_CHANNEL_PARSER.parse(source, reader);
        if (textChannel.isPresent()) return textChannel;
        reader.setIndex(index);

        Optional<?> emote = EMOTE_PARSER.parse(source, reader);
        if (emote.isPresent()) return emote;
        reader.setIndex(index);

        Optional<?> member = MEMBER_PARSER.parse(source, reader);
        if (member.isPresent()) return member;
        reader.setIndex(index);

        Optional<?> user = USER_PARSER.parse(source, reader);
        if (user.isPresent()) return user;
        reader.setIndex(index);

        Optional<?> role = ROLE_PARSER.parse(source, reader);
        if (role.isPresent()) return role;

        return Optional.empty();
    };
    public static final ArgumentParser DURATION_PARSER = (source, reader) -> {
        Optional<TimeDuration> timeDurationOptional = TimeDuration.parse(reader.readWord());
        if (timeDurationOptional.isPresent()) return Optional.of(timeDurationOptional.get());
        return Optional.empty();
    };

    // --- FACTORY ---
    public static final ParserFactory TEXT_CHANNEL = argument -> {
        if (argument.getClazz() != TextChannel.class) return Optional.empty();
        return Optional.of(TEXT_CHANNEL_PARSER);
    };
    public static final ParserFactory EMOTE = argument -> {
        if (argument.getClazz() != Emote.class) return Optional.empty();
        return Optional.of(EMOTE_PARSER);
    };
    public static final ParserFactory MEMBER = argument -> {
        if (argument.getClazz() != Member.class) return Optional.empty();
        return Optional.of(MEMBER_PARSER);
    };
    public static final ParserFactory USER = argument -> {
        if (argument.getClazz() != User.class) return Optional.empty();
        return Optional.of(USER_PARSER);
    };
    public static final ParserFactory ROLE = argument -> {
        if (argument.getClazz() != Role.class) return Optional.empty();
        return Optional.of(ROLE_PARSER);
    };
    public static final ParserFactory SNOWFLAKE = argument -> {
        if (argument.getClazz() != ISnowflake.class) return Optional.empty();
        if (argument.getModifiers().contains(REQUIRE_ENTITY)) return Optional.of(SNOWFLAKE_PARSER_REQUIRE_ENTITY);
        return Optional.of(SNOWFLAKE_PARSER);
    };
    public static final ParserFactory DURATION = argument -> {
        if (argument.getClazz() != TimeDuration.class) return Optional.empty();
        return Optional.of(DURATION_PARSER);
    };
}
