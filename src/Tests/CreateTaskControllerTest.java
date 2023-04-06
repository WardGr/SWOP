package Tests;

import Application.CreateTaskController;
import Application.IncorrectPermissionException;
import Application.Session;
import Application.SessionWrapper;
import Domain.*;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;

import static org.junit.Assert.*;

public class CreateTaskControllerTest {

    @Test
    public void testCreateTaskController() throws LoginException, UserNotFoundException, ProjectNotFoundException, InvalidTimeException, TaskNotFoundException, TaskNameAlreadyInUseException, IncorrectPermissionException, ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, ReplacedTaskNotFailedException, NewTimeBeforeSystemTimeException, IncorrectTaskStatusException, IncorrectUserException, FailTimeAfterSystemTimeException, StartTimeBeforeAvailableException, EndTimeBeforeStartTimeException {
        /*
        Session omer = new Session();
        SessionWrapper omerWrapper = new SessionWrapper(omer);
        TaskManSystem tms = new TaskManSystem(new Time(0));
        UserManager um = new UserManager();
        User brewer = um.getUser("OlavBl", "peer123");
        User boss = um.getUser("WardGr", "minecraft123");

        CreateTaskController ctc = new CreateTaskController(omerWrapper, tms, um);
        omer.login(boss);
        assertTrue(ctc.createTaskPreconditions());
        tms.createProject("project1", "description", new Time(100), new Time(1000));
        ctc.createTask("project1", "task1", "description", 1, 1, 1, "OlavBl", new LinkedList<>());
        ctc.createTask("project1", "task2", "description", 1, 1, 1, "OlavBl", new LinkedList<>());
        ctc.createTask("project1", "task3", "description", 1, 1, 1, "OlavBl", new LinkedList<>());
        assertEquals("task1", tms.showAvailableTasks().get("project1").get(0));
        assertEquals("task2", tms.showAvailableTasks().get("project1").get(1));
        assertEquals("task3", tms.showAvailableTasks().get("project1").get(2));
        tms.startTask("project1", "task1", new Time(0), brewer);
        tms.startTask("project1", "task2", new Time(0), brewer);
        tms.advanceTime(new Time(100));
        assertEquals("task3", tms.showAvailableTasks().get("project1").get(0));
        HashMap executing = new HashMap();
        LinkedList list = new LinkedList();
        list.add("task1");
        list.add("task2");
        executing.put("project1", list);
        assertEquals(executing.get("project1"), tms.showExecutingTasks().get("project1"));
        assertEquals(executing, tms.showExecutingTasks());
        tms.endTask("project1", "task1", Status.FAILED, new Time(80), brewer);
        tms.endTask("project1", "task2", Status.FAILED, new Time(80), brewer);
        ctc.replaceTask("project1", "newTask1", "Replaces old task 1", 15, 12, 30.0, "task1");
        ctc.replaceTask("project1", "newTask2", "Replaces old task 2", 15, 12, 30.0, "task2");
        assertThrows(IncorrectTaskStatusException.class, () -> ctc.replaceTask("project1", "newTask3", "Replaces old task 3", 15, 12, 30.0, "task3"));
        assertEquals("task3", tms.showAvailableTasks().get("project1").get(0));
        assertEquals("newTask1", tms.showAvailableTasks().get("project1").get(1));
        assertEquals("newTask2", tms.showAvailableTasks().get("project1").get(2));
        HashMap projects = new HashMap();
        projects.put("project1", new LinkedList());
        assertEquals(projects, tms.showExecutingTasks());
        list = new LinkedList();
        list.add("task3");
        list.add("newTask1");
        list.add("newTask2");
        projects.put("project1", list);
        assertEquals(projects, tms.showAvailableTasks());

        tms.createProject("project2", "description", new Time(100), new Time(1000));
        assertEquals(new LinkedList<>(), tms.showAvailableTasks().get("project2"));
        assertEquals(2, tms.showAvailableTasks().size());
        ctc.createTask("project2", "task1", "description", 1, 1, 1, "OlavBl", new LinkedList<>());
        ctc.createTask("project2", "task2", "description", 1, 1, 1, "OlavBl", new LinkedList<>());
        LinkedList tasks2 = new LinkedList();
        tasks2.add("task1");
        tasks2.add("task2");
        projects.put("project2", tasks2);
        assertEquals(projects, tms.showAvailableTasks());


        omer.logout();
        omer.login(brewer);
        assertFalse(ctc.createTaskPreconditions());
        assertThrows(IncorrectPermissionException.class, () -> ctc.createTask("project1", "task1", "description", 1, 1, 1, "OlavBl", null));
        assertThrows(IncorrectPermissionException.class, () -> ctc.replaceTask("project1", "task1", "task2", 12, 1, 1, "hoi"));

        */
    }
}
