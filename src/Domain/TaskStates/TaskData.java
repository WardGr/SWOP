package Domain.TaskStates;

import Domain.Role;
import Domain.Status;
import Domain.Time;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//TODO early, ontime etc

/**
 * Immutable wrapper for task, with some extra functionality
 */
public interface TaskData {
    default TaskData getTaskData(){
        return this;
    }

    String getName();

    String getDescription();

    Time getEstimatedDuration();

    double getAcceptableDeviation();

    Status getStatus();

    String getReplacementTaskName();

    String getReplacesTaskName();

    List<String> getPreviousTasksNames();

    List<String> getNextTasksNames();

    Time getStartTime();

    Time getEndTime();

    List<Role> getRequiredRoles();

    Map<String, Role> getUserNamesWithRole();

    String getProjectName();

    boolean canSafelyAddPrevTask(String prevTask);
}
