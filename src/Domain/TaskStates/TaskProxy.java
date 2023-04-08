package Domain.TaskStates;

import Domain.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Immutable wrapper for task, with some extra functionality
 */
public class TaskProxy {
    private final Task task;

    public TaskProxy(Task task){
        this.task = task;
    }

    private Task getTask(){
        return task;
    }

    public String getName(){
        return getTask().getName();
    }

    public String getDescription(){
        return getTask().getDescription();
    }

    public Time getEstimatedDuration(){
        return getTask().getEstimatedDuration();
    }
    public double getAcceptableDeviation(){
        return getTask().getAcceptableDeviation();
    }
    public Status getStatus(){
        return getTask().getStatus();
    }

    public String getReplacementTaskName(){
        if (getTask().getReplacementTask() == null){
            return null;
        } else {
            return getTask().getReplacementTask().getName();
        }
    }
    public String getReplacesTaskName(){
        if (getTask().getReplacesTask() == null){
            return null;
        } else {
            return getTask().getReplacesTask().getName();
        }
    }

    public List<String> getPreviousTasksNames(){
        List<String> prevTasksNames = new LinkedList<>();
        for (Task prevTask : getTask().getPreviousTasks()){
            prevTasksNames.add(prevTask.getName());
        }
        return prevTasksNames;
    }
    public List<String> getNextTasksNames(){
        List<String> nextTasksNames = new LinkedList<>();
        for (Task nextTask : getTask().getNextTasks()){
            nextTasksNames.add(nextTask.getName());
        }
        return nextTasksNames;
    }

    // TODO of met timespan werken?
    public Time getStartTime(){
        return getTask().getStartTime();
    }

    public Time getEndTime(){
        return getTask().getEndTime();
    }

    public List<Role> getRequiredRoles(){
        return getTask().getRequiredRoles();
    }

    public Map<String,Role> getUserNamesWithRole(){
        return getTask().getUserNamesWithRole();
    }

    public String getProjectName(){
        return getTask().getProjectName();
    }
}
