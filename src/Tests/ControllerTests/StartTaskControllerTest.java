package Tests.ControllerTests;

import Application.Command.CommandManager;
import Application.Session.Session;
import Application.Session.SessionProxy;
import Application.TaskControllers.EndTaskController;
import Application.TaskControllers.StartTaskController;
import Application.TaskControllers.UnconfirmedActionException;
import Domain.DataClasses.InvalidTimeException;
import Domain.DataClasses.Time;
import Domain.Project.ProjectData;
import Domain.Project.ProjectNameAlreadyInUseException;
import Domain.Project.ProjectNotOngoingException;
import Domain.Project.TaskNotFoundException;
import Domain.Task.*;
import Domain.Task.IncorrectTaskStatusException;
import Domain.Task.LoopDependencyGraphException;
import Domain.TaskManSystem.DueBeforeSystemTimeException;
import Domain.TaskManSystem.NewTimeBeforeSystemTimeException;
import Domain.TaskManSystem.ProjectNotFoundException;
import Domain.TaskManSystem.TaskManSystem;
import Domain.User.Role;
import Domain.User.User;
import Domain.User.UserAlreadyAssignedToTaskException;
import org.junit.Before;
import org.junit.Test;
import Application.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;

public class StartTaskControllerTest {

    private StartTaskController stc;
    private TaskManSystem taskManSystem;
    private Session current;
    private User python;
    private User sysadmin;
    private User java;
    private User man;

    @Before
    public void setUp() throws Exception {
        this.current = new Session();
        this.taskManSystem = new TaskManSystem(new Time(0));

        Set<Role> javaRole = new HashSet<>();
        javaRole.add(Role.JAVAPROGRAMMER);
        Set<Role> pythonRole = new HashSet<>();
        pythonRole.add(Role.PYTHONPROGRAMMER);
        Set<Role> sysadminRole = new HashSet<>();
        sysadminRole.add(Role.SYSADMIN);
        Set<Role> projectmanRole = new HashSet<>();
        projectmanRole.add(Role.PROJECTMANAGER);

        this.java = new User("Java", "java", javaRole);
        this.python = new User("Python", "python", pythonRole);
        this.sysadmin = new User("Sys", "sys", sysadminRole);
        this.man = new User("Pm", "pm", projectmanRole);

        current.login(java);

        taskManSystem.createProject("Omer", "Project for the Omer brewery", new Time(2000));
        List<Role> roles = new LinkedList<>();
        roles.add(Role.JAVAPROGRAMMER);
        roles.add(Role.PYTHONPROGRAMMER);
        taskManSystem.addTaskToProject("Omer", "Hire brewer", "Get suitable brewer", new Time(10), .3, roles, new HashSet<>(), new HashSet<>());
        taskManSystem.addTaskToProject("Omer", "Buy ingredients", "Get ingredients for the beer", new Time(2), .3, roles, new HashSet<>(), new HashSet<>());


        SessionProxy wrapper = new SessionProxy(current);
        CommandManager commandManager = new CommandManager();

        this.stc = new StartTaskController(wrapper, taskManSystem, commandManager);
        assertEquals(Status.AVAILABLE, taskManSystem.getTaskData("Omer", "Hire brewer").getStatus());
        assertTrue(stc.startTaskPreconditions());
        assertTrue(stc.getUserRoles().contains(Role.JAVAPROGRAMMER));
        assertEquals(1, stc.getUserRoles().size());
    }

    @Test
    public void testPreconditions() {
        assertTrue(stc.getUserRoles().contains(Role.JAVAPROGRAMMER));
        assertTrue(stc.startTaskPreconditions());

        current.logout();
        assertFalse(stc.startTaskPreconditions());

        current.login(python);
        assertTrue(stc.startTaskPreconditions());
        current.logout();

        current.login(sysadmin);
        assertTrue(stc.startTaskPreconditions());
        current.logout();

        current.login(man);
        assertFalse(stc.startTaskPreconditions());
    }

