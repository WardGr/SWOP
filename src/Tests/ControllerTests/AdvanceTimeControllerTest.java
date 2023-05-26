package Tests.ControllerTests;

import Application.SystemControllers.AdvanceTimeController;
import Application.IncorrectPermissionException;
import Application.Session.Session;
import Application.Session.SessionProxy;
import Domain.Command.CommandManager;
import Domain.DataClasses.Time;
import Domain.TaskManSystem.NewTimeBeforeSystemTimeException;
import Domain.TaskManSystem.TaskManSystem;
import Domain.User.Role;
import Domain.User.User;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;

public class AdvanceTimeControllerTest {

    private User ward;
    private User olav;
    private User dieter;

    private Session omer;
    private Session duvel;
    private Session chouffe;

    private AdvanceTimeController atcOmer;
    private AdvanceTimeController atcDuvel;
    private AdvanceTimeController atcChouffe;



    @Before
    public void setUp() throws Exception {
        // Idee is dat Ward met PROJECTMAN en JAVAPROGRAMMER in omer zit, olav zit met SYSADMIN in duvel en Dieter met JAVA- en PYTHONPROGRAMMER in chouffe
        Set<Role> wardRoles = new HashSet<>();
        wardRoles.add(Role.PROJECTMANAGER);
        wardRoles.add(Role.JAVAPROGRAMMER);

        Set<Role> olavRoles = new HashSet<>();
        olavRoles.add(Role.SYSADMIN);

        Set<Role> dieterRoles = new HashSet<>();
        dieterRoles.add(Role.JAVAPROGRAMMER);
        dieterRoles.add(Role.PYTHONPROGRAMMER);

        this.ward = new User("WardGr", "peer123", wardRoles);
        this.olav = new User("OlavBl", "peer123", olavRoles);
        this.dieter = new User("DieterVh", "peer123", dieterRoles);


        this.omer = new Session();
        omer.login(ward);
        this.duvel = new Session();
        duvel.login(olav);
        this.chouffe = new Session();
        chouffe.login(dieter);

        TaskManSystem tmsOmer = new TaskManSystem(new Time(12));
        TaskManSystem tmsDuvel = new TaskManSystem(new Time(17));
        TaskManSystem tmsChouffe = new TaskManSystem(new Time(63));

        CommandManager cmOmer = new CommandManager();
        CommandManager cmDuvel = new CommandManager();
        CommandManager cmChouffe = new CommandManager();

        this.atcOmer = new AdvanceTimeController(new SessionProxy(omer), tmsOmer, cmOmer);
        this.atcDuvel = new AdvanceTimeController(new SessionProxy(duvel), tmsDuvel, cmDuvel);
        this.atcChouffe = new AdvanceTimeController(new SessionProxy(chouffe), tmsChouffe, cmChouffe);

    }

    @Test
    public void testgetSystemTime() throws Exception {
        assertEquals(new Time(12), atcOmer.getSystemTime());
        assertEquals(new Time(17), atcDuvel.getSystemTime());
        assertEquals(new Time(63), atcChouffe.getSystemTime());
    }

    @Test
    public void testpreconditions() {
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

        // normally we would have tests with roles that cannot advance the time, but they do not exist in this iteration

    }

    @Test
    public void setNewTimeTest() throws Exception {
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
    }

    @Test
    public void advanceTimeTest() throws Exception {

        // testen of advancetime werkt
        atcOmer.advanceTime(30);
        assertEquals(new Time(42), atcOmer.getSystemTime());

        atcDuvel.advanceTime(30);
        assertEquals(new Time(47), atcDuvel.getSystemTime());

        atcChouffe.advanceTime(0);
        assertEquals(new Time(63), atcChouffe.getSystemTime());

        assertThrows(NewTimeBeforeSystemTimeException.class, () -> atcOmer.advanceTime(-1));
        assertThrows(NewTimeBeforeSystemTimeException.class, () -> atcDuvel.advanceTime(-33));
        assertThrows(NewTimeBeforeSystemTimeException.class, () -> atcChouffe.advanceTime(-100));

        atcOmer.advanceTime(120);
        assertEquals(new Time(162), atcOmer.getSystemTime());
        assertEquals(new Time(47), atcDuvel.getSystemTime());
        assertEquals(new Time(63), atcChouffe.getSystemTime());
    }


    @Test
    public void incorrectPermissionTest() {
        omer.logout();
        chouffe.logout();
        duvel.logout();

        assertThrows(IncorrectPermissionException.class, () -> atcOmer.advanceTime(30));
        assertThrows(IncorrectPermissionException.class, () -> atcDuvel.advanceTime(30));
        assertThrows(IncorrectPermissionException.class, () -> atcChouffe.advanceTime(30));

        assertThrows(IncorrectPermissionException.class, () -> atcOmer.setNewTime(new Time(100)));
        assertThrows(IncorrectPermissionException.class, () -> atcDuvel.setNewTime(new Time(100)));
        assertThrows(IncorrectPermissionException.class, () -> atcChouffe.setNewTime(new Time(100)));
    }
}
