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
    if (!createTaskPreconditions()) {
      throw new IncorrectPermissionException();
    }
    User developer = userManager.getDeveloper(user);
    taskManSystem.addTaskToProject(
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
    if (session.getRole() != Role.PROJECTMANAGER) {
      throw new IncorrectPermissionException();
    }
    taskManSystem.addAlternativeTaskToProject(
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
