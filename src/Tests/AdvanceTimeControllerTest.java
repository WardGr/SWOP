package Tests;

import Application.IncorrectPermissionException;
import Application.*;
import Domain.*;
import Domain.InvalidTimeException;
import Domain.NewTimeBeforeSystemTimeException;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class AdvanceTimeControllerTest {

    @Test
    public void testAdvanceTimeController() throws InvalidTimeException, IncorrectPermissionException, NewTimeBeforeSystemTimeException {
        // Idee is dat Ward met PROJECTMAN en JAVAPROGRAMMER in omer zit, olav zit met SYSADMIN in duvel en Dieter met JAVA- en PYTHONPROGRAMMER in chouffe
        Set<Role> WardRoles = new HashSet<>();
        WardRoles.add(Role.PROJECTMANAGER);
        WardRoles.add(Role.JAVAPROGRAMMER);

        Set<Role> OlavRoles = new HashSet<>();
        OlavRoles.add(Role.SYSADMIN);

        Set<Role> DieterRoles = new HashSet<>();
        DieterRoles.add(Role.JAVAPROGRAMMER);
        DieterRoles.add(Role.PYTHONPROGRAMMER);

        User ward = new User("WardGr", "peer123", WardRoles);
        User olav = new User("OlavBl", "peer123", OlavRoles);
        User dieter = new User("DieterVh", "peer123", DieterRoles);
        Session omer = new Session();
        omer.login(ward);
        Session duvel = new Session();
        duvel.login(olav);
        Session chouffe = new Session();
        chouffe.login(dieter);
        Session unemplSession = new Session();

        TaskManSystem tmsOmer = new TaskManSystem(new Time(12));
        TaskManSystem tmsDuvel = new TaskManSystem(new Time(17));
        TaskManSystem tmsChouffe = new TaskManSystem(new Time(63));

        AdvanceTimeController atcOmer = new AdvanceTimeController(new SessionWrapper(omer), tmsOmer);
        AdvanceTimeController atcDuvel = new AdvanceTimeController(new SessionWrapper(duvel), tmsDuvel);
        AdvanceTimeController atcChouffe = new AdvanceTimeController(new SessionWrapper(chouffe), tmsChouffe);

        // Testen of de getSystemTime werkt
        assertEquals(new Time(12), atcOmer.getSystemTime());
        assertEquals(new Time(17), atcDuvel.getSystemTime());
        assertEquals(new Time(63), atcChouffe.getSystemTime());


        // Testen of de preconditions werken
        assertTrue(atcOmer.advanceTimePreconditions());
        assertTrue(atcDuvel.advanceTimePreconditions());
        assertTrue(atcChouffe.advanceTimePreconditions());

        omer.logout();
        assertFalse(atcOmer.advanceTimePreconditions());
        omer.login(ward);

        duvel.logout();
        assertFalse(atcDuvel.advanceTimePreconditions());
        duvel.login(olav);

        chouffe.logout();
        assertFalse(atcChouffe.advanceTimePreconditions());
        chouffe.login(dieter);

        // Testen of de setNewTime werkt
        atcOmer.setNewTime(new Time(200));
        assertEquals(new Time(200), atcOmer.getSystemTime());

        atcDuvel.setNewTime(new Time(300));
        assertEquals(new Time(300), atcDuvel.getSystemTime());
        assertEquals(new Time(200), atcOmer.getSystemTime());

        atcChouffe.setNewTime(new Time(400));
        assertEquals(new Time(400), atcChouffe.getSystemTime());
        assertEquals(new Time(300), atcDuvel.getSystemTime());
        assertEquals(new Time(200), atcOmer.getSystemTime());

        assertThrows(NewTimeBeforeSystemTimeException.class, () -> atcOmer.setNewTime(new Time(100)));
        assertThrows(NewTimeBeforeSystemTimeException.class, () -> atcDuvel.setNewTime(new Time(299)));
        assertThrows(NewTimeBeforeSystemTimeException.class, () -> atcChouffe.setNewTime(new Time(399)));

        // testen of advancetime werkt
        atcOmer.advanceTime(30);
        assertEquals(new Time(230), atcOmer.getSystemTime());

        atcDuvel.advanceTime(30);
        assertEquals(new Time(330), atcDuvel.getSystemTime());

        atcChouffe.advanceTime(0);
        assertEquals(new Time(400), atcChouffe.getSystemTime());

        assertThrows(NewTimeBeforeSystemTimeException.class, () -> atcOmer.advanceTime(-1));
        assertThrows(NewTimeBeforeSystemTimeException.class, () -> atcDuvel.advanceTime(-33));
        assertThrows(NewTimeBeforeSystemTimeException.class, () -> atcChouffe.advanceTime(-100));

        atcOmer.advanceTime(120);
        assertEquals(new Time(350), atcOmer.getSystemTime());
        assertEquals(new Time(330), atcDuvel.getSystemTime());
        assertEquals(new Time(400), atcChouffe.getSystemTime());

        // Testen of de throws werken
        omer.logout();
        chouffe.logout();
        duvel.logout();

        assertThrows(IncorrectPermissionException.class, () -> atcOmer.advanceTime(30));
        assertThrows(IncorrectPermissionException.class, () -> atcDuvel.advanceTime(30));
        assertThrows(IncorrectPermissionException.class, () -> atcChouffe.advanceTime(30));

        assertThrows(IncorrectPermissionException.class, () -> atcOmer.setNewTime(new Time(100)));
        assertThrows(IncorrectPermissionException.class, () -> atcDuvel.setNewTime(new Time(100)));
        assertThrows(IncorrectPermissionException.class, () -> atcChouffe.setNewTime(new Time(100)));






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
