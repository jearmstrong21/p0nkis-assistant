package p0nki.assistant.lib.task;

import java.util.Timer;
import java.util.TimerTask;

public class DelayedTaskManager {

    private final DelayedTask delayedTask;
    private TimerTask task;
    private boolean isRunning;

    public DelayedTaskManager(DelayedTask delayedTask) {
        this.delayedTask = delayedTask;
    }

    public void assertNotRunning() {
        if (isRunning) throw new IllegalStateException("Task is running");
    }

    public void assertRunning() {
        if (!isRunning) throw new IllegalStateException("Task isn't running");
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void schedule(long delay) {
        assertNotRunning();
        isRunning = true;
        task = new TimerTask() {
            @Override
            public void run() {
                delayedTask.run();
                isRunning = false;
            }
        };
        new Timer().schedule(task, delay);
    }

    public void cancel() {
        assertRunning();
        task.cancel();
    }

}
