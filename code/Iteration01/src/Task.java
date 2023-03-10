import java.util.LinkedList;
import java.util.List;

public class Task {
    private String name;
    private String description;
    private Time estimatedDuration;
    private double acceptableDeviation;
    public Status status; // TODO: HAAL DIT ALSJEBLIEFT WEG (DIE PUBLIC)

    private Task replacementTask;
    private Task replacesTask;

    private List<Task> previousTasks;
    private List<Task> nextTasks;

    private TimeSpan timeSpan;

    public Task(String name, String description, Time estimatedDuration, double acceptableDeviation, List<Task> previousTasks) {
        this.name = name;
        this.description = description;

        this.estimatedDuration = estimatedDuration;
        this.acceptableDeviation = acceptableDeviation;

        this.replacementTask = null;
        this.replacesTask = null;

        this.previousTasks = previousTasks;
        this.nextTasks = new LinkedList<>();

        boolean available = true;
        for (Task task : previousTasks) {
            task.addNextTask(this);
            if (!task.isFinished()) {
                available = false;
            }
        }

        if (available) {
            status = Status.AVAILABLE;
        }
        else {
            status = Status.UNAVAILABLE;
        }
    }

    public Task(String taskName, String description, Time duration, double deviation, Task replacesTask) throws ReplacedTaskNotFailedException {
        if (replacesTask.getStatus() != Status.FAILED) {
            throw new ReplacedTaskNotFailedException();
        }

        this.name = taskName;
        this.description = description;
        this.estimatedDuration = duration;
        this.acceptableDeviation = deviation;
        this.replacesTask = replacesTask;
        this.status = Status.AVAILABLE;

        this.previousTasks = replacesTask.getPreviousTasks();
        this.nextTasks = replacesTask.getNextTasks();

        replacesTask.setReplacementTask(this);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        String info = "Task Name:          " + name                          + '\n' +
                      "Description:        " + description                   + '\n' +
                      "Estimated Duration: " + estimatedDuration.getTime()   + '\n' +
                      "Accepted Deviation: " + acceptableDeviation           + '\n' +
                      "Status:             " + status.toString()             + "\n\n" +
                      "Replacement Task:   " + showReplacementTaskName()     + '\n' +
                      "Replaces Task:      " + showReplacesTaskName()        + "\n\n" +
                      "Start Time:         " + showStartTime()               + '\n' +
                      "End Time:           " + showEndTime()                 + "\n\n";

        stringBuilder.append(info);
        stringBuilder.append("Next tasks:\n");
        int i = 1;
        for (Task task : nextTasks) {
            stringBuilder.append(i++).append(".").append(task.getName()).append('\n');
        }

        stringBuilder.append("Previous tasks:\n");
        i = 1;
        for (Task task : previousTasks) {
            stringBuilder.append(i++).append(".").append(task.getName()).append('\n');
        }

        return stringBuilder.toString();
    }

    private String showReplacementTaskName() {
        if (replacementTask == null) {
            return "No replacement task";
        }
        return replacementTask.getName();
    }

    private String showReplacesTaskName() {
        if (replacesTask == null) {
            return "Replaces no tasks";
        }
        return replacesTask.getName();
    }


    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    private Time getStartTime(){
        return timeSpan.getStartTime();
    }

    private Time getEndTime() {
        return timeSpan.getEndTime();
    }

    private String showStartTime() {
        if (timeSpan == null) {
            return "No start time set";
        }
        return timeSpan.getStartTime().toString();
    }

    private String showEndTime() {
        if (timeSpan == null) {
            return "No end time set";
        }
        return timeSpan.getEndTime().toString();
    }



    private void addNextTask(Task task) {
        nextTasks.add(task);
    }

    public boolean isFinished() {
        return status.equals(Status.FINISHED);
    }

    public void setReplacementTask(Task replacementTask) {
        this.replacementTask = replacementTask;
    }

    public void setReplacesTask(Task task) {
        this.replacesTask = task;
    }

    private List<Task> getPreviousTasks() {
        return List.copyOf(previousTasks);
    }

    public List<Task> getNextTasks() {
        return List.copyOf(nextTasks);
    }
}
