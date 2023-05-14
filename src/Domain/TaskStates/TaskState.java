package Domain.TaskStates;

import Domain.*;

/**
 * State class, governing the different transitions of the states of tasks
 */
interface TaskState {
    /**
     * Assigns the given user to the given task with the given role, governing the
     * AVAILABLE -> EXECUTING
     * PENDING -> EXECUTING
     * state transitions
     *
     * @param task        Task to add the currentUser to
     * @param startTime   Time at which this user will start working on the task
     * @param currentUser User to be assigned to the task
     * @param role        Role to use when assigning the user to the task
     * @throws IncorrectTaskStatusException       if the task is not available or pending
     * @throws IncorrectRoleException             if the user does not have the given role, or the given role is not necessary for the given task
     * @throws UserAlreadyAssignedToTaskException if the user is already assigned to this task
     */
    default void start(Task task, Time startTime, User currentUser, Role role) throws IncorrectTaskStatusException, IncorrectRoleException, UserAlreadyAssignedToTaskException {
        throw new IncorrectTaskStatusException("Task must be available or pending to be started");
    }

    /**
     * Returns the finishedStatus of the given task (early, on time, delayed)
     *
     * @param task The task of which to return the finishedStatus
     * @return The status which this task ended with (early, on time, delayed)
     * @throws IncorrectTaskStatusException if the task is not finished
     */
    default FinishedStatus getFinishedStatus(Task task) throws IncorrectTaskStatusException {
        throw new IncorrectTaskStatusException("Task must be finished to get its finished status");
    }

    /**
     * @return Status enum corresponding to the current status
     */
    Status getStatus();

    /**
     * Updates the availability of a task, governs the following transitions:
     * AVAILABLE -> UNAVAILABLE
     * UNAVAILABLE -> AVAILABLE
     * according to the rules of the dependency graph
     *
     * @param task The task of which to update the availability
     * @post if task is AVAILABLE and any previous task is not FINISHED then task is set to UNAVAILABLE
     * else if task is UNAVAILABLE and all previous tasks are FINISHED then task is set to AVAILABLE
     */
    default void updateAvailability(Task task) {}


    /**
     * Updates the next task according to the current tasks' state
     *
     * @param currentTask Current task
     * @param nextTask    Task to update
     * @post if current task is not finished, then nextTask will be UNAVAILABLE
     * else nextTasks' status remains unchanged
     */
    default void updateAvailabilityNextTask(Task currentTask, Task nextTask) {
        nextTask.setState(new UnavailableState());
    }

    /**
     * Adds prevTask as previous task to task
     *
     * @param task     The task to add the previous task to
     * @param prevTask The task to add as previous task
     * @throws IncorrectTaskStatusException if task is not AVAILABLE or UNAVAILABLE
     * @throws LoopDependencyGraphException if adding prevTask as a previous task to task creates a loop in the dependency graph
     */
    default void addPrevTask(Task task, Task prevTask) throws IncorrectTaskStatusException, LoopDependencyGraphException {
        throw new IncorrectTaskStatusException("Task is not (un)available");
    }

    /**
     * Checks if it is safe to add prevTask as a previous task to the given task, without introducing a loop in the dependency graph
     *
     * @param task     Task which to add prevTask to
     * @param prevTask Task to add to task
     * @return true if no loop will be created once prevTask is added as a previous task, false otherwise
     * @throws IncorrectTaskStatusException if task is not AVAILABLE or UNAVAILABLE
     */
    default boolean canSafelyAddPrevTask(Task task, Task prevTask) throws IncorrectTaskStatusException {
        throw new IncorrectTaskStatusException("Task is not (un)available");
    }

    /**
     * Checks if it is safe to add (the task corresponding to) prevTask as a previous task to the given task, without introducing a loop in the dependency graph
     *
     * @param task     Task which to add (the task corresponding to) prevTask to
     * @param prevTask The name of the task corresponding to the task to add as previous task
     * @return true if task is UNAVAILABLE or AVAILABLE and adding (the task corresponding to) prevTask does not introduce a loop in the dependency graph
     */
    default boolean canSafelyAddPrevTask(Task task, String prevTask) {
        return false;
    }

    /**
     * Replaces the given task with another task, governing the
     * FAILED -> AVAILABLE
     * FAILED -> UNAVAILABLE
     * state transitions
     *
     * @param toReplace       The task to replace
     * @param replacementTask The task to replace toReplace with
     * @throws IncorrectTaskStatusException if toReplace is not failed
     */
    default void replaceTask(Task toReplace, Task replacementTask) throws IncorrectTaskStatusException {
        throw new IncorrectTaskStatusException("The task to replace is not failed");
    }

    /**
     * Unassigns a user from a pending task, governing the
     * PENDING -> AVAILABLE
     * state transition
     *
     * @param task The task to unassign the user from
     * @param user The user to unassign from the task
     * @throws IncorrectTaskStatusException if task is not PENDING
     */
    default void unassignUser(Task task, User user) throws IncorrectTaskStatusException {
        throw new IncorrectTaskStatusException("The task is not in the pending state");
    }

    /**
     * Finishes the given task with the given endTime, governing the
     * EXECUTING -> FINISHED
     * state transition
     *
     * @param task    Task to finish
     * @param endTime Time at which this task finished
     * @throws IncorrectTaskStatusException    if the given task is not executing
     * @throws EndTimeBeforeStartTimeException if the given endTime is before the given tasks' start time
     */
    default void finish(Task task, Time endTime) throws IncorrectTaskStatusException, EndTimeBeforeStartTimeException {
        throw new IncorrectTaskStatusException("The task is not in the executing state");
    }

    /**
     * Fails the given task with the given endTime, governing the
     * EXECUTING -> FAILED
     * state transition
     *
     * @param task    The task to fail
     * @param endTime The time at which the given task failed
     * @throws IncorrectTaskStatusException    if the given task is not in the executing state
     * @throws EndTimeBeforeStartTimeException if the given endtime is before the start time of the given task
     */
    default void fail(Task task, Time endTime) throws IncorrectTaskStatusException, EndTimeBeforeStartTimeException {
        throw new IncorrectTaskStatusException("The task is not in the executing state");
    }
}
