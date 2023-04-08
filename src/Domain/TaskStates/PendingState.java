package Domain.TaskStates;

import Domain.Role;
import Domain.Status;
import Domain.Time;
import Domain.User;

public class PendingState implements TaskState {

    @Override
    public void start(Task task, Time startTime, User currentUser, Role role) {
        if (task.getUsers().contains(currentUser)){
            // if user already works on this task (but with a different role)
            task.addRole(task.getRole(currentUser));
            task.removeUser(currentUser);
        }

        task.removeRole(role);
        task.addUser(currentUser, role);

        if (task.getRequiredRoles().size() == 0){
            task.setState(new ExecutingState());
            task.setStartTime(startTime);
        }
        task.notifyObservers();
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
    public void updateNextTaskState(Task task) {
        task.setState(new UnavailableState());
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
