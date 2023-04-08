package Tests;

import Domain.InvalidTimeException;
import Domain.Time;
import Domain.TimeSpan;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class TimeSpanTest {
    @Test
    public void testTimeSpan() throws InvalidTimeException {
        TimeSpan brewery = new TimeSpan(new Time(10, 55));
        TimeSpan pilsner = new TimeSpan(new Time(15, 22));

        assertEquals("10 hours, 55 minutes", brewery.showStartTime());
        assertEquals("No end time set", brewery.showEndTime());

        assertEquals("15 hours, 22 minutes", pilsner.showStartTime());
        assertEquals("No end time set", pilsner.showEndTime());

        assertNotSame(brewery, pilsner);
        brewery.setStartTime(new Time(12, 55));
        assertEquals("12 hours, 55 minutes", brewery.showStartTime());
        assertEquals("No end time set", brewery.showEndTime());

        brewery.setEndTime(new Time(22, 0));
        brewery.setStartTime(new Time(17, 55));
        assertEquals("17 hours, 55 minutes", brewery.showStartTime());
        assertEquals("22 hours, 0 minutes", brewery.showEndTime());

        pilsner.setEndTime(new Time(17, 0));
        assertEquals("15 hours, 22 minutes", pilsner.showStartTime());
        assertEquals("17 hours, 0 minutes", pilsner.showEndTime());

        brewery.setStartTime(null);
        assertEquals("No start time set", brewery.showStartTime());
        assertEquals("22 hours, 0 minutes", brewery.showEndTime());

        brewery.setStartTime(new Time(17, 55));

        assertEquals(new Time(1, 38).getHour(), pilsner.getTimeElapsed().getHour());
        assertEquals(new Time(1, 38).getMinute(), pilsner.getTimeElapsed().getMinute());

        assertEquals(new Time(4, 5).getHour(), brewery.getTimeElapsed().getHour());
        assertEquals(new Time(4, 5).getMinute(), brewery.getTimeElapsed().getMinute());



    }
}
