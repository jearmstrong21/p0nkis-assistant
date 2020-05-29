package p0nki.p0nkisassistant.listeners;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import p0nki.commandparser.command.CommandDispatcher;
import p0nki.commandparser.command.CommandSyntaxException;
import p0nki.commandparser.node.CommandNode;
import p0nki.commandparser.node.LiteralCommandNode;
import p0nki.p0nkisassistant.P0nkisAssistant;
import p0nki.p0nkisassistant.commands.*;
import p0nki.p0nkisassistant.data.BotConfig;
import p0nki.p0nkisassistant.exceptions.PrettyException;
import p0nki.p0nkisassistant.utils.*;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandListener extends ListenerAdapter {


    public CommandListener() {

    }

    private CommandDispatcher<CommandSource, CommandResult> dispatcher;

    public static CommandListener INSTANCE = new CommandListener();


    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        dispatcher = new CommandDispatcher<>();
        //REGISTER COMMANDS
        ImageCommand.register(dispatcher);
        UnicodeInfoCommand.register(dispatcher);
        CounterCommand.register(dispatcher);
        DumpInformationCommand.register(dispatcher);
        EchoCommand.register(dispatcher);
        HelpCommands.register(dispatcher);
        MathCommand.register(dispatcher);
        MembersCommands.register(dispatcher);
        RolepollCommand.register(dispatcher);
        SourceInfoCommand.register(dispatcher);
        PingCommand.register(dispatcher);
        SmartAssCommands.register(dispatcher);
        SnowflakeCommand.register(dispatcher);
        ClojureCommands.register(dispatcher);
        streamCommandStarts();
        System.out.println(genericHelp());
    }

    public String genericHelp() {
        Map<String, Integer> map = new HashMap<>();
        dispatcher.getRoot().getChildren().forEach(node -> {
            String category = node.getCategory().isPresent() ? node.getCategory().get() : "none";
            map.put(category, 1 + map.getOrDefault(category, 0));
        });
        return map.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(entry -> entry.getKey() + ": " + entry.getValue()).collect(Collectors.joining("\n"));
    }

    public String specificHelp(Optional<String> category) {
        return dispatcher.generateHelp(category);
    }

    public Set<String> getCategories() {
        return dispatcher.getCategories().stream().map(optional -> optional.orElse("[no category")).collect(Collectors.toSet());
    }

    public String getPrefix(CommandSource source) {
        BotConfig botConfig = BotConfig.get();
        if (source.isGuild()) {
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

    private Stream<String> streamCommandStarts() {
        return dispatcher.getRoot().getChildren().stream().flatMap(node -> {
            if (node instanceof LiteralCommandNode) {
                return ((LiteralCommandNode<CommandSource, CommandResult>) node).getLiterals().stream();
            }
            throw new IllegalArgumentException(node.toString());
        });
    }

    @SuppressWarnings("UnusedReturnValue")
    public CommandResult runCommand(CommandSource source, String command) {
        command = command.trim();
        if (source.user().isBot()) return CommandResult.IGNORE;
        if (streamCommandStarts().noneMatch(command::startsWith))
            return CommandResult.IGNORE;
        try {
            source.message().addReaction(Constants.UNICODE_WAITING).queue();
            CommandResult result = dispatcher.run(source, command);
            if (result == CommandResult.SUCCESS)
                source.message().addReaction(Constants.UNICODE_SUCCESS).queue();
            else if (result == CommandResult.FAILURE) source.message().addReaction(Constants.UNICODE_FAILURE).queue();
            return result;
        } catch (PrettyException pretty) {
            source.channel().sendMessage(new CustomEmbedBuilder()
                    .title(pretty.getTitle())
                    .description(pretty.getDescription())
                    .failure()
                    .source(source)
                    .build()).queue();
        } catch (CommandSyntaxException cse) {
            String message = cse.getMessage();
            if (message.startsWith("Could not parse command: "))
                message = message.replaceFirst("Could not parse command: ", "");
            List<CommandNode<CommandSource, CommandResult>> suggestionsList = dispatcher.descendTree(source, command).nodes;
            String suggestions = suggestionsList.stream().map(CommandNode::toString).collect(Collectors.joining(", "));
            source.channel().sendMessage(new CustomEmbedBuilder()
                    .title("❌ Invalid command syntax ❌")
                    .description(message)
                    .conditional(suggestionsList.size() > 0, b -> b.field("Suggestions", suggestions, false))
                    .conditional(suggestionsList.size() == 0, b -> b.field("No suggestions", "", false))
                    .failure()
                    .source(source)
                    .build()
            ).queue();
        } catch (PermissionException exc) {
            source.channel().sendMessage(new CustomEmbedBuilder()
                    .failure()
                    .source(source)
                    .title("Permission exception")
                    .description("Permission: " + exc.getPermission().getName())
                    .build()).queue();
        } catch (Throwable ugly) {
            Utils.report(ugly, command, source);
            ugly.printStackTrace();
        }
        source.message().addReaction(Constants.UNICODE_FAILURE).queue();
        return CommandResult.FAILURE;
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
