import java.util.List;
import java.util.Map;

public class UpdateTaskController {
    private TaskManSystem taskManSystem;
    private Session session;
    private Time systemTime;
    private UpdateTaskUI ui;

    public UpdateTaskController(UpdateTaskUI ui, Session session, TaskManSystem taskManSystem, Time systemTime) {
        this.ui = ui;
        this.session = session;
        this.taskManSystem = taskManSystem;
        this.systemTime = systemTime;
    }

    private Session getSession(){ return session; }

    public void showAvailableAndExecuting(){
        if (session.getRole() != Role.DEVELOPER) {
            ui.printAccessError(Role.DEVELOPER);
            return;
        }

        List<Map.Entry<String,String>> availableTasks = taskManSystem.showAvailableTasks();
        List<Map.Entry<String,String>> executingTasks = taskManSystem.showExecutingTasks();

        ui.printAvailableAndExecuting(availableTasks,executingTasks);
    }

    public void updateTaskForm(String projectName, String taskName) {
        if (session.getRole() != Role.DEVELOPER) {
            ui.printAccessError(Role.DEVELOPER);
            return;
        }
        try {
            String taskString = taskManSystem.showTask(projectName, taskName);
            List<Status> statuses = taskManSystem.getNextStatuses(projectName,taskName);
            ui.showTask(taskString, statuses);

            Status status = taskManSystem.getStatus(projectName,taskName);
            ui.updateForm(projectName, taskName, status, systemTime.getTime());
        }
        catch (ProjectNotFoundException | TaskNotFoundException e) {
            ui.taskNotFoundError();
        }

    }

    public void failTask(String projectName, String taskName){
        if (session.getRole() != Role.DEVELOPER) {
            ui.printAccessError(Role.DEVELOPER);
            return;
        }
        try {
            taskManSystem.failTask(projectName, taskName);
        } catch (ProjectNotFoundException | TaskNotFoundException e) {
            ui.taskNotFoundError();
        }
    }

    public void startTask(String projectName, String taskName, int startTimeInput){
        if (session.getRole() != Role.DEVELOPER) {
            ui.printAccessError(Role.DEVELOPER);
            return;
        }
        try {
            Time startTime = new Time(startTimeInput);
            taskManSystem.startTask(projectName, taskName, startTime, getSystemTime(), getSession().getCurrentUser());
        } catch (ProjectNotFoundException | TaskNotFoundException e) {
            ui.taskNotFoundError();
        }
    }

    public void endTask(String projectName, String taskName, Status newStatus, int endTimeInput){
        if (session.getRole() != Role.DEVELOPER) {
            ui.printAccessError(Role.DEVELOPER);
            return;
        }
        try{
            Time endTime = new Time(endTimeInput);
            taskManSystem.endTask(projectName, taskName, newStatus, endTime, getSystemTime(), getSession().getCurrentUser());
        } catch (ProjectNotFoundException | TaskNotFoundException e) {
            ui.taskNotFoundError();
        }
    }

    private Time getSystemTime(){
        return systemTime;
    }
}
