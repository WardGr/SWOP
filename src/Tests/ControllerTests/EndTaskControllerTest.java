package Tests.ControllerTests;

import Application.Command.CommandManager;
import Application.IncorrectPermissionException;
import Application.Session.SessionProxy;
import Application.Controllers.TaskControllers.EndTaskController;
import Application.Controllers.TaskControllers.NoCurrentTaskException;
import Domain.DataClasses.Time;
import Application.Session.Session;


import Domain.DataClasses.Tuple;
import Domain.Project.ProjectStatus;
import Domain.Task.*;
import Domain.TaskManSystem.TaskManSystem;
import Domain.User.Role;
import Domain.User.User;

import org.junit.Before;
import org.junit.Test;


import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;

public class EndTaskControllerTest {

    private EndTaskController etc;
    private TaskManSystem taskManSystem;
    private Session current;
    private User java;
    private User python;
    private User sysadmin;
    private User man;

    private Set<Role> javaRole;
    private Set<Role> pythonRole;
    private Set<Role> sysadminRole;
    private Set<Role> projectmanRole;
    private List<Role> roles;

    @Before
    public void setUp() throws Exception {
        this.current = new Session();
        this.taskManSystem = new TaskManSystem(new Time(0));

        this.javaRole = new HashSet<>();
        javaRole.add(Role.JAVAPROGRAMMER);
        this.pythonRole = new HashSet<>();
        pythonRole.add(Role.PYTHONPROGRAMMER);
        this.sysadminRole = new HashSet<>();
        sysadminRole.add(Role.SYSADMIN);
        this.projectmanRole = new HashSet<>();
        projectmanRole.add(Role.PROJECTMANAGER);

        this.java = new User("Java", "java", javaRole);
        this.python = new User("Python", "python", pythonRole);
        this.sysadmin = new User("Sys", "sys", sysadminRole);
        this.man = new User("Pm", "pm", projectmanRole);

        current.login(java);

        taskManSystem.createProject("Omer", "Project for the Omer brewery", new Time(2000));
        this.roles = new LinkedList<>();
        roles.add(Role.JAVAPROGRAMMER);
        roles.add(Role.PYTHONPROGRAMMER);
        taskManSystem.addTaskToProject("Omer", "Hire brewer", "Get suitable brewer", new Time(10), .3, roles, new HashSet<>(), new HashSet<>());
        taskManSystem.addTaskToProject("Omer", "Buy ingredients", "Get ingredients for the beer", new Time(2), .3, roles, new HashSet<>(), new HashSet<>());


        SessionProxy wrapper = new SessionProxy(current);
        CommandManager commandManager = new CommandManager();

        this.etc = new EndTaskController(wrapper, taskManSystem, commandManager);
        assertEquals(Status.AVAILABLE, taskManSystem.getTaskData("Omer", "Hire brewer").getStatus());
    }


