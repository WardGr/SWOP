import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Project {
    private List<Task> tasks;
    private String name;
    private String description;
    private Time creationTime;
    private Time dueTime;

    Project(String name, String description, Time creationTime, Time dueTime) throws DueBeforeSystemTimeException {
        if (dueTime.before(creationTime)) {
            throw new DueBeforeSystemTimeException();
        }
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

    public void addTask(Task task) {
        tasks.add(task);
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Time getCreationTime() {
        return creationTime;
    }

    public Time getDueTime() {
        return dueTime;
    }

    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    public String getStatus() {
        for (Task task : tasks) {
            if (!task.isFinished()) {
                return "ongoing";
            }
        }
        return "finished";
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


    public void addTask(String taskName, String description, Time duration, float deviation, List<String> previousTaskNames) {
        // Eerst nog check zeker -> gaan we zeker doen!

        if (getTask(taskName) != null) {
            return;
        }

        List<Task> previousTasks = new ArrayList<>();
        for (String previousTaskName : previousTaskNames) {
            Task task = getTask(previousTaskName);
            if (task == null) {
                return; // TODO
            }
            previousTasks.add(task);
        }

        tasks.add(new Task(taskName, description, duration, deviation, previousTasks));

    }

    public void addAlternativeTask(String taskName, String description, Time duration, float deviation, String replaces){
        if (getTask(taskName) != null) {
            return;
        }
        Task replacesTask = getTask(replaces);
        if (replacesTask == null) {
            return; // TODO
        }
        Task task = new Task(taskName, description, duration, deviation, replacesTask);
    }

    public List<String> showAvailableTasks() {
        List<String> availableTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getStatus() == Status.AVAILABLE) {
                availableTasks.add(task.getName());
            }
        }
        return availableTasks;
    }

    public List<String> showExecutingTasks() {
        List<String> executingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getStatus() == Status.EXECUTING) {
                executingTasks.add(task.getName());
            }
        }
        return executingTasks;
    }
}
