package Domain.TaskStates;

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

    @Override
    public boolean canSafelyAddPrevTask(Task task, Task prevTask) {
        return !task.getAllNextTasks().contains(prevTask);
    }

}
