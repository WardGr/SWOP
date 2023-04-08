package Domain.TaskStates;

import Domain.*;

import java.util.*;

/**
 *  Keeps track of a task, including a list of tasks that should complete before it and which tasks it should come before
 */

// TODO: constructors aanpassen zodat da dynamisch met state shit gebeurt
public class Task {

    private final String name;
    private final String description;
    private final Time estimatedDuration;
    private final double acceptableDeviation;
    private TaskState state;

    private Task replacementTask;
    private Task replacesTask;

    private Set<Task> previousTasks = new HashSet<>();
    private Set<Task> nextTasks = new HashSet<>();

    private TimeSpan timeSpan;

    private List<Role> requiredRoles;

    private Map<User,Role> committedUsers;

    private TaskProxy taskProxy = new TaskProxy(this);

    private List<TaskObserver> observers;

    private Project project;

    /**
     * Creates a task and initialises its status using the previous tasks
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

        this.replacementTask = null;
        this.replacesTask = null;

        this.committedUsers = new HashMap<>();

        this.state = new AvailableState();
    }

    public static void NewActiveTask(String name,
                              String description,
                              Time estimatedDuration,
                              double acceptableDeviation,
                              List<Role> roles,
                              Set<Task> prevTasks,
                              Set<Task> nextTasks,
                              Project project,
                              List<TaskObserver> observers) throws IncorrectTaskStatusException, LoopDependencyGraphException, NonDeveloperRoleException {
        Task task = new Task(name, description, estimatedDuration, acceptableDeviation);

        task.setRequiredRoles(roles);
        task.setProject(project);
        task.setObservers(observers);

        try {
            for (Task prevTask : prevTasks) {
                task.getState().addPreviousTask(task, prevTask);
            }
            for (Task nextTask : nextTasks) {
                nextTask.getState().addPreviousTask(nextTask, task);
            }
        } catch (LoopDependencyGraphException e) {
            task.clearPreviousTasks();
            task.clearNextTasks();
            throw new LoopDependencyGraphException();
        } catch (IncorrectTaskStatusException e) {
            task.clearPreviousTasks();
            task.clearNextTasks();
            throw new IncorrectTaskStatusException("One of the next tasks is not (un)available");
        }

        task.notifyObservers();
    }

    public TaskProxy getTaskData(){
        return taskProxy;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        String info =
                "Task Name:          " + getName() + '\n' +
                        "Description:        " + getDescription() + '\n' +
                        "Estimated Duration: " + getEstimatedDuration().toString() + '\n' +
                        "Accepted Deviation: " + getAcceptableDeviation() + '\n' +
                        "Status:             " + getState().toString() + "\n\n" +

                        "Replacement Task:   " + showReplacementTaskName() + '\n' +
                        "Replaces Task:      " + showReplacesTaskName() + "\n\n" +

                        "Start Time:         " + showStartTime() + '\n' +
                        "End Time:           " + showEndTime() + "\n\n";// +

                        //"User:               " + getUser().getUsername() + "\n\n";

        stringBuilder.append(info);
        stringBuilder.append("Next tasks:\n");
        int i = 1;
        for (Task task : getNextTasks()) {
            stringBuilder.append(i++).append(".").append(task.getName()).append('\n');
        }

        stringBuilder.append("Previous tasks:\n");
        i = 1;
        for (Task task : getPreviousTasks()) {
            stringBuilder.append(i++).append(".").append(task.getName()).append('\n');
        }

        return stringBuilder.toString();
    }

    private void setProject(Project project){
        this.project = project;
    }

    private Project getProject(){
        return project;
    }

    String getProjectName(){
        return getProject().getName();
    }

    /**
     * @return String containing name of replacement task
     */
    private String showReplacementTaskName() {
        if (getReplacementTask() == null) {
            return "No replacement task";
        }
        return getReplacementTask().getName();
    }

