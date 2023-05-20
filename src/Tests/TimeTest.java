package Tests;

import Domain.InvalidTimeException;
import Domain.Time;
import org.junit.Test;

import java.util.Stack;

import static org.junit.Assert.*;

public class TimeTest {

    @Test
    public void timeTest() throws InvalidTimeException {
        assertThrows(InvalidTimeException.class, () -> new Time(1, 70));
        assertThrows(InvalidTimeException.class, () -> new Time(-1, 9));
        assertThrows(InvalidTimeException.class, () -> new Time(-100));

        Time time1 = new Time(1, 0);
        Time time2 = new Time(0, 40);
        Time time3 = new Time(1, 40);

        Time addition = time1.add(time2);

        assertEquals(time3, addition);
        assertEquals(time3.hashCode(), addition.hashCode());

        assertEquals(time3, new Time(100));
        assertEquals(time3.hashCode(), new Time(100).hashCode());

        assertEquals(new Time(20), time1.subtract(time2));
        assertTrue(time2.before(time1));
        assertFalse(time2.after(time1));

        assertFalse(time1.after(time1));
        assertFalse(time1.before(time1));

        assertTrue(time1.before(time3));
        assertTrue(time3.after(time1));

        assertEquals("1 hour(s), 40 minute(s)", time3.toString());
    }
}