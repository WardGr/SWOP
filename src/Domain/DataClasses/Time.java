package Domain.DataClasses;

/**
 * A timestamp
 */
public class Time implements Comparable<Time> {

    private final int hour;
    private final int minute;

    /**
     * Creates a new time object with the given hours and minutes
     *
     * @param hour   Hours to set this Time object to
     * @param minute Minutes to set this Time object to
     * @throws InvalidTimeException if hour < 0 or minute < 0 or minute > 59
     */
    public Time(int hour, int minute) throws InvalidTimeException {
        if (hour < 0 || minute < 0 || minute > 59) {
            throw new InvalidTimeException();
        }
        this.hour = hour;
        this.minute = minute;
    }

    /**
     * Creates a new Time object, calculated from the given total minutes
     *
     * @param totalMinutes The total amount of minutes this Time object must represent
     * @throws InvalidTimeException if totalMinutes < 0
     * @post getHour() == totalMinutes / 60
     * @post getMinute() == totalMinutes % 60
     */
    public Time(int totalMinutes) throws InvalidTimeException {
        if (totalMinutes < 0) {
            throw new InvalidTimeException();
        }
        this.hour = totalMinutes / 60;
        this.minute = totalMinutes % 60;
    }

    /**
     * @return getHour() * 60 + getMinute()
     */
    public int getTotalMinutes() {
        return getHour() * 60 + getMinute();
    }

    /**
     * @return This time objects' hour (a positive integer)
     */
    public int getHour() {
        return hour;
    }

    /**
     * @return This time objects minute (a positive integer from 0 to and including 59)
     */
    public int getMinute() {
        return minute;
    }

    /**
     * Checks if the given Time object depicts a time before or after this object
     *
     * @param time The time object to compare to this object
     * @return true if the given time is before this objects time
     */
    public boolean before(Time time) {
        return this.compareTo(time) < 0;
    }

    /**
     * Checks if the given Time object depicts a time before or after this object
     *
     * @param time The time object to compare to this object
     * @return true if the given time is before this objects time
     */
    public boolean after(Time time) {
        return this.compareTo(time) > 0;
    }

    /**
     * @return A string depicting this Time object, of the format "getHour() hour(s) + getMinute() + minute(s)"
     */
    @Override
    public String toString() {
        return getHour() + " hour(s), " + getMinute() + " minute(s)";
    }

    /**
     * @param obj Object to compare
     * @return false if obj is not a Time object or obj.getTotalMinutes() != getTotalMinutes(),
     * else true
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Time && ((Time) obj).getTotalMinutes() == getTotalMinutes();
    }

    /**
     * @return getTotalMinutes()
     */
    @Override
    public int hashCode() {
        return getTotalMinutes();
    }

    /**
     * Compares this Time object with another object
     *
     * @param other the Time object to be compared.
     * @return -1 if this.getTotalMinutes() < other.getTotalMinutes()
     * 0 if this.getTotalMinutes() == other.getTotalMinutes()
     * 1 if this.getTotalMinutes() > other.getTotalMinutes()
     */
    @Override
    public int compareTo(Time other) {
        return Integer.compare(getTotalMinutes(), other.getTotalMinutes());
    }

    /**
     * Creates a new Time object that depicts the subtraction of this objects' time and the given objects time
     *
     * @param subtrahend The time object to subtract with this object
     * @return A Time object depicting the subtraction of this objects' time and the given time
     * @throws InvalidTimeException if startTime.before(this)
     */
    public Time subtract(Time subtrahend) throws InvalidTimeException {
        return new Time(this.getTotalMinutes() - subtrahend.getTotalMinutes());
    }

    /**
     * Creates a new Time object that depicts the addition of this objects' time and the given objects time
     *
     * @param addend The time object to add with this object
     * @return A Time object depicting the addition of this objects' time and the given time
     */
    public Time add(Time addend) throws InvalidTimeException {
        return new Time(this.getTotalMinutes() + addend.getTotalMinutes());
    }
}