    /**
     * @return String containing name of task this task replaces
     */
    private String showReplacesTaskName() {
        if (getReplacesTask() == null) {
            return "Replaces no tasks";
        }
        return getReplacesTask().getName();
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

    public TaskState getState() {
        return state;
    }

    public Status getStatus() {
        return getState().getStatus();
    }

    void setState(TaskState state) {
        this.state = state;
    }

    void setRequiredRoles(List<Role> roles) throws NonDeveloperRoleException {
        for (Role role : roles){
            if (role != Role.SYSADMIN && role != Role.JAVAPROGRAMMER && role != Role.PYTHONPROGRAMMER){
                throw new NonDeveloperRoleException();
            }
        }
        this.requiredRoles = new LinkedList<>(roles);
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

    Set<User> getUsers() {
        return new HashSet<>(committedUsers.keySet());
    }

    private Map<User,Role> getUsersWithRole(){
        return committedUsers;
    }
    
    Map<String,Role> getUserNamesWithRole(){
        Map<String,Role> userNamesWithRole = new HashMap<>();
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
    public List<Task> getNextTasks() {
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
    void setStartTime(Time startTime) {
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
    void setEndTime(Time endTime) {
        timeSpan.setEndTime(endTime);
    }

    /**
     * @return String containing start time of this task, null if start time not set
     */
    private String showStartTime() {
        if (getTimeSpan() == null) {
            return "Task has not started yet";
        }
        return getTimeSpan().showStartTime();
    }

    /**
     * @return String containing end time of this task, null if end time not set
     */
    private String showEndTime() {
        if (getTimeSpan() == null) {
            return "Task has not ended yet";
        }
        return getTimeSpan().showEndTime();
    }

    /**
     * @return (mutable) list of all statuses this task can be changed into by the assigned user
     */
    public List<Status> getNextStatuses() {
        return getState().getNextStatuses(this);
    }


    // TODO: Alle @throws nog is nakijken
    /**
     * Changes the current task to executing status and sets the start time
     *
     * @param startTime   Time the task will start
     * @param currentUser User currently logged in
     * @throws IncorrectUserException       if the user currently logged in is not assigned to the current task
     */
    public void start(Time startTime, User currentUser, Role role)
            throws IncorrectTaskStatusException, UserAlreadyExecutingTaskException {
        if (currentUser.getExecutingTaskData() != null){
            throw new UserAlreadyExecutingTaskException();
        }
        // TODO check if the user has this role and such things
        // TODO check if the role is still needed?
        getState().start(this, startTime, currentUser, role);
    }

    /**
     * Finishes this task, giving it the given status, and updating all tasks that require this task to be completed
     *
     * @param newStatus   New status to assign this task to
     * @param endTime     Time at which this task should end
     * @param systemTime  Current system time
     * @param currentUser User currently logged in
     * @throws IncorrectUserException                 if the currently logged-in user is not assigned to this task
     * @throws IncorrectTaskStatusException     if the task is not currently EXECUTING
     * @throws FailTimeAfterSystemTimeException       if newStatus == FAILED and endTime > systemTime
     */
    public void end(
            Status newStatus,
            Time endTime,
            Time systemTime,
            User currentUser
    )
            throws IncorrectUserException, FailTimeAfterSystemTimeException, EndTimeBeforeStartTimeException, IncorrectTaskStatusException {
        if (!getUsers().contains(currentUser)) {
            throw new IncorrectUserException();
        }
        getState().end(this, newStatus, endTime, systemTime);
    }

    /**
     * Semantically replaces this (failed) task with a task created with the given task details
     *
     * @param taskName    Name of the replacing task
     * @param description Description of the replacing task
     * @param duration    Duration of the replacing task
     * @param deviation   Acceptable deviation of the replacing task
     * @throws IncorrectTaskStatusException if this task hasn't failed yet
     * @pre duration is a valid time-object
     * @post all previous tasks of this task are now assigned before the new task
     * @post all next tasks of this task are now assigned after the new task
     */
    public Task replaceTask(String taskName, String description, Time duration, double deviation) throws IncorrectTaskStatusException {
        return getState().replaceTask(this, taskName, description, duration, deviation);
    }

    // TODO de echte nu
    public static void replaceTask(String taskName, String description, Time duration, double deviation, Task replaces) throws IncorrectTaskStatusException {
        Task replacementTask = new Task(taskName, description, duration, deviation);

        replaces.getState().replaceTask(replaces, replacementTask);

        replaces.notifyObservers();
        replacementTask.notifyObservers();
    }


    Set<Task> getAllPrevTasks(){
        Set<Task> prevTasks = new HashSet<>();
        for (Task prevTask : getPreviousTasks()){
            prevTasks.addAll(prevTask.getAllPrevTasks());
        }
        return prevTasks;
    }

    Set<Task> getAllNextTasks(){
        Set<Task> prevTasks = new HashSet<>();
        for (Task prevTask : getPreviousTasks()){
            prevTasks.addAll(prevTask.getAllPrevTasks());
        }
        return prevTasks;
    }

    void addPreviousTask(Task prevTask) {
        previousTasks.add(prevTask);
    }

    void addNextTask(Task nextTask) {
        nextTasks.add(nextTask);
    }

    void removePreviousTask(Task previousTask) {
        previousTasks.remove(previousTask);
    }

    void removeNextTask(Task nextTask) {
        nextTasks.remove(nextTask);
    }

    private void clearPreviousTasks() throws IncorrectTaskStatusException {
        for (Task prevTask : getPreviousTasks()){
            getState().removePreviousTask(this, prevTask);
        }
    }

    private void clearNextTasks() throws IncorrectTaskStatusException {
        for (Task nextTask : getNextTasks()){
            nextTask.getState().removePreviousTask(nextTask, this);
        }
    }

    void notifyObservers(){
        getProject().update(this);
        for (TaskObserver observer : getObservers()){
            observer.update(this);
        }
        for (User user : getUsers()){
            user.update(this);
        }
    }

    private void setObservers(List<TaskObserver> observers){
        this.observers = new LinkedList<>(observers);
    }

    private List<TaskObserver> getObservers(){
        return new LinkedList<>(observers);
    }

    public void stopPending(User user) throws IncorrectTaskStatusException {
        getState().stopPending(this, user);
    }

    Role getRole(User user){
        return committedUsers.get(user);
    }

    void removeUser(User user){
        committedUsers.remove(user);
    }

    void addRole(Role role){
        requiredRoles.add(role);
    }

    void removeRole(Role role){
        requiredRoles.remove(role);
    }

    void addUser(User user, Role role){
        committedUsers.put(user, role);
    }

    public void finish(User user, Time endTime) throws IncorrectTaskStatusException {
        getState().finish(this, user, endTime);
    }

    public void fail(User user, Time endTime) throws IncorrectTaskStatusException {
        getState().fail(this, user, endTime);
    }

    void updateAvailability(){
        getState().updateAvailability(this);
    }
}
