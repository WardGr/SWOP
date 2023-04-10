package Domain;

import java.util.List;

/**
 * Immutable wrapper for task man
 */
public class TaskManSystemProxy {
    private final TaskManSystem taskManSystem;

    public TaskManSystemProxy(TaskManSystem tms) {
        taskManSystem = tms;
    }

    public List<String> getProjectNames() {
        return taskManSystem.getProjectNames();
    }

    public Time getSystemTime() {
        return taskManSystem.getSystemTime();
    }
}
