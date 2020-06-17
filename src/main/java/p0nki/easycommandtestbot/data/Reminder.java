package p0nki.easycommandtestbot.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import p0nki.easycommandtestbot.lib.DiscordUtils;
import p0nki.easycommandtestbot.lib.Holder;

import java.util.Date;
import java.util.Objects;

public class Reminder implements Holder {

    @JsonProperty("channel_id")
    private String channelID;
    private String user;
    private String text;
    @JsonProperty("created_at")
    private long createdAt;
    @JsonProperty("send_at")
    private long sendAt;

    public Reminder() {

    }

    public Reminder(String channelID, String user, String text, long createdAt, long sendAt) {
        this.channelID = channelID;
        this.user = user;
        this.text = text;
        this.createdAt = createdAt;
        this.sendAt = sendAt;
        Objects.requireNonNull(jda().getUserById(user)).openPrivateChannel().queue(privateChannel -> {
            privateChannel.sendMessage("Reminder created in guild " +
                    Objects.requireNonNull(jda().getTextChannelById(channelID)).getGuild().getName())
                    .embed(embed().build()).queue();
        });
    }

    public long getSendAt() {
        return sendAt;
    }

    public EmbedBuilder embed() {
        return new EmbedBuilder()
                .setTitle("Scheduled ping")
                .setDescription(text)
                .addField("Created at", new Date(createdAt).toString(), false)
                .addField("Scheduled to be sent at", new Date(sendAt).toString(), false);
    }

    public void remove() {
        Objects.requireNonNull(jda().getUserById(user)).openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Your reminder was deleted").embed(embed().build()).queue());
    }

    public String getUser() {
        return user;
    }

    public void send() {
        MessageChannel channel = DiscordUtils.getChannelById(channelID);
        if (channel == null) return;
        channel.sendMessage(Objects.requireNonNull(jda().getUserById(user)).getAsMention()).embed(embed().build()).queue();
    }

    public boolean isDueToSend() {
        return System.currentTimeMillis() > sendAt;
    }

}
