package Domain.TaskStates;

import Domain.IncorrectTaskStatusException;
import Domain.Status;

public class UnavailableState implements TaskState {

    @Override
    public void updateAvailabilityNextTask(Task task, Task nextTask) {
        nextTask.setState(new UnavailableState());
        // If this state is not finished, then the next one should be unavailable
    }

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
        if (safeAddPrevTask(task, previousTask)) {
            task.addPreviousTaskDirectly(previousTask);
            previousTask.addNextTaskDirectly(task);
        } else {
            throw new LoopDependencyGraphException();
        }
        updateAvailability(task);
    }

    public void removePreviousTask(Task task, Task previousTask) {
        task.removePreviousTaskDirectly(previousTask);
        previousTask.removeNextTaskDirectly(task);
        updateAvailability(task);
    }

    public boolean safeAddPrevTask(Task task, Task prevTask){
        return !task.getAllNextTasks().contains(prevTask);
    }

    public boolean safeAddPrevTask(Task task, String prevTaskName){
        for (Task nextTask : task.getAllNextTasks()){
            if (nextTask.getName().equals(prevTaskName)){
                return false;
            }
        }
        return true;
    }

    public boolean safeAddNextTask(Task task, String nextTaskName){
        for (Task prevTask : task.getAllPrevTasks()){
            if (prevTask.getName().equals(nextTaskName)){
                return false;
            }
        }
        return true;
    }

}
