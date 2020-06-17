package p0nki.easycommandtestbot.data;

import p0nki.easycommandtestbot.lib.data.PerGuildDataCache;
import p0nki.easycommandtestbot.lib.data.ReadWriteData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReminderData extends ReadWriteData {

    public static final PerGuildDataCache<ReminderData> CACHE = new PerGuildDataCache<>("reminder", ReminderData::new);

    private boolean enabled = true;
    private List<Reminder> reminders = new ArrayList<>();

    private ReminderData(String dir) {
        super(dir);
    }

    public List<Reminder> getReminders() {
        return Collections.unmodifiableList(reminders);
    }

    public void addReminder(Reminder reminder) {
        reminders.add(reminder);
        write();
    }

    public void removeReminder(int index) {
        reminders.get(index).remove();
        reminders.remove(index);
        write();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        write();
    }

    public void pushAndClearReminders() {
        boolean[] changed = new boolean[]{false};
        reminders = reminders.stream().filter(reminder -> {
            if (reminder.isDueToSend()) {
                reminder.send();
                changed[0] = true;
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        if (changed[0]) write();
    }

}
