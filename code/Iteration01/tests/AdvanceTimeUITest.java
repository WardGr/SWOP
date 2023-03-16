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
        User dev = new User("OlavBl", "toilet753", Role.DEVELOPER);
        Session session = new Session();
        TaskManSystem tms = new TaskManSystem(new Time(0));
        AdvanceTimeUI atui = new AdvanceTimeUI(session, tms);

        session.login(dev);
        atui.advanceTime();
        assertEquals("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function\n".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString());
        out.reset();
        session.logout();
        session.login(manager);
        System.setIn(new ByteArrayInputStream("2\n3".getBytes()));
        atui.advanceTime();
        assertEquals("Current system time is: 0:0\n" +
                "Type BACK to cancel advancing the system time any time\n" +
                "Give new system hour:\n" +
                "Give new system minute:\n" +
                "Time successfully updated\n".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString());
        out.reset();

        System.setIn(new ByteArrayInputStream("5\n7".getBytes()));
        atui.advanceTime();
        assertEquals("Current system time is: 2:3\n" +
                "Type BACK to cancel advancing the system time any time\n" +
                "Give new system hour:\n" +
                "Give new system minute:\n" +
                "Time successfully updated\n".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString());
        out.reset();
        System.setIn(new ByteArrayInputStream("BACK".getBytes()));
        atui.advanceTime();
        assertEquals("Current system time is: 5:7\n" +
                "Type BACK to cancel advancing the system time any time\n" +
                "Give new system hour:\n" +
                "Cancelled advancing time\n".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString());
        out.reset();
        System.setIn(new ByteArrayInputStream("100\nBACK".getBytes()));
        atui.advanceTime();
        assertEquals("Current system time is: 5:7\n" +
                "Type BACK to cancel advancing the system time any time\n" +
                "Give new system hour:\n" +
                "Give new system minute:\n" +
                "Cancelled advancing time\n".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString());
        out.reset();
        System.setIn(new ByteArrayInputStream("5\n60\n5\n55".getBytes()));
        atui.advanceTime();
        assertEquals("Current system time is: 5:7\n" +
                "Type BACK to cancel advancing the system time any time\n" +
                "Give new system hour:\n" +
                "Give new system minute:\n" +
                "ERROR: the chosen time is not valid\n" +
                "Current system time is: 5:7\n" +
                "Type BACK to cancel advancing the system time any time\n" +
                "Give new system hour:\n" +
                "Give new system minute:\n" +
                "Time successfully updated\n".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString());
        out.reset();
        System.setIn(new ByteArrayInputStream("5\n20\n7\n55".getBytes()));
        atui.advanceTime();
        assertEquals("Current system time is: 5:55\n" +
                "Type BACK to cancel advancing the system time any time\n" +
                "Give new system hour:\n" +
                "Give new system minute:\n" +
                "ERROR: The chosen time is before the system time\n" +
                "Current system time is: 5:55\n" +
                "Type BACK to cancel advancing the system time any time\n" +
                "Give new system hour:\n" +
                "Give new system minute:\n" +
                "Time successfully updated\n".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString());
    }
}
