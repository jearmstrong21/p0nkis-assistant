package p0nki.p0nkisassistant.commands;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import p0nki.commandparser.command.CommandDispatcher;
import p0nki.p0nkisassistant.P0nkisAssistant;
import p0nki.p0nkisassistant.utils.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MembersCommand {

    public static CommandResult onlineMembers(CommandSource source) {
        List<Member> members = source.guild().getMembers();
        source.channel().sendMessage(new CustomEmbedBuilder()
                .source(source)
                .success()
                .title("Online members, collected by top role")
                .description(members.stream()
                        .filter(member -> member.getOnlineStatus() != OnlineStatus.OFFLINE)
                        .collect(Collectors.groupingBy(member -> {
                            if (member.getRoles().size() == 0) return "null";
                            return member.getRoles().get(0).getId();
                        })).entrySet().stream().sorted(Map.Entry.comparingByKey()).map(entry -> {
                            int n = entry.getValue().size();
                            if (entry.getKey().equals("null")) return "no roles: " + n;
                            Role role = P0nkisAssistant.jda.getRoleById(entry.getKey());
                            if (role != null) {
                                return role.getAsMention() + ": " + n;
                            }
                            return "weird top role, probably a bug or uncached members: " + n;
                        }).collect(Collectors.joining("\n")))
                .build()).queue();
        return CommandResult.SUCCESS;
    }

    public static CommandResult members(CommandSource source) {
        List<Member> members = source.guild().getMembers();
        source.channel().sendMessage(new CustomEmbedBuilder()
                .source(source)
                .success()
                .title("All members, collected by top role")
                .description(members.stream()
                        .collect(Collectors.groupingBy(member -> {
                            if (member.getRoles().size() == 0) return "null";
                            return member.getRoles().get(0).getId();
                        })).entrySet().stream().sorted(Map.Entry.comparingByKey()).map(entry -> {
                            int n = entry.getValue().size();
                            Role role = null;
                            if (!entry.getKey().equals("null")) role = P0nkisAssistant.jda.getRoleById(entry.getKey());
                            if (role != null) {
                                return role.getAsMention() + ": " + n;
                            }
                            return "no top role: " + n;
                        }).collect(Collectors.joining("\n")))
                .build()).queue();
        return CommandResult.SUCCESS;
    }

    public static CommandResult offlineMembers(CommandSource source) {
        List<Member> members = source.guild().getMembers();
        source.channel().sendMessage(new CustomEmbedBuilder()
                .source(source)
                .success()
                .title("Offline members, collected by top role")
                .description(members.stream()
                        .filter(member -> member.getOnlineStatus() == OnlineStatus.OFFLINE)
                        .collect(Collectors.groupingBy(member -> {
                            if (member.getRoles().size() == 0) return "null";
                            return member.getRoles().get(0).getId();
                        })).entrySet().stream().sorted(Map.Entry.comparingByKey()).map(entry -> {
                            int n = entry.getValue().size();
                            if (entry.getKey().equals("null")) return "no roles: " + n;
                            Role role = P0nkisAssistant.jda.getRoleById(entry.getKey());
                            if (role != null) {
                                return role.getAsMention() + ": " + n;
                            }
                            return "weird top role, probably a bug or uncached members: " + n;
                        }).collect(Collectors.joining("\n")))
                .build()).queue();
        return CommandResult.SUCCESS;
    }

    public static void register(CommandDispatcher<CommandSource, CommandResult> dispatcher) {
        dispatcher.register(Nodes.literal("members")
                .requires(Requirements.IN_GUILD)
                .then(Nodes.literal("all")
                        .documentation("Shows top roles of all members")
                        .executes(context -> members(context.source()))
                )
                .then(Nodes.literal("on", "online")
                        .documentation("Shows top roles of online members only")
                        .executes(context -> onlineMembers(context.source()))
                )
                .then(Nodes.literal("off", "offline")
                        .documentation("Shows top roles of offline members only")
                        .executes(context -> offlineMembers(context.source()))
                )
        );
    }

}
