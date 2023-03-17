import java.util.LinkedList;
import java.util.List;

// TODO: DOE DIT ASJEBLIEFT MET POLYMORFISME OP STATUS OFZO, SUPER VEEL STATUS CHECKS EN DE CLASS IS GIGANTISCH
public class Task {

  private String name;
  private String description;
  private Time estimatedDuration;
  private double acceptableDeviation;
  private Status status;

  private Task replacementTask;
  private Task replacesTask;

  private List<Task> previousTasks;
  private List<Task> nextTasks;

  private TimeSpan timeSpan;

  private User user;

  /**
   * Creates a task and initialises its status using the previous tasks
   *
   * @param name Name of the new task
   * @param description Description of the new task
   * @param estimatedDuration Estimated duration of the new task
   * @param acceptableDeviation Acceptable deviation from the duration
   * @param previousTasks Tasks that must be completed before this task
   * @param user User assigned to this task
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
      status = Status.AVAILABLE;
    }
    else {
      status = Status.UNAVAILABLE;
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
                    "Status:             " + getStatus().toString() + (getStatus() == Status.FINISHED ? (", " + getFinishedStatus()) : "") + "\n\n" +

                    "Replacement Task:   " + showReplacementTaskName() + '\n' +
                    "Replaces Task:      " + showReplacesTaskName() + "\n\n" +

                    "Start Time:         " + showStartTime() + '\n' +
                    "End Time:           " + showEndTime() + "\n\n" +

                    "User:               " + user.getUsername() + "\n\n";

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

  private double getAcceptableDeviation() {
    return acceptableDeviation;
  }

  private Time getEstimatedDuration() {
    return estimatedDuration;
  }

  public Status getStatus() {
    return status;
  }

  /**
   * @return Task that replaces this task
   */
  public Task getReplacementTask() {
    return replacementTask;
  }

  /**
   * @return Task this task replaces
   */
  private Task getReplacesTask() {
    return replacesTask;
  }

  private TimeSpan getTimeSpan() {
    return timeSpan;
  }

  private User getUser() {
    return user;
  }


