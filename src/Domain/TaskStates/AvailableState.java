package Domain.TaskStates;

import Domain.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class AvailableState implements TaskState {

    @Override
    public Status getStatus() {
        return Status.AVAILABLE;
    }

    @Override
    public void start(Task task, Time startTime, User currentUser, Role role) throws IncorrectTaskStatusException, IncorrectRoleException {
        if (!task.getRequiredRoles().contains(role)){
            throw new IncorrectRoleException("Given role is not required in the task");
        }
        currentUser.startTask(task, role);

        task.removeRole(role);
        task.addUser(currentUser, role);

        if (task.getRequiredRoles().size() == 0){
            task.setState(new ExecutingState());
            task.setStartTime(startTime);
        } else {
            task.setState(new PendingState());
        }
    }

    @Override
    public List<Status> getNextStatuses(Task task) {
        List<Status> statuses = new LinkedList<>();
        statuses.add(Status.EXECUTING);
        statuses.add(Status.PENDING);
        return statuses;
    }

    @Override
    public String toString() {
        return "available";
    }

    @Override
    public void updateAvailabilityNextTask(Task task, Task nextTask) {
        nextTask.setState(new UnavailableState());
        // If this state is not finished, then the next one should be unavailable
    }

    @Override
    public void updateAvailability(Task task) {
        for (Task previousTask : task.getPreviousTasks()) {
            previousTask.updateAvailabilityNextTask(task);
            // Set this tasks' state to unavailable if previousTask is not finished
        }
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
