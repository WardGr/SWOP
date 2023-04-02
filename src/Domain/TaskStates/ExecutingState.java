package Domain.TaskStates;

import Domain.*;

import java.util.LinkedList;
import java.util.List;

public class ExecutingState implements TaskState {

    @Override
    public void end(Task task, Status newStatus, Time endTime, Time systemTime) throws FailTimeAfterSystemTimeException, EndTimeBeforeStartTimeException, IncorrectTaskStatusException {
        if (newStatus == Status.FAILED && systemTime.before(endTime)) {
            throw new FailTimeAfterSystemTimeException();
        }


        Time startTime = task.getStartTime();
        if (startTime == null) {
            throw new IncorrectTaskStatusException("Task has not started yet, despite being in the executing state (this is a bug)");
        }
        if (endTime.before(startTime)){
            throw new EndTimeBeforeStartTimeException();
        }

        task.setEndTime(endTime);

        if (!systemTime.before(endTime)) {
            changeStatus(task, newStatus);
            if (newStatus == Status.FINISHED) {
                for (Task nextTask : task.getNextTasks()) {
                    nextTask.getState().updateAvailability(nextTask); // TODO: is dit cursed?
                }
            }
        }
    }

    @Override
    public Status getStatus() {
        return Status.EXECUTING;
    }

    private void changeStatus(Task task, Status status) {
        switch(status) {
            case FINISHED -> task.setState(new FinishedState());
            case FAILED -> task.setState(new FailedState());
            case EXECUTING -> task.setState(new ExecutingState());
            case AVAILABLE -> task.setState(new AvailableState());
            case UNAVAILABLE -> task.setState(new UnavailableState());
            case PENDING -> task.setState(new PendingState());
        }
    }

    @Override
    public List<Status> getNextStatuses(Task task) {
        List<Status> statuses = new LinkedList<>();
        statuses.add(Status.FAILED);
        statuses.add(Status.FINISHED);
        return statuses;
    }

}
