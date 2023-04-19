package Tests;

import Application.AdvanceTimeController;
import Application.Session;
import Application.SessionWrapper;
import Domain.*;
import UserInterface.AdvanceTimeUI;
import org.junit.Test;
import java.io.*;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.assertEquals;

public class AdvanceTimeUITest {

    @Test
    public void testAdvanceTimeUI() throws InvalidTimeException {
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

        AdvanceTimeUI atuiOmer = new AdvanceTimeUI(atcOmer);
        AdvanceTimeUI atuiDuvel = new AdvanceTimeUI(atcDuvel);
        AdvanceTimeUI atuiChouffe = new AdvanceTimeUI(atcChouffe);

        // Testing advance on advanceTime

        assertEquals(new Time(12), tmsOmer.getSystemTime());
        assertEquals(new Time(17), tmsDuvel.getSystemTime());
        assertEquals(new Time(63), tmsChouffe.getSystemTime());

        String input = "advance\n12\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        atuiOmer.advanceTime();
        assertEquals(new Time(24), tmsOmer.getSystemTime());

        input = "advance\n17\n";
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        atuiDuvel.advanceTime();
        assertEquals(new Time(34), tmsDuvel.getSystemTime());

        input = "advance\n63\n";
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        atuiChouffe.advanceTime();
        assertEquals(new Time(126), tmsChouffe.getSystemTime());

        assertEquals(new Time(24), tmsOmer.getSystemTime());
        assertEquals(new Time(34), tmsDuvel.getSystemTime());

        // Testing new on advanceTime
        input = "new\n1\n5\n";
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        atuiOmer.advanceTime();
        assertEquals(new Time(65), tmsOmer.getSystemTime());
        assertEquals(new Time(126), tmsChouffe.getSystemTime());
        assertEquals(new Time(34), tmsDuvel.getSystemTime());

        input = "new\n0\n22\nnew\n2\n30\n";
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        atuiChouffe.advanceTime();
        assertEquals(new Time(150), tmsChouffe.getSystemTime());

        // Testing "Back" on advanceTime
        input = "BACK\n";
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        atuiChouffe.advanceTime();
        assertEquals(new Time(150), tmsChouffe.getSystemTime());

        input = "new\n1\n0\nBACK\n";
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        atuiChouffe.advanceTime();
        assertEquals(new Time(150), tmsChouffe.getSystemTime());

        input = "new\n1\nBACK\n";
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        atuiChouffe.advanceTime();
        assertEquals(new Time(150), tmsChouffe.getSystemTime());

        /*
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        User manager = new User("WardGr", "minecraft123", Role.PROJECTMANAGER);
        User dev = new User("OlavBl", "peer123", Role.DEVELOPER);
        Session session = new Session();
        SessionWrapper sessionWrapper = new SessionWrapper(session);
        TaskManSystem tms = new TaskManSystem(new Time(0));
        AdvanceTimeController advanceTimeController = new AdvanceTimeController(sessionWrapper, tms);
        AdvanceTimeUI atui = new AdvanceTimeUI(advanceTimeController);

        session.logout();
        session.login(manager);
        System.setIn(new ByteArrayInputStream("y\n2\n3".getBytes()));
        atui.advanceTime();
        assertEquals("""
                Current system time is: 0:0
                Type BACK to cancel advancing the system time any time
                Do you want to advance to a specific time? (y/n)
                Give new system hour:
                Give new system minute:
                Time successfully updated
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("y\n5\n7".getBytes()));
        atui.advanceTime();
        assertEquals("""
                Current system time is: 2:3
                Type BACK to cancel advancing the system time any time
                Do you want to advance to a specific time? (y/n)
                Give new system hour:
                Give new system minute:
                Time successfully updated
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
        System.setIn(new ByteArrayInputStream("y\nBACK".getBytes()));
        atui.advanceTime();
        assertEquals("""
                Current system time is: 5:7
                Type BACK to cancel advancing the system time any time
                Do you want to advance to a specific time? (y/n)
                Give new system hour:
                Cancelled advancing time
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
        System.setIn(new ByteArrayInputStream("y\n100\nBACK".getBytes()));
        atui.advanceTime();
        assertEquals("""
                Current system time is: 5:7
                Type BACK to cancel advancing the system time any time
                Do you want to advance to a specific time? (y/n)
                Give new system hour:
                Give new system minute:
                Cancelled advancing time
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
        System.setIn(new ByteArrayInputStream("y\n5\n60\ny\n5\n55".getBytes()));
        atui.advanceTime();
        assertEquals("""
                Current system time is: 5:7
                Type BACK to cancel advancing the system time any time
                Do you want to advance to a specific time? (y/n)
                Give new system hour:
                Give new system minute:
                ERROR: the chosen time is not valid
                Current system time is: 5:7
                Type BACK to cancel advancing the system time any time
                Do you want to advance to a specific time? (y/n)
                Give new system hour:
                Give new system minute:
                Time successfully updated
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
        System.setIn(new ByteArrayInputStream("y\n5\n20\ny\n7\n55".getBytes()));
        atui.advanceTime();
        assertEquals("""
                Current system time is: 5:55
                Type BACK to cancel advancing the system time any time
                Do you want to advance to a specific time? (y/n)
                Give new system hour:
                Give new system minute:
                ERROR: The chosen time is before the system time
                Current system time is: 5:55
                Type BACK to cancel advancing the system time any time
                Do you want to advance to a specific time? (y/n)
                Give new system hour:
                Give new system minute:
                Time successfully updated
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));

        out.reset();
        System.setIn(new ByteArrayInputStream("y\n7\nhoi\n58".getBytes()));
        atui.advanceTime();
        assertEquals("""
                Current system time is: 7:55
                Type BACK to cancel advancing the system time any time
                Do you want to advance to a specific time? (y/n)
                Give new system hour:
                Give new system minute:
                Given system minute is not an integer, please try again
                Time successfully updated
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));

        out.reset();
        System.setIn(new ByteArrayInputStream("y\nhoi\n10\nhoi\n58".getBytes()));
        atui.advanceTime();
        assertEquals("""
                Current system time is: 7:58
                Type BACK to cancel advancing the system time any time
                Do you want to advance to a specific time? (y/n)
                Give new system hour:
                Given system hour is not an integer, please try again
                Give new system minute:
                Given system minute is not an integer, please try again
                Time successfully updated
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));

        */
    }
}
