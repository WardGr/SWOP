package Domain.TaskStates;

import Domain.*;

public class PendingState implements TaskState {

    @Override
    public void start(Task task, Time startTime, User currentUser, Role role) throws IncorrectTaskStatusException, IncorrectRoleException, UserAlreadyAssignedToTaskException {
        if (!task.getRequiredRoles().contains(role)) {
            throw new IncorrectRoleException("Given role is not required in this task");
        }
        if (task.getUsers().contains(currentUser)) {
            throw new UserAlreadyAssignedToTaskException();
        }
        for (Task prevTask : task.getPreviousTasks()) {
            if (prevTask.getEndTime() == null || prevTask.getEndTime().after(startTime)) {
                throw new IncorrectTaskStatusException("Start time is before end time previous task");
            }
        }
        currentUser.assignTask(task, role);

        task.removeRole(role);
        task.commitUser(currentUser, role);

        if (task.getRequiredRoles().size() == 0) {
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
    public String toString() {
        return "pending";
    }

    @Override
    public void unassignUser(Task task, User user) {
        task.addRole(task.getRole(user));
        task.uncommitUser(user);
        if (task.getUsers().size() == 0) {
            task.setState(new AvailableState());
        }
    }
}