    @Test
    public void testIncorrectPermissions() {
        current.logout();
        current.login(man);
        assertTrue(stc.getUserRoles().contains(Role.PROJECTMANAGER));
        assertEquals(1, stc.getUserRoles().size());
        assertFalse(stc.startTaskPreconditions());

        assertThrows(IncorrectPermissionException.class, () -> stc.startTask("Omer", "Brew Beer", Role.PROJECTMANAGER, true));
        assertThrows(IncorrectPermissionException.class, () -> stc.getProjectData("Omer"));
        assertThrows(IncorrectPermissionException.class, () -> stc.getProjectData("Test"));
        assertThrows(IncorrectPermissionException.class, stc::getTaskManSystemData);
        assertThrows(IncorrectPermissionException.class, () -> stc.getTaskData("Omer", "Brew Beer"));
        assertThrows(IncorrectPermissionException.class, () -> stc.getProjectData("Omer"));
        assertThrows(IncorrectPermissionException.class, () -> stc.getProjectData("Test"));

        current.logout();
        assertThrows(IncorrectPermissionException.class, () -> stc.getProjectData("Omer"));
        assertThrows(IncorrectPermissionException.class, () -> stc.getProjectData("Test"));
        assertThrows(IncorrectPermissionException.class, stc::getTaskManSystemData);
        assertThrows(IncorrectPermissionException.class, () -> stc.getTaskData("Omer", "Brew Beer"));
        assertThrows(IncorrectPermissionException.class, () -> stc.getProjectData("Omer"));
        assertThrows(IncorrectPermissionException.class, () -> stc.getProjectData("Test"));

    }

    @Test
    public void testStart() throws Exception {

        stc.startTask("Omer", "Hire brewer", Role.JAVAPROGRAMMER, true);
        assertEquals("Hire brewer", stc.getUserTaskData().getName());

        assertEquals(Status.PENDING, taskManSystem.getTaskData("Omer", "Hire brewer").getStatus());
        assertThrows(UnconfirmedActionException.class, () -> stc.startTask("Omer", "Hire brewer", Role.JAVAPROGRAMMER, false));


        assertEquals("Omer", stc.getProjectData("Omer").getName());
        assertEquals(new Time(0), stc.getProjectData("Omer").getCreationTime());
        assertEquals(new Time(2000), stc.getProjectData("Omer").getDueTime());
        current.logout();
        current.login(python);
        assertTrue(stc.startTaskPreconditions());
        assertTrue(stc.getUserRoles().contains(Role.PYTHONPROGRAMMER));
        assertEquals(1, stc.getUserRoles().size());
        stc.startTask("Omer", "Hire brewer", Role.PYTHONPROGRAMMER, true);
        assertEquals("Hire brewer", stc.getUserTaskData().getName());

        assertEquals(Status.EXECUTING, taskManSystem.getTaskData("Omer", "Hire brewer").getStatus());
        assertEquals(new Time(0), stc.getTaskManSystemData().getSystemTime());

        taskManSystem.advanceTime(32);
        assertEquals(new Time(32), stc.getTaskManSystemData().getSystemTime());

        assertEquals(1, stc.getTaskManSystemData().getProjectsData().size());
        assertTrue(stc.getTaskManSystemData().getProjectsData().stream().map(ProjectData::getName).toList().contains("Omer"));

        taskManSystem.createProject("Test", "Brew Beer", new Time(2030));
        assertTrue(stc.getTaskManSystemData().getProjectsData().stream().map(ProjectData::getName).toList().contains("Test"));
        assertEquals(2, stc.getTaskManSystemData().getProjectsData().size());
        assertEquals(new Time(32), stc.getProjectData("Test").getCreationTime());
        assertEquals(new Time(2030), stc.getProjectData("Test").getDueTime());


        current.logout();
        current.login(sysadmin);
        assertTrue(stc.startTaskPreconditions());
        assertTrue(stc.getUserRoles().contains(Role.SYSADMIN));
        assertEquals(1, stc.getUserRoles().size());
        assertEquals("Hire brewer", stc.getTaskData("Omer", "Hire brewer").getName());
        assertEquals(2, stc.getTaskData("Omer", "Hire brewer").getUserNamesWithRole().size());
        assertTrue(stc.getTaskData("Omer", "Hire brewer").getUserNamesWithRole().containsKey("Java"));
        assertTrue(stc.getTaskData("Omer", "Hire brewer").getUserNamesWithRole().containsKey("Python"));
    }
}
