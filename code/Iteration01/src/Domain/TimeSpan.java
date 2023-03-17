package Domain;

public class TimeSpan {

    private Time startTime;
    private Time endTime;

    public TimeSpan(Time startTime) {
        this.startTime = startTime;
    }

    public Time getTimeElapsed() {
        return getEndTime().subtract(getStartTime());
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public String showStartTime() {
        Time startTime = getStartTime();
        if (startTime == null) {
            return "No start time set";
        }
        return startTime.toString();
    }

    public String showEndTime() {
        Time endTime = getEndTime();
        if (endTime == null) {
            return "No end time set";
        }
        return endTime.toString();
    }
}
