import java.util.List;
import java.util.Map;

public class UpdateTaskController {
    private TaskManSystem taskManSystem;
    private Session session;
    private UpdateTaskUI UI;

    public void showAvailableAndExecuting(){
        if (session.getRole() != Role.DEVELOPER){
            return; //TODO
        }

        List<Map.Entry<String,String>> availableTasks = taskManSystem.showAvailableTasks();
        List<Map.Entry<String,String>> executingTasks = taskManSystem.showExecutingTasks();

        UI.showAvailableAndExecuting(availableTasks,executingTasks);
    }
}
