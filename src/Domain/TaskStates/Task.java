package Domain.TaskStates;

import Domain.*;

import java.util.*;

/**
 * Keeps track of a task, including a list of tasks that should complete before it and which tasks it should come before
 */
public class Task {

    private final String name;
    private final String description;
    private final Time estimatedDuration;
    private final double acceptableDeviation;
    private TaskState state;

    private Task replacementTask;
    private Task replacesTask;

    private Set<Task> previousTasks;
    private Set<Task> nextTasks;

    private TimeSpan timeSpan;

    private List<Role> requiredRoles;

    private Map<User, Role> committedUsers;

    private TaskProxy taskProxy;

    private String projectName;

    /**
     * Creates a task and initialises its status as available (no previous or next tasks)
     *
     * @param name                Name of the new task
     * @param description         Description of the new task
     * @param estimatedDuration   Estimated duration of the new task
     * @param acceptableDeviation Acceptable deviation from the duration
     */
    public Task(
            String name,
            String description,
            Time estimatedDuration,
            double acceptableDeviation
    ) {
        this.name = name;
        this.description = description;

        this.estimatedDuration = estimatedDuration;
        this.acceptableDeviation = acceptableDeviation;

        this.committedUsers = new HashMap<>();

        this.previousTasks = new HashSet<>();
        this.nextTasks = new HashSet<>();

        this.taskProxy = new TaskProxy(this);

        this.state = new AvailableState();
    }


    /**
     * Creates a task and initialises its status depending on the given previous and next tasks
     *
     * @param name                Name of the new task
     * @param description         Description of the new task
     * @param estimatedDuration   Estimated duration of the new task
     * @param acceptableDeviation Acceptable deviation of the new task
     * @param roles               Set of required roles for this task
     * @param prevTasks           List of tasks that must be completed before this task
     * @param nextTasks           List of tasks that this task must be completed before
     * @param projectName         Project this task belongs to
     * @throws IncorrectTaskStatusException if a next task is not available nor unavailable (e.g. it is executing)
     * @throws LoopDependencyGraphException if adding this task results in a loop in the dependency graph of tasks
     * @throws NonDeveloperRoleException    // TODO: wanneer wordt deze gegooit??
     */
    public Task(String name,
                String description,
                Time estimatedDuration,
                double acceptableDeviation,
                List<Role> roles,
                Set<Task> prevTasks,
                Set<Task> nextTasks,
                String projectName) throws IncorrectTaskStatusException, LoopDependencyGraphException, NonDeveloperRoleException {
        // TODO check if roles not empty!

        this.name = name;
        this.description = description;
        this.estimatedDuration = estimatedDuration;
        this.acceptableDeviation = acceptableDeviation;

        this.committedUsers = new HashMap<>();

        this.previousTasks = new HashSet<>();
        this.nextTasks = new HashSet<>();

        this.taskProxy = new TaskProxy(this);

        setState(new AvailableState());
        setRequiredRoles(roles);
        setProjectName(projectName);

        try {
            for (Task prevTask : prevTasks) {
                addPreviousTask(prevTask);
            }
            for (Task nextTask : nextTasks) {
                nextTask.addPreviousTask(this);
            }
        } catch (LoopDependencyGraphException e) {
            clearPreviousTasks();
            clearNextTasks();
            throw new LoopDependencyGraphException();
        } catch (IncorrectTaskStatusException e) {
            clearPreviousTasks();
            clearNextTasks();
            throw new IncorrectTaskStatusException("One of the next tasks is not (un)available");
        }
    }

    public TaskProxy getTaskProxy() {
        return taskProxy;
    }

    String getProjectName() {
        return projectName;
    }

    void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    double getAcceptableDeviation() {
        return acceptableDeviation;
    }

    Time getEstimatedDuration() {
        return estimatedDuration;
    }

    TaskState getState() {
        return state;
    }

    void setState(TaskState state) {
        this.state = state;
    }

    public Status getStatus() {
        return getState().getStatus();
    }

    /**
     * @return Task that replaces this task
     */
    public Task getReplacementTask() {
        return replacementTask;
    }

