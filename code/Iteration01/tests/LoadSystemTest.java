import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.io.PrintStream;

import static org.junit.Assert.*;
public class LoadSystemTest {
    UserManager userManager = new UserManager();
    TaskManSystem taskManSystem = new TaskManSystem(new Time(0, 0));
    Session session = new Session();
    public LoadSystemTest() throws InvalidTimeException {
    }
    @Test
    public void testLoadSystem() throws ProjectNotFoundException, TaskNotFoundException, LoginException {
        controller();
    }
    @Test
    public void controller() throws ProjectNotFoundException, TaskNotFoundException, LoginException {
        User manager = userManager.getUser("WardGr","minecraft123");
        session.login(manager);
        LoadSystemController lsc = new LoadSystemController(userManager, taskManSystem, session);
        try{
            lsc.LoadSystem("code/Iteration01/tests/loadTest.json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(taskManSystem.getSystemTime().getHour(), 15);
        assertEquals(10, taskManSystem.getSystemTime().getMinute());
        List<String> projectNames = taskManSystem.getProjectNames();
        String[] expectedProjectNames = new String[]{"simpleProject", "simpleProject2", "simpleProject3", "simpleProject4", "simpleProject5"};
        for(int i = 0; i < expectedProjectNames.length; i++){
            assertEquals(projectNames.get(i), expectedProjectNames[i]);
        }
        assertEquals(taskManSystem.getStatus("simpleProject", "simpleTask"), Status.EXECUTING);
        assertEquals(taskManSystem.getStatus("simpleProject5", "simpleTask"), Status.FINISHED);
        Map<String, List<String>> executingTasks = taskManSystem.showExecutingTasks();
        Map<String, List<String>> expectedExecutingTasks = new HashMap<>();
        expectedExecutingTasks.put("simpleProject",new LinkedList<>(Collections.singleton("simpleTask")));
        expectedExecutingTasks.put("simpleProject2",new LinkedList<>());
        expectedExecutingTasks.put("simpleProject3",new LinkedList<>(Collections.singleton("replacesTask")));
        expectedExecutingTasks.put("simpleProject4",new LinkedList<>(Collections.singleton("simpleTask")));
        expectedExecutingTasks.put("simpleProject5",new LinkedList<>());
        assertEquals(executingTasks, expectedExecutingTasks);
        Map<String, List<String>> expectedAvailableTasks = new HashMap<>();
        for(String p : expectedProjectNames){
            expectedAvailableTasks.put(p,new LinkedList<>());
        }
        assertEquals(expectedAvailableTasks, taskManSystem.showAvailableTasks());
        String expectedTask = "Task Name:          simpleTask\nDescription:        first LoadSystemTask\nEstimated Duration: 16 hours, 10 minutes\nAccepted Deviation: 3.3\nStatus:             executing\n\nReplacement Task:   No replacement task\nReplaces Task:      Replaces no tasks\n\nStart Time:         1 hours, 6 minutes\nEnd Time:           No end time set\n\nUser:               SamHa\n\nNext tasks:\nPrevious tasks:\n";
        assertEquals(taskManSystem.showTask("simpleProject", "simpleTask"), expectedTask);
        expectedTask = "Task Name:          simpleTask\nDescription:        first LoadSystemTask failed\nEstimated Duration: 16 hours, 10 minutes\nAccepted Deviation: 3.3\nStatus:             failed\n\nReplacement Task:   No replacement task\nReplaces Task:      Replaces no tasks\n\nStart Time:         1 hours, 6 minutes\nEnd Time:           2 hours, 10 minutes\n\nUser:               SamHa\n\nNext tasks:\nPrevious tasks:\n";
        assertEquals(taskManSystem.showTask("simpleProject2", "simpleTask"), expectedTask);
        expectedTask = "Task Name:          replacesTask\nDescription:        second LoadSystemTask replace\nEstimated Duration: 16 hours, 10 minutes\nAccepted Deviation: 3.3\nStatus:             executing\n\nReplacement Task:   No replacement task\nReplaces Task:      simpleTask\n\nStart Time:         1 hours, 6 minutes\nEnd Time:           No end time set\n\nUser:               SamHa\n\nNext tasks:\nPrevious tasks:\n";
        assertEquals(taskManSystem.showTask("simpleProject3", "replacesTask"), expectedTask);
        expectedTask = "Task Name:          simpleTask\nDescription:        first LoadSystemTask replace\nEstimated Duration: 16 hours, 10 minutes\nAccepted Deviation: 3.3\nStatus:             failed\n\nReplacement Task:   replacesTask\nReplaces Task:      Replaces no tasks\n\nStart Time:         1 hours, 6 minutes\nEnd Time:           2 hours, 10 minutes\n\nUser:               SamHa\n\nNext tasks:\nPrevious tasks:\n";
        assertEquals(taskManSystem.showTask("simpleProject3", "simpleTask"), expectedTask);
        expectedTask = "Task Name:          simpleTask\nDescription:        first LoadSystemTask nxt\nEstimated Duration: 16 hours, 10 minutes\nAccepted Deviation: 3.3\nStatus:             executing\n\nReplacement Task:   No replacement task\nReplaces Task:      Replaces no tasks\n\nStart Time:         1 hours, 6 minutes\nEnd Time:           No end time set\n\nUser:               SamHa\n\nNext tasks:\n1.nextTask\nPrevious tasks:\n";
        assertEquals(taskManSystem.showTask("simpleProject4", "simpleTask"), expectedTask);
        expectedTask = "Task Name:          nextTask\nDescription:        second LoadSystemTask nxt\nEstimated Duration: 16 hours, 10 minutes\nAccepted Deviation: 3.3\nStatus:             unavailable\n\nReplacement Task:   No replacement task\nReplaces Task:      Replaces no tasks\n\nStart Time:         Task has not started yet\nEnd Time:           Task has not ended yet\n\nUser:               SamHa\n\nNext tasks:\nPrevious tasks:\n1.simpleTask\n";
        assertEquals(taskManSystem.showTask("simpleProject4", "nextTask"), expectedTask);
        expectedTask = "Task Name:          simpleTask\nDescription:        first LoadSystemTask finished\nEstimated Duration: 16 hours, 10 minutes\nAccepted Deviation: 3.3\nStatus:             finished, on time\n\nReplacement Task:   No replacement task\nReplaces Task:      Replaces no tasks\n\nStart Time:         1 hours, 6 minutes\nEnd Time:           2 hours, 10 minutes\n\nUser:               SamHa\n\nNext tasks:\nPrevious tasks:\n";
        assertEquals(taskManSystem.showTask("simpleProject5", "simpleTask"), expectedTask);
        try{
            manager = userManager.getUser("SamHa","pintje452");
            session.login(manager);
            lsc = new LoadSystemController(userManager, taskManSystem, session);
            lsc.LoadSystem("code/Iteration01/tests/loadTest.json");
            fail("Exception not thrown");
        }catch (IncorrectPermissionException e) {
            assertEquals("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function", e.getMessage());
        }catch (Exception e){
            fail("Wrong exception thrown");
        }
    }
    @Test
    public void ui() throws LoginException {
        LoadSystemUI lsu = new LoadSystemUI(userManager, taskManSystem, session);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        //Tests user has not the right permission
        User manager = userManager.getUser("SamHa","pintje452");
        session.login(manager);
        lsu.loadSystem();
        assertEquals("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function\n".replaceAll("\\n|\\r\\n",System.getProperty("line.separator")), out.toString());
        out.reset();

        //Tests user has right permission but wants to go back
        manager = userManager.getUser("WardGr","minecraft123");
        session.login(manager);
        System.setIn(new ByteArrayInputStream("BACK\n".getBytes()));
        lsu.loadSystem();
        assertEquals(
                """
                Type BACK to cancel system load at any time
                *********** SYSTEM LOAD FORM ***********
                please enter the path of the load file:\s
                Cancelled system load
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString());
        out.reset();

        //Tests when a user succesfully loads a system
        System.setIn(new ByteArrayInputStream("code/Iteration01/tests/loadTest.json\n".getBytes()));
        lsu.loadSystem();
        assertEquals(
                """
                Type BACK to cancel system load at any time
                *********** SYSTEM LOAD FORM ***********
                please enter the path of the load file:\s
                system succesfully loaded
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString());
        out.reset();

        //Tests when a user gives an invalid file path
        System.setIn(new ByteArrayInputStream("code/Iteration01/src/loadTest.json\n".getBytes()));
        lsu.loadSystem();
        assertEquals(
                """
                Type BACK to cancel system load at any time
                *********** SYSTEM LOAD FORM ***********
                please enter the path of the load file:\s
                ERROR: file not found
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString());
        out.reset();

        //Tests wwhen a user gives a  file with invalid logic
        System.setIn(new ByteArrayInputStream("code/Iteration01/tests/InvalidLogicTest.json\n".getBytes()));
        lsu.loadSystem();
        assertEquals(
                """
                Type BACK to cancel system load at any time
                *********** SYSTEM LOAD FORM ***********
                please enter the path of the load file:\s
                ERROR: invalid file logic
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString());
        out.reset();
    }
}
