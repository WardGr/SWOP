public class CreateProjectController {

  private final Session session;
  private final TaskManSystem taskManSystem;

  public CreateProjectController(
    Session session,
    TaskManSystem taskManSystem
  ) {
    this.session = session;
    this.taskManSystem = taskManSystem;
  }

  private Session getSession() {
    return session;
  }

  private TaskManSystem getTaskManSystem() {
    return taskManSystem;
  }

  public boolean createProjectPreconditions() {
    return getSession().getRole() == Role.PROJECTMANAGER;
  }

  public void createProject(String projectName, String projectDescription, int dueHour, int dueMinute) throws IncorrectPermissionException, ProjectNameAlreadyInUseException, NotValidTimeException, DueBeforeSystemTimeException {
    if (getSession().getRole() != Role.PROJECTMANAGER) {
      throw new IncorrectPermissionException();
    }
    getTaskManSystem().createProject(projectName, projectDescription, dueHour, dueMinute);
  }
}
