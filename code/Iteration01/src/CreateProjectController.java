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

  public void createProject(String projectName, String projectDescription, int dueHour, int dueMinute) throws IncorrectPermissionException, ProjectNameAlreadyInUseException, InvalidTimeException, DueBeforeSystemTimeException {
    if (getSession().getRole() != Role.PROJECTMANAGER) {
      throw new IncorrectPermissionException("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");
    }
    getTaskManSystem().createProject(projectName, projectDescription, new Time(dueHour,dueMinute));
  }
}
