package Tests;

import Application.AdvanceTimeController;
import Application.Session;
import Domain.Role;
import Domain.TaskManSystem;
import Domain.Time;
import Domain.User;
import UserInterface.AdvanceTimeUI;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class AdvanceTimeUITest {
    @Test
    public void testAdvanceTimeUI() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        User manager = new User("WardGr", "minecraft123", Role.PROJECTMANAGER);
        User dev = new User("OlavBl", "peer123", Role.DEVELOPER);
        Session session = new Session();
        TaskManSystem tms = new TaskManSystem(new Time(0));
        AdvanceTimeController advanceTimeController = new AdvanceTimeController(session, tms);
        AdvanceTimeUI atui = new AdvanceTimeUI(advanceTimeController);

        session.login(dev);
        atui.advanceTime();
        assertEquals("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function\n".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
        session.logout();
        session.login(manager);
        System.setIn(new ByteArrayInputStream("2\n3".getBytes()));
        atui.advanceTime();
        assertEquals("""
                Current system time is: 0:0
                Type BACK to cancel advancing the system time any time
                Give new system hour:
                Give new system minute:
                Time successfully updated
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("5\n7".getBytes()));
        atui.advanceTime();
        assertEquals("""
                Current system time is: 2:3
                Type BACK to cancel advancing the system time any time
                Give new system hour:
                Give new system minute:
                Time successfully updated
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
        System.setIn(new ByteArrayInputStream("BACK".getBytes()));
        atui.advanceTime();
        assertEquals("""
                Current system time is: 5:7
                Type BACK to cancel advancing the system time any time
                Give new system hour:
                Cancelled advancing time
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
        System.setIn(new ByteArrayInputStream("100\nBACK".getBytes()));
        atui.advanceTime();
        assertEquals("""
                Current system time is: 5:7
                Type BACK to cancel advancing the system time any time
                Give new system hour:
                Give new system minute:
                Cancelled advancing time
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
        System.setIn(new ByteArrayInputStream("5\n60\n5\n55".getBytes()));
        atui.advanceTime();
        assertEquals("""
                Current system time is: 5:7
                Type BACK to cancel advancing the system time any time
                Give new system hour:
                Give new system minute:
                ERROR: the chosen time is not valid
                Current system time is: 5:7
                Type BACK to cancel advancing the system time any time
                Give new system hour:
                Give new system minute:
                Time successfully updated
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
        System.setIn(new ByteArrayInputStream("5\n20\n7\n55".getBytes()));
        atui.advanceTime();
        assertEquals("""
                Current system time is: 5:55
                Type BACK to cancel advancing the system time any time
                Give new system hour:
                Give new system minute:
                ERROR: The chosen time is before the system time
                Current system time is: 5:55
                Type BACK to cancel advancing the system time any time
                Give new system hour:
                Give new system minute:
                Time successfully updated
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));

        out.reset();
        System.setIn(new ByteArrayInputStream("7\nhoi\n58".getBytes()));
        atui.advanceTime();
        assertEquals("""
                Current system time is: 7:55
                Type BACK to cancel advancing the system time any time
                Give new system hour:
                Give new system minute:
                Given system minute is not an integer, please try again
                Time successfully updated
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));

        out.reset();
        System.setIn(new ByteArrayInputStream("hoi\n10\nhoi\n58".getBytes()));
        atui.advanceTime();
        assertEquals("""
                Current system time is: 7:58
                Type BACK to cancel advancing the system time any time
                Give new system hour:
                Given system hour is not an integer, please try again
                Give new system minute:
                Given system minute is not an integer, please try again
                Time successfully updated
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
    }
}
