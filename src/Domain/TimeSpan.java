package Domain;

/**
 * A span of time, holding a start time and an end time
 */
public class TimeSpan {

    private Time startTime;
    private Time endTime;

    /**
     * Creates a new TimeSpan object by setting the given start time
     *
     * @param startTime The start time to set for this TimeSpan object
     */
    public TimeSpan(Time startTime) {
        this.startTime = startTime;
    }

    /**
     * @return This TimeSpans' total elapsed time, as a Time object
     */
    public Time getTimeElapsed() {
        try {
            return getEndTime().subtract(getStartTime());
        } catch (InvalidTimeException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return The endTime of this TimeSpan object, as a Time object
     */
    public Time getEndTime() {
        return endTime;
    }

    /**
     * Sets the endTime of this TimeSpan object
     *
     * @param endTime Time object to set as new endtime
     * @throws EndTimeBeforeStartTimeException if the given endtime is before this objects' start time
     */
    public void setEndTime(Time endTime) throws EndTimeBeforeStartTimeException {
        if (endTime != null && getStartTime() != null && endTime.before(getStartTime())) {
            throw new EndTimeBeforeStartTimeException();
        }
        this.endTime = endTime;
    }

    /**
     * @return This objects' startTime, as a Time object
     */
    public Time getStartTime() {
        return startTime;
    }

    /**
     * Overwrites this TimeSpan objects' startTime
     *
     * @param startTime The new startTime to set
     * @throws EndTimeBeforeStartTimeException if the given startTime is after the current endTime
     */
    public void setStartTime(Time startTime) throws EndTimeBeforeStartTimeException {
        if (startTime != null && getEndTime() != null && getEndTime().before(startTime)) {
            throw new EndTimeBeforeStartTimeException();
        }
        this.startTime = startTime;
    }

    /**
     * @return String representing this TimeSpan's start time
     */
    public String showStartTime() {
        Time startTime = getStartTime();
        if (startTime == null) {
            return "No start time set";
        }
        return startTime.toString();
    }

    /**
     * @return String representing this TimeSpan's end time
     */
    public String showEndTime() {
        Time endTime = getEndTime();
        if (endTime == null) {
            return "No end time set";
        }
        return endTime.toString();
    }
}
