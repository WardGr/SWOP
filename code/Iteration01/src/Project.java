import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Keeps all the information regarding an existing project, including its tasks
 */
public class Project {

  private List<Task> tasks;
  private List<Task> replacedTasks;
  private String name;
  private String description;
  private Time creationTime;
  private Time dueTime;

  Project(String name, String description, Time creationTime, Time dueTime)
    throws DueBeforeSystemTimeException {
    if (!dueTime.after(creationTime)) {
      throw new DueBeforeSystemTimeException();
    }
    this.tasks = new LinkedList<>();
    this.replacedTasks = new LinkedList<>();
    this.name = name;
    this.description = description;
    this.creationTime = creationTime;
    this.dueTime = dueTime;
  }

  @Override
  public String toString() {
    StringBuilder projectString = new StringBuilder();

    projectString.append("Project Name:  " + getName() + '\n' +
                         "Description:   " + getDescription() + '\n' +
                         "Creation Time: " + getCreationTime() + '\n' +
                         "Due Time:      " + getDueTime() + '\n'
    );
    projectString.append("\nTasks:\n");
    int index = 1;
    for (Task task : getTasks()) {
      projectString.append(index++ + "." + task.getName() + '\n');
    }
    // TODO geef de totale uitvoeringstijd !!! en de status toch ook?
    // TODO en de gereplacete tasks door andere?

    return projectString.toString();
  }

  public String getDescription() {
    return description;
  }

  public String getName() {
    return name;
  }

  public Time getCreationTime() {
    return creationTime;
  }

  public Time getDueTime() {
    return dueTime;
  }

  /**
   * @return an IMMUTABLE list of the current projects task
   */
  public List<Task> getTasks() {
    return List.copyOf(tasks);
  }

  /**
   * @return Status of the current project, finished if all tasks are finished, ongoing otherwise
   */
  public String getStatus() {
    if (getTasks().size() == 0) {
      return "ongoing";
    }
    for (Task task : getTasks()) {
      if (task.getStatus() != Status.FINISHED) {
        return "ongoing";
      }
    }
    return "finished";
  }

  /**
   * Passes the user input taskname on to the taskManager to fetch the corresponding task
   * @param selectedTaskName User input, may correspond to a task name
   * @return The (unique) task corresponding with selectedTaskName, or null
   */
  public Task getTask(String selectedTaskName) {
    for (Task task : getTasks()) {
      if (task.getName().equals(selectedTaskName)) {
        return task;
      }
    }
    return null;
  }

  /**
   * Creates a task with the given information and adds it to this project
   *
   * @param taskName Name of the task to create and add
   * @param description Description of the task
   * @param duration Duration of the task
   * @param deviation Accepted deviation of the task
   * @param previousTaskNames Names of the tasks to be completed before the newly created task
   * @param user User to be assigned to this task
   * @throws TaskNotFoundException if one of the given tasks to be completed before the new task does not exist
   * @throws TaskNameAlreadyInUseException if the given task name is already in use for this project
   */
  public void addTask(
    String taskName,
    String description,
    Time duration,
    double deviation,
    List<String> previousTaskNames,
    User user
  ) throws TaskNotFoundException, TaskNameAlreadyInUseException {
    if (getTask(taskName) != null) {
      throw new TaskNameAlreadyInUseException();
    }

    List<Task> previousTasks = new ArrayList<>();
    for (String previousTaskName : previousTaskNames) {
      Task task = getTask(previousTaskName);
      if (task == null) {
        throw new TaskNotFoundException();
      }
      previousTasks.add(task);
    }

    tasks.add(
      new Task(taskName, description, duration, deviation, previousTasks, user)
    );
  }

  /**
   * Replaces the given (FAILED) task by a newly created task created with the given information
   *
   * @param taskName Name of the newly created task
   * @param description Description of the new task
   * @param duration Duration of the new task
   * @param deviation Accepted deviation of the new task
   * @param replaces Name of the task to replace
   * @throws ReplacedTaskNotFailedException if the task corresponding to "replaces" has not failed
   * @throws TaskNotFoundException if the task corresponding to "replaces" does not exist
   * @throws TaskNameAlreadyInUseException if the given taskName is already in use in this project
   */
  public void replaceTask(
    String taskName,
    String description,
    Time duration,
    double deviation,
    String replaces
  )
    throws ReplacedTaskNotFailedException, TaskNotFoundException, TaskNameAlreadyInUseException {
    if (getTask(taskName) != null) {
      throw new TaskNameAlreadyInUseException();
    }

    Task replacesTask = getTask(replaces);
    if (replacesTask == null) {
      throw new TaskNotFoundException();
    }
    replacesTask.replaceTask(taskName, description, duration, deviation);

    tasks.remove(replacesTask); // TODO: SETTERS VAN MAKEN
    replacedTasks.add(replacesTask);
    tasks.add(replacesTask.getReplacementTask());
  }

