package Domain.TaskStates;

import Domain.IncorrectTaskStatusException;
import Domain.Role;
import Domain.Status;
import Domain.Time;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Immutable wrapper for task, with some extra functionality
 */
public class TaskProxy {
    private final Task task;

    /**
     * Creates a new task proxy object with the given task
     *
     * @param task the task of which to create a task proxy
     */
    public TaskProxy(Task task) {
        this.task = task;
    }

    /**
     * @return The task this proxy represents
     */
    private Task getTask() {
        return task;
    }

    /**
     * @return A string containing the tasks' name
     */
    public String getName() {
        return getTask().getName();
    }

    /**
     * @return A string containing the tasks' description
     */
    public String getDescription() {
        return getTask().getDescription();
    }

    /**
     * @return A time object depicting the tasks' estimated duration
     */
    public Time getEstimatedDuration() {
        return getTask().getEstimatedDuration();
    }

    /**
     * @return The tasks' acceptable deviation from the tasks' estimated duration
     */
    public double getAcceptableDeviation() {
        return getTask().getAcceptableDeviation();
    }

    /**
     * @return The tasks' internal state (AVAILABLE, UNAVAILABLE, FINISHED, EXECUTING, FAILED, PENDING)
     */
    public Status getStatus() {
        return getTask().getStatus();
    }

    /**
     * @return The tasks' finished state
     * @throws IncorrectTaskStatusException if the task is not FINISHED
     */
    public FinishedStatus getFinishedStatus() throws IncorrectTaskStatusException {
        return getTask().getFinishedStatus();
    }

    /**
     * @return A string depicting the name of the task that replaces this proxy's task
     */
    public String getReplacementTaskName() {
        if (getTask().getReplacementTask() == null) {
            return null;
        } else {
            return getTask().getReplacementTask().getName();
        }
    }

    /**
     * @return A string depicting the name of the task that this proxy's task replaces
     */
    public String getReplacesTaskName() {
        if (getTask().getReplacesTask() == null) {
            return null;
        } else {
            return getTask().getReplacesTask().getName();
        }
    }

    /**
     * @return A list of the names of all previous tasks of this proxy's task
     */
    public List<String> getPrevTaskNames() {
        List<String> prevTasksNames = new LinkedList<>();
        for (Task prevTask : getTask().getprevTasks()) {
            prevTasksNames.add(prevTask.getName());
        }
        return prevTasksNames;
    }

    /**
     * @return A list of the names of all next tasks of this proxy's task
     */
    public List<String> getNextTasksNames() {
        List<String> nextTasksNames = new LinkedList<>();
        for (Task nextTask : getTask().getNextTasks()) {
            nextTasksNames.add(nextTask.getName());
        }
        return nextTasksNames;
    }

    /**
     * @return A time object depicting this tasks' start time
     */
    public Time getStartTime() {
        return getTask().getStartTime();
    }

    /**
     * @return A time object depicting this tasks' end time
     */
    public Time getEndTime() {
        return getTask().getEndTime();
    }

    /**
     * @return A list of all roles that are unfulfilled for this task
     */
    public List<Role> getUnfulfilledRoles() {
        return getTask().getUnfulfilledRoles();
    }

    /**
     * @return A map mapping the names, as Strings, of all committed users to their roles assigned for this task
     */
    public Map<String, Role> getUserNamesWithRole() {
        return getTask().getUserNamesWithRole();
    }

    /**
     * @return A String depicting this proxy's tasks' projects' name
     */
    public String getProjectName() {
        return getTask().getProjectName();
    }

    /**
     * Checks if it is safe to add (the task corresponding to) the given prevTask as previous task to this proxy's task
     * without introducing loops in the dependency graph
     *
     * @param prevTask Name of the task corresponding to the task to check
     * @return true if (the task corresponding to) the given prevTask can safely be added as a previous task to
     * this proxy's task without introducing a loop in the dependency graph, false otherwise
     */
    public boolean cansafelyAddPrevTask(String prevTask) {
        return getTask().canSafelyAddprevTask(prevTask);
    }
}
