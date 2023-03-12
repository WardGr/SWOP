import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
public class Time implements Comparable<Time> {
    private final int hour;
    private final int minute;

    public Time(int hour, int minute) throws NotValidTimeException {
        if (hour < 0 || hour > 23 || minute < 0 || minute > 59) { throw new NotValidTimeException();}
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }
    public int getMinute() {return minute;}

    public boolean before(Time time) {
        return this.compareTo(time) < 0;
    }

    public boolean after(Time time) {
        return this.compareTo(time) > 0;
    }

    @Override
    public String toString() {
        return Integer.toString(hour) + ":" + Integer.toString(minute);
    }

    @Override
    public int compareTo(Time other) {
        if (getHour() == other.getHour()){
            if (getMinute() == other.getMinute()) {
                return 0;
            } else if (getMinute() < other.getMinute()){
                return -1;
            }
            return 1;
        }
        if (getHour() < other.getHour()) {
            return -1;
        }
        return 1;
    }

    /*public static Time difference(Time time1, Time time2){
        if (time1.compareTo(time2) < 0){
            return new Time(0,0);
        }
        int hours = time1.getHour() - time2.getHour();
        int minutes;
        if (time1.getMinute() < time2.getMinute()){
            hours -= 1;
            minutes = 60 - (time1.getMinute() - time2.getMinute());
        } else {
            minutes = time1.getMinute() - time2.getMinute();
        }
        return new Time(hours,minutes);
    }*/
}