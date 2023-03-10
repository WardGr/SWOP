import java.util.List;

public class Task {
    private String name;
    private String description;
    private Time estimatedDuration;
    private float acceptableDeviation;
    private Status status;
    private Task replacementTask;
    private Task replacesTask;
    private TimeSpan timeSpan;

    public Task(String name, String description, Time estimatedDuration, float acceptableDeviation, List<Task> previousTasks, List<Task> nextTasks, Task replacesTask) {
        this.name = name;
        this.description = description;
        this.estimatedDuration = estimatedDuration;
        this.acceptableDeviation = acceptableDeviation;
        //this.status = new Status();
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

    @Override
    public String toString() {
        return  "Task Name:          " + name                + '\n' +
                "Description:        " + description         + '\n' +
                "Estimated Duration: " + estimatedDuration   + '\n' +
                "Accepted Deviation: " + acceptableDeviation + '\n' +
                "Status:             " + status.toString()   + '\n';
    }

    // TODO: MOET GE EIGENLIJK BIJ EEN STRING RETURNEN OOK COPYOF DOEN? DAS TOCH EEN LIJST?
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

    public boolean isFinished() {
        return status.equals(Status.FINISHED);
    }

    public void setReplacementTask(Task replacementTask) {
        this.replacementTask = replacementTask;
    }
}
