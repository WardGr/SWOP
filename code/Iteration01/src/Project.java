import java.util.LinkedList;
import java.util.List;

public class Project {
    private List<Task> tasks;
    private String name;
    private String description;
    private int creationTime;
    private int dueTime;

    Project(String name, String description, int creationTime, int dueTime) {
        this.tasks = new LinkedList<>();
        this.name = name;
        this.description = description;
        this.creationTime = creationTime;
        this.dueTime = dueTime;
    }

    @Override
    public String toString() {
        StringBuilder projectString = new StringBuilder();

        projectString.append(   "Project Name:  " + getName()         + '\n' +
                                "Description:   " + getDescription()  + '\n' +
                                "Creation Time: " + getCreationTime() + '\n' +
                                "Due time:      " + getDueTime()      + '\n');
        projectString.append("\nTasks:\n");
        int index = 1;
        for (Task task : getTasks()) {
            projectString.append(index++ + "." + task.getName() + '\n');
        }

        return projectString.toString();
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public int getCreationTime() {
        return creationTime;
    }

    public int getDueTime() {
        return dueTime;
    }

    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Passes the user input taskname on to the taskManager to fetch the corresponding task
     * @param selectedTaskName User input, may correspond to a task name
     * @return The (unique) task corresponding with selectedTaskName, or null
     */
    public Task getTask(String selectedTaskName) {
        for (Task task : getTasks()) {
            if (task.getName().equals(selectedTaskName)) {
                return task;
            }
        }
        return null;
    }


    public void addTask(String taskName, String description, int duration, int deviation) {
        // Eerst nog check zeker
        tasks.add(new Task(taskName, description, duration, deviation));
    }
}
