package Tests.ControllerTests;

import Application.Command.CommandManager;
import Application.IncorrectPermissionException;
import Application.Session.Session;
import Application.Session.SessionProxy;
import Application.TaskControllers.DeleteTaskController;
import Application.TaskControllers.UnconfirmedActionException;
import Domain.DataClasses.Time;
import Domain.DataClasses.Tuple;
import Domain.TaskManSystem.TaskManSystem;
import Domain.User.Role;
import Domain.User.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class DeleteTaskControllerTest {

    private Session session;
    private User projectManager;
    private User python;
    private User java;
    private TaskManSystem taskManSystem;
    private DeleteTaskController deleteTaskController;

    @Before
    public void setUp() throws Exception {
        this.session = new Session();
        this.projectManager = new User("DieterVH", "computer776", Set.of(Role.PROJECTMANAGER));
        this.python = new User("OlavBl", "peer123", Set.of(Role.PYTHONPROGRAMMER));
        this.java = new User("Omer", "peer123", Set.of(Role.JAVAPROGRAMMER));
        this.taskManSystem = new TaskManSystem(new Time(0));
        this.deleteTaskController = new DeleteTaskController(new SessionProxy(session), taskManSystem, new CommandManager());
        session.login(projectManager);

        taskManSystem.createProject("Omer", "Brew omer beer", new Time(30));
        taskManSystem.createProject("Duvel", "Brew duvel beer", new Time(30));


        taskManSystem.addTaskToProject("Omer", "Drink omer beer", "Drink beer", new Time(30), 0.1, List.of(Role.PYTHONPROGRAMMER, Role.JAVAPROGRAMMER), Set.of(), Set.of());
        taskManSystem.startTask("Omer", "Drink omer beer", java, Role.JAVAPROGRAMMER);


        taskManSystem.addTaskToProject("Omer", "Brew omer beer", "Brew beer", new Time(30), 0.1, List.of(Role.PYTHONPROGRAMMER), Set.of(), Set.of());
        taskManSystem.startTask("Omer", "Brew omer beer", python, Role.PYTHONPROGRAMMER);

        taskManSystem.addTaskToProject("Omer", "Sell omer beer", "Sell beer", new Time(30), 0.1, List.of(Role.PYTHONPROGRAMMER), Set.of(new Tuple<>("Omer", "Brew omer beer")), Set.of());
        taskManSystem.addTaskToProject("Duvel", "Brew duvel beer", "Brew beer", new Time(30), 0.1, List.of(Role.PYTHONPROGRAMMER, Role.JAVAPROGRAMMER), Set.of(new Tuple<>("Omer", "Sell omer beer")), Set.of());
        taskManSystem.addTaskToProject("Duvel", "Sell duvel beer", "Sell beer", new Time(30), 0.1, List.of(Role.PYTHONPROGRAMMER), Set.of(new Tuple<>("Duvel", "Brew duvel beer")), Set.of());
    }

    @Test
    public void testPreconditions() {
        assertTrue(deleteTaskController.deleteTaskPreconditions());
        session.logout();
        assertFalse(deleteTaskController.deleteTaskPreconditions());
        session.login(python);
        assertFalse(deleteTaskController.deleteTaskPreconditions());
        session.logout();
        session.login(projectManager);
        assertTrue(deleteTaskController.deleteTaskPreconditions());
    }

    @Test
    public void testNeedDeleteConfirmation() throws Exception {
        assertTrue(deleteTaskController.needDeleteConfirmation("Omer", "Brew omer beer"));
        assertTrue(deleteTaskController.needDeleteConfirmation("Omer", "Drink omer beer"));
        assertFalse(deleteTaskController.needDeleteConfirmation("Omer", "Sell omer beer"));
        assertFalse(deleteTaskController.needDeleteConfirmation("Duvel", "Sell duvel beer"));
    }

    @Test
    public void testUncomfirmedActionException() throws Exception {
        assertThrows(UnconfirmedActionException.class, () -> deleteTaskController.deleteTask("Omer", "Brew omer beer", false));
        assertThrows(UnconfirmedActionException.class, () -> deleteTaskController.deleteTask("Omer", "Drink omer beer", false));
        deleteTaskController.deleteTask("Omer", "Sell omer beer", false);
        deleteTaskController.deleteTask("Duvel", "Sell duvel beer", false);
    }

    @Test
    public void testIncorrectPermissions() {
        session.logout();
        assertThrows(IncorrectPermissionException.class, () -> deleteTaskController.deleteTask("Omer", "Brew omer beer", true));
        assertThrows(IncorrectPermissionException.class, () -> deleteTaskController.getTaskData("Omer", "Brew omer beer"));
        assertThrows(IncorrectPermissionException.class, () -> deleteTaskController.getProjectData("Omer"));
        assertThrows(IncorrectPermissionException.class, () -> deleteTaskController.getTaskManSystemData());

        session.login(python);
        assertThrows(IncorrectPermissionException.class, () -> deleteTaskController.deleteTask("Omer", "Brew omer beer", true));
        assertThrows(IncorrectPermissionException.class, () -> deleteTaskController.getTaskData("Omer", "Brew omer beer"));
        assertThrows(IncorrectPermissionException.class, () -> deleteTaskController.getProjectData("Omer"));
        assertThrows(IncorrectPermissionException.class, () -> deleteTaskController.getTaskManSystemData());
    }

    @Test
    public void testDeleteTask() throws Exception {
        assertEquals(3, taskManSystem.getProjectData("Omer").getTotalTaskCount());
        deleteTaskController.deleteTask("Omer", "Brew omer beer", true);
        assertEquals(2, taskManSystem.getProjectData("Omer").getTotalTaskCount());
        assertEquals(2, taskManSystem.getProjectData("Duvel").getTotalTaskCount());

        deleteTaskController.deleteTask("Duvel", "Brew duvel beer", true);
        assertEquals(1, taskManSystem.getProjectData("Duvel").getTotalTaskCount());
    }

    @Test
    public void testGetters() throws Exception {
        assertEquals(taskManSystem.getProjectsData(), deleteTaskController.getTaskManSystemData().getProjectsData());

        assertEquals(taskManSystem.getProjectData("Omer"), deleteTaskController.getProjectData("Omer"));
        assertEquals(taskManSystem.getProjectData("Duvel"), deleteTaskController.getProjectData("Duvel"));

        assertEquals(taskManSystem.getTaskData("Omer", "Brew omer beer"), deleteTaskController.getTaskData("Omer", "Brew omer beer"));
        assertEquals(taskManSystem.getTaskData("Duvel", "Brew duvel beer"), deleteTaskController.getTaskData("Duvel", "Brew duvel beer"));
    }

    @Test
    public void integrationTest() throws Exception {
        deleteTaskController.deleteTask("Omer", "Brew omer beer", true);
        assertEquals(2, taskManSystem.getProjectData("Omer").getTotalTaskCount());
        assertTrue(taskManSystem.getTaskData("Omer", "Sell omer beer").getPrevTasksData().isEmpty());

        deleteTaskController.deleteTask("Duvel", "Brew duvel beer", true);
        assertEquals(1, taskManSystem.getProjectData("Duvel").getTotalTaskCount());
        assertTrue(taskManSystem.getTaskData("Duvel", "Sell duvel beer").getPrevTasksData().isEmpty());
        assertTrue(taskManSystem.getTaskData("Omer", "Sell omer beer").getNextTasksData().isEmpty());
    }
}
