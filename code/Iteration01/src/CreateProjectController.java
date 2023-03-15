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

  /**
   * Checks the preconditions for the createProject use case
   *
   * @return whether the preconditions are satisfied
   */
  public boolean createProjectPreconditions() {
    return getSession().getRole() == Role.PROJECTMANAGER;
  }

  /**
   * Creates the project with given name, description and due time
   *
   * @param projectName project name given by the user
   * @param projectDescription project description given by the user
   * @param dueHour due hour given by the user
   * @param dueMinute due minute given by the user
   * @throws IncorrectPermissionException if the user is not logged in as a project manager
   * @throws ProjectNameAlreadyInUseException if the given project name is already in use
   * @throws InvalidTimeException if dueMinute > 59 or dueMinute < 0
   * @throws DueBeforeSystemTimeException if the due time is before the system time (the project should have been completed already)
   */
  public void createProject(String projectName, String projectDescription, int dueHour, int dueMinute) throws IncorrectPermissionException, ProjectNameAlreadyInUseException, InvalidTimeException, DueBeforeSystemTimeException {
    if (getSession().getRole() != Role.PROJECTMANAGER) {
      throw new IncorrectPermissionException("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");
    }
    getTaskManSystem().createProject(projectName, projectDescription, dueHour, dueMinute);
  }
}
