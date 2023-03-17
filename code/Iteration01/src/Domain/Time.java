package Domain;

public class Time implements Comparable<Time> {

    private final int hour;
    private final int minute;

    public Time(int hour, int minute) throws InvalidTimeException {
        if (minute < 0 || minute > 59) {
            throw new InvalidTimeException();
        }
        this.hour = hour;
        this.minute = minute;
    }

    public Time(int totalMinutes) {
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
        return getHour() + " hours, " + getMinute() + " minutes";
    }

    @Override
    public int compareTo(Time other) {
        if (getHour() == other.getHour()) {
            if (getMinute() == other.getMinute()) {
                return 0;
            } else if (getMinute() < other.getMinute()) {
                return -1;
            }
            return 1;
        }
        if (getHour() < other.getHour()) {
            return -1;
        }
        return 1;
    }

    public Time subtract(Time startTime) {
        return new Time(this.getTotalMinutes() - startTime.getTotalMinutes());
    }
}
