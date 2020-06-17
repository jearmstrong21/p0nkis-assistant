package p0nki.easycommandtestbot.cogs;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import p0nki.easycommand.annotations.*;
import p0nki.easycommand.arguments.Parsers;
import p0nki.easycommandtestbot.data.Reminder;
import p0nki.easycommandtestbot.data.ReminderData;
import p0nki.easycommandtestbot.lib.requirements.RequireGuild;
import p0nki.easycommandtestbot.lib.requirements.RequireManageServer;
import p0nki.easycommandtestbot.lib.requirements.RequireMessageManage;
import p0nki.easycommandtestbot.lib.task.RepeatedTaskManager;
import p0nki.easycommandtestbot.lib.utils.*;

import java.util.Objects;

@CommandCog(name = "reminder", requirements = RequireGuild.class)
public class ReminderCog implements CogInitializer, Holder {

    @Command(literals = @Literal("reminder"), names = {"enable", "e"}, requirements = RequireManageServer.class)
    public void enable(@Source DiscordSource source) {
        ReminderData data = ReminderData.CACHE.of(source);
        if (data.isEnabled()) {
            source.send("Reminders are already enabled");
        } else {
            data.setEnabled(true);
            source.send("Reminders enabled");
        }
    }

    @Command(literals = @Literal("reminder"), names = {"disable", "d"}, requirements = RequireManageServer.class)
    public void disable(@Source DiscordSource source) {
        ReminderData data = ReminderData.CACHE.of(source);
        if (data.isEnabled()) {
            data.setEnabled(false);
            source.send("Reminders disabled");
        } else {
            source.send("Reminders are already disabled");
        }
    }

    @Command(literals = @Literal("reminder"), names = {"add", "a"})
    public void add(@Source DiscordSource source, @Argument(name = "durations") TimeDuration[] durations, @Argument(name = "text", modifiers = Parsers.GREEDY_STRING) String text) {
        ReminderData data = ReminderData.CACHE.of(source);
        if (data.isEnabled()) {
            long totalTime = 0;
            StringBuilder str = new StringBuilder();
            for (TimeDuration duration : durations) {
                totalTime += duration.getMilliseconds();
                str.append(duration.toString()).append(" ");
            }
            data.addReminder(new Reminder(source.channel().getId(), source.user().getId(), text, System.currentTimeMillis(), System.currentTimeMillis() + totalTime));
            source.send("Reminder created due in " + str);
        } else {
            source.send("Reminders are disabled in this guild");
        }
    }

    @Command(literals = @Literal("reminder"), names = {"list", "l"})
    public void list(@Source DiscordSource source) {
        ReminderData data = ReminderData.CACHE.of(source);
        if (data.isEnabled() || source.member().hasPermission(Permission.MESSAGE_MANAGE) || source.member().hasPermission(Permission.MANAGE_SERVER)) {
            DiscordUtils.paginateList(source, data.getReminders().size(), 10, 0, value -> {
                Reminder reminder = data.getReminders().get(value);
                if (reminder.isDueToSend()) return "Due to send, coming soon";
                User user = Objects.requireNonNull(jda().getUserById(reminder.getUser()));
                return user.getAsMention() + " (" + user.getAsTag() + "): " + DiscordUtils.formatTimeDifference(System.currentTimeMillis(), reminder.getSendAt());
            });
        } else {
            source.send("Reminders are disabled in this guild");
        }
    }

    @Command(literals = @Literal("reminder"), names = {"remove", "r"}, requirements = RequireMessageManage.class)
    public void remove(@Source DiscordSource source, @Argument(name = "index") int index) {
        ReminderData data = ReminderData.CACHE.of(source);
        index--;
        if (index >= 0 && index < data.getReminders().size()) {
            data.removeReminder(index);
            source.send("Reminder removed");
        } else {
            source.send("Invalid reminder index. There are only " + data.getReminders().size() + " active reminders in this guild.");
        }
    }

    @Command(literals = @Literal("reminder"), names = {"info", "i"})
    public void info(@Source DiscordSource source, @Argument(name = "index") int index) {
        ReminderData data = ReminderData.CACHE.of(source);
        if (data.isEnabled()) {
            index--;
            if (index >= 0 && index < data.getReminders().size()) {
                source.channel().sendMessage("Reminder " + (index + 1)).embed(data.getReminders().get(index).embed().build()).queue();
            } else {
                source.send("Invalid reminder index. There are only " + data.getReminders().size() + " active remindcers in this guild.");
            }
        }
    }

    @Override
    public void initialize() {
        new RepeatedTaskManager((iterations) -> {
            for (String user : ReminderData.CACHE.getKeys()) {
                ReminderData.CACHE.of(user).pushAndClearReminders();
            }
        }, 1000).schedule();
    }

}
