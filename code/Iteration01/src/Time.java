import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
public class Time {
    //TODO kunnen we deze niet als "comparable" ofzo maken?

    int time;

    public Time(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public boolean before(Time time) {
        return (getTime() < time.getTime());
    }

    @Override
    public String toString() {
        return Integer.toString(time);
    }
}