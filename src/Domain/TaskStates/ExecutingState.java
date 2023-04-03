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
            case FINISHED -> task.setState(getFinishedState(task));
            case FAILED -> task.setState(new FailedState());
            case EXECUTING -> task.setState(new ExecutingState());
            case AVAILABLE -> task.setState(new AvailableState());
            case UNAVAILABLE -> task.setState(new UnavailableState());
            case PENDING -> task.setState(new PendingState());
        }
    }

    /**
     * @return Status regarding when this task was finished (early, on time or delayed), based on acceptable deviation and duration
     */
    private TaskState getFinishedState(Task task) {
        int differenceMinutes = task.getTimeSpan().getTimeElapsed().getTotalMinutes();
        int durationMinutes = task.getEstimatedDuration().getTotalMinutes();

        if (differenceMinutes < (1 - task.getAcceptableDeviation()) * durationMinutes) {
            return new FinishedEarlyState();
        } else if (differenceMinutes < (1 + task.getAcceptableDeviation()) * durationMinutes) {
            return new FinishedOnTimeState();
        } else {
            return new FinishedDelayedState();
        }
    }

    @Override
    public List<Status> getNextStatuses(Task task) {
        List<Status> statuses = new LinkedList<>();
        statuses.add(Status.FAILED);
        statuses.add(Status.FINISHED);
        return statuses;
    }

    @Override
    public String toString() {
        return "executing";
    }

    @Override
    public void updateNextTaskState(Task task) {
        task.setState(new UnavailableState());
    }

}
