package Tests.ControllerTests;

import Application.*;
import Application.Session.LoginException;
import Application.Session.Session;
import Application.Session.SessionProxy;
import Application.Command.LoadSystemCommands.InvalidFileException;
import Application.Controllers.SystemControllers.LoadSystemController;
import Application.Command.CommandManager;
import Domain.DataClasses.InvalidTimeException;
import Domain.DataClasses.Time;
import Domain.Project.ProjectData;
import Domain.Project.ProjectStatus;
import Domain.Project.TaskNotFoundException;
import Domain.TaskManSystem.ProjectNotFoundException;
import Domain.TaskManSystem.TaskManSystem;
import Domain.Task.Status;
import Domain.Task.TaskData;
import Domain.User.Role;
import Domain.User.User;
import Domain.User.UserManager;
import org.junit.Test;

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
            lsc.LoadSystem("src/Resources/jsons/availableTask.json");
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
        assertTrue(taskData.getNextTasksData().isEmpty());
        assertTrue(taskData.getPrevTasksData().isEmpty());
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
            lsc.LoadSystem("src/Resources/jsons/executingTask.json");
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
            lsc.LoadSystem("src/Resources/jsons/pendingTask.json");
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
            lsc.LoadSystem("src/Resources/jsons/finishedTask.json");
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
            lsc.LoadSystem("src/Resources/jsons/failedTask.json");
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
            lsc.LoadSystem("src/Resources/jsons/replaceTask.json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        TaskData replacedTask = taskManSystem.getTaskData("replaceProject", "replacedTask");
        assertEquals(replacedTask.getStatus(), Status.FAILED);
        assertEquals(replacedTask.getEndTime().getHour(), 3);
        assertEquals(replacedTask.getEndTime().getMinute(), 11);
        TaskData replacesTask = taskManSystem.getTaskData("replaceProject", "replacesTask");
        assertEquals(replacesTask.getUnfulfilledRoles().size(), 0);
        assertEquals(replacesTask.getStatus(), Status.EXECUTING);
        assertEquals(replacesTask.getReplacesTaskName(), "replacedTask");

        ProjectData projectData = taskManSystem.getProjectData("replaceProject");
        assertTrue(projectData.getReplacedTasksData().contains(replacedTask));
        session.logout();
    }
    @Test
    public void previousTaskTest() throws LoginException, ProjectNotFoundException, TaskNotFoundException {
        User manager = userManager.getUser("DieterVH", "computer776");
        session.login(manager);

        //test adding prevTask
        try {
            lsc.LoadSystem("src/Resources/jsons/previousTask.json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        TaskData taskData = taskManSystem.getTaskData("previousProject", "previousTask");
        TaskData nextTask = taskManSystem.getTaskData("previousProject", "nextTask");

        assertEquals(taskData.getStatus(), Status.EXECUTING);
        assertTrue(taskData.getNextTasksData().contains(nextTask));
        assertEquals(nextTask.getStatus(), Status.UNAVAILABLE);
        assertTrue(nextTask.getPrevTasksData().contains(taskData));
        assertEquals(nextTask.getPrevTasksData().size(), 1);
        assertTrue(nextTask.getNextTasksData().isEmpty());
        session.logout();
    }
    @Test
    public void dependingProject() throws LoginException, ProjectNotFoundException, TaskNotFoundException {
        User manager = userManager.getUser("DieterVH", "computer776");
        session.login(manager);

        //test adding prevTask
        try {
            lsc.LoadSystem("src/Resources/jsons/dependingProject.json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        TaskData taskData = taskManSystem.getTaskData("previousProject", "previousTask");
        TaskData nextTask = taskManSystem.getTaskData("nextProject", "nextTask");

        assertEquals(taskData.getStatus(), Status.EXECUTING);
        assertTrue(taskData.getNextTasksData().contains(nextTask));
        assertEquals(nextTask.getStatus(), Status.UNAVAILABLE);
        assertTrue(nextTask.getPrevTasksData().contains(taskData));
        assertEquals(nextTask.getPrevTasksData().size(), 1);
        assertTrue(nextTask.getNextTasksData().isEmpty());
        session.logout();
    }
    @Test
    public void multipleProjectsTest() throws LoginException, ProjectNotFoundException, TaskNotFoundException {
        User manager = userManager.getUser("DieterVH", "computer776");
        session.login(manager);

        //testing loading multiple projects from 1 file

        try {
            lsc.LoadSystem("src/Resources/jsons/multipleProjects.json");
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
        assertTrue(taskData.getNextTasksData().isEmpty());
        assertTrue(taskData.getPrevTasksData().isEmpty());
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
            lsc.LoadSystem("src/Resources/jsons/InvalidLogicTest.json.json");
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
            lsc.LoadSystem("src/Resources/jsons/availableTask.json");
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
            lsc.LoadSystem("src/Resources/jsons/invalidRole.json");
        } catch (Exception e) {
            assertTrue(e instanceof InvalidFileException);
            assertEquals(e.getMessage(), "ERROR: File logic is invalid so couldn't setup system.");
        }
        session.logout();
    }
}
