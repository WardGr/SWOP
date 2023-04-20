package Domain;

import java.util.List;

/**
 * Immutable proxy for TaskManSystem
 */
public class TaskManSystemProxy {
    private final TaskManSystem taskManSystem;

    /**
     * @param tms The TaskManSystem object this is a proxy of
     */
    public TaskManSystemProxy(TaskManSystem tms) {
        taskManSystem = tms;
    }

    /**
     * @return A list of Strings of all project names of all projects belonging
     * to the corresponding TaskManSystem
     */
    public List<String> getProjectNames() {
        return taskManSystem.getProjectNames();
    }

    /**
     * @return The system time of the corresponding TaskManSystem
     */
    public Time getSystemTime() {
        return taskManSystem.getSystemTime();
    }
}
