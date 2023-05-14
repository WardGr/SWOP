package Domain.TaskStates;

import Domain.Status;

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

    public boolean canSafelyAddPrevTask(Task task, Task prevTask) {
        return !task.getAllNextTasks().contains(prevTask);
    }

    public boolean canSafelyAddPrevTask(Task task, String prevTaskName) {
        for (Task nextTask : task.getAllNextTasks()) {
            if (nextTask.getName().equals(prevTaskName)) {
                return false;
            }
        }
        return true;
    }

}
