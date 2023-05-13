package Domain.TaskStates;

import Domain.*;

interface TaskState {
    // TODO of task waarbij de status hoort als veld bijhouden?
    default void start(Task task, Time startTime, User currentUser, Role role) throws IncorrectTaskStatusException, IncorrectRoleException, UserAlreadyAssignedToTaskException {
        throw new IncorrectTaskStatusException("Task must be available or pending to be started");
    }

    // TODO: is dit een goeie keuze? gewoon temporary omda het anders ni compileert
    Status getStatus();

    default void updateAvailability(Task task) {}

    default void updateAvailabilityNextTask(Task nextTask) {
        nextTask.setState(new UnavailableState());
        // If this state is not finished, then the next one should be unavailable
    }

    default void addPreviousTask(Task task, Task prevTask) throws IncorrectTaskStatusException, LoopDependencyGraphException {
        throw new IncorrectTaskStatusException("Task is not (un)available");
    }

    default boolean canSafelyAddPrevTask(Task task, Task prevTask) throws IncorrectTaskStatusException {
        throw new IncorrectTaskStatusException("Task is not (un)available");
        // TODO of met false?
    }

    default boolean canSafelyAddPrevTask(Task task, String prevTask) {
        return false;
    }

    default void replaceTask(Task replaces, Task replacementTask) throws IncorrectTaskStatusException {
        throw new IncorrectTaskStatusException("The task to replace is not failed");
    }

    default void unassignUser(Task task, User user) throws IncorrectTaskStatusException {
        throw new IncorrectTaskStatusException("The task is not in the pending state");
    }

    default void finish(Task task, User user, Time endTime) throws IncorrectTaskStatusException, IncorrectUserException, EndTimeBeforeStartTimeException {
        throw new IncorrectTaskStatusException("The task is not in the executing state");
    }

    default void fail(Task task, User user, Time endTime) throws IncorrectTaskStatusException, IncorrectUserException, EndTimeBeforeStartTimeException {
        throw new IncorrectTaskStatusException("The task is not in the executing state");
    }
}
