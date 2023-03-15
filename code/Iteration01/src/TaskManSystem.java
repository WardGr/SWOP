import java.util.*;

/**
 * Central domain-level system class, keeps track of system time and all projects, first point of entry into the domain
 * layer
 */
public class TaskManSystem {

  private List<Project> projects;
  private Time systemTime;

  public TaskManSystem(Time systemTime) {
    this.systemTime = systemTime;
    projects = new LinkedList<>();
  }

  public int getSystemHour() {
    return getSystemTime().getHour();
  }

  public int getSystemMinute() {
    return getSystemTime().getMinute();
  }

  private Time getSystemTime() {
    return systemTime;
  }

  private List<Project> getProjects() {
    return projects;
  }

  /**
   * Returns the project corresponding to the given project name if no such project exists then returns null
   * @param projectName Name of the project
   * @return Project corresponding to the given project name, null if no such project exists
   */
  private Project getProject(String projectName) {
    for (Project project : projects) {
      if (project.getName().equals(projectName)) {
        return project;
      }
    }
    return null;
  }

  /**
   * @return List of all project names
   */
  public List<String> getProjectNames() { // todo: momenteel niet gebruikt
    List<String> names = new LinkedList<>();
    for (Project project : projects) {
      names.add(project.getName());
    }
    return names;
  }

  /**
   * Returns a map which maps project names to their status
   */
  public Map<String, String> getProjectNamesWithStatus() {
    Map<String, String> statuses = new HashMap<>();
    for (Project project : getProjects()) {
      statuses.put(project.getName(), project.getStatus());
    }
    return statuses;
  }

  /** TODO: maybe it's cleaner to just return the empty string if no such project is found? Semantically this is logical and it removes an exception to be caught...
   * Returns detailed information about the given project
   * @param projectName Name of the project of which to return the details
   * @return string containing detailed information about the project  + a list of its tasks
   * @throws ProjectNotFoundException if the given project name does not correspond to an existing project
   */
  public String showProject(String projectName)
    throws ProjectNotFoundException {
    Project project = getProject(projectName);
    if (project == null) {
      throw new ProjectNotFoundException();
    }
    return project.toString();
  }

  /** TODO: maybe it's cleaner to just return the empty string if no such task is found? Semantically this is logical and it removes an exception to be caught...
   * Returns detailed information about the given task
   * @param projectName Name of the project which the task belongs to
   * @param taskName Name of the task of which to return detailed information
   * @return string containing detailed information about the task
   * @throws ProjectNotFoundException if the given project name does not correspond to an existing project
   * @throws TaskNotFoundException if the given task does not correspond to an existing task within the given project
   */
  public String showTask(String projectName, String taskName)
    throws ProjectNotFoundException, TaskNotFoundException {
    Project project = getProject(projectName);
    if (project == null) {
      throw new ProjectNotFoundException();
    }
    return project.showTask(taskName);
  }

  /**
   * Creates a project with given name, description and due time, using the system time as start time
   *
   * @param projectName Name of the project to create
   * @param projectDescription Description of the project to create
   * @param dueTime Time at which the project is due
   * @throws DueBeforeSystemTimeException if the given due time is before system time
   * @throws ProjectNameAlreadyInUseException if the given project name is already in use
   */
  public void createProject(String projectName, String projectDescription, Time dueTime) throws ProjectNameAlreadyInUseException, DueBeforeSystemTimeException {
    createProject(projectName, projectDescription, getSystemTime(), dueTime);
  }

  /**
   * Creates a project with given name, description and due time, using the given time as start time
   *
   * @param projectName Name of the project to create
   * @param projectDescription Description of the project to create
   * @param startTime Time at which the project is to start
   * @param dueTime Time at which the project is due
   * @throws DueBeforeSystemTimeException if the given due time is before system time
   * @throws ProjectNameAlreadyInUseException if the given project name is already in use
   */
  public void createProject(
          String projectName,
          String projectDescription,
          Time startTime,
          Time dueTime
  )
          throws DueBeforeSystemTimeException, ProjectNameAlreadyInUseException {
    if (getProject(projectName) == null) {
      Project newProject = new Project(
              projectName,
              projectDescription,
              startTime,
              dueTime
      );
      projects.add(newProject);
    } else {
      throw new ProjectNameAlreadyInUseException();
    }
  }

