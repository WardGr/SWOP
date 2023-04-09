package Tests;

import Domain.EndTimeBeforeStartTimeException;
import Domain.InvalidTimeException;
import Domain.TaskStates.NonDeveloperRoleException;
import Domain.Time;
import Domain.TimeSpan;
import org.junit.Test;

import static org.junit.Assert.*;

public class TimeSpanTest {
    @Test
    public void testTimeSpan() throws InvalidTimeException, EndTimeBeforeStartTimeException {
        TimeSpan brewery = new TimeSpan(new Time(10, 55));
        TimeSpan pilsner = new TimeSpan(new Time(15, 22));

        assertEquals("10 hour(s), 55 minute(s)", brewery.showStartTime());
        assertEquals("No end time set", brewery.showEndTime());

        assertEquals("15 hour(s), 22 minute(s)", pilsner.showStartTime());
        assertEquals("No end time set", pilsner.showEndTime());

        assertNotSame(brewery, pilsner);
        brewery.setStartTime(new Time(12, 55));
        assertEquals("12 hour(s), 55 minute(s)", brewery.showStartTime());
        assertEquals("No end time set", brewery.showEndTime());

        brewery.setEndTime(new Time(22, 0));
        brewery.setStartTime(new Time(17, 55));
        assertEquals("17 hour(s), 55 minute(s)", brewery.showStartTime());
        assertEquals("22 hour(s), 0 minute(s)", brewery.showEndTime());

        pilsner.setEndTime(new Time(17, 0));
        assertEquals("15 hour(s), 22 minute(s)", pilsner.showStartTime());
        assertEquals("17 hour(s), 0 minute(s)", pilsner.showEndTime());

        brewery.setStartTime(null);
        assertEquals("No start time set", brewery.showStartTime());
        assertEquals("22 hour(s), 0 minute(s)", brewery.showEndTime());

        brewery.setStartTime(new Time(17, 55));

        assertEquals(new Time(1, 38).getHour(), pilsner.getTimeElapsed().getHour());
        assertEquals(new Time(1, 38).getMinute(), pilsner.getTimeElapsed().getMinute());

        assertEquals(new Time(4, 5).getHour(), brewery.getTimeElapsed().getHour());
        assertEquals(new Time(4, 5).getMinute(), brewery.getTimeElapsed().getMinute());

        TimeSpan exceptionTimeSpan = new TimeSpan(new Time(10));
        assertThrows(EndTimeBeforeStartTimeException.class, () -> exceptionTimeSpan.setEndTime(new Time(5)));
        exceptionTimeSpan.setEndTime(new Time(15));
        assertThrows(EndTimeBeforeStartTimeException.class, () -> exceptionTimeSpan.setStartTime(new Time(20)));



    }
}
