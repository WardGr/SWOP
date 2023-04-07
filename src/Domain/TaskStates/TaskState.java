package Domain.TaskStates;

import Domain.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

interface TaskState {
    // TODO of task waarbij de status hoort als veld bijhouden?
    default void start(Task task, Time startTime, User currentUser, Role role) throws IncorrectTaskStatusException {
        throw new IncorrectTaskStatusException("Task must be available or pending to be started");
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

    default void updateAvailability(Task task) {};

    // TODO: is dit een goeie keuze? Op zich doet ge een status check, maar specifiek voor 1 type maar...
    default boolean isFinished() {
        return false;
    }

    // TODO: Hier default meegeven?
    default void updateNextTaskState(Task task) {};

    default void addPreviousTask(Task task, Task prevTask) throws IncorrectTaskStatusException, LoopDependencyGraphException {
        throw new IncorrectTaskStatusException("Task is not (un)available");
    }

    default void removePreviousTask(Task task, Task previousTask) throws IncorrectTaskStatusException {
        throw new IncorrectTaskStatusException("Task is not (un)available");
    }

    default boolean safeAddPrevTask(Task task, Task prevTask) throws IncorrectTaskStatusException {
        throw new IncorrectTaskStatusException("Task is not (un)available");
    }

    default void replaceTask(Task replaces, Task replacementTask) throws IncorrectTaskStatusException {
        throw new IncorrectTaskStatusException("The task to replace is not failed");
    }

    default void stopPending(Task task, User user) throws IncorrectTaskStatusException {
        throw new IncorrectTaskStatusException("The task is not in the pending state");
    }
}
