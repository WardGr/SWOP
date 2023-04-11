package Domain.TaskStates;

import Domain.*;

public class ExecutingState implements TaskState {

    @Override
    public Status getStatus() {
        return Status.EXECUTING;
    }


    @Override
    public void finish(Task task, User currentUser, Time endTime) throws IncorrectTaskStatusException, EndTimeBeforeStartTimeException {
        task.setState(new FinishedState());
        task.setEndTime(endTime);

        for (User user : task.getCommittedUsers()) {
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
    public void fail(Task task, Time endTime) throws EndTimeBeforeStartTimeException {
        task.setState(new FailedState());
        task.setEndTime(endTime);

        for (User user : task.getCommittedUsers()) {
            user.endTask();
            task.uncommitUser(user);
        }
    }

}
