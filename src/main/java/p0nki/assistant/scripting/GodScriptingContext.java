package p0nki.assistant.scripting;

import net.dv8tion.jda.api.entities.*;
import p0nki.assistant.lib.utils.DiscordSource;
import p0nki.pesl.api.PESLContext;
import p0nki.pesl.api.PESLEvalException;
import p0nki.pesl.api.object.ArrayObject;
import p0nki.pesl.api.object.BuiltinMapLikeObject;
import p0nki.pesl.api.object.UndefinedObject;

import java.util.Objects;
import java.util.stream.Collectors;

public class GodScriptingContext extends BaseScriptingContext {

    public GodScriptingContext(DiscordSource source) {
        super(source);
    }

    @Override
    protected void patch(PESLContext context) {
        context.setKey("guilds", new ArrayObject(jda().getGuilds().stream().map(this::createGuild).collect(Collectors.toList())));
        context.setKey("userById", PESLUtils.wrap(arguments -> {
            PESLEvalException.validArgumentListLength(arguments, 1);
            return createUser(Objects.requireNonNull(jda().getUserById(arguments.get(0).castToString())));
        }));
        context.setKey("emoteById", PESLUtils.wrap(arguments -> {
            PESLEvalException.validArgumentListLength(arguments, 1);
            return createEmote(Objects.requireNonNull(jda().getEmoteById(arguments.get(0).castToString())));
        }));
        context.setKey("emotesByName", PESLUtils.wrap(arguments -> {
            PESLEvalException.validArgumentListLength(arguments, 1);
            return new ArrayObject(jda().getEmotesByName(arguments.get(0).castToString(), true).stream().map(this::createEmote).collect(Collectors.toList()));
        }));
        context.setKey("channelById", PESLUtils.wrap(arguments -> {
            PESLEvalException.validArgumentListLength(arguments, 1);
            return createTextChannel(Objects.requireNonNull(jda().getTextChannelById(arguments.get(0).castToString())));
        }));
        context.setKey("guildById", PESLUtils.wrap(arguments -> {
            PESLEvalException.validArgumentListLength(arguments, 1);
            return createGuild(Objects.requireNonNull(jda().getGuildById(arguments.get(0).castToString())));
        }));
    }

    @Override
    protected BuiltinMapLikeObject patchMember(BuiltinMapLikeObject object, Member member) {
        getContext().let("Data", UndefinedObject.INSTANCE);
        return object
                .put("ban", PESLUtils.wrap(arguments -> {
                    PESLEvalException.validArgumentListLength(arguments, 0, 1, 2);
                    if (arguments.size() == 0) {
                        member.ban(0).complete();
                    } else if (arguments.size() == 1) {
                        member.ban((int) arguments.get(0).asNumber().getValue()).complete();
                    } else {
                        member.ban((int) arguments.get(0).asNumber().getValue(), arguments.get(1).castToString()).complete();
                    }
                    return UndefinedObject.INSTANCE;
                }))
                .put("kick", PESLUtils.wrap(arguments -> {
                    PESLEvalException.validArgumentListLength(arguments, 0, 1);
                    if (arguments.size() == 0) {
                        member.kick().complete();
                    } else {
                        member.kick(arguments.get(0).castToString()).complete();
                    }
                    return UndefinedObject.INSTANCE;
                }))
                .put("mute", PESLUtils.wrap(arguments -> {
                    PESLEvalException.validArgumentListLength(arguments, 0, 1);
                    if (arguments.size() == 0) {
                        member.mute(true).complete();
                    } else {
                        member.mute(arguments.get(0).asBoolean().getValue()).complete();
                    }
                    return UndefinedObject.INSTANCE;
                }))
                .put("modifyNickname", PESLUtils.wrap(arguments -> {
                    PESLEvalException.validArgumentListLength(arguments, 1);
                    if (arguments.get(0) == UndefinedObject.INSTANCE) {
                        member.modifyNickname(null).complete();
                    } else {
                        member.modifyNickname(arguments.get(0).castToString()).complete();
                    }
                    return UndefinedObject.INSTANCE;
                }))
                .put("addRole", PESLUtils.wrap(arguments -> {
                    PESLEvalException.validArgumentListLength(arguments, 1);
                    member.getGuild().addRoleToMember(member, Objects.requireNonNull(member.getGuild().getRoleById(arguments.get(0).castToString()))).queue();
                    return UndefinedObject.INSTANCE;
                }))
                .put("removeRole", PESLUtils.wrap(arguments -> {
                    PESLEvalException.validArgumentListLength(arguments, 1);
                    member.getGuild().removeRoleFromMember(member, Objects.requireNonNull(member.getGuild().getRoleById(arguments.get(0).castToString()))).queue();
                    return UndefinedObject.INSTANCE;
                }));
    }

