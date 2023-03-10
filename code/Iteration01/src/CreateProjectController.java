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

    public void createProject(String projectName, String projectDescription, String dueTime) {
        if (session.getRole() != Role.PROJECTMANAGER) {
            createProjectUI.printAccessError(Role.PROJECTMANAGER);
            return;
        }
        try {
            taskManSystem.createProject(projectName, projectDescription, getSystemTime(), dueTime);
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

    public void createTask(String projectName, String taskName, String description, Time duration, float deviation, List<String> previousTasks, List<String> nextTasks, String replacesTask) {
        if (session.getRole() != Role.PROJECTMANAGER) {
            createProjectUI.printAccessError(Role.PROJECTMANAGER);
            return;
        }
        User currentUser = session.getCurrentUser();

        taskManSystem.addTaskToProject(projectName,taskName,description,duration,deviation,previousTasks,nextTasks,replacesTask);
    }
}
