package Tests;

import Domain.EndTimeBeforeStartTimeException;
import Domain.InvalidTimeException;
import Domain.Time;
import Domain.TimeSpan;
import org.junit.Test;

import static org.junit.Assert.*;

public class TimeSpanTest {
    @Test
    public void testCreation() throws InvalidTimeException {
        TimeSpan timeSpan = new TimeSpan(null);
        assertNull(timeSpan.getStartTime());
        assertNull(timeSpan.getEndTime());
        assertNull(timeSpan.getTimeElapsed());

        TimeSpan timeSpan2 = new TimeSpan(new Time(50));
        assertEquals(new Time(50), timeSpan2.getStartTime());
        assertNull(timeSpan2.getEndTime());
        assertNull(timeSpan2.getTimeElapsed());
    }

    @Test
    public void testStartTime() throws InvalidTimeException, EndTimeBeforeStartTimeException {
        TimeSpan timeSpan = new TimeSpan(null);

        timeSpan.setStartTime(new Time(20));
        assertEquals(new Time(20), timeSpan.getStartTime());
        assertNull(timeSpan.getEndTime());
        assertNull(timeSpan.getTimeElapsed());

        timeSpan.setEndTime(new Time(20));
        assertEquals(new Time(0), timeSpan.getTimeElapsed());

        assertThrows(EndTimeBeforeStartTimeException.class, () -> timeSpan.setStartTime(new Time(25)));
        timeSpan.setStartTime(new Time(15));
        assertEquals(new Time(15), timeSpan.getStartTime());
        assertEquals(new Time(20), timeSpan.getEndTime());
        assertEquals(new Time(5), timeSpan.getTimeElapsed());

        timeSpan.setStartTime(null);
        assertNull(timeSpan.getStartTime());
        assertEquals(new Time(20), timeSpan.getEndTime());
        assertNull(timeSpan.getTimeElapsed());
    }

    @Test
    public void testEndTime() throws InvalidTimeException, EndTimeBeforeStartTimeException {
        TimeSpan timeSpanNoStartTime = new TimeSpan(null);
        timeSpanNoStartTime.setEndTime(new Time(50));

        assertEquals(new Time(50), timeSpanNoStartTime.getEndTime());
        assertNull(timeSpanNoStartTime.getStartTime());
        assertNull(timeSpanNoStartTime.getTimeElapsed());

        TimeSpan timeSpan = new TimeSpan(new Time(50));
        assertThrows(EndTimeBeforeStartTimeException.class, () -> timeSpan.setEndTime(new Time(40)));

        timeSpan.setEndTime(new Time(60));
        assertEquals(new Time(50), timeSpan.getStartTime());
        assertEquals(new Time(60), timeSpan.getEndTime());
        assertEquals(new Time(10), timeSpan.getTimeElapsed());

        timeSpan.setEndTime(null);
        assertEquals(new Time(50), timeSpan.getStartTime());
        assertNull(timeSpan.getEndTime());
        assertNull(timeSpan.getTimeElapsed());
    }

    @Test
    public void testShowTimes() throws InvalidTimeException, EndTimeBeforeStartTimeException {
        TimeSpan timeSpan = new TimeSpan(null);

        assertEquals("No start time set", timeSpan.showStartTime());
        assertEquals("No end time set", timeSpan.showEndTime());

        timeSpan.setStartTime(new Time(5));
        timeSpan.setEndTime(new Time(20));

        assertEquals(new Time(5).toString(), timeSpan.showStartTime());
        assertEquals(new Time(20).toString(), timeSpan.showEndTime());
    }
}
