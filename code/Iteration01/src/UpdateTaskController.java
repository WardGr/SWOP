import java.util.List;
import java.util.Map;

public class UpdateTaskController {

  private final TaskManSystem taskManSystem;
  private final Session session;

  public UpdateTaskController(
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

  public boolean updateTaskPreconditions() {
    return getSession().getRole() == Role.DEVELOPER;
  }

  public Map<String, String> availableTasksNames() throws IncorrectPermissionException {
    if (getSession().getRole() != Role.DEVELOPER) {
      throw new IncorrectPermissionException("You must be logged in with the " + Role.DEVELOPER + " role to call this function");
    }
    return getTaskManSystem().showAvailableTasks();
  }

  public Map<String, String> executingTasksNames() throws IncorrectPermissionException {
    if (getSession().getRole() != Role.DEVELOPER) {
      throw new IncorrectPermissionException("You must be logged in with the " + Role.DEVELOPER + " role to call this function");
    }
    return getTaskManSystem().showExecutingTasks();
  }

  public String showTask(String projectName, String taskName) throws ProjectNotFoundException, TaskNotFoundException, IncorrectPermissionException {
    if (getSession().getRole() != Role.DEVELOPER) {
      throw new IncorrectPermissionException("You must be logged in with the " + Role.DEVELOPER + " role to call this function");
    }
    return getTaskManSystem().showTask(projectName,taskName);
  }

  public List<Status> getNextStatuses(String projectName, String taskName) throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException {
    if (getSession().getRole() != Role.DEVELOPER) {
      throw new IncorrectPermissionException("You must be logged in with the " + Role.DEVELOPER + " role to call this function");
    }
    return getTaskManSystem().getNextStatuses(projectName, taskName);
  }

  public Status getStatus(String projectName, String taskName) throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException {
    if (getSession().getRole() != Role.DEVELOPER) {
      throw new IncorrectPermissionException("You must be logged in with the " + Role.DEVELOPER + " role to call this function");
    }
    return getTaskManSystem().getStatus(projectName, taskName);
  }

  public int getSystemHour() throws IncorrectPermissionException {
    if (getSession().getRole() != Role.DEVELOPER) {
      throw new IncorrectPermissionException("You must be logged in with the " + Role.DEVELOPER + " role to call this function");
    }
    return getTaskManSystem().getSystemHour();
  }

  public int getSystemMinute() throws IncorrectPermissionException {
    if (getSession().getRole() != Role.DEVELOPER) {
      throw new IncorrectPermissionException("You must be logged in with the " + Role.DEVELOPER + " role to call this function");
    }
    return getTaskManSystem().getSystemMinute();
  }

  public void startTask(String projectName, String taskName, int startHourInput, int startMinuteInput) throws IncorrectPermissionException, ProjectNotFoundException, InvalidTimeException, UserNotAllowedToChangeTaskException, TaskNotFoundException, IncorrectTaskStatusException {
    if (getSession().getRole() != Role.DEVELOPER) {
      throw new IncorrectPermissionException("You must be logged in with the " + Role.DEVELOPER + " role to call this function");
    }
    getTaskManSystem().startTask(projectName, taskName, startHourInput, startMinuteInput, getSession().getCurrentUser());
  }

  public void endTask(String projectName, String taskName, Status newStatus, int endHourInput, int endMinuteInput) throws IncorrectPermissionException, FailTimeAfterSystemTimeException, ProjectNotFoundException, InvalidTimeException, UserNotAllowedToChangeTaskException, TaskNotFoundException, IncorrectTaskStatusException {
    if (getSession().getRole() != Role.DEVELOPER) {
      throw new IncorrectPermissionException("You must be logged in with the " + Role.DEVELOPER + " role to call this function");
    }
    getTaskManSystem().endTask(projectName, taskName, newStatus, endHourInput, endMinuteInput, getSession().getCurrentUser());
  }
}