  /**
   * @return Mutable list of all tasks that should be completed before this task
   */
  private List<Task> getPreviousTasks() {
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
  private Time getStartTime() {
    if (getTimeSpan() == null) {
      return null;
    }
    return getTimeSpan().getStartTime();
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
    List<Status> statuses = new LinkedList<>();
    switch (getStatus()) {
      case AVAILABLE -> {
        statuses.add(Status.EXECUTING);
        return statuses;
      }
      case EXECUTING -> {
        statuses.add(Status.FINISHED);
        statuses.add(Status.FAILED);
        return statuses;
      }
    }
    return statuses;
  }

  private void setNextTasks(List<Task> newNextTasks) {
    this.nextTasks = newNextTasks;
  }

  private void setReplacesTask(Task replacesTask) {
    this.replacesTask = replacesTask;
  }

  private void setTimeSpan(Time startTime) {
    this.timeSpan = new TimeSpan(startTime);
  }

  private void setReplacementTask(Task replacementTask) {
    this.replacementTask = replacementTask;
  }

  private void setStatus(Status status) {
    this.status = status;
  }

  /**
   * Sets the start time on the current timeSpan
   * @param startTime New start time
   */
  private void setStartTime(Time startTime) {
    TimeSpan timeSpan = getTimeSpan();
    if (timeSpan == null) {
      setTimeSpan(startTime);
    }
    else {
      timeSpan.setStartTime(startTime);
    }
  }



  /**
   * Sets the end time on the current timeSpan
   * @param endTime New end time
   */
  private void setEndTime(Time endTime) throws IncorrectTaskStatusException {
    TimeSpan timeSpan = getTimeSpan();
    // als het systeem al Executing is moet er eigenlijk al een starttime zijn en dus een TimeSpan
    if (timeSpan == null) {
      throw new IncorrectTaskStatusException("");
    }
    timeSpan.setEndTime(endTime);
  }

  private void addNextTask(Task task) {
    nextTasks.add(task);
  }

  private void removeNextTask(Task task) {
    nextTasks.remove(task);
  }

  private void addPreviousTask(Task task) {
    previousTasks.add(task);
  }

  private void removePreviousTask(Task task) {
    previousTasks.remove(task);
  }

  /**
   * @return Status regarding when this task was finished (early, on time or delayed), based on acceptable deviation and duration
   */
  private FinishedStatus getFinishedStatus() {
    if (getStatus() != Status.FINISHED) {
      return null;
    }

    int differenceMinutes = getTimeSpan().getTimeElapsed().getTotalMinutes();
    int durationMinutes = getEstimatedDuration().getTotalMinutes();

    if (differenceMinutes < (1 - getAcceptableDeviation()) * durationMinutes) {
      return FinishedStatus.EARLY;
    }
    else if (differenceMinutes < (1 + getAcceptableDeviation()) * durationMinutes) {
      return FinishedStatus.ON_TIME;
    }
    else {
      return FinishedStatus.DELAYED;
    }
  }

  /**
   * Changes the current task to executing status and sets the start time
   *
   * @param startTime Time the task will start
   * @param systemTime Current system time
   * @param currentUser User currently logged in
   * @throws IncorrectUserException if the user currently logged in is not assigned to the current task
   * @throws IncorrectTaskStatusException if the task is not available
   */
  public void start(Time startTime, Time systemTime, User currentUser)
    throws IncorrectUserException, IncorrectTaskStatusException {
    if (getUser() != currentUser) {
      throw new IncorrectUserException();
    }
    if (getStatus() != Status.AVAILABLE) {
      throw new IncorrectTaskStatusException("");
    }
    setStartTime(startTime);
    if (!systemTime.before(startTime)) {
      setStatus(Status.EXECUTING);
    }
  }

  /**
   * Finishes this task, giving it the given status, and updating all tasks that require this task to be completed
   *
   * @param newStatus New status to assign this task to
   * @param endTime Time at which this task should end
   * @param systemTime Current system time
   * @param currentUser User currently logged in
   * @throws IncorrectUserException if the currently logged-in user is not assigned to this task
   * @throws IncorrectTaskStatusException if the task is not currently EXECUTING
   * @throws FailTimeAfterSystemTimeException if newStatus == FAILED and endTime > systemTime
   */
  public void end(
    Status newStatus,
    Time endTime,
    Time systemTime,
    User currentUser
  )
    throws IncorrectUserException, IncorrectTaskStatusException, FailTimeAfterSystemTimeException {
    if (getUser() != currentUser) {
      throw new IncorrectUserException();
    }
    if (getStatus() != Status.EXECUTING) {
      throw new IncorrectTaskStatusException("d");
    }
    if (newStatus == Status.FAILED) {
      if (systemTime.before(endTime)) {
        throw new FailTimeAfterSystemTimeException();
      }
      setStatus(Status.FAILED);
    } else if (newStatus == Status.FINISHED && !systemTime.before(endTime)) {
      setStatus(Status.FINISHED);
      for (Task nextTask : getNextTasks()) {
        if (nextTask.checkAvailable()) {
          nextTask.setStatus(Status.AVAILABLE);
        }
      }
    }
    setEndTime(endTime);
  }

  /**
   * Updates this tasks' status depending on new system time
   * @param newTime New system time
   */
  public void advanceTime(Time newTime) {
    Status status = getStatus();
    switch (status) {
      case EXECUTING -> {
        if (getEndTime() != null && !newTime.before(getEndTime())) {
          setStatus(Status.FINISHED);
          for (Task nextTask : getNextTasks()) {
            if (nextTask.checkAvailable()) {
              nextTask.setStatus(Status.AVAILABLE);
            }
          }
        }
      }
      case AVAILABLE -> {
        if (getStartTime() != null && !newTime.before(getStartTime())) {
          setStatus(Status.EXECUTING);
        }
      }
    }
  }

  /**
   * @return true if all previous tasks are finished and this task is unavailable, false otherwise
   */
  private boolean checkAvailable() {
    if (getStatus() != Status.UNAVAILABLE) {
      return false;
    }
    for (Task task : getPreviousTasks()) {
      if (task.getStatus() != Status.FINISHED) {
        return false;
      }
    }
    return true;
  }

  /**
   * Semantically replaces this (failed) task with a task created with the given task details
   *
   * @pre duration is a valid time-object
   * @post all previous tasks of this task are now assigned before the new task
   * @post all next tasks of this task are now assigned after the new task
   *
   * @param taskName Name of the replacing task
   * @param description Description of the replacing task
   * @param duration Duration of the replacing task
   * @param deviation Acceptable deviation of the replacing task
   * @throws ReplacedTaskNotFailedException if the current task has not failed yet
   */
  public Task replaceTask(String taskName, String description, Time duration, double deviation) throws ReplacedTaskNotFailedException {
    if (getStatus() != Status.FAILED) {
      throw new ReplacedTaskNotFailedException();
    }

    Task newTask = new Task(taskName, description, duration, deviation, new LinkedList<>(), getUser());

    for (Task task : getPreviousTasks()) {
      task.removeNextTask(this);
      this.removePreviousTask(task);
      task.addNextTask(newTask);
      newTask.addPreviousTask(task);
    }

    for (Task task : getNextTasks()) {
      task.removePreviousTask(this);
      this.removeNextTask(task);
      task.addPreviousTask(newTask);
      newTask.addNextTask(task);
    }

    setReplacementTask(newTask);
    newTask.setReplacesTask(this);

    return newTask;
  }
}
