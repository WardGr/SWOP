package Domain.TaskStates;

import Domain.*;

import java.util.*;

/**
 * Keeps track of a task, including:
 * a list of tasks that should complete before it
 * a list of tasks that it should be finished before
 * a list of users currently committed to the task, alongside their role
 * a list of roles required to finish the task
 */
public class Task {

    private final String name;
    private final String description;
    private final Time estimatedDuration;
    private final double acceptableDeviation;
    private TaskState state;

    private Task replacementTask;
    private Task replacesTask;

    private final Set<Task> prevTasks;
    private final Set<Task> nextTasks;

    private TimeSpan timeSpan;

    private List<Role> requiredRoles;

    private final Map<User, Role> committedUsers;

    private final TaskData taskData;

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

        this.prevTasks = new HashSet<>();
        this.nextTasks = new HashSet<>();

        this.taskData = new TaskData(this);

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
     * @throws IllegalTaskRolesException    if any of the given roles is not a developer role, or the given set of roles is empty
     */
    public Task(String name,
                String description,
                Time estimatedDuration,
                double acceptableDeviation,
                List<Role> roles,
                Set<Task> prevTasks,
                Set<Task> nextTasks,
                String projectName) throws IncorrectTaskStatusException, LoopDependencyGraphException, IllegalTaskRolesException {
        if (roles.size() == 0) {
            throw new IllegalTaskRolesException("Set of roles should not be empty");
        }

        this.name = name;
        this.description = description;
        this.estimatedDuration = estimatedDuration;
        this.acceptableDeviation = acceptableDeviation;

        this.committedUsers = new HashMap<>();

        this.prevTasks = new HashSet<>();
        this.nextTasks = new HashSet<>();

        this.taskData = new TaskData(this);

        setState(new AvailableState());
        setRequiredRoles(roles);
        setProjectName(projectName);

        try {
            for (Task prevTask : prevTasks) {
                addprevTask(prevTask);
            }
            for (Task nextTask : nextTasks) {
                nextTask.addprevTask(this);
            }
        } catch (LoopDependencyGraphException e) {
            clearprevTasks();
            clearNextTasks();
            throw new LoopDependencyGraphException();
        } catch (IncorrectTaskStatusException e) {
            clearprevTasks();
            clearNextTasks();
            throw new IncorrectTaskStatusException("One of the next tasks is not (un)available");
        }
    }

    /**
     * @return A read-only proxy of this task, containing certain details of the task
     */
    public TaskData getTaskProxy() {
        return taskData;
    }

    /**
     * @return A string with the name of the project this task is part of
     */
    String getProjectName() {
        return projectName;
    }

    /**
     * @param projectName The name of the project to replace the current project with
     */
    void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * @return A string containing the name of this task
     */
    public String getName() {
        return name;
    }

    /**
     * @return A string containing the description of this task
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return This tasks' acceptable deviation from the estimated duration
     */
    double getAcceptableDeviation() {
        return acceptableDeviation;
    }

    /**
     * @return A time object representing this tasks' estimated duration
     */
    Time getEstimatedDuration() {
        return estimatedDuration;
    }

    /**
     * @return This tasks' state
     */
    TaskState getState() {
        return state;
    }

    /**
     * @param state The state to replace this tasks' state with
     */
    void setState(TaskState state) {
        this.state = state;
    }

    /**
     * @return Status enum depicting this tasks' internal state
     */
    public Status getStatus() {
        return getState().getStatus();
    }

    /**
     * @return Task that replaces this task
     */
    public Task getReplacementTask() {
        return replacementTask;
    }

    /**
     * @param replacementTask The task to set as this tasks' replacementTask
     */
    void setReplacementTask(Task replacementTask) {
        this.replacementTask = replacementTask;
    }

    /**
     * @return Task this task replaces
     */
    public Task getReplacesTask() {
        return replacesTask;
    }

    /**
     * @param replacesTask The task this task replaces
     */
    void setReplacesTask(Task replacesTask) {
        this.replacesTask = replacesTask;
    }

    /**
     * @return TimeSpan object that depicts this tasks' time between start and end time
     */
    TimeSpan getTimeSpan() {
        return timeSpan;
    }

    /**
     * @param startTime The start time of this task
     */
    private void setTimeSpan(Time startTime) {
        this.timeSpan = new TimeSpan(startTime);
    }

    /**
     * @return A mutable list of roles this task requires before it can be executed
     */
    List<Role> getRequiredRoles() {
        return new LinkedList<>(requiredRoles);
    }

