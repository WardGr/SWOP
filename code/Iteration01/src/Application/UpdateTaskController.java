package Application;

import Application.IncorrectPermissionException;
import Application.Session;
import Domain.*;

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

  /**
   * Checks if the preconditions for the updatetask use-case are satisfied
   *
   * @return true if preconditions satisfied, else false
   */
  public boolean updateTaskPreconditions() {
    return getSession().getRole() == Role.DEVELOPER;
  }

  public Map<String, List<String>> availableTasksNames() throws IncorrectPermissionException {
    if (getSession().getRole() != Role.DEVELOPER) {
      throw new IncorrectPermissionException("You must be logged in with the " + Role.DEVELOPER + " role to call this function");
    }
    return getTaskManSystem().showAvailableTasks();
  }

  /**
   * Returns a map that maps project names to task names of tasks that are currently executing and assigned to that project
   *
   * @return A map with pairs (project, task), with "project" being the name of the project that task is assigned to, and
   * "task" the name of the task
   * @throws IncorrectPermissionException if the user is not logged in as a developer
   */
  public Map<String, List<String>> executingTasksNames() throws IncorrectPermissionException {
    if (getSession().getRole() != Role.DEVELOPER) {
      throw new IncorrectPermissionException("You must be logged in with the " + Role.DEVELOPER + " role to call this function");
    }
    return getTaskManSystem().showExecutingTasks();
  }

  /**
   * Creates a string containing detailed information about the task with given taskName, belonging to the project with
   * given projectName
   *
   * @param projectName Name of project given by the user
   * @param taskName Name of task given by the user
   * @return String containing detailed information about the given project
   * @throws IncorrectPermissionException if user is not logged in as project manager
   * @throws ProjectNotFoundException if given project name does not correspond to an existing project
   * @throws TaskNotFoundException if the given task does not correspond to an existing task belonging to the given project
   */
  public String showTask(String projectName, String taskName) throws ProjectNotFoundException, TaskNotFoundException, IncorrectPermissionException {
    if (getSession().getRole() != Role.DEVELOPER) {
      throw new IncorrectPermissionException("You must be logged in with the " + Role.DEVELOPER + " role to call this function");
    }
    return getTaskManSystem().showTask(projectName,taskName);
  }

  /**
   * Gets the next possible statuses the given task can be updated to
   *
   * @param projectName Name of the project to which the task belongs, given by the user
   * @param taskName Name of the task of which the statuses are requested, given by the user
   * @return List of statuses the given task can be updated to
   *
   * @throws IncorrectPermissionException if the user is not logged in as developer
   * @throws ProjectNotFoundException if the given project name does not correspond to an existing project
   * @throws TaskNotFoundException if the given task name does not correspond to an existing task within the given project
   */
  public List<Status> getNextStatuses(String projectName, String taskName) throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException {
    if (getSession().getRole() != Role.DEVELOPER) {
      throw new IncorrectPermissionException("Incorrect permission: Domain.User is not a developer");
    }
    return getTaskManSystem().getNextStatuses(projectName, taskName);
  }

  /**
   * Gets the current task of a given task
   *
   * @param projectName Name of the project to which the task is assigned, given by the user
   * @param taskName Name of the task of which to get the current status, given by the user
   * @return Domain.Status of the task corresponding to the task name given by the user
   * @throws IncorrectPermissionException if the user is not logged in as developer
   * @throws ProjectNotFoundException if the given project name does not correspond to an existing project
   * @throws TaskNotFoundException if the given task name does not correspond to an existing task within the given project
   */
  public Status getStatus(String projectName, String taskName) throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException {
    if (getSession().getRole() != Role.DEVELOPER) {
      throw new IncorrectPermissionException("You must be logged in with the " + Role.DEVELOPER + " role to call this function");
    }
    return getTaskManSystem().getStatus(projectName, taskName);
  }

  /**
   * Gets the current system hour
   *
   * @return current system hour
   * @throws IncorrectPermissionException if the user is not logged in as developer
   */
  public int getSystemHour() throws IncorrectPermissionException {
    if (getSession().getRole() != Role.DEVELOPER) {
      throw new IncorrectPermissionException("Incorrect permission: Domain.User is not a developer");
    }
    return getTaskManSystem().getSystemTime().getHour();
  }

  /**
   * Gets the current system minute
   *
   * @return current system minute
   * @throws IncorrectPermissionException if the user is not logged in as developer
   */
  public int getSystemMinute() throws IncorrectPermissionException {
    if (getSession().getRole() != Role.DEVELOPER) {
      throw new IncorrectPermissionException("Incorrect permission: Domain.User is not a developer");
    }
    return getTaskManSystem().getSystemTime().getMinute();
  }

  /**
   * Sets the start time of the given task, automatically updating its status (executing or available) depending on
   * the current system time
   *
   * @param projectName Name of the project the task is assigned to, given by the user
   * @param taskName Name of the task, given by the user
   * @param startHour Hour of the starting time, given by the user
   * @param startMinute Minute of the starting time, given by the user
   * @throws IncorrectPermissionException if the user is not logged in as a developer
   * @throws ProjectNotFoundException if the given project name does not correspond to an existing project
   * @throws InvalidTimeException if startMinuteInput < 0 or startMinuteInput > 59
   * @throws IncorrectUserException if the current user is not assigned to the given task
   * @throws TaskNotFoundException if the given task name does not correspond to an existing task
   * @throws IncorrectTaskStatusException if the given task is not available
   */
  public void startTask(String projectName, String taskName, int startHour, int startMinute) throws IncorrectPermissionException, ProjectNotFoundException, InvalidTimeException, IncorrectUserException, TaskNotFoundException, IncorrectTaskStatusException {
    if (getSession().getRole() != Role.DEVELOPER) {
      throw new IncorrectPermissionException("Incorrect permission: Domain.User is not a developer");
    }
    getTaskManSystem().startTask(projectName, taskName, new Time(startHour, startMinute), getSession().getCurrentUser());
  }

  /**
   * Ends the current task, setting it to finished or failed as given by the user
   *
   * @param projectName Name of the project the task is assigned to, given by the user
   * @param taskName Name of the task, given by the user
   * @param endHour Hour of the end time, given by the user
   * @param endMinute Minute of the end time, given by the user
   * @param newStatus The status the user wishes to change this task to (finished or failed)
   * @throws IncorrectPermissionException if the user is not logged in as developer
   * @throws FailTimeAfterSystemTimeException if the given end time is after the system time and the given status is failed
   * @throws ProjectNotFoundException if the given project name does not correspond to an existing project
   * @throws InvalidTimeException if endMinuteInput > 59 or endMinuteInput < 0
   * @throws IncorrectUserException if the current user is not assigned to the given task
   * @throws TaskNotFoundException if the given task name does not correspond to an existing task
   * @throws IncorrectTaskStatusException if the given task is not executing
   */
  public void endTask(String projectName, String taskName, Status newStatus, int endHour, int endMinute) throws IncorrectPermissionException, FailTimeAfterSystemTimeException, ProjectNotFoundException, InvalidTimeException, IncorrectUserException, TaskNotFoundException, IncorrectTaskStatusException {
    if (getSession().getRole() != Role.DEVELOPER) {
      throw new IncorrectPermissionException("Incorrect permission: Domain.User is not a developer");
    }
    getTaskManSystem().endTask(projectName, taskName, newStatus, new Time(endHour, endMinute), getSession().getCurrentUser());
  }
}
