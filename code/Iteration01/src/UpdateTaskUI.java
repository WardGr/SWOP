import java.util.List;
import java.util.Map;

public class UpdateTaskUI {
    private UpdateTaskController controller;

    public void updateTaskStatus() {
        controller.showAvailableAndExecuting();

        // TODO HIER VERDER!!!
    }

    public void showAvailableAndExecuting(List<Map.Entry<String,String>> availableTasks, List<Map.Entry<String,String>> executingTasks) {
        System.out.println("*** AVAILABLE TASKS ***");
        for (Map.Entry<String,String> entry : availableTasks) {
            System.out.println("Project: " + entry.getKey() + " Task: " + entry.getValue());
        }
        System.out.println("");
        System.out.println("*** EXECUTING TASKS ***");
        for (Map.Entry<String,String> entry : availableTasks) {
            System.out.println("Project: " + entry.getKey() + " Task: " + entry.getValue());
        }
    }
}
