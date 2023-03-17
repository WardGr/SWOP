import Application.AdvanceTimeController;
import Application.IncorrectPermissionException;
import Application.Session;
import Domain.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class AdvanceTimeControllerTest {

    @Test
    public void testAdvanceTimeController() throws InvalidTimeException, IncorrectPermissionException, NewTimeBeforeSystemTimeException {
        Session omer = new Session();
        User brewer = new User("OlavBl", "toilet753", Role.DEVELOPER);
        User boss = new User("WardGr", "minecraft123", Role.PROJECTMANAGER);
        omer.login(boss);

        Time systime = new Time(0);
        TaskManSystem tms = new TaskManSystem(systime);

        AdvanceTimeController atc = new AdvanceTimeController(omer, tms);
        assertEquals(0, atc.getSystemHour());
        assertEquals(0, atc.getSystemMinute());
        assertTrue(atc.advanceTimePreconditions());

        atc.setNewTime(20, 17);
        assertEquals(20, atc.getSystemHour());
        assertEquals(17, atc.getSystemMinute());

        atc.setNewTime(33, 58);
        assertEquals(33, atc.getSystemHour());
        assertEquals(58, atc.getSystemMinute());

        Exception exception = assertThrows(InvalidTimeException.class, () -> {
            atc.setNewTime(100, 60);
        });

        exception = assertThrows(InvalidTimeException.class, () -> {
            atc.setNewTime(100, -1);
        });

        exception = assertThrows(NewTimeBeforeSystemTimeException.class, () -> {
            atc.setNewTime(2, 58);
        });

        exception = assertThrows(NewTimeBeforeSystemTimeException.class, () -> {
            atc.setNewTime(33, 57);
        });

        omer.logout();
        omer.login(brewer);
        exception = assertThrows(IncorrectPermissionException.class, () -> {
            atc.setNewTime(100, 60);
        });

        assertEquals(33, atc.getSystemHour());
        assertEquals(58, atc.getSystemMinute());
    }
}
