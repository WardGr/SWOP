package Domain.TaskStates;

import Domain.Status;

public class UnavailableState implements TaskState {

    @Override
    public void updateNextTaskState(Task task) {
        task.setState(new UnavailableState()); // If this state is unavailable, then the next one should be too
    }

    @Override
    public void updateAvailability(Task task) {
        task.setState(new AvailableState());
        for (Task previousTask : task.getPreviousTasks()) {
            previousTask.getState().updateNextTaskState(task); // Set this tasks' state to unavailable if previousTask is not finished
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
            task.addPreviousTask(previousTask);
            previousTask.addNextTask(task);
        } else {
            throw new LoopDependencyGraphException();
        }
        updateAvailability(task);
    }

    public void removePreviousTask(Task task, Task previousTask) {
        task.removePreviousTask(previousTask);
        previousTask.removeNextTask(task);
        updateAvailability(task);
    }

    public boolean safeAddPrevTask(Task task, Task prevTask){
        return !task.getAllNextTasks().contains(prevTask);
    }

}
