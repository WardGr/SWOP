package Domain.TaskStates;

import Domain.*;

/**
 * Task state class governing the task transitions from the EXECUTING state
 */
public class ExecutingState implements TaskState {

    @Override
    public Status getStatus() {
        return Status.EXECUTING;
    }


    @Override
    public void finish(Task task, Time endTime) throws EndTimeBeforeStartTimeException {
        task.setEndTime(endTime);
        task.setState(new FinishedState());

        for (Task nextTask : task.getNextTasks()) {
            nextTask.updateAvailability();
        }
    }

    @Override
    public void fail(Task task, Time endTime) throws EndTimeBeforeStartTimeException {
        task.setEndTime(endTime);
        task.setState(new FailedState());
    }

    @Override
    public void stop(Task task, User currentUser) {
        currentUser.endTask();
        task.uncommitUser(currentUser);
        if (task.getCommittedUsers().size() == 0) {
            task.setState(new AvailableState());
        }
    }
}
