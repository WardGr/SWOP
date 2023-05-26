package Domain.Task;

import Domain.DataClasses.EndTimeBeforeStartTimeException;
import Domain.DataClasses.Time;

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
    public void undoStart(Task task) {
        if (task.getCommittedUsers().size() - 1 == 0) {
            task.setState(new AvailableState());
        } else {
            task.setState(new PendingState());
        }
    }

    @Override
    public String toString() {
        return getStatus().toString();
    }
}
