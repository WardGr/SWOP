package Tests.ControllerTests;

import Application.Session.SessionProxy;
import Application.TaskControllers.CreateTaskController;
import Application.Command.CommandManager;
import Domain.TaskManSystem.TaskManSystem;
import Domain.User.Role;
import Domain.User.User;
import Domain.DataClasses.Time;
import org.junit.Before;
import Application.Session.Session;
import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class CreateTaskControllerTest {

    // We define two separate systems, one with projectmanager and one with programmer roles, to test the different permissions
    // without having to login and logout all the time
    private CreateTaskController managerCreateTaskController;
    private Session managerSession;
    private Set<Role> rolesManager;
    private CreateTaskController programmerCreateTaskController;
    private Set<Role> rolesProgrammer;
    private Session programmerSession;


    @Before
    public void setUp() throws Exception {
        Session managerSession = new Session();
        SessionProxy sessionProxy = new SessionProxy(managerSession);
        Session programmerSession = new Session();
        SessionProxy sessionProxy2 = new SessionProxy(programmerSession);

        Set<Role> rolesManager = new HashSet<>();
        rolesManager.add(Role.PROJECTMANAGER);
        User projectManager = new User("WardGr", "peer123", rolesManager);
        managerSession.login(projectManager);

        Set<Role> rolesProgrammer = new HashSet<>();
        rolesProgrammer.add(Role.JAVAPROGRAMMER);
        User programmerUser = new User("WardGr", "peer123", rolesProgrammer);
        programmerSession.login(programmerUser);

        TaskManSystem managerTaskManSystem = new TaskManSystem(new Time(0));
        managerTaskManSystem.createProject("Project 1", "Project 1 description", new Time(1000));
        CommandManager managerCommandManager = new CommandManager();

        TaskManSystem programmerTaskManSystem = new TaskManSystem(new Time(0));
        programmerTaskManSystem.createProject("Project 1", "Project 1 description", new Time(1000));
        CommandManager programmerCommandManager = new CommandManager();

        this.managerCreateTaskController = new CreateTaskController(sessionProxy, managerTaskManSystem, managerCommandManager);
        this.managerSession = managerSession;
        this.rolesManager = rolesManager;

        this.programmerCreateTaskController = new CreateTaskController(sessionProxy2, programmerTaskManSystem, programmerCommandManager);
        this.programmerSession = programmerSession;
        this.rolesProgrammer = rolesProgrammer;
    }

    @Test
    public void testTaskPreconditions() {
        assertTrue(managerCreateTaskController.taskPreconditions());
        assertFalse(programmerCreateTaskController.taskPreconditions());
    }

    @Test
    public void testCreateTask() throws Exception {
        List<Role> taskRoles = new LinkedList<>();
        taskRoles.add(Role.JAVAPROGRAMMER);

        managerCreateTaskController.createTask("Project 1", "Task 1", "Task 1 description", new Time(1000), 0.1, taskRoles, new HashSet<>(), new HashSet<>());
        assertEquals("Task 1", managerCreateTaskController.getTaskData("Project 1", "Task 1").getName());







    }

}