    @Override
    protected BuiltinMapLikeObject patchGuild(BuiltinMapLikeObject object, Guild guild) {
        return object;
    }

    @Override
    protected BuiltinMapLikeObject patchMessageChannel(BuiltinMapLikeObject object, MessageChannel messageChannel) {
        return object
                .put("send", PESLUtils.wrap(arguments -> {
                    PESLEvalException.validArgumentListLength(arguments, 1);
                    return createMessage(messageChannel.sendMessage(PESLUtils.parseMessage(arguments.get(0))).complete());
                }))
                .put("fetch", PESLUtils.wrap(arguments -> {
                    PESLEvalException.validArgumentListLength(arguments, 1);
                    return createMessage(messageChannel.retrieveMessageById(arguments.get(0).castToString()).complete());
                }))
                .put("latest", PESLUtils.wrap(arguments -> {
                    PESLEvalException.validArgumentListLength(arguments, 0);
                    return messageChannel.hasLatestMessage() ? createMessage(messageChannel.retrieveMessageById(messageChannel.getLatestMessageId()).complete()) : UndefinedObject.INSTANCE;
                }));
    }

    @Override
    protected BuiltinMapLikeObject patchTextChannel(BuiltinMapLikeObject object, TextChannel textChannel) {
        return object;
    }

    @Override
    protected BuiltinMapLikeObject patchPrivateChannel(BuiltinMapLikeObject object, PrivateChannel privateChannel) {
        return object;
    }

    @Override
    protected BuiltinMapLikeObject patchEmote(BuiltinMapLikeObject object, Emote emote) {
        return object;
    }

    @Override
    protected BuiltinMapLikeObject patchUser(BuiltinMapLikeObject object, User user) {
        return object
                .put("mutualGuilds", PESLUtils.simpleFunc(new ArrayObject(user.getMutualGuilds().stream().map(this::createGuild).collect(Collectors.toList()))))
                .put("dm", PESLUtils.wrap(arguments -> {
                    PESLEvalException.validArgumentListLength(arguments, 0);
                    return createPrivateChannel(user.openPrivateChannel().complete());
                }));
    }

    @Override
    protected BuiltinMapLikeObject patchRole(BuiltinMapLikeObject object, Role role) {
        return object;
    }

    @Override
    protected BuiltinMapLikeObject patchMessage(BuiltinMapLikeObject object, Message message) {
        return object
                .put("remove", PESLUtils.wrap(arguments -> { // `remove` since `delete` is a keyword
                    PESLEvalException.validArgumentListLength(arguments, 0);
                    message.delete().complete();
                    return UndefinedObject.INSTANCE;
                }))
                .put("pin", PESLUtils.wrap(arguments -> {
                    PESLEvalException.validArgumentListLength(arguments, 0);
                    message.pin().complete();
                    return UndefinedObject.INSTANCE;
                }))
                .put("unpin", PESLUtils.wrap(arguments -> {
                    PESLEvalException.validArgumentListLength(arguments, 0);
                    message.unpin().complete();
                    return UndefinedObject.INSTANCE;
                }))
                .put("clearReactions", PESLUtils.wrap(arguments -> {
                    PESLEvalException.validArgumentListLength(arguments, 0);
                    message.clearReactions().complete();
                    return UndefinedObject.INSTANCE;
                }))
                .put("edit", PESLUtils.wrap(arguments -> {
                    PESLEvalException.validArgumentListLength(arguments, 1);
                    message.editMessage(PESLUtils.parseMessage(arguments.get(0))).complete();
                    return UndefinedObject.INSTANCE;
                }));
    }
}
