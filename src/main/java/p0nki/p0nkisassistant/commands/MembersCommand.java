package p0nki.p0nkisassistant.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import p0nki.p0nkisassistant.P0nkisAssistant;
import p0nki.p0nkisassistant.listeners.CommandListener;
import p0nki.p0nkisassistant.utils.CommandSource;
import p0nki.p0nkisassistant.utils.CustomEmbedBuilder;
import p0nki.p0nkisassistant.utils.Utils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static p0nki.p0nkisassistant.utils.BrigadierUtils.literal;

public class MembersCommand {

    public static int onlineMembers(CommandSource source) {
        List<Member> members = source.guild().getMembers();
        source.to.sendMessage(new CustomEmbedBuilder()
                .source(source)
                .success()
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
        return CommandListener.SUCCESS;
    }

    public static int members(CommandSource source) {
        List<Member> members = source.guild().getMembers();
        source.to.sendMessage(new CustomEmbedBuilder()
                .source(source)
                .success()
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
        return CommandListener.SUCCESS;
    }

    public static int offlineMembers(CommandSource source) {
        List<Member> members = source.guild().getMembers();
        source.to.sendMessage(new CustomEmbedBuilder()
                .source(source)
                .success()
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
        return CommandListener.SUCCESS;
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(literal("members")
                .requires(Utils.isFromGuild())
                .then(literal("all").executes(context -> members(context.getSource())))
                .then(literal("online").executes(context -> onlineMembers(context.getSource())))
                .then(literal("offline").executes(context -> offlineMembers(context.getSource())))
        );
    }

}
