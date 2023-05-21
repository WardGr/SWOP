package Tests;

import Domain.InvalidTimeException;
import Domain.Time;
import org.junit.Before;
import org.junit.Test;

import java.util.Stack;

import static org.junit.Assert.*;

public class TimeTest {
    private Time time1;
    private Time time2;
    private Time time3;
    private Time time4;
    @Before
    public void setUp() throws InvalidTimeException {
        time1 = new Time(1, 0);
        time2 = new Time(40);
        time3 = new Time(1, 40);
        time4 = new Time(80);
    }
    @Test
    public void testCreateTime(){
        assertThrows(InvalidTimeException.class, () -> new Time(1, 70));
        assertThrows(InvalidTimeException.class, () -> new Time(-1, 9));
        assertThrows(InvalidTimeException.class, () -> new Time(1, -9));
        assertThrows(InvalidTimeException.class, () -> new Time(-100));
    }

    @Test
    public void testGetters(){
        assertEquals(60, time1.getTotalMinutes());
        assertEquals(1, time1.getHour());
        assertEquals(0, time1.getMinute());

        assertEquals(40, time2.getTotalMinutes());
        assertEquals(0, time2.getHour());
        assertEquals(40, time2.getMinute());

        assertEquals(100, time3.getTotalMinutes());
        assertEquals(1, time3.getHour());
        assertEquals(40, time3.getMinute());

        assertEquals(80, time4.getTotalMinutes());
        assertEquals(1, time4.getHour());
        assertEquals(20, time4.getMinute());
    }

    @Test
    public void testBeforeAfter() throws InvalidTimeException {
        assertFalse(time1.before(time2));
        assertTrue(time1.after(time2));
        assertTrue(time2.before(time1));
        assertFalse(time2.after(time1));

        assertFalse(time1.before(new Time(60)));
        assertFalse(time1.after(new Time(60)));
    }

    @Test
    public void testSubstractAdd() throws InvalidTimeException {
        assertEquals(new Time(2,40), time1.add(time3));
        assertEquals(time1.add(time3), time3.add(time1));

        assertEquals(new Time(0), time1.subtract(time1));
        assertEquals(new Time(40), time4.subtract(time2));
        assertThrows(InvalidTimeException.class, () -> time2.subtract(time4));

    }

    @Test
    public void testHashCode(){
        assertEquals(time1.getTotalMinutes(), time1.hashCode());
        assertEquals(time4.getTotalMinutes(), time4.hashCode());
    }

    @Test
    public void testToString(){
        assertEquals("1 hour(s), 0 minute(s)", time1.toString());
        assertEquals("0 hour(s), 40 minute(s)", time2.toString());
        assertEquals("1 hour(s), 40 minute(s)", time3.toString());
        assertEquals("1 hour(s), 20 minute(s)", time4.toString());
    }
}