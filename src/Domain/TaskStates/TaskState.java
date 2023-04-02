package Domain.TaskStates;

import Domain.*;

import java.util.LinkedList;
import java.util.List;

interface TaskState {

    default void start(Task task, Time startTime, Time systemTime) throws StartTimeBeforeAvailableException, IncorrectTaskStatusException {
        throw new IncorrectTaskStatusException("Task must be available to be started");
    }

    default List<Status> getNextStatuses(Task task) {
        return new LinkedList<>();
    }

    default void end(Task task, Status newStatus, Time endTime, Time systemTime) throws FailTimeAfterSystemTimeException, EndTimeBeforeStartTimeException, IncorrectTaskStatusException {
        throw new IncorrectTaskStatusException("Task must be executing");
    }

    default Task replaceTask(Task task, String taskName, String description, Time duration, double deviation) throws IncorrectTaskStatusException {
        throw new IncorrectTaskStatusException("The task to replace has not failed");
    }

    // TODO: is dit een goeie keuze? gewoon temporary omda het anders ni compileert
    default Status getStatus() {
        return null;
    }

    default void updateAvailability(Task nextTask) {};

    // TODO: is dit een goeie keuze? Op zich doet ge een status check, maar specifiek voor 1 type maar...
    default boolean isFinished() {
        return false;
    }

    default FinishedStatus getFinishedStatus(Task task) {
        return null;
    }
}