  /**
   * @return A list of names of all tasks in this project that are available
   */
  public List<String> showAvailableTasks() {
    List<String> availableTasks = new LinkedList<>();
    for (Task task : getTasks()) {
      if (task.getStatus() == Status.AVAILABLE) {
        availableTasks.add(task.getName());
      }
    }
    return availableTasks;
  }

  /**
   * @return A list of names of all tasks in this project that are executing
   */
  public List<String> showExecutingTasks() {
    List<String> executingTasks = new LinkedList<>();
    for (Task task : tasks) {
      if (task.getStatus() == Status.EXECUTING) {
        executingTasks.add(task.getName());
      }
    }
    return executingTasks;
  }

  /**
   * Gets detailed information about the task with the given task name
   *
   * @param taskName Name of the task of which to return
   * @return String containing detailed information about the given task
   * @throws TaskNotFoundException if taskName does not correspond to an existing task within this project
   */
  public String showTask(String taskName) throws TaskNotFoundException {
    Task task = getTask(taskName);
    if (task == null) {
      throw new TaskNotFoundException();
    }
    return task.toString();
  }

  /**
   * Gets a list of all possible next statuses the given task could be changed into by the assigned user
   *
   * @param taskName Name of the task of which to return the next statuses
   * @return List of all possible next statuses of the given task
   * @throws TaskNotFoundException if the given taskName does not correspond to an existing task within this project
   */
  public List<Status> getNextStatuses(String taskName)
    throws TaskNotFoundException {
    Task task = getTask(taskName);
    if (task == null) {
      throw new TaskNotFoundException();
    }
    return task.getNextStatuses();
  }

  /**
   * Gets the status of the task within this project corresponding to the given task name
   *
   * @param taskName Name of the task for which to retrieve the current status
   * @return Current status of the task corresponding to taskName
   * @throws TaskNotFoundException if taskName does not correspond to an existing task within this project
   */
  public Status getStatus(String taskName) throws TaskNotFoundException {
    Task task = getTask(taskName);
    if (task == null) {
      throw new TaskNotFoundException();
    }
    return task.getStatus();
  }

  /**
   * Sets the start time of the given task, and changes its status according to the given system time
   *
   * @param taskName Name of the status of which to change the status
   * @param startTime Start time of the given task
   * @param systemTime Current system-time
   * @param currentUser User currently logged in
   * @throws TaskNotFoundException if the given task name does not correspond to an existing task within this project
   * @throws IncorrectUserException if currentUser is not assigned to the given task
   * @throws IncorrectTaskStatusException if the given task status is not currently AVAILABLE
   */
  public void startTask(
    String taskName,
    Time startTime,
    Time systemTime,
    User currentUser
  )
    throws TaskNotFoundException, IncorrectUserException, IncorrectTaskStatusException {
    Task task = getTask(taskName);
    if (task == null) {
      throw new TaskNotFoundException();
    }
    task.start(startTime, systemTime, currentUser);
  }

  /**
   * Ends the given task and sets its status to FAILED or FINISHED according to input
   *
   * @param taskName Name of the status to end
   * @param newStatus New status of the task
   * @param endTime Time at which the task was/will be finished/failed
   * @param systemTime Current system-time
   * @param currentUser User currently logged in
   * @throws TaskNotFoundException if taskName does not correspond to an existing task
   * @throws FailTimeAfterSystemTimeException if newStatus == FAILED and endTime > systemTime
   * @throws IncorrectUserException if currentUser is not the user assigned to this task
   * @throws IncorrectTaskStatusException if the given task status is not EXECUTING
   */
  public void endTask(
    String taskName,
    Status newStatus,
    Time endTime,
    Time systemTime,
    User currentUser
  )
    throws TaskNotFoundException, FailTimeAfterSystemTimeException, IncorrectUserException, IncorrectTaskStatusException {
    Task task = getTask(taskName);
    if (task == null) {
      throw new TaskNotFoundException();
    }
    task.end(newStatus, endTime, systemTime, currentUser);
  }

  /**
   * Updates all tasks in this project with the new system time
   *
   * @param newTime New system time
   */
  public void advanceTime(Time newTime) {
    for (Task task : getTasks()) {
      task.advanceTime(newTime);
    }
  }
}
