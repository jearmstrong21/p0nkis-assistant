package p0nki.assistant.cogs;

import net.dv8tion.jda.api.entities.Activity;
import p0nki.assistant.data.StatusData;
import p0nki.assistant.lib.data.serializers.ActivityDeserializer;
import p0nki.assistant.lib.data.serializers.ActivitySerializer;
import p0nki.assistant.lib.requirements.RequireOwner;
import p0nki.assistant.lib.task.RepeatedTaskManager;
import p0nki.assistant.lib.utils.CogInitializer;
import p0nki.assistant.lib.utils.DiscordSource;
import p0nki.assistant.lib.utils.DiscordUtils;
import p0nki.assistant.lib.utils.Holder;
import p0nki.easycommand.annotations.*;
import p0nki.easycommand.arguments.Parsers;

import java.util.Objects;

@CommandCog(name = "status", requirements = RequireOwner.class)
public class StatusCog implements Holder, CogInitializer {

    private long nextUpdate = 0;
    private RepeatedTaskManager manager;

    @Override
    public void initialize() {
        manager = new RepeatedTaskManager(iterations -> chooseActivity(), StatusData.VALUE.getPeriod());
        manager.schedule();
    }

    private void chooseActivity() {
        Activity activity;
        if (StatusData.VALUE.getActivities().size() == 0) activity = Activity.playing("[no activity data given]");
        else
            activity = StatusData.VALUE.getActivities().get((int) (Math.random() * StatusData.VALUE.getActivities().size()));
        jda().getPresence().setActivity(activity);
        System.out.println("ACTIVITY " + ActivitySerializer.serializeType(activity) + ": " + activity.getName());
        nextUpdate = System.currentTimeMillis() + StatusData.VALUE.getPeriod();
    }

    @Command(literals = @Literal("status"), names = {"list", "l"})
    public void list(@Source DiscordSource source) {
        DiscordUtils.paginateList(source, StatusData.VALUE.getActivities().size(), 0, index -> {
            Activity activity = StatusData.VALUE.getActivities().get(index);
            return ActivitySerializer.serializeType(activity) + ": " + activity.getName();
        });
    }

    @Command(literals = @Literal("status"), names = {"add", "a"}, requirements = RequireOwner.class)
    public void add(@Source DiscordSource source, @Argument(name = "type") String type, @Argument(name = "content", modifiers = Parsers.GREEDY_STRING) String content) {
        try {
            StatusData.VALUE.addActivity(Activity.of(ActivityDeserializer.deserializeType(type), content));
            source.send("Added status");
        } catch (UnsupportedOperationException e) {
            source.send(e.getMessage());
        }
    }

    @Command(literals = @Literal("status"), names = {"remove", "r"}, requirements = RequireOwner.class)
    public void remove(@Source DiscordSource source, @Argument(name = "index") int index) {
        index--;
        if (index < 0 || index >= StatusData.VALUE.getActivities().size()) {
            source.send("Invalid index");
        } else {
            StatusData.VALUE.removeActivity(index);
            source.send("Removed activity");
        }
    }

    @Command(literals = @Literal("status"), names = {"next", "n"}, requirements = RequireOwner.class)
    public void next(@Source DiscordSource source) {
        chooseActivity();
        source.send("It is done \uD83D\uDC4C");
    }

    @Command(literals = @Literal("status"), names = {"info", "i"})
    public void info(@Source DiscordSource source) {
        source.send(String.format("Current status type: %s\nPeriod: %s\nTime until next change: %s", ActivitySerializer.serializeType(Objects.requireNonNull(jda().getPresence().getActivity())), StatusData.VALUE.getPeriod() + "ms", DiscordUtils.formatTimeDifference(System.currentTimeMillis(), nextUpdate)));
    }

    @Command(literals = @Literal("status"), names = {"period", "p"}, requirements = RequireOwner.class)
    public void period(@Source DiscordSource source, @Argument(name = "period") long period) {
        StatusData.VALUE.setPeriod(period);
        source.send("Period updated");
        manager.cancel();
        manager = new RepeatedTaskManager(iterations -> chooseActivity(), StatusData.VALUE.getPeriod());
        manager.schedule();
    }

}
