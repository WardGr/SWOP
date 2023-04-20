package Tests;

import Application.AdvanceTimeController;
import Application.IncorrectPermissionException;
import Application.Session;
import Application.SessionProxy;
import Domain.*;
import UserInterface.AdvanceTimeUI;
import org.junit.Test;
import java.io.*;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AdvanceTimeUITest {

    @Test
    public void testAdvanceTimeUI() throws InvalidTimeException, IOException, IncorrectPermissionException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
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

        AdvanceTimeController atcOmer = new AdvanceTimeController(new SessionProxy(omer), tmsOmer);
        AdvanceTimeController atcDuvel = new AdvanceTimeController(new SessionProxy(duvel), tmsDuvel);
        AdvanceTimeController atcChouffe = new AdvanceTimeController(new SessionProxy(chouffe), tmsChouffe);

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

        // Testing incorrect permissions on advanceTime
        out.reset();

        duvel.logout();
        atuiDuvel.advanceTime();
        assertEquals("""
        You must be logged in with the project manager role or a Developer role to call this function
        """.replaceAll("\n|\r\n", System.getProperty("line.separator")), out.toString().replaceAll("\n|\r\n", System.getProperty("line.separator")));
        out.reset();
        duvel.login(olav);

        chouffe.logout();
        out.reset();
        atuiChouffe.advanceTime();
        assertEquals("""
        You must be logged in with the project manager role or a Developer role to call this function
        """.replaceAll("\n|\r\n", System.getProperty("line.separator")), out.toString().replaceAll("\n|\r\n", System.getProperty("line.separator")));
        out.reset();
        chouffe.login(dieter);

        // Tesjting chooseAdvanceMethod with wrong input
        assertEquals(new Time(150), tmsChouffe.getSystemTime());


        input = "advnce\nBACK\n";
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        out.reset();
        atuiChouffe.chooseAdvanceMethod();
        assertEquals(out.toString().replaceAll("\n|\r\n", System.getProperty("line.separator")), """
                Current system time is: 2 hour(s), 30 minute(s)
                Type BACK to cancel advancing the system time any time
                Do you want to advance the clock with a certain amount of minutes or choose a new timestamp
                advance/new
                Do you want to advance the time with a certain amount of minutes or choose a new timestamp
                advance/new
                """.replaceAll("\n|\r\n", System.getProperty("line.separator")));
        assertTrue(out.toString().replaceAll("\n|\r\n", System.getProperty("line.separator")).contains("""
                Do you want to advance the clock with a certain amount of minutes or choose a new timestamp
                advance/new
                Do you want to advance the time with a certain amount of minutes or choose a new timestamp
                advance/new
                """.replaceAll("\n|\r\n", System.getProperty("line.separator"))));

        input = "nw\nnew\n22\n3\n";

        input = "nw\nnew\nBACK\n";
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        out.reset();
        atuiChouffe.chooseAdvanceMethod();
        assertTrue(out.toString().replaceAll("\n|\r\n", System.getProperty("line.separator")).contains("""
                Type BACK to cancel advancing the system time any time
                Do you want to advance the clock with a certain amount of minutes or choose a new timestamp
                advance/new
                Do you want to advance the time with a certain amount of minutes or choose a new timestamp
                advance/new
                Give new system hour:
                Cancelled advancing time
                """.replaceAll("\n|\r\n", System.getProperty("line.separator"))));

        input = "hoi\nadvance\nBACK\n";
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        out.reset();
        atuiChouffe.chooseAdvanceMethod();
        assertTrue(out.toString().replaceAll("\n|\r\n", System.getProperty("line.separator")).contains("""
                Cancelled advancing time
                """.replaceAll("\n|\r\n", System.getProperty("line.separator"))));

        input = "hoi\nadvance\n2\n";
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        out.reset();
        atuiChouffe.chooseAdvanceMethod();
        assertEquals(new Time(152), tmsChouffe.getSystemTime());

        input = "hoi\nadvance\nja\n5";
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        out.reset();
        atuiChouffe.chooseAdvanceMethod();
        assertTrue(out.toString().replaceAll("\n|\r\n", System.getProperty("line.separator")).contains("""
                Given system minute is not an integer, please try again
                """.replaceAll("\n|\r\n", System.getProperty("line.separator"))));
        assertEquals(new Time(157), tmsChouffe.getSystemTime());

        input = "hoi\nadvance\nja\n-5\nBACK\n";
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        out.reset();
        atuiChouffe.chooseAdvanceMethod();
        assertTrue(out.toString().replaceAll("\n|\r\n", System.getProperty("line.separator")).contains("""
                ERROR: The chosen time is before the system time
                """.replaceAll("\n|\r\n", System.getProperty("line.separator"))));
        assertEquals(new Time(157), tmsChouffe.getSystemTime());

        input = "hoi\nnew\n-5\n-5\nBACK\n";
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        out.reset();
        atuiChouffe.chooseAdvanceMethod();
        assertTrue(out.toString().replaceAll("\n|\r\n", System.getProperty("line.separator")).contains("""
                ERROR: the chosen time is not valid
                """.replaceAll("\n|\r\n", System.getProperty("line.separator"))));
        assertEquals(new Time(157), tmsChouffe.getSystemTime());

        input = "hoi\nnew\n-5\n5\nBACK\n";
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        out.reset();
        atuiChouffe.chooseAdvanceMethod();
        assertTrue(out.toString().replaceAll("\n|\r\n", System.getProperty("line.separator")).contains("""
                ERROR: the chosen time is not valid
                """.replaceAll("\n|\r\n", System.getProperty("line.separator"))));
        assertEquals(new Time(157), tmsChouffe.getSystemTime());

        input = "hoi\nnew\n5\n-5\nnew\n2\n38";
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        out.reset();
        atuiChouffe.chooseAdvanceMethod();
        assertTrue(out.toString().replaceAll("\n|\r\n", System.getProperty("line.separator")).contains("""
                ERROR: the chosen time is not valid
                """.replaceAll("\n|\r\n", System.getProperty("line.separator"))));
        assertEquals(new Time(158), tmsChouffe.getSystemTime());

        input = "nw\nnew\njo\n5\n4";
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        out.reset();
        atuiChouffe.chooseAdvanceMethod();
        assertTrue(out.toString().replaceAll("\n|\r\n", System.getProperty("line.separator")).contains("""
                Given system hour is not an integer, please try again
                """.replaceAll("\n|\r\n", System.getProperty("line.separator"))));
        assertEquals(new Time(304), tmsChouffe.getSystemTime());

        input = "nw\nnew\n5\na\n5\n";
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        out.reset();
        atuiChouffe.chooseAdvanceMethod();
        assertTrue(out.toString().replaceAll("\n|\r\n", System.getProperty("line.separator")).contains("""
                Given system minute is not an integer, please try again
                """.replaceAll("\n|\r\n", System.getProperty("line.separator"))));
        assertEquals(new Time(305), tmsChouffe.getSystemTime());

    }
}
