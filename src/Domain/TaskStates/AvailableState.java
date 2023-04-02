package Domain.TaskStates;

import Domain.*;

import java.util.LinkedList;
import java.util.List;

class AvailableState implements TaskState {

    @Override
    public Status getStatus() {
        return Status.AVAILABLE;
    }
    @Override
    public void start(Task task, Time startTime, Time systemTime) throws StartTimeBeforeAvailableException {
        task.setStartTime(startTime);
        if (!systemTime.before(startTime)) {
            task.setState(new ExecutingState());
        }
    }

    @Override
    public List<Status> getNextStatuses(Task task) {
        List<Status> statuses = new LinkedList<>();
        statuses.add(Status.EXECUTING);
        statuses.add(Status.PENDING);
        return statuses;
    }

}
