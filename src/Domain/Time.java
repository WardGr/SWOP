package Domain;

/**
 * A timestamp
 */
public class Time implements Comparable<Time> {

    private final int hour;
    private final int minute;

    public Time(int hour, int minute) throws InvalidTimeException {
        if (hour < 0 || minute < 0 || minute > 59) {
            throw new InvalidTimeException();
        }
        this.hour = hour;
        this.minute = minute;
    }

    public Time(int totalMinutes) throws InvalidTimeException {
        if (totalMinutes < 0){
            throw new InvalidTimeException();
        }
        this.hour = totalMinutes / 60;
        this.minute = totalMinutes % 60;
    }

    public int getTotalMinutes() {
        return getHour() * 60 + getMinute();
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public boolean before(Time time) {
        return this.compareTo(time) < 0;
    }

    public boolean after(Time time) {
        return this.compareTo(time) > 0;
    }

    @Override
    public String toString() {
        return getHour() + " hour(s), " + getMinute() + " minute(s)";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Time && ((Time) obj).getTotalMinutes() == getTotalMinutes();
    }

    @Override
    public int hashCode() {
        return getTotalMinutes();
    }

    @Override
    public int compareTo(Time other) {
        return Integer.compare(getTotalMinutes(), other.getTotalMinutes());
    }

    public Time subtract(Time startTime) throws InvalidTimeException {
        return new Time(this.getTotalMinutes() - startTime.getTotalMinutes());
    }
    public Time add(Time startTime) throws InvalidTimeException {
        return new Time(this.getTotalMinutes() + startTime.getTotalMinutes());
    }
}
