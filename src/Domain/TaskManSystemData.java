package Domain;

import java.util.List;

/**
 * Read-only data class for TaskManSystem
 */
public class TaskManSystemData {
    private final TaskManSystem taskManSystem;

    /**
     * @param tms The TaskManSystem object this data belongs to
     */
    public TaskManSystemData(TaskManSystem tms) {
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
