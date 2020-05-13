package p0nki.p0nkisassistant.utils;

import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import p0nki.p0nkisassistant.exceptions.InvalidEmbedException;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CustomEmbedBuilder {

    private String url = null;
    private String title = null;
    private String description = null;
    private OffsetDateTime timestamp = null;
    private Color color = Color.WHITE;
    private MessageEmbed.Thumbnail thumbnail = null;
    private MessageEmbed.AuthorInfo author = null;
    private MessageEmbed.Footer footer = null;
    private MessageEmbed.ImageInfo image = null;
    private List<MessageEmbed.Field> fields = new ArrayList<>();
    private boolean sourced = false;
    private boolean colored = false;

    public CustomEmbedBuilder() {
    }

    public CustomEmbedBuilder conditional(boolean bool, Function<CustomEmbedBuilder, CustomEmbedBuilder> function) {
        if (bool) {
            return function.apply(this);
        }
        return this;
    }

    public CustomEmbedBuilder copy() {
        CustomEmbedBuilder embed = new CustomEmbedBuilder();
        embed.url = url;
        embed.title = title;
        embed.description = description;
        embed.timestamp = timestamp;
        embed.color = color;
        embed.thumbnail = thumbnail;
        embed.author = author;
        embed.footer = footer;
        embed.image = image;
        embed.fields = new ArrayList<>(fields);
        embed.sourced = sourced;
        embed.colored = colored;
        return embed;
    }

    public CustomEmbedBuilder success() {
        return color(Constants.SUCCESS);
    }

    public CustomEmbedBuilder waiting() {
        return color(Constants.WAITING);
    }

    public CustomEmbedBuilder failure() {
        return color(Constants.FAILURE);
    }

    public CustomEmbedBuilder source(User user) {
        footer = new MessageEmbed.Footer(user.getAsTag(), user.getEffectiveAvatarUrl(), null);
        timestamp = OffsetDateTime.now();
        sourced = true;
        return this;
    }

    public CustomEmbedBuilder source(CommandSource source) {
        return source(source.source);
    }

    public CustomEmbedBuilder title(String title) {
        this.title = title;
        return this;
    }

    public CustomEmbedBuilder description(String description) {
        this.description = description;
        return this;
    }

    public CustomEmbedBuilder timestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    private CustomEmbedBuilder color(Color color) {
        colored = true;
        this.color = color;
        return this;
    }

    public CustomEmbedBuilder field(String name, String value, boolean inline) {
        fields.add(new MessageEmbed.Field(name, value, inline, true));
        return this;
    }

    public CustomEmbedBuilder thumbnail(String url) {
        this.thumbnail = new MessageEmbed.Thumbnail(url, null, 0, 0);
        return this;
    }

    public MessageEmbed build() {
        if (!sourced) throw new InvalidEmbedException("Embed has no source");
        if (!colored) throw new InvalidEmbedException("Embed not colored");
        return new MessageEmbed(url, title, description, EmbedType.RICH, timestamp, color.getRGB(), thumbnail, null, author, null, footer, image, fields);
    }

}
