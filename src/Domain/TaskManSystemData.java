package Domain;

import java.util.List;

/**
 * Read-only data interface for TaskManSystem
 */
public interface TaskManSystemData {

    default TaskManSystemData getTaskManSystemData(){
        return this;
    }

    /**
     * @return A list of Strings of all project names of all projects belonging
     * to the corresponding TaskManSystem
     */
    List<String> getProjectNames();

    /**
     * @return The system time of the corresponding TaskManSystem
     */
    Time getSystemTime();
}