    void setReplacementTask(Task replacementTask) {
        this.replacementTask = replacementTask;
    }

    /**
     * @return Task this task replaces
     */
    public Task getReplacesTask() {
        return replacesTask;
    }

    void setReplacesTask(Task replacesTask) {
        this.replacesTask = replacesTask;
    }

    TimeSpan getTimeSpan() {
        return timeSpan;
    }

    private void setTimeSpan(Time startTime) {
        this.timeSpan = new TimeSpan(startTime);
    }

    List<Role> getRequiredRoles() {
        return new LinkedList<>(requiredRoles);
    }

    FinishedStatus getFinishedStatus() throws IncorrectTaskStatusException {
        return getState().getFinishedStatus(this);
    }

    List<Role> getUnfulfilledRoles() {
        List<Role> unfulfilledRoles = getRequiredRoles();
        unfulfilledRoles.removeAll(getUsersWithRole().values());
        return unfulfilledRoles;
    }

    void setRequiredRoles(List<Role> roles) throws NonDeveloperRoleException {
        for (Role role : roles) {
            if (role != Role.SYSADMIN && role != Role.JAVAPROGRAMMER && role != Role.PYTHONPROGRAMMER) {
                throw new NonDeveloperRoleException();
            }
        }
        this.requiredRoles = new LinkedList<>(roles);
    }

    Set<User> getCommittedUsers() {
        return new HashSet<>(getUsersWithRole().keySet());
    }

    private Map<User, Role> getUsersWithRole() {
        return committedUsers;
    }

    Map<String, Role> getUserNamesWithRole() {
        Map<String, Role> userNamesWithRole = new HashMap<>();
        getUsersWithRole().forEach((user, role) -> userNamesWithRole.put(user.getUsername(), role));
        return userNamesWithRole;
    }

    /**
     * @return Mutable list of all tasks that should be completed before this task
     */
    List<Task> getPreviousTasks() {
        return new LinkedList<>(previousTasks);
    }

    /**
     * @return Mutable list of all tasks that this task should be completed before
     */
    List<Task> getNextTasks() {
        return new LinkedList<>(nextTasks);
    }

    /**
     * @return Start time if this is set, null otherwise
     */
    Time getStartTime() {
        if (getTimeSpan() == null) {
            return null;
        }
        return getTimeSpan().getStartTime();
    }

    /**
     * Sets the start time on the current timeSpan
     *
     * @param startTime New start time
     */
    void setStartTime(Time startTime) throws IncorrectTaskStatusException {
        if (getTimeSpan() != null) {
            throw new IncorrectTaskStatusException("Starttime already given on an available/pending task");
        }
        setTimeSpan(startTime);
    }

    /**
     * @return End time if this is set, null otherwise
     */
    Time getEndTime() {
        if (getTimeSpan() == null) {
            return null;
        }
        return getTimeSpan().getEndTime();
    }

    /**
     * Sets the end time on the current timeSpan
     *
     * @param endTime New end time
     */
    void setEndTime(Time endTime) throws EndTimeBeforeStartTimeException {
        getTimeSpan().setEndTime(endTime);
    }


    // TODO: Alle @throws nog is nakijken

    /**
     * Changes the current task to executing status and sets the start time
     *
     * @param startTime   Time the task will start
     * @param currentUser User currently logged in
     * @throws IncorrectUserException if the user currently logged in is not assigned to the current task
     */
    public void start(Time startTime, User currentUser, Role role)
            throws IncorrectTaskStatusException, IncorrectRoleException, UserAlreadyAssignedToTaskException {
        getState().start(this, startTime, currentUser, role);
    }

    /**
     * Semantically replaces this (failed) task with a task created with the given task details
     *
     * @param replacement The task to replace this task with
     * @throws IncorrectTaskStatusException if this task hasn't failed yet
     * @pre duration is a valid time-object
     * @post all previous tasks of this task are now assigned before the new task
     * @post all next tasks of this task are now assigned after the new task
     */
    public void replaceTask(Task replacement) throws IncorrectTaskStatusException {
        getState().replaceTask(this, replacement);
    }

