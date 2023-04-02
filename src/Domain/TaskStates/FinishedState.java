package Domain.TaskStates;

import Domain.FinishedStatus;
import Domain.Status;

public class FinishedState implements TaskState {

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public FinishedStatus getFinishedStatus(Task task) {
        int differenceMinutes = task.getTimeSpan().getTimeElapsed().getTotalMinutes();
        int durationMinutes = task.getEstimatedDuration().getTotalMinutes();

        if (differenceMinutes < (1 - task.getAcceptableDeviation()) * durationMinutes) {
            return FinishedStatus.EARLY;
        } else if (differenceMinutes < (1 + task.getAcceptableDeviation()) * durationMinutes) {
            return FinishedStatus.ON_TIME;
        } else {
            return FinishedStatus.DELAYED;
        }
    }

    @Override
    public Status getStatus() {
        return Status.FINISHED;
    }
}