    /**
     * Sets the roles required for this task to execute
     *
     * @param roles The new required roles
     * @throws IllegalTaskRolesException if one of the given roles is not a developer role
     */
    void setRequiredRoles(List<Role> roles) throws IllegalTaskRolesException {
        for (Role role : roles) {
            if (role != Role.SYSADMIN && role != Role.JAVAPROGRAMMER && role != Role.PYTHONPROGRAMMER) {
                throw new IllegalTaskRolesException("One of the given roles is not a developer role");
            }
        }
        this.requiredRoles = new LinkedList<>(roles);
    }

    /**
     * @return This tasks' finishedstatus (early, on time, delayed)
     * @throws IncorrectTaskStatusException if this task is not Finished
     */
    FinishedStatus getFinishedStatus() throws IncorrectTaskStatusException {
        return getState().getFinishedStatus(this);
    }

    /**
     * @return A list of all roles that still need to be fulfilled for this task to be able to execute
     */
    List<Role> getUnfulfilledRoles() {
        List<Role> unfulfilledRoles = getRequiredRoles();
        for (Role role : getUsersWithRole().values()){
            unfulfilledRoles.remove(role);
        }
        return unfulfilledRoles;
    }

    /**
     * @return A set of all users, as User objects, that are committed to this task
     */
    Set<User> getCommittedUsers() {
        return new HashSet<>(getUsersWithRole().keySet());
    }

    /**
     * @return A map of all committed users, as User objects, mapped to their roles
     */
    private Map<User, Role> getUsersWithRole() {
        return committedUsers;
    }

    /**
     * @return A map of all names of committed users mapped to their roles
     */
    Map<String, Role> getUserNamesWithRole() {
        Map<String, Role> userNamesWithRole = new HashMap<>();
        getUsersWithRole().forEach((user, role) -> userNamesWithRole.put(user.getUsername(), role));
        return userNamesWithRole;
    }

