package Domain.TaskStates;

import Domain.Status;

public class UnavailableState implements TaskState {

    @Override
    public void updateAvailability(Task task) {
        task.setState(new AvailableState());
        for (Task previousTask : task.getPreviousTasks()) {
            previousTask.updateAvailabilityNextTask(task);
            // Set this tasks' state to unavailable if previousTask is not finished
        }
    }

    @Override
    public Status getStatus() {
        return Status.UNAVAILABLE;
    }

    @Override
    public String toString() {
        return "unavailable";
    }

    public void addPreviousTask(Task task, Task previousTask) throws LoopDependencyGraphException {
        if (canSafelyAddPrevTask(task, previousTask)) {
            task.addPreviousTaskDirectly(previousTask);
            previousTask.addNextTaskDirectly(task);
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