    @Test
    public void integrationTest() throws Exception {
        assertTrue(etc.endTaskPreconditions());
        taskManSystem.startTask("Omer", "Hire brewer", java, Role.JAVAPROGRAMMER);
        assertEquals(Status.PENDING, taskManSystem.getTaskData("Omer", "Hire brewer").getStatus());
        taskManSystem.startTask("Omer", "Hire brewer", python, Role.PYTHONPROGRAMMER);
        assertEquals(Status.EXECUTING, taskManSystem.getTaskData("Omer", "Hire brewer").getStatus());

        taskManSystem.advanceTime(10);
        etc.finishCurrentTask();
        assertEquals(Status.FINISHED, taskManSystem.getTaskData("Omer", "Hire brewer").getStatus());

        assertEquals(Status.AVAILABLE, taskManSystem.getTaskData("Omer", "Buy ingredients").getStatus());
        taskManSystem.startTask("Omer", "Buy ingredients", java, Role.JAVAPROGRAMMER);
        assertEquals(Status.PENDING, taskManSystem.getTaskData("Omer", "Buy ingredients").getStatus());
        taskManSystem.startTask("Omer", "Buy ingredients", python, Role.PYTHONPROGRAMMER);
        assertEquals(Status.EXECUTING, taskManSystem.getTaskData("Omer", "Buy ingredients").getStatus());

        current.logout();
        current.login(java);
        etc.failCurrentTask();
        assertEquals(Status.FAILED, taskManSystem.getTaskData("Omer", "Buy ingredients").getStatus());

        Set<Tuple<String, String>> prev = new HashSet<>();
        Set<Tuple<String, String>> next = new HashSet<>();
        taskManSystem.addTaskToProject("Omer", "Make beer", "Make the beer", new Time(10), .3, roles, new HashSet<>(), new HashSet<>());
        taskManSystem.addTaskToProject("Omer", "Sell beer", "Sell the beer", new Time(10), .3, roles, new HashSet<>(), new HashSet<>());
        assertEquals(Status.AVAILABLE, taskManSystem.getTaskData("Omer", "Make beer").getStatus());
        assertEquals(Status.AVAILABLE, taskManSystem.getTaskData("Omer", "Sell beer").getStatus());
        prev.add(new Tuple<>("Omer", "Make beer"));
        next.add(new Tuple<>("Omer", "Sell beer"));
        taskManSystem.addTaskToProject("Omer", "Clean up", "Clean up the brewery", new Time(10), .3, roles, prev, next);
        assertEquals(Status.UNAVAILABLE, taskManSystem.getTaskData("Omer", "Clean up").getStatus());
        assertEquals(Status.AVAILABLE, taskManSystem.getTaskData("Omer", "Make beer").getStatus());
        assertEquals(Status.UNAVAILABLE, taskManSystem.getTaskData("Omer", "Sell beer").getStatus());

        taskManSystem.startTask("Omer", "Make beer", java, Role.JAVAPROGRAMMER);
        taskManSystem.startTask("Omer", "Make beer", python, Role.PYTHONPROGRAMMER);

        taskManSystem.advanceTime(10);
        etc.finishCurrentTask();
        assertEquals(Status.FINISHED, taskManSystem.getTaskData("Omer", "Make beer").getStatus());
        assertEquals(Status.AVAILABLE, taskManSystem.getTaskData("Omer", "Clean up").getStatus());

        taskManSystem.startTask("Omer", "Clean up", java, Role.JAVAPROGRAMMER);
        assertEquals(Status.PENDING, taskManSystem.getTaskData("Omer", "Clean up").getStatus());
        taskManSystem.startTask("Omer", "Clean up", python, Role.PYTHONPROGRAMMER);
        taskManSystem.advanceTime(1);
        current.logout();
        current.login(java);
        etc.failCurrentTask();

        assertEquals(Status.FAILED, taskManSystem.getTaskData("Omer", "Clean up").getStatus());
        assertEquals(Status.UNAVAILABLE, taskManSystem.getTaskData("Omer", "Sell beer").getStatus());

        taskManSystem.replaceTaskInProject("Omer", "Hire cleanup", "Hire someone to do the cleanup", new Time(2), .4, "Clean up");
        assertEquals(Status.AVAILABLE, taskManSystem.getTaskData("Omer", "Hire cleanup").getStatus());
        taskManSystem.startTask("Omer", "Hire cleanup", java, Role.JAVAPROGRAMMER);
        assertEquals(Status.PENDING, taskManSystem.getTaskData("Omer", "Hire cleanup").getStatus());
        assertEquals(Status.FAILED, taskManSystem.getTaskData("Omer", "Clean up").getStatus());
        assertEquals(Status.UNAVAILABLE, taskManSystem.getTaskData("Omer", "Sell beer").getStatus());
        taskManSystem.startTask("Omer", "Hire cleanup", python, Role.PYTHONPROGRAMMER);
        taskManSystem.advanceTime(2);
        current.logout();
        current.login(java);

        etc.finishCurrentTask();
        assertEquals(Status.FINISHED, taskManSystem.getTaskData("Omer", "Hire cleanup").getStatus());
        assertEquals(Status.FAILED, taskManSystem.getTaskData("Omer", "Clean up").getStatus());
        assertEquals(Status.AVAILABLE, taskManSystem.getTaskData("Omer", "Sell beer").getStatus());

        current.logout();
        current.login(sysadmin);

        assertEquals(etc.getTaskData("Omer", "Hire brewer").getStatus(), Status.FINISHED);
        assertEquals(etc.getTaskData("Omer", "Buy ingredients").getStatus(), Status.FAILED);
        assertEquals(etc.getTaskData("Omer", "Make beer").getStatus(), Status.FINISHED);
        assertEquals(etc.getTaskData("Omer", "Clean up").getStatus(), Status.FAILED);
        assertEquals(etc.getTaskData("Omer", "Sell beer").getStatus(), Status.AVAILABLE);
        assertEquals(etc.getTaskData("Omer", "Hire cleanup").getStatus(), Status.FINISHED);
        assertEquals(etc.getTaskData("Omer", "Hire brewer").getEndTime(), new Time(10));
        assertEquals(etc.getTaskData("Omer", "Buy ingredients").getEndTime(), new Time(10));
        assertEquals(etc.getTaskData("Omer", "Make beer").getEndTime(), new Time(20));
        assertEquals(etc.getTaskData("Omer", "Clean up").getEndTime(), new Time(21));
        assertNull(etc.getTaskData("Omer", "Sell beer").getEndTime());

        assertEquals(ProjectStatus.ONGOING, etc.getProjectData("Omer").getStatus());
        assertEquals(new Time(0), etc.getProjectData("Omer").getCreationTime());
        assertEquals(new Time(2000), etc.getProjectData("Omer").getDueTime());
        assertEquals("Project for the Omer brewery", etc.getProjectData("Omer").getDescription());

        taskManSystem.createProject("Test", "just a test", new Time(30));
        assertEquals(new Time(23), etc.getProjectData("Test").getCreationTime());
        assertEquals("just a test", etc.getProjectData("Test").getDescription());


        assertEquals(new Time(23), etc.getTaskManSystemData().getSystemTime());
        assertEquals(2, etc.getTaskManSystemData().getProjectsData().size());


    }

