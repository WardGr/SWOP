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
            showProjectsUI.printAccessError(Role.PROJECTMANAGER);
            return;
        }
        List<String> names = taskManSystem.getProjectNames();
        List<String> statuses = taskManSystem.getStatuses();
        ShowProjectsUI.printProjects(names, statuses);
    }

    public void showProject(String projectName) {
        String projectString = taskManSystem.showProject(projectName);
        ShowProjectsUI.printProjectDetails(projectString);
    }

    public void showTask(String projectName, String taskName) {
        String taskString = taskManSystem.showTask(projectName, taskName);
        ShowProjectsUI.printTaskDetails(taskString);
    }
}
