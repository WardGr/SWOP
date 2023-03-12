import java.util.List;

public class CreateProjectController {
    private Session session;
    private TaskManSystem taskManSystem;
    private CreateProjectUI createProjectUI;

    public CreateProjectController(Session session, CreateProjectUI createProjectUI, TaskManSystem taskManSystem) {
        this.session = session;
        this.createProjectUI = createProjectUI;
        this.taskManSystem = taskManSystem;
    }

    public void createProjectForm(){
        if (session.getRole() != Role.PROJECTMANAGER) {
            createProjectUI.printAccessError(Role.PROJECTMANAGER);
            return;
        }
        createProjectUI.createProjectForm();
    }

    public void createProject(String projectName, String projectDescription, int dueHour, int dueMinute) {
        if (session.getRole() != Role.PROJECTMANAGER) {
            createProjectUI.printAccessError(Role.PROJECTMANAGER);
            return;
        }
        try {
            taskManSystem.createProject(projectName, projectDescription, dueHour, dueMinute);
            createProjectUI.messageProjectCreation(projectName);
        }
        catch (NotValidTimeException e){
            createProjectUI.notValidTimeError();
        }
        catch (DueBeforeSystemTimeException e) {
            createProjectUI.dueBeforeSystemTimeError();
        } catch (ProjectNameAlreadyInUseException e) {
            createProjectUI.projectAlreadyInUseError();
        }
    }


}
