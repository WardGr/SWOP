package Domain.TaskStates;

import Domain.Status;

public class UnavailableState implements TaskState {

    @Override
    public void updateAvailability(Task task) {
        boolean available = true;
        for (Task previousTask : task.getPreviousTasks()) {
            if (!previousTask.getState().isFinished()) {
                available = false;
            }
        }

        if (available) {
            task.setState(new AvailableState());
        }
    }

    @Override
    public Status getStatus() {
        return Status.UNAVAILABLE;
    }

    @Override
    public String toString() {
        return "unavailable";
    }
}