    Set<Task> getAllNextTasks() {
        Set<Task> nextTasks = new HashSet<>();
        nextTasks.add(this);
        for (Task nextTask : getNextTasks()) {
            nextTasks.addAll(nextTask.getAllNextTasks());
        }
        return nextTasks;
    }

    void addPreviousTaskDirectly(Task prevTask) {
        previousTasks.add(prevTask);
    }

    void addNextTaskDirectly(Task nextTask) {
        nextTasks.add(nextTask);
    }

    void removePreviousTaskDirectly(Task previousTask) {
        previousTasks.remove(previousTask);
    }

    void removeNextTaskDirectly(Task nextTask) {
        nextTasks.remove(nextTask);
    }

    private void clearPreviousTasks() throws IncorrectTaskStatusException {
        for (Task prevTask : getPreviousTasks()) {
            getState().removePreviousTask(this, prevTask);
        }
    }

    private void clearNextTasks() throws IncorrectTaskStatusException {
        for (Task nextTask : getNextTasks()) {
            nextTask.getState().removePreviousTask(nextTask, this);
        }
    }

    public void unassignUser(User user) throws IncorrectTaskStatusException {
        getState().unassignUser(this, user);
    }


    void uncommitUser(User user) {
        committedUsers.remove(user);
    }


    void commitUser(User user, Role role) {
        committedUsers.put(user, role);
    }

    /**
     * Finishes this task, giving it the FINISHED status, and updating all tasks that require this task to be completed
     *
     * @param endTime                           Time at which this task should end
     * @throws IncorrectUserException           if the currently logged-in user is not assigned to this task
     * @throws IncorrectTaskStatusException     if the task is not currently EXECUTING
     * @throws EndTimeBeforeStartTimeException  if endTime > systemTime
     */
    public void finish(User currentUser, Time endTime) throws IncorrectTaskStatusException, IncorrectUserException, EndTimeBeforeStartTimeException {
        if (!getCommittedUsers().contains(currentUser)) {
            throw new IncorrectUserException("This user is not assigned to the current task");
        }
        getState().finish(this, endTime);

        for (User user : getCommittedUsers()) {
            user.endTask();
            uncommitUser(user);
        }
    }


    /**
     * Finishes this task, giving it the FAILED status, and updating all tasks that require this task to be completed
     *
     * @param endTime                           Time at which this task should end
     * @throws IncorrectUserException           if the currently logged-in user is not assigned to this task
     * @throws IncorrectTaskStatusException     if the task is not currently EXECUTING
     * @throws EndTimeBeforeStartTimeException  if endTime > systemTime
     */
    public void fail(User currentUser, Time endTime) throws IncorrectTaskStatusException, IncorrectUserException, EndTimeBeforeStartTimeException {
        if (!getCommittedUsers().contains(currentUser)) {
            throw new IncorrectUserException("This user is not assigned to the current task");
        }
        getState().fail(this, endTime);
        for (User user : getCommittedUsers()) {
            user.endTask();
            uncommitUser(user);
        }
    }

    void updateAvailability() throws IncorrectTaskStatusException {
        getState().updateAvailability(this);
    }

    void updateAvailabilityNextTask(Task nextTask) {
        getState().updateAvailabilityNextTask(nextTask);
    }

    public void addPreviousTask(Task prevTask) throws IncorrectTaskStatusException, LoopDependencyGraphException {
        getState().addPreviousTask(this, prevTask);
    }

    boolean canSafelyAddPrevTask(String prevTask) {
        return getState().canSafelyAddPrevTask(this, prevTask);
    }

    public void addNextTask(Task nextTask) throws IncorrectTaskStatusException, LoopDependencyGraphException {
        nextTask.addPreviousTask(this);
    }

    public void removePreviousTask(Task prevTask) throws IncorrectTaskStatusException {
        getState().removePreviousTask(this, prevTask);
    }

    public void removeNextTask(Task nextTask) throws IncorrectTaskStatusException {
        nextTask.removePreviousTask(this);
    }
}
