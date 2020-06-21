package p0nki.assistant.data;

import net.dv8tion.jda.api.entities.Activity;
import p0nki.assistant.lib.data.ReadWriteData;

import java.util.ArrayList;
import java.util.List;

public class StatusData extends ReadWriteData {

    public static final StatusData VALUE = new StatusData();

    private long period = 5 * 60 * 1000; // every 5 minutes
    private List<Activity> activities = new ArrayList<>();

    private StatusData() {
        super(".", "status");
        read();
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
        write();
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void addActivity(Activity activity) {
        activities.add(activity);
        write();
    }

    public void removeActivity(int index) {
        activities.remove(index);
        write();
    }

}
