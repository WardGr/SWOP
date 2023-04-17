package Tests;

import Application.CreateTaskController;
import Application.IncorrectPermissionException;
import Application.Session;
import Application.SessionWrapper;
import Domain.*;
import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.NonDeveloperRoleException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class CreateTaskControllerTest {

    @Test
    public void testCreateTaskController() throws LoginException, UserNotFoundException, ProjectNotFoundException, InvalidTimeException, TaskNotFoundException, TaskNameAlreadyInUseException, IncorrectPermissionException, ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, NewTimeBeforeSystemTimeException, IncorrectTaskStatusException, IncorrectUserException, EndTimeBeforeStartTimeException, ProjectNotOngoingException, LoopDependencyGraphException, NonDeveloperRoleException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        Session wardsSession = new Session();
        SessionWrapper omerWrapper = new SessionWrapper(wardsSession);
        Set wardsRoles = new HashSet();
        wardsRoles.add(Role.PROJECTMANAGER);
        wardsRoles.add(Role.JAVAPROGRAMMER);
        wardsRoles.add(Role.PYTHONPROGRAMMER);
        User ward = new User("WardGr", "peer123", wardsRoles);
        wardsSession.login(ward);
        TaskManSystem tms = new TaskManSystem(new Time(12));

        CreateTaskController ctc = new CreateTaskController(omerWrapper, tms, new UserManager());

        assertTrue(ctc.createTaskPreconditions());

        wardsSession.logout();
        assertFalse(ctc.createTaskPreconditions());

        tms.createProject("Omer", "Brew omer beer", new Time(30));
        List roles = new ArrayList();
        roles.add(Role.JAVAPROGRAMMER);
        roles.add(Role.PYTHONPROGRAMMER);

        Set falseRoles = new HashSet();
        falseRoles.add(Role.SYSADMIN);
        User false1 = new User("false1", "false1", falseRoles);
        wardsSession.login(false1);
        assertFalse(ctc.createTaskPreconditions());
        assertThrows(IncorrectPermissionException.class, () -> {
            ctc.createTask("Omer", "Hire brewer", "Find a suitable brewer for our beer", new Time(5), 1,  roles, new HashSet<>(), new HashSet<>());
        });
        assertThrows(IncorrectPermissionException.class, () -> {
            ctc.replaceTask("omer", "Switch Brewer", "Hire a new brewer", new Time(5), 1, "Hire Brewer");
        });

        wardsSession.logout();

        falseRoles = new HashSet();
        falseRoles.add(Role.JAVAPROGRAMMER);
        User false2 = new User("false2", "false2", falseRoles);
        wardsSession.login(false2);
        assertFalse(ctc.createTaskPreconditions());
        assertThrows(IncorrectPermissionException.class, () -> {
            ctc.createTask("Omer", "Hire brewer", "Find a suitable brewer for our beer", new Time(5), 1,  roles, new HashSet<>(), new HashSet<>());
        });
        assertThrows(IncorrectPermissionException.class, () -> {
            ctc.replaceTask("omer", "Switch Brewer", "Hire a new brewer", new Time(5), 1, "Hire Brewer");
        });

        wardsSession.logout();

        falseRoles = new HashSet();
        falseRoles.add(Role.PYTHONPROGRAMMER);
        User false3 = new User("false3", "false3", falseRoles);
        wardsSession.login(false3);
        assertFalse(ctc.createTaskPreconditions());
        assertThrows(IncorrectPermissionException.class, () -> {
            ctc.createTask("Omer", "Hire brewer", "Find a suitable brewer for our beer", new Time(5), 1,  roles, new HashSet<>(), new HashSet<>());
        });
        assertThrows(IncorrectPermissionException.class, () -> {
            ctc.replaceTask("omer", "Switch Brewer", "Hire a new brewer", new Time(5), 1, "Hire Brewer");
        });

        wardsSession.logout();
        wardsSession.login(ward);

        ctc.createTask("Omer", "Hire brewer", "Find a suitable brewer for our beer", new Time(5), 1,  roles, new HashSet<>(), new HashSet<>());
        Set prev = new HashSet();
        prev.add("Hire brewer");

        ctc.createTask("Omer", "Buy ingredients", "Buy ingredients for the beer", new Time(5), 1,  roles, prev, new HashSet<>());

        Set prev2 = new HashSet(prev);
        prev2.add("Buy ingredients");

        ctc.createTask("Omer", "Brew beer", "Brew the beer", new Time(5), 1,  roles, prev2, new HashSet<>());

        assertThrows(ProjectNotFoundException.class, () -> {
            ctc.createTask("Omer2", "Brew beer", "Brew the beer", new Time(5), 1,  roles, prev2, new HashSet<>());
        });
        assertThrows(ProjectNotFoundException.class, () -> {
            ctc.createTask("LeFort", "Brew beer", "Brew the beer", new Time(5), 1,  roles, prev2, new HashSet<>());
        });

        assertThrows(InvalidTimeException.class, () -> {
            ctc.createTask("Omer", "Brew beer", "Brew the beer", new Time(-5), 1,  roles, prev2, new HashSet<>());
        });
        assertThrows(InvalidTimeException.class, () -> {
            ctc.createTask("Omer", "Brew beer", "Brew the beer", new Time(-1), 1,  roles, prev2, new HashSet<>());
        });

        assertThrows(TaskNameAlreadyInUseException.class, () -> {
            ctc.createTask("Omer", "Hire brewer", "Find a suitable brewer for our beer", new Time(5), 1,  roles, new HashSet<>(), new HashSet<>());
        });
        assertThrows(TaskNameAlreadyInUseException.class, () -> {
            ctc.createTask("Omer", "Buy ingredients", "Buy ingredients for the beer", new Time(5), 1,  roles, new HashSet<>(), new HashSet<>());
        });

        HashSet fakeTask = new HashSet();
        fakeTask.add("fake");
        assertThrows(TaskNotFoundException.class, () -> {
            ctc.createTask("Omer", "Fire brewer", "fire the current lead of brewery", new Time(5), 1,  roles, fakeTask, new HashSet<>());
        });

        // TODO de andere exceptionso testen??

        // Test replaceTask
        Set x = new HashSet();
        x.add(Role.PYTHONPROGRAMMER);
        User dieter = new User("Dieter", "Dieter", x);

        assertEquals(Status.AVAILABLE, tms.getTaskData("Omer", "Hire brewer").getStatus());
        assertEquals(Status.UNAVAILABLE, tms.getTaskData("Omer", "Buy ingredients").getStatus());
        tms.startTask("Omer", "Hire brewer", ward, Role.JAVAPROGRAMMER);
        tms.startTask("Omer", "Hire brewer", dieter, Role.PYTHONPROGRAMMER);
        assertEquals(Status.EXECUTING, tms.getTaskData("Omer", "Hire brewer").getStatus());
        tms.advanceTime(10);
        tms.failTask("Omer", "Hire brewer", ward);

        assertEquals(Status.FAILED, tms.getTaskData("Omer", "Hire brewer").getStatus());

        ctc.replaceTask("Omer", "Replace brewer", "Replace incapable brewer for new one", new Time(5), 1, "Hire brewer");
        assertEquals(Status.AVAILABLE, tms.getTaskData("Omer", "Replace brewer").getStatus());
        assertEquals(Status.FAILED, tms.getTaskData("Omer", "Hire brewer").getStatus());
        assertEquals(Status.UNAVAILABLE, tms.getTaskData("Omer", "Buy ingredients").getStatus());

        tms.startTask("Omer", "Replace brewer", ward, Role.JAVAPROGRAMMER);
        tms.startTask("Omer", "Replace brewer", dieter, Role.PYTHONPROGRAMMER);
        tms.advanceTime(10);
        tms.finishTask("Omer", "Replace brewer", ward);
        assertEquals(Status.FINISHED, tms.getTaskData("Omer", "Replace brewer").getStatus());
        assertEquals(Status.AVAILABLE, tms.getTaskData("Omer", "Buy ingredients").getStatus());


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
        ctc.createTask("project1", "task", "description", 1, 1, 1, "OlavBl", new LinkedList<>());
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
