import java.util.LinkedList;
import java.util.List;

public class Task {
    private String name;
    private String description;
    private Time estimatedDuration;
    private float acceptableDeviation;
    private Status status;

    private Task replacementTask;
    private Task replacesTask;

    private List<Task> previousTasks;
    private List<Task> nextTasks;

    private TimeSpan timeSpan;

    public Task(String name, String description, Time estimatedDuration, float acceptableDeviation, List<Task> previousTasks) {
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

        /*
        if (replacesTask != null) {
            replacesTask.setReplacementTask(this);
            if (replacesTask.getStatus() == Status.FAILED){
                boolean allPreviousFinished = true;
                for (Task previousTask : previousTasks) {
                    if (previousTask.getStatus() != Status.FINISHED) {
                        allPreviousFinished = false;
                        break; //goed?
                    }
                }
                if (allPreviousFinished) {
                    this.status = Status.EXECUTING;
                }

                this.status = Status.AVAILABLE;
                this.timeSpan = new TimeSpan(replacesTask.getStartTime());
            } else {
                this.status = Status.UNAVAILABLE;
            }
        } else {
            this.status =
        }
        */

    }

    public Task(String taskName, String description, Time duration, float deviation) {
        this.name = taskName;
        this.description = description;
        this.estimatedDuration = duration;
        this.acceptableDeviation = deviation;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        String info = "Task Name:          " + name                          + '\n' +
                      "Description:        " + description                   + '\n' +
                      "Estimated Duration: " + estimatedDuration.getTime()   + '\n' +
                      "Accepted Deviation: " + acceptableDeviation           + '\n' +
                      "Status:             " + status.toString()             + '\n' +
                      "Replacement Task:   " + showReplacementTaskName()     + '\n' +
                      "Replaces Task:      " + showReplacesTaskName()        + '\n' +
                      "Start Time:         " + getStartTime().getTime()      + '\n' +
                      "End Time:           " + getEndTime().getTime()        + '\n';

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
        return replacementTask.getName();
    }


    public String getName() {
        return name;
    }

    private Status getStatus() {
        return status;
    }

    private Time getStartTime(){
        return timeSpan.getStartTime();
    }

    private Time getEndTime() {
        return timeSpan.getEndTime();
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

    public void addReplacementTask(String taskName, String description, Time duration, float deviation) {
        Task replacementTask = new Task(taskName, description, duration, deviation);
        this.setReplacementTask(replacementTask);
        replacementTask.setReplacesTask(this);
    }
}
