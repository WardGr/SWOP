package Domain.TaskStates;

import Domain.Status;

public class FinishedState implements TaskState {

    @Override
    public Status getStatus() {
        return Status.FINISHED;
    }

    @Override
    public void updateAvailabilityNextTask(Task nextTask) {
    }
}
