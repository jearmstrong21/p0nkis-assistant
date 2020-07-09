package p0nki.assistant.scripting;

import net.dv8tion.jda.api.entities.*;
import p0nki.assistant.lib.utils.DiscordSource;
import p0nki.assistant.lib.utils.Holder;
import p0nki.pesl.api.PESLContext;
import p0nki.pesl.api.PESLEvalException;
import p0nki.pesl.api.object.*;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.stream.Collectors;

public class BaseScriptingContext implements Holder {

    private final PESLContext context;
    private final DiscordSource source;

    public BaseScriptingContext(DiscordSource source) {
        this.source = source;
        context = new PESLContext();
        if (source.isGuild()) {
            context.setKey("guild", createGuild(source.guild()));
            context.setKey("channel", createTextChannel(source.textChannel()));
            context.setKey("author", createMember(source.member()));
        } else {
            context.setKey("channel", createPrivateChannel(source.privateChannel()));
            context.setKey("author", createUser(source.user()));
        }
        context.setKey("message", createMessage(source.message()));
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

    protected BuiltinMapLikeObject patchMessageChannel(BuiltinMapLikeObject object, MessageChannel messageChannel) {
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

    private BuiltinMapLikeObject create(@Nonnull ISnowflake snowflake, String name) {
        BuiltinMapLikeObject object = new BuiltinMapLikeObject(name)
                .put("id", new StringObject(snowflake.getId()))
                .put("createdAt", new NumberObject(snowflake.getTimeCreated().toInstant().toEpochMilli()));
        if (snowflake instanceof IMentionable) {
            object.put("mention", new StringObject(((IMentionable) snowflake).getAsMention()));
        }
        return object;
    }

    private BuiltinMapLikeObject createColor(@Nonnull Color color) {
        return new BuiltinMapLikeObject("color")
                .put("r", new NumberObject(color.getRed()))
                .put("g", new NumberObject(color.getGreen()))
                .put("b", new NumberObject(color.getBlue()));
    }

    public BuiltinMapLikeObject createMember(@Nonnull Member member) {
        return patchMember(create(member, "member")
                .put("username", new StringObject(member.getUser().getName()))
                .put("discrim", new StringObject(member.getUser().getDiscriminator()))
                .put("avatar", new StringObject(member.getUser().getEffectiveAvatarUrl()))
                .put("roles", PESLUtils.simpleFunc(new ArrayObject(member.getRoles().stream().map(this::createRole).collect(Collectors.toList()))))
                .put("joinedAt", new NumberObject(member.getTimeJoined().toInstant().toEpochMilli()))
                .put("nickname", new StringObject(member.getEffectiveName()))
                .put("isOwner", new BooleanObject(member.isOwner()))
                .put("asUser", PESLUtils.wrap(arguments -> {
                    PESLEvalException.validArgumentListLength(arguments, 0);
                    return createUser(member.getUser());
                })), member);
    }

    public BuiltinMapLikeObject createGuild(@Nonnull Guild guild) {
        return patchGuild(create(guild, "guild")
                .put("name", new StringObject(guild.getName()))
                .put("icon", PESLUtils.nullableString(guild.getIconUrl()))
                .put("memberCount", new NumberObject(guild.getMemberCount()))
                .put("members", PESLUtils.simpleFunc(new ArrayObject(guild.getMembers().stream().map(this::createMember).collect(Collectors.toList()))))
                .put("emotes", PESLUtils.simpleFunc(new ArrayObject(guild.getEmotes().stream().map(this::createEmote).collect(Collectors.toList()))))
                .put("textChannels", PESLUtils.simpleFunc(new ArrayObject(guild.getTextChannels().stream().map(this::createTextChannel).collect(Collectors.toList()))))
                .put("roles", PESLUtils.simpleFunc(new ArrayObject(guild.getRoles().stream().map(this::createRole).collect(Collectors.toList()))))
                .put("owner", PESLUtils.simpleFunc(PESLUtils.nullable(guild.getOwner(), this::createMember))), guild);
    }

    public BuiltinMapLikeObject createTextChannel(@Nonnull TextChannel textChannel) {
        return patchMessageChannel(patchTextChannel(create(textChannel, "channelMessageText")
                .put("name", new StringObject(textChannel.getName()))
                .put("topic", PESLUtils.nullableString(textChannel.getTopic())), textChannel), textChannel);
    }

    public BuiltinMapLikeObject createPrivateChannel(@Nonnull PrivateChannel privateChannel) {
        return patchMessageChannel(patchPrivateChannel(create(privateChannel, "channelMessagePrivate")
                .put("user", createUser(privateChannel.getUser())), privateChannel), privateChannel);
    }

    public BuiltinMapLikeObject createEmote(@Nonnull Emote emote) {
        return patchEmote(create(emote, "emote")
                .put("name", new StringObject(emote.getName()))
                .put("url", new StringObject(emote.getImageUrl()))
                .put("animated", new BooleanObject(emote.isAnimated())), emote);
    }

    public BuiltinMapLikeObject createUser(@Nonnull User user) {
        return patchUser(create(user, "user")
                .put("username", new StringObject(user.getName()))
                .put("discrim", new StringObject(user.getDiscriminator()))
                .put("avatar", new StringObject(user.getEffectiveAvatarUrl())), user);
    }

    public BuiltinMapLikeObject createRole(@Nonnull Role role) {
        return patchRole(create(role, "role")
                .put("name", new StringObject(role.getName()))
                .put("color", PESLUtils.nullable(role.getColor(), this::createColor))
                .put("position", new NumberObject(role.getPosition()))
                .put("mentionable", new BooleanObject(role.isMentionable()))
                .put("hoisted", new BooleanObject(role.isHoisted()))
                .put("public", new BooleanObject(role.isPublicRole())), role);
    }

    public BuiltinMapLikeObject createAttachment(@Nonnull Message.Attachment attachment) {
        return create(attachment, "messageAttachment")
                .put("url", new StringObject(attachment.getUrl()))
                .put("proxy", new StringObject(attachment.getProxyUrl()))
                .put("filename", new StringObject(attachment.getFileName()))
                .put("ext", PESLUtils.nullableString(attachment.getFileExtension()))
                .put("image", new BooleanObject(attachment.isImage()))
                .put("video", new BooleanObject(attachment.isVideo()));
    }

    public BuiltinMapLikeObject createEmbedAuthor(@Nonnull MessageEmbed.AuthorInfo author) {
        return new BuiltinMapLikeObject("messageEmbedAuthor")
                .put("name", PESLUtils.nullableString(author.getName()))
                .put("url", PESLUtils.nullableString(author.getUrl()))
                .put("iconUrl", PESLUtils.nullableString(author.getIconUrl()))
                .put("proxyIconUrl", PESLUtils.nullableString(author.getProxyIconUrl()));
    }

    public BuiltinMapLikeObject createEmbedImage(@Nonnull MessageEmbed.ImageInfo image) {
        return new BuiltinMapLikeObject("messageEmbedImage")
                .put("width", new NumberObject(image.getWidth()))
                .put("height", new NumberObject(image.getHeight()))
                .put("url", PESLUtils.nullableString(image.getUrl()))
                .put("proxy_url", PESLUtils.nullableString(image.getProxyUrl()));
    }

    public BuiltinMapLikeObject createEmbedFooter(@Nonnull MessageEmbed.Footer footer) {
        return new BuiltinMapLikeObject("messageEmbedFooter")
                .put("iconUrl", PESLUtils.nullableString(footer.getIconUrl()))
                .put("proxyIconUrl", PESLUtils.nullableString(footer.getProxyIconUrl()))
                .put("text", PESLUtils.nullableString(footer.getText()));
    }

    public BuiltinMapLikeObject createEmbedField(@Nonnull MessageEmbed.Field field) {
        return new BuiltinMapLikeObject("messageEmbedField")
                .put("name", PESLUtils.nullableString(field.getName()))
                .put("value", PESLUtils.nullableString(field.getValue()))
                .put("inline", new BooleanObject(field.isInline()));
    }

    public BuiltinMapLikeObject createEmbed(@Nonnull MessageEmbed embed) {
        return new BuiltinMapLikeObject("messageEmbed")
                .put("title", PESLUtils.nullableString(embed.getTitle()))
                .put("description", PESLUtils.nullableString(embed.getDescription()))
                .put("color", PESLUtils.nullable(embed.getColor(), this::createColor))
                .put("author", PESLUtils.nullable(embed.getAuthor(), this::createEmbedAuthor))
                .put("url", PESLUtils.nullableString(embed.getUrl()))
                .put("image", PESLUtils.nullable(embed.getImage(), this::createEmbedImage))
                .put("footer", PESLUtils.nullable(embed.getFooter(), this::createEmbedFooter))
                .put("fields", new ArrayObject(embed.getFields().stream().map(this::createEmbedField).collect(Collectors.toList())));
    }

    public BuiltinMapLikeObject createMessage(@Nonnull Message message) {
        return patchMessage(create(message, "message")
                .put("content", new StringObject(message.getContentRaw()))
                .put("attachments", new ArrayObject(message.getAttachments().stream().map(this::createAttachment).collect(Collectors.toList())))
                .put("embeds", new ArrayObject(message.getEmbeds().stream().map(this::createEmbed).collect(Collectors.toList())))
                .put("pinned", new BooleanObject(message.isPinned()))
                .put("author", PESLUtils.simpleFunc(createUser(message.getAuthor())))
                .put("guild", PESLUtils.simpleFunc(PESLUtils.nullable(message.getGuild(), this::createGuild)))
                .put("channel", PESLUtils.simpleFunc(createMessageChannel(message.getChannel()))), message);
    }

    public final BuiltinMapLikeObject createMessageChannel(@Nonnull MessageChannel channel) {
        if (channel instanceof TextChannel) return createTextChannel((TextChannel) channel);
        if (channel instanceof PrivateChannel) return createPrivateChannel((PrivateChannel) channel);
        throw new AssertionError(channel.getClass());
    }

}
