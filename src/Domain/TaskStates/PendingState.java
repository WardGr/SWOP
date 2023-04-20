package Domain.TaskStates;

import Domain.*;

/**
 * Task state class governing the task transitions from the PENDING state
 */
public class PendingState implements TaskState {

    @Override
    public void start(Task task, Time startTime, User currentUser, Role role) throws IncorrectTaskStatusException, IncorrectRoleException, UserAlreadyAssignedToTaskException {
        if (!task.getUnfulfilledRoles().contains(role)) {
            throw new IncorrectRoleException("Given role is not required in this task");
        }
        for (Task prevTask : task.getprevTasks()) {
            if (prevTask.getEndTime() == null || prevTask.getEndTime().after(startTime)) {
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
            // als user al op deze task werkte als enige kan het zijn dat de status
            // terug available wordt bij het verwijderen van deze
        }
    }

    @Override
    public Status getStatus() {
        return Status.PENDING;
    }


    @Override
    public void unassignUser(Task task, User user) {
        task.uncommitUser(user);
        if (task.getCommittedUsers().size() == 0) {
            task.setState(new AvailableState());
        }
    }
}