    @Test
    public void testFailTask() throws Exception {
        current.login(python);
        taskManSystem.startTask("Omer", "Buy ingredients", python, Role.PYTHONPROGRAMMER);
        taskManSystem.startTask("Omer", "Buy ingredients", java, Role.JAVAPROGRAMMER);

        etc.failCurrentTask();
        assertEquals(Status.FAILED, taskManSystem.getTaskData("Omer", "Buy ingredients").getStatus());
    }

    @Test
    public void testFinishTask() throws Exception {
        taskManSystem.startTask("Omer", "Hire brewer", java, Role.JAVAPROGRAMMER);
        taskManSystem.startTask("Omer", "Hire brewer", python, Role.PYTHONPROGRAMMER);

        current.login(java);
        etc.finishCurrentTask();
        assertEquals(Status.FINISHED, taskManSystem.getTaskData("Omer", "Hire brewer").getStatus());
    }

    @Test
    public void testIncorrectPermissions() {
        current.logout();
        current.login(man);
        assertFalse(etc.endTaskPreconditions());
        assertThrows(IncorrectPermissionException.class, etc::getTaskManSystemData);
        assertThrows(IncorrectPermissionException.class, () -> etc.getProjectData("Omer"));
        assertThrows(IncorrectPermissionException.class, () -> etc.getTaskData("Omer", "Hire brewer"));
        assertThrows(IncorrectPermissionException.class, () -> etc.getTaskData("Omer", "Buy ingredients"));
        assertThrows(IncorrectPermissionException.class, () -> etc.getTaskData("Omer", "Make beer"));
        assertThrows(IncorrectPermissionException.class, () -> etc.getProjectData("Test"));
        assertThrows(IncorrectPermissionException.class, () -> etc.finishCurrentTask());
        assertThrows(IncorrectPermissionException.class, () -> etc.failCurrentTask());
        current.logout();
        current.login(java);
        assertThrows(NoCurrentTaskException.class, () -> etc.finishCurrentTask());
        assertThrows(NoCurrentTaskException.class, () -> etc.failCurrentTask());
    }

    @Test
    public void testPreconditions() {
        current.logout();
        current.login(java);
        assertTrue(etc.endTaskPreconditions());
        current.logout();
        current.login(python);
        assertTrue(etc.endTaskPreconditions());
        current.logout();
        current.login(sysadmin);
        assertTrue(etc.endTaskPreconditions());
        current.logout();
        current.login(man);
        assertFalse(etc.endTaskPreconditions());
    }

    @Test
    public void testGetUserRoles() {
        assertEquals(1, etc.getUserRoles().size());
        Set<Role> twoRolesSet = new HashSet<>();
        twoRolesSet.add(Role.PYTHONPROGRAMMER);
        twoRolesSet.add(Role.JAVAPROGRAMMER);
        User twoRoles = new User("2Roles", "2Roles", twoRolesSet);
        current.logout();
        current.login(twoRoles);
        assertEquals(2, etc.getUserRoles().size());

        current.logout();
        assertEquals(0, etc.getUserRoles().size());
    }
}
