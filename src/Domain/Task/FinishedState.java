package Domain.Task;

/**
 * Task state class governing the task transitions from the FINISHED state
 */
public class FinishedState implements TaskState {

    @Override
    public Status getStatus() {
        return Status.FINISHED;
    }

    /**
     * Returns the status of the finished task (early, on time, delayed)
     *
     * @param task Finished Task of which to get the finished status
     * @return Enum with finished status (early, on time, delayed)
     */
    @Override
    public FinishedStatus getFinishedStatus(Task task) {
        if (task.getTimeSpan().getTimeElapsed().getTotalMinutes() < (1 - task.getAcceptableDeviation()) * task.getEstimatedDuration().getTotalMinutes()) {
            return FinishedStatus.EARLY;
        } else if (task.getTimeSpan().getTimeElapsed().getTotalMinutes() > (1 + task.getAcceptableDeviation()) * (task.getEstimatedDuration().getMinute())) {
            return FinishedStatus.DELAYED;
        } else {
            return FinishedStatus.ON_TIME;
        }
    }

    @Override
    public void undoEnd(Task task) {
        task.setState(new ExecutingState());
    }

    @Override
    public void updateAvailabilityNextTask(Task task, Task nextTask) {
    }

    @Override
    public String toString() {
        return getStatus().toString();
    }
}