  /**
   * Creates a task with the given information and adds it to the project corresponding to the given project name
   *
   * @param projectName Project name of project which to add the created task to
   * @param taskName Task name of the task to add to the project
   * @param description Task description of the task
   * @param durationTime Duration of the task
   * @param deviation Acceptable deviation of the task
   * @param previousTasks Tasks to be completed before the task
   * @param givenUser User to assign to the task
   * @throws ProjectNotFoundException if the given project name does not correspond to an existing project
   * @throws TaskNotFoundException if one of the previous tasks does not correspond to an existing task
   * @throws TaskNameAlreadyInUseException if the given task name is already used by another task belonging to the given project
   */
  public void addTaskToProject(
    String projectName,
    String taskName,
    String description,
    Time durationTime,
    double deviation,
    List<String> previousTasks,
    User givenUser
  )
    throws ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException {
    Project project = getProject(projectName);
    if (project == null) {
      throw new ProjectNotFoundException();
    }
    project.addTask(
      taskName,
      description,
      durationTime,
      deviation,
      previousTasks,
      givenUser
    );
  }

  /**
   * Replaces a given (FAILED) task in a given project with a new task created from the given information
   *
   * @param replaces Name of the task to replace
   * @param projectName Name of the project in which to replace the task
   * @param taskName Name of the task to create
   * @param description Description of the task to create
   * @param durationTime Duration of the task to create
   * @param deviation Accepted deviation of the task to create
   * @throws ReplacedTaskNotFailedException if the task to replace has not failed yet
   * @throws ProjectNotFoundException if the given project name does not correspond to an existing project
   * @throws TaskNotFoundException if the given task name does not correspond to a task within the given project
   * @throws TaskNameAlreadyInUseException if the task name to use for the new task is already in use by another task within the project
   */
  public void replaceTaskInProject(
    String projectName,
    String taskName,
    String description,
    Time durationTime,
    double deviation,
    String replaces
  )
    throws ReplacedTaskNotFailedException, ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException {
    Project project = getProject(projectName);
    if (project == null) {
      throw new ProjectNotFoundException();
    }
    project.replaceTask(
      taskName,
      description,
      durationTime,
      deviation,
      replaces
    );
  }

  /**
   * @return A map with tuples (project, task) mapping all available tasks to their projects by their names
   */
  public Map<String, String> showAvailableTasks() {
    Map<String, String> availableTasks = new HashMap<>();
    for (Project project : projects) {
      List<String> tasks = project.showAvailableTasks();
      String projectName = project.getName();
      for (String task : tasks) {
        availableTasks.put(projectName, task);
      }
    }
    return availableTasks;
  }

  /**
   * @return A map with tuples (project, task) mapping all executing tasks to their projects by their names
   */
  public Map<String, String> showExecutingTasks() {
    Map<String, String> executingTasks = new HashMap<>();
    for (Project project : projects) {
      List<String> tasks = project.showExecutingTasks();
      String projectName = project.getName();
      for (String task : tasks) {
        executingTasks.put(projectName, task);
      }
    }
    return executingTasks;
  }

  /**
   * Gets the list of next possible statuses the given task in the given project can be changed into by the user assigned
   * to this task
   *
   * @param projectName Name of the project
   * @param taskName Name of the task for which to return the next possible statuses
   * @return A list of statuses to which the given task can be changed by the assigned user
   * @throws ProjectNotFoundException if the given project does not correspond to an existing project
   * @throws TaskNotFoundException if the given task does not correspond to an existing task within the given project
   */
  public List<Status> getNextStatuses(String projectName, String taskName)
    throws ProjectNotFoundException, TaskNotFoundException {
    Project project = getProject(projectName);
    if (project == null) {
      throw new ProjectNotFoundException();
    }
    return project.getNextStatuses(taskName);
  }

