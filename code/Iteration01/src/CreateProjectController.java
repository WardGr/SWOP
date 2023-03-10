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
        if (session.getRole() != Role.PROJECTMANAGER) { /* TODO: deze check moet eigenlijk in de UI gebeuren?
                                                            anders vult de user heel die prompt in en dan pas aan het
                                                            einde krijgt em te weten dat hij geen PM is -_- */
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

    public void createTask(String projectName, String taskName, String description, Time duration, float deviation, List<String> previousTasks) {
        if (session.getRole() != Role.PROJECTMANAGER) { /* TODO: deze check moet eigenlijk in de UI gebeuren?
                                                            anders vult de user heel die prompt in en dan pas aan het
                                                            einde krijgt em te weten dat hij geen PM is -_- */
            createProjectUI.printAccessError(Role.PROJECTMANAGER);
            return;
        }
        taskManSystem.addTaskToProject(projectName,taskName,description,duration,deviation,previousTasks);
    }

    public void replaceTask(String projectName, String taskName, String description, Time durationTime, float deviationFloat, String replaces) {
        if (session.getRole() != Role.PROJECTMANAGER) { /* TODO: deze check moet eigenlijk in de UI gebeuren?
                                                            anders vult de user heel die prompt in en dan pas aan het
                                                            einde krijgt em te weten dat hij geen PM is -_- */
            createProjectUI.printAccessError(Role.PROJECTMANAGER);
            return;
        }
        taskManSystem.addAlternativeTask(projectName, taskName, description, durationTime, deviationFloat, replaces);
    }
}
