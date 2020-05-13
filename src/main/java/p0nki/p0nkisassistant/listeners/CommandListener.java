package p0nki.p0nkisassistant.listeners;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import p0nki.p0nkisassistant.P0nkisAssistant;
import p0nki.p0nkisassistant.commands.*;
import p0nki.p0nkisassistant.data.BotConfig;
import p0nki.p0nkisassistant.exceptions.PrettyException;
import p0nki.p0nkisassistant.utils.CommandSource;
import p0nki.p0nkisassistant.utils.CustomEmbedBuilder;
import p0nki.p0nkisassistant.utils.Utils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CommandListener extends ListenerAdapter {

    public static final int SUCCESS = 0;
    public static final int IGNORE = 1;
    public static final int FAILURE = 2;

    public CommandListener() {

    }

    private CommandDispatcher<CommandSource> dispatcher;

    public static CommandListener INSTANCE = new CommandListener();

    public String dumpTree() {
        StringBuilder sb = new StringBuilder();
        List<CommandNode<CommandSource>> nodes = new ArrayList<>(dispatcher.getRoot().getChildren());
        List<Integer> depths = nodes.stream().map(o -> 0).collect(Collectors.toList());
        while (nodes.size() > 0) {
            CommandNode<CommandSource> node = nodes.get(0);
            nodes.remove(0);
            int depth = depths.get(0);
            depths.remove(0);

            sb.append("\t".repeat(depth));
            if (node instanceof LiteralCommandNode) {
                sb.append(node.getName());
            } else if (node instanceof ArgumentCommandNode) {
                sb.append(node.getName()).append(": ").append(((ArgumentCommandNode<CommandSource, ?>) node).getType().toString());
            } else {
                sb.append(node.getName()).append(":[bad node type]:").append(node.getClass().toString());
            }
            if (node.getRedirect() != null) sb.append(" -> ").append(node.getRedirect());
            sb.append("\n");
            nodes.addAll(0, node.getChildren());
            depths.addAll(0, IntStream.range(0, node.getChildren().size()).mapToObj(x -> depth + 1).collect(Collectors.toList()));
        }
        return sb.toString();
    }

    public static void waiting(CommandSource source) {
        try {
            source.message.addReaction("\u25b6\ufe0f").queue();
        } catch (InsufficientPermissionException ignored) {

        }
    }

    public static void success(CommandSource source) {
        try {
            source.message.addReaction("\u2705").queue();
        } catch (InsufficientPermissionException ignored) {

        }
    }

    public static void failure(CommandSource source) {
        try {
            source.message.addReaction("\u274c").queue();
        } catch (InsufficientPermissionException ignored) {

        }
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        dispatcher = new CommandDispatcher<>();
//        RolepollCommand.register(dispatcher);
//        ZalgoCommand.register(dispatcher);
//        ExecuteCommand.register(dispatcher);
//        InfoCommand.register(dispatcher);
//        SayCommand.register(dispatcher);
//        ConfigCommand.register(dispatcher);
//        PingCommand.register(dispatcher);
//        TrollCommand.register(dispatcher);
//        DumpDataCommand.register(dispatcher);
//        MessageCountCommand.register(dispatcher);
//        PrefixCommand.register(dispatcher);
//        DeleteCommand.register(dispatcher);
//        NickCommand.register(dispatcher);
//        LoggerCommand.register(dispatcher);
//        StarboardCommand.register(dispatcher);
//        ReactionCommand.register(dispatcher);
//        RepeatStringCommand.register(dispatcher);
//        EchoCommand.register(dispatcher);
//        HelpCommand.register(dispatcher);
        EchoCommand.register(dispatcher);
        MembersCommand.register(dispatcher);
        ImageCommand.register(dispatcher);
        RolepollCommand.register(dispatcher);
        UnicodeInfoCommand.register(dispatcher);
        ExecuteCommand.register(dispatcher);
        DumpTreeCommand.register(dispatcher);
        DumpInformationCommand.register(dispatcher);
        HelpCommand.register(dispatcher);
        System.out.println(dumpTree());
//        commandStarts = dispatcher.getRoot().getChildren().stream().map(CommandNode::getName).collect(Collectors.toList());
//        log(dispatcher.getRoot(), "");
//        dispatcher.getRoot().getChildren().forEach(n -> log(n, ""));
//        System.out.println(String.join("", commandStarts));
    }

//    private void log(CommandNode<CommandSource> node, String indent) {
//        System.out.println(indent + node.getName());
//        node.getChildren().forEach(n -> log(n, indent + "\t"));
//    }

    public String getPrefix(CommandSource source) {
        BotConfig botConfig = BotConfig.get();
        if (source.from instanceof TextChannel) {
            if (botConfig.guildPrefixes.containsKey(source.guild().getId())) {
                return botConfig.guildPrefixes.get(source.guild().getId());
            }
        }
        return botConfig.basePrefix;
    }

    public String stripPrefix(CommandSource source, String msg) {
        BotConfig botConfig = BotConfig.get();
        List<String> possiblePrefixes = new ArrayList<>(List.of(
                "<@" + P0nkisAssistant.jda.getSelfUser().getId() + ">",
                "<@!" + P0nkisAssistant.jda.getSelfUser().getId() + ">",
                getPrefix(source)
        ));
        possiblePrefixes.add(botConfig.basePrefix);
        for (String str : possiblePrefixes) {
            if (msg.startsWith(str)) {
                return msg.substring(str.length());
            }
        }
        return null;
    }

    @SuppressWarnings("UnusedReturnValue")
    public int runCommand(CommandSource source, String command) {
        command = command.trim();
        if (dispatcher.getRoot().getChildren().stream().map(CommandNode::getName).noneMatch(command::startsWith))
            return CommandListener.IGNORE;
        try {
            waiting(source);
            int result = dispatcher.execute(command, source);
            if (result == SUCCESS) success(source);
            else if (result == FAILURE) failure(source);
            return result;
        } catch (PrettyException pretty) {
            source.to.sendMessage(new CustomEmbedBuilder()
                    .title(pretty.getTitle())
                    .description(pretty.getDescription())
                    .failure()
                    .source(source)
                    .build()).queue();
        } catch (CommandSyntaxException cse) {
            String message = cse.getMessage();
            if (message.startsWith("Could not parse command: "))
                message = message.replaceFirst("Could not parse command: ", "");
            List<Suggestion> suggestionList = dispatcher.getCompletionSuggestions(dispatcher.parse(command + " ", source)).join().getList();
            String suggestions = suggestionList.stream().map(Suggestion::getText).collect(Collectors.joining(", "));
            source.to.sendMessage(new CustomEmbedBuilder()
                    .title("❌ Invalid command syntax ❌")
                    .description(message)
                    .conditional(suggestionList.size() > 0, b -> b.field("Suggestions", suggestions, false))
                    .conditional(suggestionList.size() == 0, b -> b.field("No suggestions", "", false))
                    .failure()
                    .source(source)
                    .build()
            ).queue();
        } catch (PermissionException exc) {
            source.to.sendMessage(new CustomEmbedBuilder()
                    .failure()
                    .source(source)
                    .title("Permission exception")
                    .description("Permission: " + exc.getPermission().getName())
                    .build()).queue();
        } catch (Throwable ugly) {
            Utils.report(ugly, command, source);
            ugly.printStackTrace();
        }
        failure(source);
        return FAILURE;
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (FunListener.INSTANCE.isHMM(CommandSource.of(event), event.getMessage().getContentRaw())) return;
        String msg = event.getMessage().getContentRaw();
        CommandSource source = CommandSource.of(event);
        String command = stripPrefix(source, msg);
        if (command != null) {
            runCommand(source, command);
        }
    }
}
