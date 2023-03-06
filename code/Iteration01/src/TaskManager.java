import java.util.LinkedList;
import java.util.List;

public class TaskManager {
    private List<Task> tasks;

    public TaskManager() {
        // Test Tasks for now :)

        Task task1 = new Task("Task 1", "Do stuff", 100, 1);
        Task task2 = new Task("Task 2", "Do more stuff", 500, 2);
        LinkedList<Task> tasks = new LinkedList<>();
        tasks.add(task1);
        tasks.add(task2);

        this.tasks = tasks;
    }

    /**
     * @return A copy of the current tasks
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Returns the task corresponding to the given name
     * @param selectedTaskName User input, may correspond to a task name
     * @return The (unique) task corresponding with selectedTaskName, or null
     */
    public Task getTask(String selectedTaskName) {
        for (Task task : tasks) {
            if (task.getName().equals(selectedTaskName)) {
                return task;
            }
        }
        return null;
    }

    public boolean addTask(String taskName, String description, long duration, int deviation) {
        try {
            Task newTask = new Task(taskName, description, duration, deviation);
            tasks.add(newTask);
            return true;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
