package Domain;

/**
 * A span of time, holding a start time and an end time
 */
public class TimeSpan {

    private Time startTime;
    private Time endTime;

    public TimeSpan(Time startTime) {
        this.startTime = startTime;
    }

    public Time getTimeElapsed() {
        try{
            return getEndTime().subtract(getStartTime());
        } catch (InvalidTimeException e) {
            throw new RuntimeException(e); // TODO: deze exception zou nooit kunnen volgens invarianten
        }
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) throws EndTimeBeforeStartTimeException {
        if (endTime != null && getStartTime() != null && endTime.before(getStartTime())) {
            throw new EndTimeBeforeStartTimeException();
        }
        this.endTime = endTime;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) throws EndTimeBeforeStartTimeException {
        if (startTime != null && getEndTime() != null && getEndTime().before(startTime)){
            throw new EndTimeBeforeStartTimeException();
        }
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
