import java.util.List;

public class Project {
    private final TaskManager taskManager;
    private final String name;
    private final String description;
    private final int creationTime;
    private final int dueTime;

    Project(String name, String description, int creationTime, int dueTime) {
        taskManager = new TaskManager();
        this.name = name;
        this.description = description;
        this.creationTime = creationTime;
        this.dueTime = dueTime;
    }

    @Override
    public String toString() {
        StringBuilder projectString = new StringBuilder();

        projectString.append(   "Project Name:  " + name         + '\n' +
                                "Description:   " + description  + '\n' +
                                "Creation Time: " + creationTime + '\n' +
                                "Due time:      " + dueTime      + '\n');

        projectString.append("\nTasks:\n");
        List<Task> tasks = getTasks();
        int index = 1;
        for (Task task : tasks) {
            projectString.append(index++ + "." + task.getName() + '\n');
        }

        return projectString.toString();

    }


    public String getName() {
        return name;
    }

    /**
     * Passes the user input taskname on to the taskManager to fetch the corresponding task
     * @param selectedTaskName User input, may correspond to a task name
     * @return The (unique) task corresponding with selectedTaskName, or null
     */
    public Task getTask(String selectedTaskName) {
        return taskManager.getTask(selectedTaskName);
    }

    public List<Task> getTasks() {
        return taskManager.getTasks();
    }
}
