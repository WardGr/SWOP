package Domain.TaskStates;

import Domain.*;

import java.util.LinkedList;
import java.util.List;
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

    private final List<Task> previousTasks;
    private List<Task> nextTasks;

    private TimeSpan timeSpan;

    private final User user;

    /**
     * Creates a task and initialises its status using the previous tasks
     *
     * @param name                Name of the new task
     * @param description         Description of the new task
     * @param estimatedDuration   Estimated duration of the new task
     * @param acceptableDeviation Acceptable deviation from the duration
     * @param previousTasks       Tasks that must be completed before this task
     * @param user                User assigned to this task
     */
    public Task(
            String name,
            String description,
            Time estimatedDuration,
            double acceptableDeviation,
            List<Task> previousTasks,
            User user
    ) {
        this.name = name;
        this.description = description;

        this.estimatedDuration = estimatedDuration;
        this.acceptableDeviation = acceptableDeviation;

        this.replacementTask = null;
        this.replacesTask = null;

        this.previousTasks = previousTasks;
        this.nextTasks = new LinkedList<>();

        this.user = user;

        boolean available = true;
        for (Task task : previousTasks) {
            task.addNextTask(this);
            if (task.getStatus() != Status.FINISHED) {
                available = false;
            }
        }

        if (available) {
            state = new AvailableState();
        } else {
            state = new UnavailableState();
        }
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
                        "End Time:           " + showEndTime() + "\n\n" +

                        "User:               " + getUser().getUsername() + "\n\n";

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

    /**
     * @return String containing name of replacement task
     */
    private String showReplacementTaskName() {
        if (replacementTask == null) {
            return "No replacement task";
        }
        return getReplacementTask().getName();
    }

    /**
     * @return String containing name of task this task replaces
     */
    private String showReplacesTaskName() {
        if (replacesTask == null) {
            return "Replaces no tasks";
        }
        return getReplacesTask().getName();
    }

    public String getName() {
        return name;
    }

    private String getDescription() {
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
    private Task getReplacesTask() {
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

    User getUser() {
        return user;
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

    private void setNextTasks(List<Task> newNextTasks) {
        this.nextTasks = newNextTasks;
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
    void setStartTime(Time startTime) throws StartTimeBeforeAvailableException {
        for (Task prevTask : getPreviousTasks()){
            if (prevTask.getEndTime().after(startTime)){
                throw new StartTimeBeforeAvailableException();
            }
        }

        TimeSpan timeSpan = getTimeSpan();
        if (timeSpan == null) {
            setTimeSpan(startTime);
        } else {
            timeSpan.setStartTime(startTime);
        }
    }

    /**
     * @return End time if this is set, null otherwise
     */
    private Time getEndTime() {
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

    void addNextTask(Task task) {
        nextTasks.add(task);
    }

    void removeNextTask(Task task) {
        nextTasks.remove(task);
    }

    void addPreviousTask(Task task) {
        previousTasks.add(task);
    }

    void removePreviousTask(Task task) {
        previousTasks.remove(task);
    }

    /**
     * @return Status regarding when this task was finished (early, on time or delayed), based on acceptable deviation and duration
     */
    private FinishedStatus getFinishedStatus() {
        return getState().getFinishedStatus(this);
    }

    // TODO: Alle @throws nog is nakijken
    /**
     * Changes the current task to executing status and sets the start time
     *
     * @param startTime   Time the task will start
     * @param systemTime  Current system time
     * @param currentUser User currently logged in
     * @throws IncorrectUserException       if the user currently logged in is not assigned to the current task
     */
    public void start(Time startTime, Time systemTime, User currentUser)
            throws IncorrectUserException, StartTimeBeforeAvailableException, IncorrectTaskStatusException {
        if (getUser() != currentUser) {
            throw new IncorrectUserException();
        }
        getState().start(this, startTime, systemTime);
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
        if (getUser() != currentUser) {
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
}
