package Domain.TaskStates;

import Domain.IncorrectTaskStatusException;
import Domain.Status;
import Domain.Tuple;

/**
 * Task state class governing the task transitions from the UNAVAILABLE state
 */
public class UnavailableState implements TaskState {

    @Override
    public void updateAvailability(Task task) {
        task.setState(new AvailableState());
        for (Task prevTask : task.getPrevTasks()) {
            prevTask.updateAvailabilityNextTask(task);
            // Set this tasks' state to unavailable if prevTask is not finished
        }
    }

    @Override
    public Status getStatus() {
        return Status.UNAVAILABLE;
    }


    public void addPrevTask(Task task, Task prevTask) throws LoopDependencyGraphException {
        if (canSafelyAddPrevTask(task, prevTask)) {
            task.addPrevTaskDirectly(prevTask);
            prevTask.addNextTaskDirectly(task);
        } else {
            throw new LoopDependencyGraphException();
        }
        updateAvailability(task);
    }

    @Override
    public boolean canSafelyAddPrevTask(Task task, Tuple<String,String> prevTask) {
        for (Task nextTask : task.getAllNextTasks()) {
            if ( prevTask.equals( new Tuple<>(nextTask.getProjectName(), nextTask.getName()) ) ){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if it is safe to add prevTask as a previous task to the given task, without introducing a loop in the dependency graph
     *
     * @param task     Task which to add prevTask to
     * @param prevTask Task to add to task
     * @return true if no loop will be created once prevTask is added as a previous task, false otherwise
     * @throws IncorrectTaskStatusException if task is not AVAILABLE or UNAVAILABLE
     */
    public boolean canSafelyAddPrevTask(Task task, Task prevTask) {
        return !task.getAllNextTasks().contains(prevTask);
    }

}
