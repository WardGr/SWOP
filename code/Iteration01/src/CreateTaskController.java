import java.util.List;

public class CreateTaskController {

  private final Session session;
  private final TaskManSystem taskManSystem;
  private final UserManager userManager;

  public CreateTaskController(
    Session session,
    TaskManSystem taskManSystem,
    UserManager userManager
  ) {
    this.session = session;
    this.taskManSystem = taskManSystem;
    this.userManager = userManager;
  }


  private Session getSession() {
    return session;
  }

  private TaskManSystem getTaskManSystem() {
    return taskManSystem;
  }

  private UserManager getUserManager() {
    return userManager;
  }

  /**
   * @return whether the preconditions for the createtask use-case are met
   */
  public boolean createTaskPreconditions() {
    return getSession().getRole() == Role.PROJECTMANAGER;
  }

  /**
   * Creates a task attached to the given project, with task name, description, duration, acceptable deviation, assigned
   * user and tasks to be completed before this task all given by the user
   *
   * @param projectName Name of project the task will be added to, given by the user
   * @param taskName Name of new task, given by the user
   * @param description Description of new task, given by the user
   * @param durationHour Hours of the new tasks' duration, given by the user
   * @param durationMinute Minutes of the new tasks' duration, given by the user
   * @param deviation Acceptable deviation from the given duration, given by the user as a percentage
   * @param user Name of the user this task is allocated to
   * @param previousTasks List of names of tasks that should be completed before this one, given by the user
   * @throws ProjectNotFoundException If the given project name does not correspond to an existing project
   * @throws InvalidTimeException If durationMinute > 59 or durationMinute < 0
   * @throws TaskNotFoundException If any of the previous tasks do not correspond to an existing task
   * @throws TaskNameAlreadyInUseException If the given task name is already in use within the given project
   * @throws IncorrectPermissionException If the user is not logged in as project manager
   * @throws UserNotFoundException If the user attached to the new task does not exist
   */ // lord heavens above
  public void createTask(
    String projectName,
    String taskName,
    String description,
    int durationHour,
    int durationMinute,
    double deviation,
    String user,
    List<String> previousTasks
  ) throws ProjectNotFoundException, InvalidTimeException, TaskNotFoundException, TaskNameAlreadyInUseException, IncorrectPermissionException, UserNotFoundException {
    if (getSession().getRole() != Role.PROJECTMANAGER) {
      throw new IncorrectPermissionException("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");
    }
    User developer = getUserManager().getDeveloper(user);
    getTaskManSystem().addTaskToProject(
        projectName,
        taskName,
        description,
        new Time(durationHour, durationMinute),
        deviation,
        previousTasks,
        developer
      );
  }

  /**
   * Creates a new task with the given task information, replacing a failed task
   *
   * @param projectName Project name corresponding to the project to which both tasks belong, given by the user
   * @param taskName Task name of the replacement task, given by the user
   * @param description Task description of the replacement task, given by the user
   * @param durationHour Hours of the replacement tasks' duration, given by the user
   * @param durationMinute Minutes of the replacement tasks' duration, given by the user
   * @param deviation Acceptable deviation from the given duration, given by the user
   * @param replaces Task name of the task that the new task would replace, given by the user
   * @throws TaskNotFoundException If the given task name of the task to replace does not correspond to an existing task
   * @throws ProjectNotFoundException If the given project name does not correspond to an existing project
   * @throws TaskNameAlreadyInUseException If the given task name is already in use as a task name within the given project
   * @throws IncorrectPermissionException If the user is not logged in as project manager
   * @throws ReplacedTaskNotFailedException If the task to replace has not failed yet
   * @throws InvalidTimeException If durationMinute > 59 or durationMinute < 0
   */
  public void replaceTask(
    String projectName,
    String taskName,
    String description,
    int durationHour,
    int durationMinute,
    double deviation,
    String replaces
  ) throws IncorrectPermissionException, ReplacedTaskNotFailedException, ProjectNotFoundException, InvalidTimeException, TaskNotFoundException, TaskNameAlreadyInUseException {
    if (getSession().getRole() != Role.PROJECTMANAGER) {
      throw new IncorrectPermissionException("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");
    }
    getTaskManSystem().replaceTaskInProject(
        projectName,
        taskName,
        description,
        new Time(durationHour, durationMinute),
        deviation,
        replaces
    );
  }
}
