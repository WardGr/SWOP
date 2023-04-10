package Tests;

import Application.IncorrectPermissionException;
import Domain.InvalidTimeException;
import Domain.NewTimeBeforeSystemTimeException;
import org.junit.Test;

public class AdvanceTimeControllerTest {

    @Test
    public void testAdvanceTimeController() throws InvalidTimeException, IncorrectPermissionException, NewTimeBeforeSystemTimeException {
        /*
        Session omer = new Session();
        SessionWrapper omerWrapper = new SessionWrapper(omer);
        User brewer = new User("OlavBl", "peer123", Role.DEVELOPER);
        User boss = new User("WardGr", "minecraft123", Role.PROJECTMANAGER);
        omer.login(boss);

        Time systime = new Time(0);
        TaskManSystem tms = new TaskManSystem(systime);

        AdvanceTimeController atc = new AdvanceTimeController(omerWrapper, tms);
        assertEquals(0, atc.getSystemHour());
        assertEquals(0, atc.getSystemMinute());
        assertTrue(atc.advanceTimePreconditions());

        atc.setNewTime(20, 17);
        assertEquals(20, atc.getSystemHour());
        assertEquals(17, atc.getSystemMinute());

        atc.setNewTime(33, 58);
        assertEquals(33, atc.getSystemHour());
        assertEquals(58, atc.getSystemMinute());

        assertThrows(InvalidTimeException.class, () -> {
            atc.setNewTime(100, 60);
        });

        assertThrows(InvalidTimeException.class, () -> {
            atc.setNewTime(100, -1);
        });

        assertThrows(NewTimeBeforeSystemTimeException.class, () -> {
            atc.setNewTime(2, 58);
        });

        assertThrows(NewTimeBeforeSystemTimeException.class, () -> {
            atc.setNewTime(33, 57);
        });

        assertThrows(NewTimeBeforeSystemTimeException.class, () -> {
            atc.setNewTime(-10);
        });

        omer.logout();
        omer.login(brewer);
        atc.setNewTime(100, 59);

        assertEquals(100, atc.getSystemHour());
        assertEquals(59, atc.getSystemMinute());

        */
    }
}
