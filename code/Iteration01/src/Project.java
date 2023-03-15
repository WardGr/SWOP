import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Project {

  private List<Task> tasks;
  private List<Task> replacedTasks;
  private String name;
  private String description;
  private Time creationTime;
  private Time dueTime;

  Project(String name, String description, Time creationTime, Time dueTime)
    throws DueBeforeSystemTimeException {
    if (dueTime.before(creationTime)) {
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
                         "Due time:      " + getDueTime() + '\n'
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

  public List<Task> getTasks() {
    return List.copyOf(tasks);
  }

  public String getStatus() {
    if (tasks.size() == 0) {
      return "ongoing";
    }
    for (Task task : tasks) {
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
    Task replacementTask = new Task(
      taskName,
      description,
      duration,
      deviation,
      replacesTask
    );
    tasks.remove(replacesTask); // TODO: SETTERS VAN MAKEN
    replacedTasks.add(replacesTask);
    tasks.add(replacementTask);
  }

  public List<String> showAvailableTasks() {
    List<String> availableTasks = new ArrayList<>();
    for (Task task : tasks) {
      if (task.getStatus() == Status.AVAILABLE) {
        availableTasks.add(task.getName());
      }
    }
    return availableTasks;
  }

  public List<String> showExecutingTasks() {
    List<String> executingTasks = new ArrayList<>();
    for (Task task : tasks) {
      if (task.getStatus() == Status.EXECUTING) {
        executingTasks.add(task.getName());
      }
    }
    return executingTasks;
  }

  public String showTask(String taskName) throws TaskNotFoundException {
    Task task = getTask(taskName);
    if (task == null) {
      throw new TaskNotFoundException();
    }
    return task.toString();
  }

  public List<Status> getNextStatuses(String taskName)
    throws TaskNotFoundException {
    Task task = getTask(taskName);
    if (task == null) {
      throw new TaskNotFoundException();
    }
    return task.getNextStatuses();
  }

  public Status getStatus(String taskName) throws TaskNotFoundException {
    Task task = getTask(taskName);
    if (task == null) {
      throw new TaskNotFoundException();
    }
    return task.getStatus();
  }

  public void startTask(
    String taskName,
    Time startTime,
    Time systemTime,
    User currentUser
  )
    throws TaskNotFoundException, UserNotAllowedToChangeTaskException, IncorrectTaskStatusException {
    Task task = getTask(taskName);
    if (task == null) {
      throw new TaskNotFoundException();
    }
    task.start(startTime, systemTime, currentUser);
  }

  public void endTask(
    String taskName,
    Status newStatus,
    Time endTime,
    Time systemTime,
    User currentUser
  )
    throws TaskNotFoundException, FailTimeAfterSystemTimeException, UserNotAllowedToChangeTaskException, IncorrectTaskStatusException {
    Task task = getTask(taskName);
    if (task == null) {
      throw new TaskNotFoundException();
    }
    task.end(newStatus, endTime, systemTime, currentUser);
  }

  public void advanceTime(Time newTime) {
    for (Task task : getTasks()) {
      task.advanceTime(newTime);
    }
  }
}
