import org.junit.Test;
import static org.junit.Assert.*;
public class LoadSystemTest {
    UserManager userManager = new UserManager();
    TaskManSystem taskManSystem = new TaskManSystem(new Time(0, 0));
    Session session = new Session();
    LoadSystemUI loadSystemUI = new LoadSystemUI(userManager, taskManSystem, session);

    public LoadSystemTest() throws InvalidTimeException {
    }
    @Test
    public void testLoadSystem() throws ProjectNotFoundException, TaskNotFoundException {
        LoadSystemController lsc = new LoadSystemController(userManager, taskManSystem, session, loadSystemUI);
        lsc.LoadSystem("code/Iteration01/tests/loadTest.json");
        assertEquals(taskManSystem.getSystemTime().getHour(), 15);
        assertTrue(taskManSystem.getSystemTime().getMinute() == 10);
        assertEquals(taskManSystem.getProjectNames().get(0), "simpleProject");
        assertEquals(taskManSystem.getStatus("simpleProject", "simpleTask"), Status.EXECUTING);


    }
}
