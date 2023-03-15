import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class TimeTest {
    @Test
    public void testTime() throws InvalidTimeException {
        Time time1 = new Time(23, 17);
        Time time2 = new Time(360);
        Time time3 = new Time(0, 0);
        Time time4 = new Time(360, 22);
        assertEquals(23, time1.getHour());
        assertEquals(17, time1.getMinute());
        assertEquals(6, time2.getHour());
        assertEquals(0, time2.getMinute());
        String string1 = "23 hours, 17 minutes";
        String string2 = "6 hours, 0 minutes";
        String string3 = "0 hours, 0 minutes";
        String string4 = "360 hours, 22 minutes";

        Exception exception = assertThrows(InvalidTimeException.class, () -> {
            Time time5 = new Time(23, 60);
        });

        Exception exception1 = assertThrows(InvalidTimeException.class, () -> {
            Time time6 = new Time(24, -3);
        });

        assertFalse(time1.before(time2));
        assertFalse(time2.after(time1));
        assertTrue(time3.before(time1));
        assertTrue(time3.before(time2));
        assertTrue(time2.after(time3));
        assertTrue(time1.after(time3));
        assertTrue(time4.after(time1));
        assertTrue(time4.after(time2));
        assertTrue(time4.after(time3));
        assertFalse(time4.before(time1));
        assertFalse(time4.before(time2));
        assertFalse(time4.before(time3));
        assertEquals(string1, time1.toString());
        assertEquals(string2, time2.toString());
        assertEquals(string3, time3.toString());
        assertEquals(string4, time4.toString());
    }
}
