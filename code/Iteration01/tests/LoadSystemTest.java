import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;
public class LoadSystemTest {
    UserManager userManager = new UserManager();
    TaskManSystem taskManSystem = new TaskManSystem(new Time(0, 0));
    Session session = new Session();
    public LoadSystemTest() throws InvalidTimeException {
    }
    @Test
    public void testLoadSystem() throws ProjectNotFoundException, TaskNotFoundException, IncorrectPermissionException, LoginException {
        User manager = userManager.getUser("WardGr","minecraft123");
        session.login(manager);
        LoadSystemController lsc = new LoadSystemController(userManager, taskManSystem, session);
        try{
            lsc.LoadSystem("code/Iteration01/tests/loadTest.json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(taskManSystem.getSystemTime().getHour(), 15);
        assertTrue(taskManSystem.getSystemTime().getMinute() == 10);
        assertEquals(taskManSystem.getProjectNames().get(0), "simpleProject");
        assertEquals(taskManSystem.getStatus("simpleProject", "simpleTask"), Status.EXECUTING);


    }
}
