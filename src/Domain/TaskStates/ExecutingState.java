package Domain.TaskStates;

import Domain.*;

public class ExecutingState implements TaskState {

    @Override
    public Status getStatus() {
        return Status.EXECUTING;
    }


    @Override
    public void finish(Task task, Time endTime) throws IncorrectTaskStatusException, EndTimeBeforeStartTimeException {
        task.setState(new FinishedState());
        task.setEndTime(endTime);

        for (Task nextTask : task.getNextTasks()) {
            nextTask.updateAvailability();
        }
    }

    @Override
    public void fail(Task task, Time endTime) throws EndTimeBeforeStartTimeException {
        task.setState(new FailedState());
        task.setEndTime(endTime);
    }
}
