import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Task {
    private String name;
    private String description;
    private Time estimatedDuration;
    private double acceptableDeviation;
    private Status status;

    private Task replacementTask;
    private Task replacesTask;

    private List<Task> previousTasks;
    private List<Task> nextTasks;

    private TimeSpan timeSpan;

    private User user;

    public Task(String name, String description, Time estimatedDuration, double acceptableDeviation, List<Task> previousTasks, User user) {
        this.name = name;
        this.description = description;

        this.estimatedDuration = estimatedDuration;
        this.acceptableDeviation = acceptableDeviation;

        this.replacementTask = null;
        this.replacesTask = null;

        this.previousTasks = previousTasks;
        this.nextTasks = new LinkedList<>();

        this.user = user;

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

    public Task(String taskName, String description, Time duration, double deviation, Task replacesTask, User currentUser) throws ReplacedTaskNotFailedException {
        if (replacesTask.getStatus() != Status.FAILED) {
            throw new ReplacedTaskNotFailedException();
        }
        if (replacesTask.getUser() != currentUser){
            // TODO exception
        }

        this.name = taskName;
        this.description = description;
        this.estimatedDuration = duration;
        this.acceptableDeviation = deviation;
        this.replacesTask = replacesTask;
        this.status = Status.AVAILABLE;
        this.user = replacesTask.getUser();

        this.previousTasks = replacesTask.getPreviousTasks();
        for (Task task : previousTasks){
            task.replaceNextTask(replacesTask, this);
        }
        this.nextTasks = replacesTask.getNextTasks();
        for (Task task : nextTasks){
            task.replacePreviousTask(replacesTask, this);
        }

        replacesTask.setReplacementTask(this);
    }

    private void replaceNextTask(Task oldTask, Task newTask){
        nextTasks.remove(oldTask);
        nextTasks.add(newTask);
    }

    private void replacePreviousTask(Task oldTask, Task newTask){
        previousTasks.remove(oldTask);
        previousTasks.add(newTask);
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
                      "End Time:           " + showEndTime()                 + "\n\n" +
                      "User:               " + user.getUsername()            + "\n\n";

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

        // TODO de early, on time, of delay berekenen en laten zien

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

    private User getUser() { return user; };

    private String showStartTime() {
        if (timeSpan == null) {
            return "No start time set";
        }
        return timeSpan.showStartTime();
    }

    private String showEndTime() {
        if (timeSpan == null) {
            return "No end time set";
        }
        return timeSpan.showEndTime();
    }



    private void addNextTask(Task task) {
        nextTasks.add(task);
    }

    public boolean isFinished() {
        return status.equals(Status.FINISHED);
    }

    private void setReplacementTask(Task replacementTask) {
        this.replacementTask = replacementTask;
    }

    private List<Task> getPreviousTasks() {
        return List.copyOf(previousTasks);
    }

    public List<Task> getNextTasks() {
        return List.copyOf(nextTasks);
    }

    public List<Status> getNextStatuses() {
        List<Status> statuses = new ArrayList<>();
        switch (getStatus()){
            case AVAILABLE -> {
                statuses.add(Status.EXECUTING);
                return statuses;
            }
            case EXECUTING -> {
                statuses.add(Status.FINISHED);
                statuses.add(Status.FAILED);
                return statuses;
            }
            default -> {}
        }
        return statuses;

    }

    private void setStatus(Status status){
        this.status = status;
    }

    private void setStartTime(Time startTime){
        TimeSpan timeSpan = getTimeSpan();
        if (timeSpan == null) {
            setTimeSpan(new TimeSpan(startTime));
        } else {
            timeSpan.setStartTime(startTime);
        }
    }

    private void setEndTime(Time endTime){
        TimeSpan timeSpan = getTimeSpan();
        // als het systeem al Executing is moet er eigenlijk al een starttime zijn en dus een TimeSpan
        if (timeSpan == null) {
            return; // TODO
        }
        timeSpan.setEndTime(endTime);
    }

    private TimeSpan getTimeSpan(){
        return this.timeSpan;
    }

    private void setTimeSpan(TimeSpan timeSpan){
        this.timeSpan = timeSpan;
    }

    public void fail(){
        this.setStatus(Status.FAILED);
    }

    public void start(Time startTime, Time systemTime, User currentUser){
        if (getUser() != currentUser){
            // TODO exception
        }
        if (getStatus() != Status.AVAILABLE){
            // TODO exception
        }
        setStartTime(startTime);
        if (!systemTime.before(startTime)){
            setStatus(Status.EXECUTING);
        }
    }

    public void end(Status newStatus, Time endTime, Time systemTime, User currentUser){
        if (getUser() != currentUser){
            // TODO exception
        }
        if (getStatus() != Status.EXECUTING){
            // TODO exception
        }
        if (newStatus == Status.FAILED){
            if (systemTime.before(endTime)){
                // TODO exception
            }
            setStatus(Status.FAILED);
        } else if (newStatus == Status.FINISHED){
            if (!systemTime.before(endTime)){
                setStatus(Status.FINISHED);
                for (Task nextTask : getNextTasks()){
                    nextTask.checkAvailable();
                }
            }
        }
        setEndTime(endTime);
    }

    public void advanceTime(Time newTime){
        Status status = getStatus();
        switch (status){
            case EXECUTING -> {
                if (!newTime.before(getEndTime())){
                    setStatus(Status.FINISHED);
                    for (Task nextTask : getNextTasks()){
                        nextTask.checkAvailable();
                    }
                }
            }
            case AVAILABLE -> {
                if (!newTime.before(getStartTime())) {
                    setStatus(Status.EXECUTING);
                }
            }
        }
    }

    private void checkAvailable(){
        if (getStatus() != Status.UNAVAILABLE){
            return;
        }
        for (Task task : previousTasks){
            if (task.getStatus() != Status.FINISHED){
                return;
            }
        }
        setStatus(Status.AVAILABLE);
    }
}
