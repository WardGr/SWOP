package Domain.TaskStates;

import Domain.*;

/**
 * Task state class governing the task transitions from the AVAILABLE state
 */
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
        for (Task prevTask : task.getPrevTasks()) {
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
        for (Task prevTask : task.getPrevTasks()) {
            prevTask.updateAvailabilityNextTask(task);
            // Set this tasks' state to unavailable if prevTask is not finished
        }
    }

    @Override
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
