package Tests;

import Application.LoadSystemController;
import Application.Session;
import Application.SessionWrapper;
import Domain.*;
import Domain.TaskStates.TaskProxy;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class LoadSystemTest {
    UserManager userManager = new UserManager();
    TaskManSystem taskManSystem = new TaskManSystem(new Time(0, 0));
    Session session = new Session();
    SessionWrapper sessionWrapper = new SessionWrapper(session);

    LoadSystemController lsc = new LoadSystemController(sessionWrapper, taskManSystem, userManager);

    public LoadSystemTest() throws InvalidTimeException {
    }

    @Test
    public void testLoadSystem() throws ProjectNotFoundException, TaskNotFoundException, LoginException {
        /*
        controller();
        //checks if the old system gets discarded and the new gets loaded
        controller();
        ui();

        */
    }

    @Test
    public void controller() throws ProjectNotFoundException, TaskNotFoundException, LoginException {

        User manager = userManager.getUser("DieterVH", "computer776");
        session.login(manager);
        try {
            lsc.LoadSystem("src/Tests/jsons/availableTask.json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //tests basic project fields
        assertEquals(taskManSystem.getSystemTime().getHour(), 15);
        assertEquals(10, taskManSystem.getSystemTime().getMinute());
        ProjectProxy projectData = taskManSystem.getProjectData("availableProject");
        assertEquals(projectData.getName(), "availableProject");
        assertEquals(projectData.getStatus(), ProjectStatus.ONGOING);
        assertEquals(projectData.getDescription(), "first LoadSystemTest");
        assertEquals(projectData.getCreationTime().getHour(), 1);
        assertEquals(projectData.getCreationTime().getMinute(), 11);
        assertEquals(projectData.getDueTime().getHour(), 16);
        assertEquals(projectData.getDueTime().getMinute(), 10);

        //tests basic task fields
        TaskProxy taskData = taskManSystem.getTaskData("availableProject", "availableTask");
        assertEquals(taskData.getName(), "availableTask");
        assertEquals(taskData.getDescription(), "first LoadSystemTask");
        //assertEquals(taskData.getRequiredRoles().get(0), Role.JAVAPROGRAMMER);
        assertEquals(taskData.getNextTasksNames().size(), 0);
        assertEquals(taskData.getPreviousTasksNames().size(), 0);
        assertEquals(taskData.getProjectName(), "availableProject");
        assertNull(taskData.getReplacesTaskName());
        assertEquals(taskData.getEstimatedDuration().getHour(), 16);
        assertEquals(taskData.getEstimatedDuration().getMinute(), 10);
        assertEquals(3.3, taskData.getAcceptableDeviation(), 0.0);
        assertNull(taskData.getEndTime());

        //tests if available
        assertEquals(taskData.getStatus(), Status.AVAILABLE);
        assertTrue(taskData.getUnfulfilledRoles().contains(Role.JAVAPROGRAMMER));
        assertEquals(taskData.getUnfulfilledRoles().size(), 1);

        //test executing task
        try {
            lsc.LoadSystem("src/Tests/jsons/executingTask.json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        taskData = taskManSystem.getTaskData("executingProject", "executingTask");

        assertEquals(taskData.getStatus(), Status.EXECUTING);

        //test if users are correctly loaded
        assertTrue(taskData.getUserNamesWithRole().containsKey("SamHa"));
        assertTrue(taskData.getUserNamesWithRole().containsKey("OlavBl"));
        assertEquals(taskData.getUserNamesWithRole().get("OlavBl"), Role.PYTHONPROGRAMMER);
        assertEquals(taskData.getUserNamesWithRole().get("SamHa"), Role.JAVAPROGRAMMER);

        //test pending task
        try {
            lsc.LoadSystem("src/Tests/jsons/pendingTask.json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        taskData = taskManSystem.getTaskData("pendingProject", "pendingTask");

        assertEquals(taskData.getStatus(), Status.PENDING);
        assertTrue(taskData.getUserNamesWithRole().containsKey("SamHa"));
        assertFalse(taskData.getUserNamesWithRole().containsKey("OlavBl"));
        assertEquals(taskData.getUserNamesWithRole().get("SamHa"), Role.JAVAPROGRAMMER);

        //TODO replace, prev, multiple projects, started ended and remaining task in 1, testen met 2 starten dezelfde tijd

        //test finished task
        try {
            lsc.LoadSystem("src/Tests/jsons/finishedTask.json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        taskData = taskManSystem.getTaskData("finishedProject", "finishedTask");
        assertEquals(taskData.getStatus(), Status.FINISHED);
        assertEquals(taskData.getEndTime().getHour(), 3);
        assertEquals(taskData.getEndTime().getMinute(), 10);

        //test failed task
        try {
            lsc.LoadSystem("src/Tests/jsons/failedTask.json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        taskData = taskManSystem.getTaskData("failedProject", "failedTask");
        assertEquals(taskData.getStatus(), Status.FAILED);
        assertEquals(taskData.getEndTime().getHour(), 3);
        assertEquals(taskData.getEndTime().getMinute(), 10);


        //test replacing task
        try {
            lsc.LoadSystem("src/Tests/jsons/replaceTask.json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        taskData = taskManSystem.getTaskData("replaceProject", "replacedTask");
        assertEquals(taskData.getStatus(), Status.FAILED);
        assertEquals(taskData.getEndTime().getHour(), 3);
        assertEquals(taskData.getEndTime().getMinute(), 11);
        taskData = taskManSystem.getTaskData("replaceProject", "replacesTask");
        assertEquals(taskData.getUnfulfilledRoles().size(), 0);
        assertEquals(taskData.getStatus(), Status.EXECUTING);
        assertEquals(taskData.getReplacesTaskName(), "replacedTask");
        projectData = taskManSystem.getProjectData("replaceProject");
        assertTrue(projectData.getReplacedTasksNames().contains("replacedTask"));

        //test adding previousTask




    }

    @Test
    public void ui() throws LoginException {
        /*

        LoadSystemUI lsu = new LoadSystemUI(lsc);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        //Tests user has not the right permission
        User manager = userManager.getUser("SamHa", "trein123");
        session.login(manager);
        lsu.loadSystem();
        assertEquals("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function\n".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString());
        out.reset();

        //Tests user has right permission but wants to go back
        manager = userManager.getUser("WardGr", "minecraft123");
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
        System.setIn(new ByteArrayInputStream("src/Tests/loadTest.json\n".getBytes()));
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
        System.setIn(new ByteArrayInputStream("src/src/loadTest.json\n".getBytes()));
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
        System.setIn(new ByteArrayInputStream("src/Tests/InvalidLogicTest.json\n".getBytes()));
        lsu.loadSystem();
        assertEquals(
                """
                        Type BACK to cancel system load at any time
                        *********** SYSTEM LOAD FORM ***********
                        please enter the path of the load file:\s
                        ERROR: invalid file logic
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString());
        out.reset();

        */
    }

}
