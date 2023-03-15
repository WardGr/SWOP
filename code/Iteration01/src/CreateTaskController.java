import java.util.List;

public class CreateTaskController {

  private final Session session;
  private final TaskManSystem taskManSystem;
  private final UserManager userManager;

  public CreateTaskController(
    Session session,
    CreateTaskUI ui,
    TaskManSystem taskManSystem,
    UserManager userManager
  ) {
    this.session = session;
    this.taskManSystem = taskManSystem;
    this.userManager = userManager;
  }

  public boolean createTaskPreconditions() {
    return getSession().getRole() == Role.PROJECTMANAGER;
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
        durationHour,
        durationMinute,
        deviation,
        previousTasks,
        developer
      );
  }

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
    getTaskManSystem().addAlternativeTaskToProject(
        projectName,
        taskName,
        description,
        durationHour,
        durationMinute,
        deviation,
        replaces
    );
    // TODO vervangen door gewoon een invalid input exception?
  }
}
