package Domain.TaskStates;

import Domain.FinishedStatus;
import Domain.Status;

public class FinishedState implements TaskState {

    @Override
    public boolean isFinished() {
        return true;
    }
    @Override
    public Status getStatus() {
        return Status.FINISHED;
    }

    @Override
    public void updateNextTaskState(Task task) {
        task.setState(new UnavailableState()); // If this state is not finished, then the next one should be unavailable
    }
}
