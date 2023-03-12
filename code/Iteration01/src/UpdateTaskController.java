import java.util.List;
import java.util.Map;

public class UpdateTaskController {

  private TaskManSystem taskManSystem;
  private Session session;
  private UpdateTaskUI ui;

  public UpdateTaskController(
    UpdateTaskUI ui,
    Session session,
    TaskManSystem taskManSystem
  ) {
    this.ui = ui;
    this.session = session;
    this.taskManSystem = taskManSystem;
  }

  private Session getSession() {
    return session;
  } //TODO overal zo het veld aanspreken ??

  public void showAvailableAndExecuting() {
    if (session.getRole() != Role.DEVELOPER) {
      ui.printAccessError(Role.DEVELOPER);
      return;
    }

    List<Map.Entry<String, String>> availableTasks =
      taskManSystem.showAvailableTasks();
    List<Map.Entry<String, String>> executingTasks =
      taskManSystem.showExecutingTasks();

    ui.printAvailableAndExecuting(availableTasks, executingTasks);
  }

  public void updateTaskForm(String projectName, String taskName) {
    if (session.getRole() != Role.DEVELOPER) {
      ui.printAccessError(Role.DEVELOPER);
      return;
    }
    try {
      String taskString = taskManSystem.showTask(projectName, taskName);
      List<Status> statuses = taskManSystem.getNextStatuses(
        projectName,
        taskName
      );
      ui.showTask(taskString, statuses);

      Status status = taskManSystem.getStatus(projectName, taskName);
      ui.updateForm(
        projectName,
        taskName,
        status,
        taskManSystem.getSystemHour(),
        taskManSystem.getSystemMinute()
      );
    } catch (ProjectNotFoundException | TaskNotFoundException e) {
      ui.taskNotFoundError();
    }
  }

  public void startTask(
    String projectName,
    String taskName,
    int startHourInput,
    int startMinuteInput
  ) {
    if (session.getRole() != Role.DEVELOPER) {
      ui.printAccessError(Role.DEVELOPER);
      return;
    }
    try {
      taskManSystem.startTask(
        projectName,
        taskName,
        startHourInput,
        startMinuteInput,
        getSession().getCurrentUser()
      );
    } catch (ProjectNotFoundException | TaskNotFoundException e) {
      ui.taskNotFoundError();
    } catch (NotValidTimeException e) {
      ui.printNotValidTimeError(projectName, taskName);
    } catch (UserNotAllowedToChangeTaskException e) {
      ui.userNotAllowedToUpdateTaskError();
    } catch (WrongTaskStatusException e) {
      ui.wrongTaskStatusException(projectName, taskName);
    }
  }

  public void endTask(
    String projectName,
    String taskName,
    Status newStatus,
    int endHourInput,
    int endMinuteInput
  ) {
    if (session.getRole() != Role.DEVELOPER) {
      ui.printAccessError(Role.DEVELOPER);
      return;
    }
    try {
      taskManSystem.endTask(
        projectName,
        taskName,
        newStatus,
        endHourInput,
        endMinuteInput,
        getSession().getCurrentUser()
      );
    } catch (ProjectNotFoundException | TaskNotFoundException e) {
      ui.taskNotFoundError();
    } catch (NotValidTimeException e) {
      ui.printNotValidTimeError(projectName, taskName);
    } catch (FailTimeAfterSystemTimeException e) {
      ui.failTimeAfterSystemTime(projectName, taskName);
    } catch (UserNotAllowedToChangeTaskException e) {
      ui.userNotAllowedToUpdateTaskError();
    } catch (WrongTaskStatusException e) {
      ui.wrongTaskStatusException(projectName, taskName);
    }
  }
}
