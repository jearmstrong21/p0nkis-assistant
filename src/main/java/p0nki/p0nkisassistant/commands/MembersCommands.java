package p0nki.p0nkisassistant.commands;

import net.dv8tion.jda.api.OnlineStatus;
import p0nki.commandparser.command.CommandDispatcher;
import p0nki.p0nkisassistant.utils.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MembersCommands {

    public static void register(CommandDispatcher<CommandSource, CommandResult> dispatcher) {
        dispatcher.register(Nodes.literal("allmembers")
                .requires(Requirements.IN_GUILD)
                .category("members")
                .documentation("Counts top roles of all members")
                .executes(context -> {
                    Map<String, Integer> roles = new HashMap<>();
                    context.source().guild().getMembers().forEach(member -> {
                        String name = member.getRoles().size() > 0 ? member.getRoles().get(0).getAsMention() : "no top role";
                        roles.put(name, 1 + roles.getOrDefault(name, 0));
                    });
                    context.source().channel().sendMessage(new CustomEmbedBuilder().success().source(context.source()).title("Top roles of all members").description(roles.entrySet().stream().map(entry -> entry.getKey() + ": " + entry.getValue()).collect(Collectors.joining("\n"))).build()).queue();
                    return CommandResult.SUCCESS;
                })
        );
        dispatcher.register(Nodes.literal("onlinemembers")
                .requires(Requirements.IN_GUILD)
                .category("members")
                .documentation("Counts top roles of online members")
                .executes(context -> {
                    Map<String, Integer> roles = new HashMap<>();
                    context.source().guild().getMembers().forEach(member -> {
                        String name = member.getOnlineStatus() == OnlineStatus.ONLINE ? (member.getRoles().size() > 0 ? member.getRoles().get(0).getAsMention() : "no top role") : "offline";
                        roles.put(name, 1 + roles.getOrDefault(name, 0));
                    });
                    context.source().channel().sendMessage(new CustomEmbedBuilder().success().source(context.source()).title("Top roles of online members").description(roles.entrySet().stream().map(entry -> entry.getKey() + ": " + entry.getValue()).collect(Collectors.joining("\n"))).build()).queue();
                    return CommandResult.SUCCESS;
                })
        );
    }

}
