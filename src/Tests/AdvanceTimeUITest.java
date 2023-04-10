package Tests;

import org.junit.Test;

public class AdvanceTimeUITest {
    @Test
    public void testAdvanceTimeUI() {
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
