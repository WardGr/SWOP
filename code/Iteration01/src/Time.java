import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
/*public class Time {
    // TODO eens goed bekijke, dit gaat tijd nemen vanaf het moment dat syssteem aanstaat
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private final LocalTime now;
    public Time() {
        now = LocalTime.now(ZoneOffset.UTC);
        System.out.println(java.time.LocalTime.now());
    }

    public String getTime() {
        return dtf.format(now);
    }

}
*/

public class Time {
    private final long beginTime = System.currentTimeMillis();
    private final long endTime;
    public Time(long duration) {
        endTime = beginTime + duration;
    }

    private long getTime() {
        return System.currentTimeMillis();
    }

    public boolean inTime() {
        return getTime() < endTime;
    }

    public long timeLeft() {
        return endTime - getTime();
    }

}