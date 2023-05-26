package Domain.Task;

import Domain.DataClasses.Time;
import Domain.User.Role;

import java.util.List;
import java.util.Map;

/**
 * Read-only data class for task, with some extra functionality
 */
public interface TaskData {

    default TaskData getTaskData() {return this;}

    /**
     * @return A string containing the tasks' name
     */
    String getName();

    /**
     * @return A string containing the tasks' description
     */
    String getDescription();

    /**
     * @return A time object depicting the tasks' estimated duration
     */
    Time getEstimatedDuration();

    /**
     * @return The tasks' acceptable deviation from the tasks' estimated duration
     */
    double getAcceptableDeviation();

    /**
     * @return The tasks' internal state (AVAILABLE, UNAVAILABLE, FINISHED, EXECUTING, FAILED, PENDING)
     */
    Status getStatus();

    /**
     * @return The tasks' finished state
     * @throws IncorrectTaskStatusException if the task is not FINISHED
     */
    FinishedStatus getFinishedStatus() throws IncorrectTaskStatusException;

    /**
     * @return A string depicting the name of the task that replaces this task
     */
    String getReplacementTaskName();

    /**
     * @return A string depicting the name of the task that the task replaces
     */
    String getReplacesTaskName();

    /**
     * @return A time object depicting this tasks' start time
     */
    Time getStartTime();

    /**
     * @return A time object depicting this tasks' end time
     */
    Time getEndTime();

    /**
     * @return A list of all roles that are unfulfilled for this task
     */
    List<Role> getUnfulfilledRoles();

    /**
     * @return A map mapping the names, as Strings, of all committed users to their roles assigned for this task
     */
    Map<String, Role> getUserNamesWithRole();

    /**
     * @return A String depicting this tasks' projects' name
     */
    String getProjectName();

    /**
     * @return A list of task data of all previous tasks of this task
     */
    List<TaskData> getPrevTasksData();

    /**
     * @return A list of task data of all next tasks of this task
     */
    List<TaskData> getNextTasksData();


    /**
     * Checks if it is safe to add (the task corresponding to) the given prevTask as previous task to this task
     * without introducing loops in the dependency graph
     *
     * @param prevTask Name of the task corresponding to the task to check
     * @return true if (the task corresponding to) the given prevTask can safely be added as a previous task to
     * this proxy's task without introducing a loop in the dependency graph, false otherwise
     */
    boolean canSafelyAddPrevTask(TaskData prevTask);
}
