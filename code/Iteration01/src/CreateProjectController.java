import java.util.List;

public class CreateProjectController {
    private Session session;
    private TaskManSystem taskManSystem;
    private CreateProjectUI createProjectUI;
    private Time systemTime;

    public CreateProjectController(Session session, CreateProjectUI createProjectUI, TaskManSystem taskManSystem) {
        this.session = session;
        this.createProjectUI = createProjectUI;
        this.taskManSystem = taskManSystem;
        this.systemTime = new Time(0);
    }

    public void createProject(String projectName, String projectDescription, int dueTime) {
        if (session.getRole() != Role.PROJECTMANAGER) { /* TODO: deze check moet eigenlijk in de UI gebeuren?
                                                            anders vult de user heel die prompt in en dan pas aan het
                                                            einde krijgt em te weten dat hij geen PM is -_- */
            createProjectUI.printAccessError(Role.PROJECTMANAGER);
            return;
        }
        try {
            taskManSystem.createProject(projectName, projectDescription, getSystemTime(), new Time(dueTime));
        }
        catch (NumberFormatException e){
            createProjectUI.dueTimeFormatError();
        }
        catch (DueBeforeSystemTimeException e) {
            createProjectUI.dueBeforeSystemTimeError();
        }
    }

    private Time getSystemTime() {
        return systemTime;
    }

    public void createTask(String projectName, String taskName, String description, int duration, double deviation, List<String> previousTasks) {
        if (session.getRole() != Role.PROJECTMANAGER) { /* TODO: deze check moet eigenlijk in de UI gebeuren?
                                                            anders vult de user heel die prompt in en dan pas aan het
                                                            einde krijgt em te weten dat hij geen PM is -_- */
            createProjectUI.printAccessError(Role.PROJECTMANAGER);
            return;
        }
        try {
            taskManSystem.addTaskToProject(projectName, taskName, description, new Time(duration), deviation, previousTasks, session.getCurrentUser());
        }
        catch (ProjectNotFoundException e) {
            createProjectUI.printProjectNotFound();
        }
        catch (TaskNotFoundException e) {
            createProjectUI.printTaskNotFound();
        }
    }

    public void replaceTask(String projectName, String taskName, String description, int duration, double deviation, String replaces) {
        if (session.getRole() != Role.PROJECTMANAGER) { /* TODO: deze check moet eigenlijk in de UI gebeuren?
                                                            anders vult de user heel die prompt in en dan pas aan het
                                                            einde krijgt em te weten dat hij geen PM is -_- */
            createProjectUI.printAccessError(Role.PROJECTMANAGER);
            return;
        }
        try {
            taskManSystem.addAlternativeTaskToProject(projectName, taskName, description, new Time(duration), deviation, replaces, session.getCurrentUser());
        } catch (ReplacedTaskNotFailedException e) {
            createProjectUI.printTaskNotFailedError();
        } catch (ProjectNotFoundException e) {
            createProjectUI.printProjectNotFound();
        } catch (TaskNotFoundException e) {
            createProjectUI.printTaskNotFound();
        }
    }
}
