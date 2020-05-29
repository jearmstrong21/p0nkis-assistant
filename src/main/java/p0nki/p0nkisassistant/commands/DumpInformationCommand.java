package p0nki.p0nkisassistant.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import p0nki.commandparser.argument.IntegerArgumentType;
import p0nki.commandparser.command.CommandDispatcher;
import p0nki.p0nkisassistant.P0nkisAssistant;
import p0nki.p0nkisassistant.arguments.GuildArgumentType;
import p0nki.p0nkisassistant.arguments.TextChannelArgumentType;
import p0nki.p0nkisassistant.utils.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DumpInformationCommand {

    public static CommandResult dumpGuilds(CommandSource source) {
        String guilds = P0nkisAssistant.jda.getGuilds().stream().map(Object::toString).collect(Collectors.joining("\n"));
        if (guilds.length() > Constants.MESSAGE_SIZE - 7) {
            source.channel().sendFile(guilds.getBytes(), "GUILDS DUMP " + new Date().toString()).queue();
        } else {
            source.channel().sendMessage("```\n" + guilds + "```").queue();
        }
        return CommandResult.SUCCESS;
    }

    public static CommandResult dumpChannels(CommandSource source, Guild guild) {
        String channels = guild.getChannels().stream().map(Object::toString).collect(Collectors.joining("\n"));
        if (channels.length() > Constants.MESSAGE_SIZE - 7) {
            source.channel().sendFile(channels.getBytes(), "CHANNELS DUMP FOR GUILD " + guild + " " + new Date().toString()).queue();
        } else {
            source.channel().sendMessage("```\n" + channels + "```").queue();
        }
        return CommandResult.SUCCESS;
    }

    public static CommandResult dumpMessages(CommandSource source, TextChannel channel, int count) {
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
            source.channel().sendFile(messages.toString().getBytes(), "MESSAGES DUMP FOR CHANNEL " + channel + " " + new Date().toString()).queue();
        } else {
            source.channel().sendMessage("```\n" + messages + "```").queue();
        }
        return CommandResult.SUCCESS;
    }

    public static void register(CommandDispatcher<CommandSource, CommandResult> dispatcher) {
        dispatcher.register(Nodes.literal("dumpguilds")
                .requires(Requirements.IS_OWNER)
                .category("dumps")
                .documentation("Dumps guilds")
                .executes(context -> dumpGuilds(context.source()))
        );
        dispatcher.register(Nodes.literal("dumpchannels")
                .requires(Requirements.IS_OWNER)
                .category("dumps")
                .documentation("Dumps channels in guild")
                .then(Nodes.guild("guild")
                        .executes(context -> dumpChannels(context.source(), GuildArgumentType.get(context, "guild"))
                        )
                )
        );
        dispatcher.register(Nodes.literal("dumpmessages")
                .requires(Requirements.IS_OWNER)
                .category("dumps")
                .documentation("Dumps messages in a channel")
                .then(Nodes.textChannel("channel")
                        .then(Nodes.integer("count", 1, 100)
                                .executes(context -> dumpMessages(context.source(), TextChannelArgumentType.get(context, "channel"), IntegerArgumentType.get(context, "count")))
                        )
                )
        );
    }

}
