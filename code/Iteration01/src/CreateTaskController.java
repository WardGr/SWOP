import java.util.List;

public class CreateTaskController {

    private final Session session;
    private final TaskManSystem taskManSystem;
    private final UserManager userManager;
    private final CreateTaskUI ui;

    public CreateTaskController(Session session, CreateTaskUI ui, TaskManSystem taskManSystem, UserManager userManager) {
        this.session = session;
        this.ui = ui;
        this.taskManSystem = taskManSystem;
        this.userManager = userManager;
    }

    public void createTaskForm(){
        if (session.getRole() != Role.PROJECTMANAGER) {
            ui.printAccessError(Role.PROJECTMANAGER);
            return;
        }
        ui.createTaskForm();
    }

    public void createTask(String projectName, String taskName, String description, int durationHour, int durationMinute, double deviation, String user, List<String> previousTasks) {
        if (session.getRole() != Role.PROJECTMANAGER) {
            ui.printAccessError(Role.PROJECTMANAGER);
            return;
        }
        try {
            User developer = userManager.getDeveloper(user);
            taskManSystem.addTaskToProject(projectName, taskName, description, durationHour, durationMinute, deviation, previousTasks, developer);
            ui.messageTaskCreation(projectName, taskName);
        }
        catch (ProjectNotFoundException e) {
            ui.printProjectNotFound();
        } catch (TaskNotFoundException e) {
            ui.printTaskNotFound();
        } catch (NotValidTimeException e) {
            ui.printNotValidTimeError();
        } catch (TaskNameAlreadyInUseException e) {
            ui.taskNameAlreadyUsedError();
        } catch (UserNotFoundException e) {
            ui.UserNotDeveloperError();
        }
        // TODO vervangen door gewoon een invalid input exception?
    }

    public void replaceTask(String projectName, String taskName, String description, int durationHour, int durationMinute, double deviation, String replaces) {
        if (session.getRole() != Role.PROJECTMANAGER) {
            ui.printAccessError(Role.PROJECTMANAGER);
            return;
        }
        try {
            taskManSystem.addAlternativeTaskToProject(projectName, taskName, description, durationHour, durationMinute, deviation, replaces);
            ui.messageTaskCreation(projectName, taskName);
        } catch (ReplacedTaskNotFailedException e) {
            ui.printTaskNotFailedError();
        } catch (ProjectNotFoundException e) {
            ui.printProjectNotFound();
        } catch (TaskNotFoundException e) {
            ui.printTaskNotFound();
        } catch (NotValidTimeException e) {
            ui.printNotValidTimeError();
        } catch (TaskNameAlreadyInUseException e) {
            ui.taskNameAlreadyUsedError();
        }
        // TODO vervangen door gewoon een invalid input exception?
    }
}
