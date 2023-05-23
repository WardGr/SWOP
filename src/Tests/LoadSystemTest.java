package Tests;

import Application.*;
import Domain.*;
import Domain.TaskStates.TaskData;
import UserInterface.LoadSystemUI;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class LoadSystemTest {

    UserManager userManager = new UserManager();
    TaskManSystem taskManSystem = new TaskManSystem(new Time(0, 0));
    Session session = new Session();
    SessionProxy sessionProxy = new SessionProxy(session);

    CommandManager commandManager = new CommandManager();
    LoadSystemController lsc = new LoadSystemController(sessionProxy, taskManSystem, userManager, commandManager);

    public LoadSystemTest() throws InvalidTimeException {
    }

    @Test
    public void testLoadSystem() throws ProjectNotFoundException, TaskNotFoundException, LoginException {
        availableTaskTest();
        executingTaskTest();
        pendingTaskTest();
        finishedTaskTest();
        failedTaskTest();
        replacingTaskTest();
        previousTaskTest();
        multipleProjectsTest();
        wrongPathTest();
        invalidLogicTest();
        invalidRoleTest();
        //ui();

    }
    @Test
    public void availableTaskTest() throws LoginException, ProjectNotFoundException, TaskNotFoundException {
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
        ProjectData projectData = taskManSystem.getProjectData("availableProject");
        assertEquals(projectData.getName(), "availableProject");
        assertEquals(projectData.getStatus(), ProjectStatus.ONGOING);
        assertEquals(projectData.getDescription(), "first LoadSystemTest");
        assertEquals(projectData.getCreationTime().getHour(), 1);
        assertEquals(projectData.getCreationTime().getMinute(), 11);
        assertEquals(projectData.getDueTime().getHour(), 16);
        assertEquals(projectData.getDueTime().getMinute(), 10);

        //tests basic task fields
        TaskData taskData = taskManSystem.getTaskData("availableProject", "availableTask");
        assertEquals(taskData.getName(), "availableTask");
        assertEquals(taskData.getDescription(), "first LoadSystemTask");
        //assertEquals(taskData.getRequiredRoles().get(0), Role.JAVAPROGRAMMER);
        assertEquals(taskData.getNextTaskNames().size(), 0);
        assertEquals(taskData.getPrevTaskNames().size(), 0);
        assertEquals(taskData.getProjectName(), "availableProject");
        assertNull(taskData.getReplacesTaskName());
        assertEquals(taskData.getEstimatedDuration().getHour(), 16);
        assertEquals(taskData.getEstimatedDuration().getMinute(), 10);
        assertEquals(3.3, taskData.getAcceptableDeviation(), 0.0);
        assertNull(taskData.getEndTime());
        session.logout();
    }
    @Test
    public void executingTaskTest() throws LoginException, ProjectNotFoundException, TaskNotFoundException {
        User manager = userManager.getUser("DieterVH", "computer776");
        session.login(manager);

        //test executing task
        try {
            lsc.LoadSystem("src/Tests/jsons/executingTask.json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        TaskData taskData = taskManSystem.getTaskData("executingProject", "executingTask");

        assertEquals(taskData.getStatus(), Status.EXECUTING);

        //test if users are correctly loaded
        assertTrue(taskData.getUserNamesWithRole().containsKey("SamHa"));
        assertTrue(taskData.getUserNamesWithRole().containsKey("OlavBl"));
        assertEquals(taskData.getUserNamesWithRole().get("OlavBl"), Role.PYTHONPROGRAMMER);
        assertEquals(taskData.getUserNamesWithRole().get("SamHa"), Role.JAVAPROGRAMMER);
        session.logout();
    }
    @Test
    public void pendingTaskTest() throws LoginException, ProjectNotFoundException, TaskNotFoundException {
        User manager = userManager.getUser("DieterVH", "computer776");
        session.login(manager);

        //test pending task
        try {
            lsc.LoadSystem("src/Tests/jsons/pendingTask.json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        TaskData taskData = taskManSystem.getTaskData("pendingProject", "pendingTask");

        assertEquals(taskData.getStatus(), Status.PENDING);
        assertTrue(taskData.getUserNamesWithRole().containsKey("SamHa"));
        assertFalse(taskData.getUserNamesWithRole().containsKey("OlavBl"));
        assertEquals(taskData.getUserNamesWithRole().get("SamHa"), Role.JAVAPROGRAMMER);
        session.logout();
    }
    @Test
    public void finishedTaskTest() throws LoginException, ProjectNotFoundException, TaskNotFoundException {
        User manager = userManager.getUser("DieterVH", "computer776");
        session.login(manager);


        //test finished task
        try {
            lsc.LoadSystem("src/Tests/jsons/finishedTask.json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        TaskData taskData = taskManSystem.getTaskData("finishedProject", "finishedTask");
        assertEquals(taskData.getStatus(), Status.FINISHED);
        assertEquals(taskData.getEndTime().getHour(), 3);
        assertEquals(taskData.getEndTime().getMinute(), 10);
        session.logout();
    }
    @Test
    public void failedTaskTest() throws LoginException, ProjectNotFoundException, TaskNotFoundException {
        User manager = userManager.getUser("DieterVH", "computer776");
        session.login(manager);

        //test failed task
        try {
            lsc.LoadSystem("src/Tests/jsons/failedTask.json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        TaskData taskData = taskManSystem.getTaskData("failedProject", "failedTask");
        assertEquals(taskData.getStatus(), Status.FAILED);
        assertEquals(taskData.getEndTime().getHour(), 3);
        assertEquals(taskData.getEndTime().getMinute(), 10);
        session.logout();
    }
    @Test
    public void replacingTaskTest() throws LoginException, ProjectNotFoundException, TaskNotFoundException {
        User manager = userManager.getUser("DieterVH", "computer776");
        session.login(manager);

        //test replacing task
        try {
            lsc.LoadSystem("src/Tests/jsons/replaceTask.json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        TaskData taskData = taskManSystem.getTaskData("replaceProject", "replacedTask");
        assertEquals(taskData.getStatus(), Status.FAILED);
        assertEquals(taskData.getEndTime().getHour(), 3);
        assertEquals(taskData.getEndTime().getMinute(), 11);
        taskData = taskManSystem.getTaskData("replaceProject", "replacesTask");
        assertEquals(taskData.getUnfulfilledRoles().size(), 0);
        assertEquals(taskData.getStatus(), Status.EXECUTING);
        assertEquals(taskData.getReplacesTaskName(), "replacedTask");
        ProjectData projectData = taskManSystem.getProjectData("replaceProject");
        assertTrue(projectData.getReplacedTasksNames().contains("replacedTask"));
        session.logout();
    }
    @Test
    public void previousTaskTest() throws LoginException, ProjectNotFoundException, TaskNotFoundException {
        User manager = userManager.getUser("DieterVH", "computer776");
        session.login(manager);

        //test adding prevTask
        try {
            lsc.LoadSystem("src/Tests/jsons/previousTask.json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        TaskData taskData = taskManSystem.getTaskData("previousProject", "previousTask");
        assertEquals(taskData.getStatus(), Status.EXECUTING);
        assertTrue(taskData.getNextTaskNames().contains(new Tuple<>("previousProject","nextTask")));
        taskData = taskManSystem.getTaskData("previousProject", "nextTask");
        assertEquals(taskData.getStatus(), Status.UNAVAILABLE);
        assertTrue(taskData.getPrevTaskNames().contains(new Tuple<>("previousProject","previousTask")));
        assertEquals(taskData.getPrevTaskNames().size(), 1);
        assertEquals(taskData.getNextTaskNames().size(), 0);
        session.logout();
    }
    @Test
    public void multipleProjectsTest() throws LoginException, ProjectNotFoundException, TaskNotFoundException {
        User manager = userManager.getUser("DieterVH", "computer776");
        session.login(manager);

        //testing loading multiple projects from 1 file

        try {
            lsc.LoadSystem("src/Tests/jsons/multipleProjects.json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //tests basic project fields
        assertEquals(taskManSystem.getSystemTime().getHour(), 15);
        assertEquals(10, taskManSystem.getSystemTime().getMinute());
        ProjectData projectData = taskManSystem.getProjectData("availableProject");
        assertEquals(projectData.getName(), "availableProject");
        assertEquals(projectData.getStatus(), ProjectStatus.ONGOING);
        assertEquals(projectData.getDescription(), "first LoadSystemTest");
        assertEquals(projectData.getCreationTime().getHour(), 4);
        assertEquals(projectData.getCreationTime().getMinute(), 11);
        assertEquals(projectData.getDueTime().getHour(), 16);
        assertEquals(projectData.getDueTime().getMinute(), 10);

        projectData = taskManSystem.getProjectData("executingProject");
        assertEquals(projectData.getName(), "executingProject");
        assertEquals(projectData.getStatus(), ProjectStatus.ONGOING);
        assertEquals(projectData.getDescription(), "LoadSystemTest");
        assertEquals(projectData.getCreationTime().getHour(), 1);
        assertEquals(projectData.getCreationTime().getMinute(), 11);
        assertEquals(projectData.getDueTime().getHour(), 16);
        assertEquals(projectData.getDueTime().getMinute(), 10);

        projectData = taskManSystem.getProjectData("finishedProject");
        assertEquals(projectData.getName(), "finishedProject");
        assertEquals(projectData.getStatus(), ProjectStatus.FINISHED);
        assertEquals(projectData.getDescription(), "LoadSystemTest");
        assertEquals(projectData.getCreationTime().getHour(), 2);
        assertEquals(projectData.getCreationTime().getMinute(), 9);
        assertEquals(projectData.getDueTime().getHour(), 17);
        assertEquals(projectData.getDueTime().getMinute(), 10);

        //tests basic task fields
        TaskData taskData = taskManSystem.getTaskData("availableProject", "availableTask");
        assertEquals(taskData.getName(), "availableTask");
        assertEquals(taskData.getDescription(), "first LoadSystemTask");
        //assertEquals(taskData.getRequiredRoles().get(0), Role.JAVAPROGRAMMER);
        assertEquals(taskData.getNextTaskNames().size(), 0);
        assertEquals(taskData.getPrevTaskNames().size(), 0);
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

        //test finished test
        taskData = taskManSystem.getTaskData("finishedProject", "finishedTask");
        assertEquals(taskData.getStatus(), Status.FINISHED);
        assertEquals(taskData.getEndTime().getHour(), 3);
        assertEquals(taskData.getEndTime().getMinute(), 10);

        //test executing task
        taskData = taskManSystem.getTaskData("executingProject", "executingTask");
        assertEquals(taskData.getStatus(), Status.EXECUTING);

        //test if users are correctly loaded
        assertTrue(taskData.getUserNamesWithRole().containsKey("SanderSc"));
        assertTrue(taskData.getUserNamesWithRole().containsKey("HannahEr"));
        assertEquals(taskData.getUserNamesWithRole().get("HannahEr"), Role.PYTHONPROGRAMMER);
        assertEquals(taskData.getUserNamesWithRole().get("SanderSc"), Role.SYSADMIN);

        session.logout();
    }
    @Test
    public void wrongPathTest() throws LoginException {
        User manager = userManager.getUser("DieterVH", "computer776");
        session.login(manager);

        //tests wrong file path error
        try {
            lsc.LoadSystem("src/Tests/jsons/InvalidLogicTest.json.json");
        } catch (Exception e) {
            assertTrue(e instanceof InvalidFileException);
            assertEquals(e.getMessage(), "ERROR: File path is invalid.");
        }
        session.logout();
    }
    @Test
    public void invalidRoleTest() throws LoginException {
        session.login(userManager.getUser("WardGr", "minecraft123"));
        try {
            lsc.LoadSystem("src/Tests/jsons/availableTask.json");
        } catch (Exception e) {
            assertTrue(e instanceof IncorrectPermissionException);
            assertEquals(e.getMessage(), "You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");
        }
        session.logout();
    }
    @Test
    public void invalidLogicTest() throws LoginException {
        User manager = userManager.getUser("DieterVH", "computer776");
        session.login(manager);

        //tests invalid file logic && invalidRole exception
        try {
            lsc.LoadSystem("src/Tests/jsons/invalidRole.json");
        } catch (Exception e) {
            assertTrue(e instanceof InvalidFileException);
            assertEquals(e.getMessage(), "ERROR: File logic is invalid so couldn't setup system.");
        }
        session.logout();
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
        manager = userManager.getUser("DieterVH", "computer776");
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
        System.setIn(new ByteArrayInputStream("src/Tests/jsons/multipleProjects.json\n".getBytes()));
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
                        ERROR: File path is invalid.
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString());
        out.reset();

        //Tests when a user gives a  file with invalid logic
        System.setIn(new ByteArrayInputStream("src/Tests/jsons/invalidRole.json\n".getBytes()));
        lsu.loadSystem();
        assertEquals(
                """
                        Type BACK to cancel system load at any time
                        *********** SYSTEM LOAD FORM ***********
                        please enter the path of the load file:\s
                        ERROR: File logic is invalid so couldn't setup system.
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString());
        out.reset();

         */

    }
}