  /**
   * Gets the status of the given task within the given project
   *
   * @param projectName Name of the project to which the task is assigned
   * @param taskName Name of the task of which to return the status
   * @return Status of the given task (AVAILABLE, UNAVAILABLE, EXECUTING, FINISHED, FAILED)
   * @throws ProjectNotFoundException if the given project does not correspond to an existing project
   * @throws TaskNotFoundException if the given task does not correspond to an existing task within the given project
   */
  public Status getStatus(String projectName, String taskName)
    throws ProjectNotFoundException, TaskNotFoundException {
    Project project = getProject(projectName);
    if (project == null) {
      throw new ProjectNotFoundException();
    }
    return project.getStatus(taskName);
  }

  /**
   * Sets the start time of the given (AVAILABLE) task, and changes its status to EXECUTING if this time is after the system time
   *
   * @param projectName Name of the project to which the task to start is attached
   * @param taskName Name of the task to start
   * @param startHour Hour at which the task should start
   * @param startMinute Minute at which the task should start
   * @param currentUser User currently logged in
   * @throws ProjectNotFoundException if the given project name does not correspond to an existing project
   * @throws TaskNotFoundException if the given task name does not correspond to an existing task within the given project
   * @throws InvalidTimeException if startMinute < 0 or startMinute > 59
   * @throws UserNotAllowedToChangeTaskException if currentUser is not the user assigned to the given task
   * @throws IncorrectTaskStatusException if the task status is not AVAILABLE
   */
  public void startTask(
    String projectName,
    String taskName,
    int startHour,
    int startMinute,
    User currentUser
  )
    throws ProjectNotFoundException, TaskNotFoundException, InvalidTimeException, UserNotAllowedToChangeTaskException, IncorrectTaskStatusException {
    Project project = getProject(projectName);
    if (project == null) {
      throw new ProjectNotFoundException();
    }
    project.startTask(
      taskName,
      new Time(startHour, startMinute),
      getSystemTime(),
      currentUser
    );
  }

  /**
   * Sets the end time of the given (EXECUTING) task, and changes its status to the given status
   *
   * @param projectName Name of the project to which the task to start is attached
   * @param taskName Name of the task to start
   * @param endHour Hour at which the task should start
   * @param endMinute Minute at which the task should start
   * @param newStatus Status to change the given task into
   * @param currentUser User currently logged in
   * @throws ProjectNotFoundException if the given project name does not correspond to an existing project
   * @throws TaskNotFoundException if the given task name does not correspond to an existing task within the given project
   * @throws InvalidTimeException if startMinute < 0 or startMinute > 59
   * @throws FailTimeAfterSystemTimeException if newStatus == FAILED and the given end time is after the system time
   * @throws UserNotAllowedToChangeTaskException if currentUser is not the user assigned to the given task
   * @throws IncorrectTaskStatusException if the task status is not EXECUTING
   */
  public void endTask(
    String projectName,
    String taskName,
    Status newStatus,
    int endHour,
    int endMinute,
    User currentUser
  )
    throws ProjectNotFoundException, TaskNotFoundException, InvalidTimeException, FailTimeAfterSystemTimeException, UserNotAllowedToChangeTaskException, IncorrectTaskStatusException {
    Project project = getProject(projectName);
    if (project == null) {
      throw new ProjectNotFoundException();
    }
    project.endTask(
      taskName,
      newStatus,
      new Time(endHour, endMinute),
      getSystemTime(),
      currentUser
    );
  }

  /**
   * Advances the system time
   *
   * @param newHour Hour which to change the system time to
   * @param newMinute Minute which to change the system time to
   * @throws NewTimeBeforeSystemTimeException if the given time is before the current system time
   * @throws InvalidTimeException if newMinute < 0 or newMinute > 59
   */
  public void advanceTime(int newHour, int newMinute)
    throws NewTimeBeforeSystemTimeException, InvalidTimeException {
    Time newTime = new Time(newHour, newMinute);
    if (newTime.before(systemTime)) {
      throw new NewTimeBeforeSystemTimeException();
    }
    for (Project project : projects) {
      project.advanceTime(newTime);
    }
    systemTime = newTime;
  }
}
