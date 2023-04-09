package Domain.TaskStates;

import Domain.*;

public class PendingState implements TaskState {

    @Override
    public void start(Task task, Time startTime, User currentUser, Role role) throws IncorrectTaskStatusException, IncorrectRoleException {
        if (!task.getRequiredRoles().contains(role)){
            throw new IncorrectRoleException("Given role is not required in this task");
        }
        for (Task prevTask : task.getPreviousTasks()){
            if (prevTask.getEndTime() == null || prevTask.getEndTime().after(startTime)){
                throw new IncorrectTaskStatusException("Start time is before end time previous task");
            }
        }
        currentUser.startTask(task, role);

        task.removeRole(role);
        task.addUser(currentUser, role);

        if (task.getRequiredRoles().size() == 0){
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
    public void updateAvailabilityNextTask(Task task, Task nextTask) {
        nextTask.setState(new UnavailableState());
        // If this state is not finished, then the next one should be unavailable
    }

    @Override
    public void stopPending(Task task, User user){
        task.addRole(task.getRole(user));
        task.removeUser(user);
        if (task.getUsers().size() == 0){
            task.setState(new AvailableState());
        }
    }
}
