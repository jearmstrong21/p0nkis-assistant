package p0nki.p0nkisassistant.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import p0nki.p0nkisassistant.P0nkisAssistant;
import p0nki.p0nkisassistant.listeners.CommandListener;
import p0nki.p0nkisassistant.utils.CommandSource;
import p0nki.p0nkisassistant.utils.Constants;
import p0nki.p0nkisassistant.utils.Utils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static p0nki.p0nkisassistant.utils.BrigadierUtils.*;

public class DumpInformationCommand {

    public static int dumpGuilds(CommandSource source) {
        String guilds = P0nkisAssistant.jda.getGuilds().stream().map(Object::toString).collect(Collectors.joining("\n"));
        if (guilds.length() > Constants.MESSAGE_SIZE - 7) {
            source.to.sendFile(guilds.getBytes(), "GUILDS DUMP " + new Date().toString()).queue();
        } else {
            source.to.sendMessage("```\n" + guilds + "```").queue();
        }
        return CommandListener.SUCCESS;
    }

    public static int dumpChannels(CommandSource source, Guild guild) {
        String channels = guild.getChannels().stream().map(Object::toString).collect(Collectors.joining("\n"));
        if (channels.length() > Constants.MESSAGE_SIZE - 7) {
            source.to.sendFile(channels.getBytes(), "CHANNELS DUMP FOR GUILD " + guild + " " + new Date().toString()).queue();
        } else {
            source.to.sendMessage("```\n" + channels + "```").queue();
        }
        return CommandListener.SUCCESS;
    }

    public static int dumpMessages(CommandSource source, TextChannel channel, int count) {
        List<Message> messageList = channel.getIterableHistory().cache(false).limit(count).complete();
        Collections.reverse(messageList);
        StringBuilder messages = new StringBuilder();
        for (Message message : messageList) {
            messages
                    .append("-------\nAuthor: ")
                    .append(message.getAuthor().getAsTag())
                    .append("\n")
                    .append(message.getEmbeds().size())
                    .append(" embeds\n")
                    .append(message.getAttachments().size())
                    .append(" attachments.\nCreated at ")
                    .append(new Date(message.getTimeCreated().toInstant().toEpochMilli()).toString())
                    .append(message.isEdited() ? ("\nEdited at " + new Date(Objects.requireNonNull(message.getTimeEdited()).toInstant().toEpochMilli()).toString() + "\n") : "\nNot edited\n")
                    .append("Content:\n")
                    .append(message.getContentRaw())
                    .append("\n");
            message.getAttachments().forEach(attachment -> messages.append("Attachment: ").append(attachment.getUrl()).append("\n"));
            messages.append("\n");
        }
        if (messages.length() > Constants.MESSAGE_SIZE - 7) {
            source.to.sendFile(messages.toString().getBytes(), "MESSAGES DUMP FOR CHANNEL " + channel + " " + new Date().toString()).queue();
        } else {
            source.to.sendMessage("```\n" + messages + "```").queue();
        }
        return CommandListener.SUCCESS;
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(literal("dumpguilds")
                .requires(Utils.isOwner())
                .executes(context -> dumpGuilds(context.getSource()))
        );
        dispatcher.register(literal("dumpchannels")
                .requires(Utils.isOwner())
                .then(argument("guild", guild())
                        .executes(context -> dumpChannels(context.getSource(), context.getArgument("guild", Guild.class)))
                )
        );
        dispatcher.register(literal("dumpmessages")
                .requires(Utils.isOwner())
                .then(argument("channel", channel())
                        .then(argument("count", integer())
                                .executes(context -> dumpMessages(context.getSource(), context.getArgument("channel", TextChannel.class), context.getArgument("count", Integer.class)))
                        )
                )
        );
    }

}
