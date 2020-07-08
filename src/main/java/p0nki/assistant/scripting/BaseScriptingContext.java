package p0nki.assistant.scripting;

import net.dv8tion.jda.api.entities.*;
import p0nki.assistant.lib.utils.DiscordSource;
import p0nki.pesl.api.PESLContext;
import p0nki.pesl.api.object.*;

import java.awt.*;
import java.util.stream.Collectors;

public class BaseScriptingContext {

    private final PESLContext context;
    private final DiscordSource source;

    public BaseScriptingContext(DiscordSource source) {
        this.source = source;
        context = new PESLContext();
        if (source.isGuild()) {
            context.setKey("guild", createGuild(source.guild()));
            context.setKey("channel", createTextChannel(source.textChannel()));
            context.setKey("member", createMember(source.member()));
        } else {
            context.setKey("channel", createPrivateChannel(source.privateChannel()));
        }
        context.setKey("user", createUser(source.user()));
        patch(context);
    }

    public PESLContext getContext() {
        return context;
    }

    public DiscordSource getSource() {
        return source;
    }

    protected void patch(PESLContext context) {

    }

    protected BuiltinMapLikeObject patchMember(BuiltinMapLikeObject object, Member member) {
        return object;
    }

    protected BuiltinMapLikeObject patchGuild(BuiltinMapLikeObject object, Guild guild) {
        return object;
    }

    protected BuiltinMapLikeObject patchTextChannel(BuiltinMapLikeObject object, TextChannel textChannel) {
        return object;
    }

    protected BuiltinMapLikeObject patchPrivateChannel(BuiltinMapLikeObject object, PrivateChannel privateChannel) {
        return object;
    }

    protected BuiltinMapLikeObject patchEmote(BuiltinMapLikeObject object, Emote emote) {
        return object;
    }

    protected BuiltinMapLikeObject patchUser(BuiltinMapLikeObject object, User user) {
        return object;
    }

    protected BuiltinMapLikeObject patchRole(BuiltinMapLikeObject object, Role role) {
        return object;
    }

    protected BuiltinMapLikeObject patchMessage(BuiltinMapLikeObject object, Message message) {
        return object;
    }

    private BuiltinMapLikeObject create(ISnowflake snowflake, String name) {
        BuiltinMapLikeObject object = new BuiltinMapLikeObject(name)
                .put("id", new StringObject(snowflake.getId()))
                .put("createdAt", new NumberObject(snowflake.getTimeCreated().toInstant().toEpochMilli()));
        if (snowflake instanceof IMentionable) {
            object.put("mention", new StringObject(((IMentionable) snowflake).getAsMention()));
        }
        return object;
    }

    private BuiltinMapLikeObject createColor(Color color) {
        return new BuiltinMapLikeObject("color")
                .put("r", new NumberObject(color.getRed()))
                .put("g", new NumberObject(color.getGreen()))
                .put("b", new NumberObject(color.getBlue()));
    }

    public BuiltinMapLikeObject createMember(Member member) {
        return patchMember(create(member, "member")
                .put("username", new StringObject(member.getUser().getName()))
                .put("discrim", new StringObject(member.getUser().getDiscriminator()))
                .put("roles", PESLUtils.simpleFunc(new ArrayObject(member.getRoles().stream().map(this::createRole).collect(Collectors.toList()))))
                .put("joinedAt", new NumberObject(member.getTimeJoined().toInstant().toEpochMilli()))
                .put("nickname", new StringObject(member.getEffectiveName()))
                .put("avatar", new StringObject(member.getUser().getEffectiveAvatarUrl()))
                .put("owner", new BooleanObject(member.isOwner())), member);
    }

    public BuiltinMapLikeObject createGuild(Guild guild) {
        return patchGuild(create(guild, "guild")
                .put("name", new StringObject(guild.getName()))
                .put("icon", PESLUtils.nullableString(guild.getIconUrl()))
                .put("memberCount", new NumberObject(guild.getMemberCount()))
                .put("members", PESLUtils.simpleFunc(new ArrayObject(guild.getMembers().stream().map(this::createMember).collect(Collectors.toList()))))
                .put("emotes", PESLUtils.simpleFunc(new ArrayObject(guild.getEmotes().stream().map(this::createEmote).collect(Collectors.toList()))))
                .put("textChannels", PESLUtils.simpleFunc(new ArrayObject(guild.getTextChannels().stream().map(this::createTextChannel).collect(Collectors.toList()))))
                .put("roles", PESLUtils.simpleFunc(new ArrayObject(guild.getRoles().stream().map(this::createRole).collect(Collectors.toList()))))
                .put("owner", createMember(guild.getOwner())), guild);
    }

    public BuiltinMapLikeObject createTextChannel(TextChannel textChannel) {
        return patchTextChannel(create(textChannel, "textChannel")
                .put("name", new StringObject(textChannel.getName()))
                .put("topic", PESLUtils.nullableString(textChannel.getTopic())), textChannel);
    }

    public BuiltinMapLikeObject createPrivateChannel(PrivateChannel privateChannel) {
        return patchPrivateChannel(create(privateChannel, "privateChannel")
                .put("user", createUser(privateChannel.getUser())), privateChannel);
    }

    public BuiltinMapLikeObject createEmote(Emote emote) {
        return patchEmote(create(emote, "emote")
                .put("name", new StringObject(emote.getName()))
                .put("url", new StringObject(emote.getImageUrl()))
                .put("animated", new BooleanObject(emote.isAnimated())), emote);
    }

    public BuiltinMapLikeObject createUser(User user) {
        return patchUser(create(user, "user")
                .put("name", new StringObject(user.getName()))
                .put("discrim", new StringObject(user.getDiscriminator()))
                .put("avatar", new StringObject(user.getEffectiveAvatarUrl())), user);
    }

    public BuiltinMapLikeObject createRole(Role role) {
        return patchRole(create(role, "role")
                .put("name", new StringObject(role.getName()))
                .put("color", PESLUtils.nullable(role.getColor(), this::createColor))
                .put("position", new NumberObject(role.getPosition()))
                .put("mentionable", new BooleanObject(role.isMentionable()))
                .put("hoisted", new BooleanObject(role.isHoisted()))
                .put("public", new BooleanObject(role.isPublicRole())), role);
    }

    public BuiltinMapLikeObject createAttachment(Message.Attachment attachment) {
        return create(attachment, "attachment")
                .put("url", new StringObject(attachment.getUrl()))
                .put("proxy", new StringObject(attachment.getProxyUrl()))
                .put("filename", new StringObject(attachment.getFileName()))
                .put("ext", PESLUtils.nullableString(attachment.getFileExtension()))
                .put("image", new BooleanObject(attachment.isImage()))
                .put("video", new BooleanObject(attachment.isVideo()));
    }

    public BuiltinMapLikeObject createMessage(Message message) {
        return patchMessage(create(message, "message")
                .put("content", new StringObject(message.getContentRaw()))
                .put("attachments", new ArrayObject(message.getAttachments().stream().map(this::createAttachment).collect(Collectors.toList())))
                .put("embeds", NullObject.INSTANCE)
                .put("author", PESLUtils.simpleFunc(createUser(message.getAuthor()))), message);
    }

}
