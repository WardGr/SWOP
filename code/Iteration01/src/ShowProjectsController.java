import java.util.List;

public class ShowProjectsController {
    private ShowProjectsUI showProjectsUI;
    private Session session;
    private TaskManSystem taskManSystem;

    public ShowProjectsController(Session session, ShowProjectsUI showProjectsUI, TaskManSystem taskManSystem) {
        this.showProjectsUI = showProjectsUI;
        this.session = session;
        this.taskManSystem = taskManSystem;
    }

    public void showProjects() {
        if (session.getRole() != Role.PROJECTMANAGER) {
            showProjectsUI.printAccessError(Role.PROJECTMANAGER); // TODO die functie als super zetten in de ui?
            return;
        }
        List<String> names = taskManSystem.getProjectNames();
        List<String> statuses = taskManSystem.getStatuses();
        showProjectsUI.printProjects(names, statuses);
    }

    public void showProject(String projectName) {
        if (session.getRole() != Role.PROJECTMANAGER) {
            showProjectsUI.printAccessError(Role.PROJECTMANAGER);
            return;
        }
        try {
            String projectString = taskManSystem.showProject(projectName);
            showProjectsUI.printProjectDetails(projectString, projectName);
        }
        catch (ProjectNotFoundException e) {
            showProjectsUI.projectNotFoundError();
        }

    }

    public void showTask(String projectName, String taskName) {
        if (session.getRole() != Role.PROJECTMANAGER) {
            showProjectsUI.printAccessError(Role.PROJECTMANAGER);
            return;
        }
        try {
            String taskString = taskManSystem.showTask(projectName, taskName);
            showProjectsUI.printTaskDetails(taskString, projectName);
        }
        catch (ProjectNotFoundException e) {
            showProjectsUI.projectNotFoundError();
        }
        catch (TaskNotFoundException e) {
            showProjectsUI.taskNotFoundError(projectName);
        }

    }
}
