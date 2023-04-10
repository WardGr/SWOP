package Domain.TaskStates;

import Domain.*;

import java.util.LinkedList;
import java.util.List;

public class ExecutingState implements TaskState {

    @Override
    public Status getStatus() {
        return Status.EXECUTING;
    }

    @Override
    public String toString() {
        return "executing";
    }

    @Override
    public void finish(Task task, User currentUser, Time endTime) throws IncorrectTaskStatusException, IncorrectUserException, EndTimeBeforeStartTimeException {
        task.setState(new FinishedState());
        task.setEndTime(endTime);

        for (User user : task.getUsers()) {
            user.endTask();
            // TODO wel of niet users verwijderen uit de task? misschien wel handig voor een uitbreiding om ze bij te houden
            // task.addRole(task.getRole(user));
            // task.removeUser(user);
        }

        for (Task nextTask : task.getNextTasks()) {
            nextTask.updateAvailability();
        }
    }

    @Override
    public void fail(Task task, User currentUser, Time endTime) throws IncorrectUserException, EndTimeBeforeStartTimeException {
        task.setState(new FailedState());
        task.setEndTime(endTime);

        for (User user : task.getUsers()) {
            user.endTask();
        }
    }

}
