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
    public void updateAvailabilityNextTask(Task task, Task nextTask) {}
}
