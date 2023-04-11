package Domain.TaskStates;

import Domain.*;

public class AvailableState implements TaskState {

    @Override
    public Status getStatus() {
        return Status.AVAILABLE;
    }

    @Override
    public void start(Task task, Time startTime, User currentUser, Role role) throws IncorrectTaskStatusException, IncorrectRoleException, UserAlreadyAssignedToTaskException {
        if (!task.getUnfulfilledRoles().contains(role)) {
            throw new IncorrectRoleException("Given role is not required in the task");
        }
        for (Task prevTask : task.getPreviousTasks()) {
            if (prevTask.getEndTime().after(startTime)) {
                throw new IncorrectTaskStatusException("Start time is before end time previous task");
            }
        }
        currentUser.assignTask(task, role);
        task.commitUser(currentUser, role);

        if (task.getUnfulfilledRoles().size() == 0) {
            task.setState(new ExecutingState());
            task.setStartTime(startTime);
        } else {
            task.setState(new PendingState());
        }
    }
    @Override
    public String toString() {
        return "available";
    }

    @Override
    public void updateAvailability(Task task) {
        for (Task previousTask : task.getPreviousTasks()) {
            previousTask.updateAvailabilityNextTask(task);
            // Set this tasks' state to unavailable if previousTask is not finished
        }
    }

    @Override
    public void addPreviousTask(Task task, Task previousTask) throws LoopDependencyGraphException {
        if (canSafelyAddPrevTask(task, previousTask)) {
            task.addPreviousTaskDirectly(previousTask);
            previousTask.addNextTaskDirectly(task);
        } else {
            throw new LoopDependencyGraphException();
        }
        updateAvailability(task);
    }

    @Override
    public void removePreviousTask(Task task, Task previousTask) {
        task.removePreviousTaskDirectly(previousTask);
        previousTask.removeNextTaskDirectly(task);
        updateAvailability(task);
    }

    @Override
    public boolean canSafelyAddPrevTask(Task task, String prevTaskName) {
        for (Task nextTask : task.getAllNextTasks()) {
            if (nextTask.getName().equals(prevTaskName)) {
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
