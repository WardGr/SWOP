package Domain.Task;

import Domain.DataClasses.EndTimeBeforeStartTimeException;
import Domain.DataClasses.Time;
import Domain.DataClasses.TimeSpan;
import Domain.User.IncorrectUserException;
import Domain.User.Role;
import Domain.User.User;
import Domain.User.UserAlreadyAssignedToTaskException;

import java.util.*;

/**
 * Keeps track of a task, including:
 * a list of tasks that should complete before it
 * a list of tasks that it should be finished before
 * a list of users currently committed to the task, alongside their role
 * a list of roles required to finish the task
 */
public class Task implements TaskData{x@

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

        setState(new AvailableState());
        setRequiredRoles(roles);
        setProjectName(projectName);

        try {
            for (Task prevTask : prevTasks) {
                addPrevTask(prevTask);
            }
            for (Task nextTask : nextTasks) {
                nextTask.addPrevTask(this);
            }
        } catch (LoopDependencyGraphException e) {
            clearPrevTasks();
            clearNextTasks();
            throw new LoopDependencyGraphException();
        } catch (IncorrectTaskStatusException e) {
            clearPrevTasks();
            clearNextTasks();
            throw new IncorrectTaskStatusException("One of the next tasks is not (un)available");
        }
    }



    /**
     * @return A string with the name of the project this task is part of
     */
    @Override
    public String getProjectName() {
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
    @Override
    public String getName() {
        return name;
    }

    /**
     * @return A string containing the description of this task
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * @return This tasks' acceptable deviation from the estimated duration
     */
    @Override
    public double getAcceptableDeviation() {
        return acceptableDeviation;
    }

    /**
     * @return A time object representing this tasks' estimated duration
     */
    @Override
    public Time getEstimatedDuration() {
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
    @Override
    public Status getStatus() {
        return getState().getStatus();
    }

    /**
     * @return Task that replaces this task
     */
    Task getReplacementTask() {
        return replacementTask;
    }

    /**
     * @return A string depicting the name of the task that replaces this task
     */
    @Override
    public String getReplacementTaskName() {
        if (getReplacementTask() == null) {
            return null;
        } else {
            return getReplacementTask().getName();
        }
    }

    @Override
    public List<TaskData> getPrevTasksData() {
        return getPrevTasks().stream().map(Task::getTaskData).toList();
    }

    @Override
    public List<TaskData> getNextTasksData() {
        return getNextTasks().stream().map(Task::getTaskData).toList();
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
    Task getReplacesTask() {
        return replacesTask;
    }

    /**
     * @return A string depicting the name of the task that the task replaces
     */
    @Override
    public String getReplacesTaskName() {
        if (getReplacesTask() == null) {
            return null;
        } else {
            return getReplacesTask().getName();
        }
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
     * Clears the TimeSpan of this Task
     */
    private void clearTimeSpan() {
        this.timeSpan = null;
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
    public FinishedStatus getFinishedStatus() throws IncorrectTaskStatusException {
        return getState().getFinishedStatus(this);
    }

    /**
     * @return A list of all roles that still need to be fulfilled for this task to be able to execute
     */
    public List<Role> getUnfulfilledRoles() {
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
    public Map<String, Role> getUserNamesWithRole() {
        Map<String, Role> userNamesWithRole = new HashMap<>();
        getUsersWithRole().forEach((user, role) -> userNamesWithRole.put(user.getUsername(), role));
        return userNamesWithRole;
    }

    /**
     * @return Mutable list of all tasks that should be completed before this task
     */
    List<Task> getPrevTasks() {
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
    public Time getStartTime() {
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
    public Time getEndTime() {
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
     * Undoes the task starting
     *
     * @param currentUser                    User that started the task
     * @throws IncorrectTaskStatusException  if this task is not executing or pending
     * @throws IncorrectUserException        if the given user is not assigned to this task
     */
    public void undoStart(User currentUser)
            throws IncorrectTaskStatusException, IncorrectUserException {
        if (!getCommittedUsers().contains(currentUser)){
            throw new IncorrectUserException("Given user is not assigned to this task");
        }
        getState().undoStart( this);
        currentUser.endTask();
        uncommitUser(currentUser);
        clearTimeSpan();
    }

    /**
     * Undoes the task finishing or failing
     *
     * @throws IncorrectTaskStatusException         if this task is not finished or failed
     * @throws UserAlreadyAssignedToTaskException   if a user is already assigned to a task
     * @throws IncorrectRoleException               if a user commits to a task with a role that is not required
     */
    public void undoEnd() throws IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        getState().undoEnd(this);
        for (Task nextTask : getNextTasks()){
            nextTask.updateAvailability();
        }
        for (User user : getCommittedUsers()){
            Role role = getUsersWithRole().get(user);
            user.assignTask(this, role);
        }
        try{
            setEndTime(null);
        } catch (EndTimeBeforeStartTimeException e) {
            throw new RuntimeException(e);
        }
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
     */
    private void clearPrevTasks() {
        for (Task prevTask : getPrevTasks()) {
            removePrevTask(prevTask);
        }
    }

    /**
     * Removes all next tasks from this tasks' list of next tasks and sets the involved tasks' states accordingly
     *
     */
    private void clearNextTasks() {
        for (Task nextTask : getNextTasks()) {
            removeNextTask(nextTask);
        }
    }

    /**
     * Clears this task to its initial state
     */
    public void clearTask() {
        clearPrevTasks();
        clearNextTasks();
        clearTimeSpan();
        if (getReplacementTask() != null){
            getReplacementTask().setReplacesTask(null);
            setReplacementTask(null);
        }
        if (getReplacesTask() != null){
            getReplacesTask().setReplacementTask(null);
            setReplacesTask(null);
        }
        updateAvailability();
        for (User user : getCommittedUsers()) {
            user.endTask();
            uncommitUser(user);
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
        }
    }

    /**
     * Updates this tasks' availability, setting it to AVAILABLE or UNAVAILABLE according to its' previous tasks
     *
     */
    void updateAvailability() {
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
    public void addPrevTask(Task prevTask) throws IncorrectTaskStatusException, LoopDependencyGraphException {
        getState().addPrevTask(this, prevTask);
    }

    /**
     * Checks if adding this task as a previous task would cause a loop in the dependency graph of the project this task belongs to
     *
     * @param prevTask Name of the task to test adding
     * @return true if adding (the task corresponding to) prevTask does not introduce a loop in the dependency graph, false otherwise
     */
    public boolean canSafelyAddPrevTask(TaskData prevTask) {
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
        nextTask.addPrevTask(this);
    }

    /**
     * Removes the given previous task from this task, updating the involved tasks' states according to the system rules
     *
     * @param prevTask Task to remove as previous task
     */
    public void removePrevTask(Task prevTask) {
        removePrevTaskDirectly(prevTask);
        prevTask.removeNextTaskDirectly(this);
        updateAvailability();
    }

    /**
     * Removes the given next task from this task, updating the involved tasks' states according to the system rules
     *
     * @param nextTask Task to remove as next task
     * @throws IncorrectTaskStatusException if this task is not AVAILABLE or UNAVAILABLE
     */
    public void removeNextTask(Task nextTask) {
        nextTask.removePrevTask(this);
    }

    @Override
    public String toString(){
        return "(" + getProjectName() + ", " + getName() + ")";
    }
}
