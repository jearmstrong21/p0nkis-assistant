package p0nki.easycommandtestbot.lib.task;

import p0nki.easycommandtestbot.lib.utils.Holder;

import java.util.Timer;
import java.util.TimerTask;

public class RepeatedTaskManager implements Holder {

    private final RepeatedTask repeatedTask;
    private final long period;
    private TimerTask task = null;
    private int iterations;
    private boolean isRunning;

    public RepeatedTaskManager(RepeatedTask repeatedTask, long period) {
        this.period = period;
        iterations = 0;
        isRunning = false;
        this.repeatedTask = repeatedTask;
    }

    private TimerTask task() {
        task = new TimerTask() {
            @Override
            public void run() {
                repeatedTask.run(iterations++);
            }
        };
        return task;
    }

    public void resetIterations() {
        iterations = 0;
    }

    public void assertNotRunning() {
        if (isRunning) throw new IllegalStateException("Task is running");
    }

    public void assertRunning() {
        if (!isRunning) throw new IllegalStateException("Task isn't running");
    }

    public void schedule() {
        assertNotRunning();
        new Timer().schedule(task(), 0, period);
        isRunning = true;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void schedule(long delay) {
        assertNotRunning();
        new Timer().schedule(task(), delay, period);
        isRunning = true;
    }

    public void cancel() {
        assertRunning();
        task.cancel();
        isRunning = false;
    }

    // TODO repeated task

    // TODO some sort of serialization

    // TODO port comicbot

    // TODO tricks

    // TODO lisp eval

    // TODO revisit espressolisp and add math std lib and full lib import for object-return libs

    // TODO create arithmetic parser to familiarize with that sort of thing
    // TODO ^ for that have two routines: parseProgram() which recursively calls parseProgram() and parseMathStack() which uses a stack and parseProgram() ?
    // TODO ^ javascript-like syntax hopefully

}
