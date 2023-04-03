package Domain.TaskStates;

import Domain.Status;

public class PendingState implements TaskState {

    @Override
    public Status getStatus() {
        return Status.PENDING;
    }

    @Override
    public String toString() {
        return "pending";
    }

    @Override
    public void updateNextTaskState(Task task) {
        task.setState(new UnavailableState());
    }
}