    /**
     * @return Mutable list of all tasks that should be completed before this task
     */
    List<Task> getprevTasks() {
        return new LinkedList<>(prevTasks);
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


    /**
     * Changes the current task to executing status and sets the start time
     *
     * @param startTime   Time the task will start
     * @param currentUser User currently logged in
     * @param role        Role currentuser wants to use to start this task
     * @throws IncorrectTaskStatusException       if this task is not available or pending
     * @throws IncorrectRoleException             if this role is not necessary for the given task OR
     *                                            the given user does not have the given role
     * @throws UserAlreadyAssignedToTaskException if this user is already assigned to this task
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

    /**
     * @return A list containing this task and all tasks that are directly and indirectly dependent on this task as a next task
     */
    Set<Task> getAllNextTasks() {
        Set<Task> nextTasks = new HashSet<>();
        nextTasks.add(this);
        for (Task nextTask : getNextTasks()) {
            nextTasks.addAll(nextTask.getAllNextTasks());
        }
        return nextTasks;
    }

    /**
     * Adds the given previous task to this tasks' list of previous tasks
     *
     * @param prevTask The task to add to this tasks' previoustasks
     */
    void addPrevTaskDirectly(Task prevTask) {
        prevTasks.add(prevTask);
    }

    /**
     * Adds the given next task to this tasks' list of next tasks
     *
     * @param nextTask The task to add to this tasks' nextTasks
     */
    void addNextTaskDirectly(Task nextTask) {
        nextTasks.add(nextTask);
    }

    /**
     * Removes the given previous task from this tasks' list of previous tasks
     *
     * @param prevTask task to remove from this tasks' list of previous tasks
     */
    void removePrevTaskDirectly(Task prevTask) {
        prevTasks.remove(prevTask);
    }

    /**
     * Removes the given next task from this tasks' list of previous tasks
     *
     * @param nextTask task to remove from this tasks' next tasks
     */
    void removeNextTaskDirectly(Task nextTask) {
        nextTasks.remove(nextTask);
    }

    /**
     * Removes all previous tasks from this tasks' list of previous tasks and sets the involved tasks' states accordingly
     *
     * @throws IncorrectTaskStatusException if this task is not AVAILABLE or UNAVAILABLE
     */
    private void clearprevTasks() throws IncorrectTaskStatusException {
        for (Task prevTask : getprevTasks()) {
            getState().removePrevTask(this, prevTask);
        }
    }

    /**
     * Removes all next tasks from this tasks' list of next tasks and sets the involved tasks' states accordingly
     *
     * @throws IncorrectTaskStatusException if the current task is not AVAILABLE or UNAVAILABLE
     */
    private void clearNextTasks() throws IncorrectTaskStatusException {
        for (Task nextTask : getNextTasks()) {
            nextTask.getState().removePrevTask(nextTask, this);
        }
    }

    /**
     * Unassigns a user from a pending task
     *
     * @param user User to unassign from this task
     * @throws IncorrectTaskStatusException if this task is not PENDING
     */
    public void unassignUser(User user) throws IncorrectTaskStatusException {
        getState().unassignUser(this, user);
    }

    /**
     * Removes a committed user from this task
     *
     * @param user user to remove
     */
    void uncommitUser(User user) {
        committedUsers.remove(user);
    }

    /**
     * Adds a committed user to this task
     *
     * @param user user to add
     * @param role role to commit this user with
     */
    void commitUser(User user, Role role) {
        committedUsers.put(user, role);
    }

    /**
     * Finishes this task, giving it the FINISHED status, and updating all tasks that require this task to be completed
     *
     * @param endTime Time at which this task should end
     * @throws IncorrectUserException          if the currently logged-in user is not assigned to this task
     * @throws IncorrectTaskStatusException    if the task is not currently EXECUTING
     * @throws EndTimeBeforeStartTimeException if endTime > this.startTime
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
     * @param endTime Time at which this task should end
     * @throws IncorrectUserException          if the currently logged-in user is not assigned to this task
     * @throws IncorrectTaskStatusException    if the task is not currently EXECUTING
     * @throws EndTimeBeforeStartTimeException if endTime > systemTime
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

    /**
     * Updates this tasks' availability, setting it to AVAILABLE or UNAVAILABLE according to its' previous tasks
     *
     * @throws IncorrectTaskStatusException if the task is not AVAILABLE or UNAVAILABLE
     */
    void updateAvailability() throws IncorrectTaskStatusException {
        getState().updateAvailability(this);
    }

    /**
     * Updates the next tasks' availability according to the current tasks' status, setting it to UNAVAILABLE if the current task is not finished
     *
     * @param nextTask task whose availability to update
     */
    void updateAvailabilityNextTask(Task nextTask) {
        getState().updateAvailabilityNextTask(this, nextTask);
    }

    /**
     * Adds the given previous task as a previous task to this task, updating the involved tasks' states according to the system rules
     *
     * @param prevTask Task to  add as previous task
     * @throws IncorrectTaskStatusException if this task is not AVAILABLE or UNAVAILABLE
     * @throws LoopDependencyGraphException if adding this task would cause a loop in the dependency graph of the project this task belongs to
     */
    public void addprevTask(Task prevTask) throws IncorrectTaskStatusException, LoopDependencyGraphException {
        getState().addPrevTask(this, prevTask);
    }

    /**
     * Checks if adding this task as a previous task would cause a loop in the dependency graph of the project this task belongs to
     *
     * @param prevTask Name of the task to test adding
     * @return true if adding (the task corresponding to) prevTask does not introduce a loop in the dependency graph, false otherwise
     */
    boolean canSafelyAddprevTask(String prevTask) {
        return getState().canSafelyAddPrevTask(this, prevTask);
    }

    /**
     * Adds the given task as a next task to this task, updating the involved tasks' states according to the system rules
     *
     * @param nextTask Task to add as next task to this task
     * @throws IncorrectTaskStatusException if this task is not AVAILABLE or UNAVAILABLE
     * @throws LoopDependencyGraphException if adding this task would cause a loop in the dependency graph
     */
    public void addNextTask(Task nextTask) throws IncorrectTaskStatusException, LoopDependencyGraphException {
        nextTask.addprevTask(this);
    }

    /**
     * Removes the given previous task from this task, updating the involved tasks' states according to the system rules
     *
     * @param prevTask Task to remove as previous task
     * @throws IncorrectTaskStatusException if this task is not AVAILABLE or UNAVAILABLE
     */
    public void removeprevTask(Task prevTask) throws IncorrectTaskStatusException {
        getState().removePrevTask(this, prevTask);
    }

    /**
     * Removes the given next task from this task, updating the involved tasks' states according to the system rules
     *
     * @param nextTask Task to remove as next task
     * @throws IncorrectTaskStatusException if this task is not AVAILABLE or UNAVAILABLE
     */
    public void removeNextTask(Task nextTask) throws IncorrectTaskStatusException {
        nextTask.removeprevTask(this);
    }
}
