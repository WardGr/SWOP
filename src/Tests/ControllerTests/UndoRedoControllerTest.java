package Tests.ControllerTests;

import Application.Command.CommandManager;
import Application.Command.ProjectCommands.CreateProjectCommand;
import Application.Command.TaskCommands.CreateTaskCommand;
import Domain.DataClasses.Time;
import Domain.DataClasses.Tuple;
import org.junit.Before;
import Application.Session.*;
import Application.Controllers.SystemControllers.UndoRedoController;
import Domain.TaskManSystem.TaskManSystem;
import Domain.User.User;
import Domain.User.Role;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;


public class UndoRedoControllerTest {

    private Session session;
    private UndoRedoController undoRedoController;
    private TaskManSystem taskManSystem;
    private CommandManager commandManager;
    private User python;
    private User java;
    private User sysadmin;
    private User projectManager;
    private CreateTaskCommand createTaskCommand;
    private CreateProjectCommand createProjectCommand;

    @Before
    public void setUp() throws Exception {
        this.session = new Session();
        this.taskManSystem = new TaskManSystem(new Time(0));
        this.commandManager = new CommandManager();
        this.undoRedoController = new UndoRedoController(new SessionProxy(session), commandManager);
        this.python = new User("OlavBl", "peer123", Set.of(Role.PYTHONPROGRAMMER));
        this.java = new User("WardGr", "minecraft123", Set.of(Role.JAVAPROGRAMMER));
        this.sysadmin = new User("SanderSc", "appelboom885", Set.of(Role.SYSADMIN));
        this.projectManager = new User("DieterVH", "computer776", Set.of(Role.PROJECTMANAGER));
        this.createProjectCommand = new CreateProjectCommand(taskManSystem, "Project", "test", new Time(10));
        this.createTaskCommand = new CreateTaskCommand(taskManSystem, "Project", "test", "task description", new Time(10), 0.1, List.of(Role.PYTHONPROGRAMMER), Set.of(), Set.of());

        session.login(projectManager);
        createProjectCommand.execute();
        commandManager.addExecutedCommand(createProjectCommand, session.getCurrentUser());
        createTaskCommand.execute();
        commandManager.addExecutedCommand(createTaskCommand, session.getCurrentUser());
    }


    @Test
    public void testPreconditions() {
        session.logout();
        assertFalse(undoRedoController.undoRedoPreconditions());

        session.login(python);
        assertTrue(undoRedoController.undoRedoPreconditions());
        session.logout();

        session.login(java);
        assertTrue(undoRedoController.undoRedoPreconditions());
        session.logout();

        session.login(sysadmin);
        assertTrue(undoRedoController.undoRedoPreconditions());
        session.logout();

        session.login(projectManager);
        assertTrue(undoRedoController.undoRedoPreconditions());
        session.logout();
    }

    @Test
    public void testGetExecutedCommands() {
        assertEquals(List.of(new Tuple<>(createProjectCommand, "DieterVH"), new Tuple<>(createTaskCommand, "DieterVH")), undoRedoController.getExecutedCommands());
    }

    @Test
    public void testGetLastExecutedCommand() {
        assertEquals(createTaskCommand, undoRedoController.getLastExecutedCommand());
    }

    @Test
    public void testGetLastUndoneCommand() {
        assertNull(undoRedoController.getLastUndoneCommand());
    }

    @Test
    public void testGetUndoneCommands() {
        assertEquals(List.of(), undoRedoController.getUndoneCommands());
    }

    @Test
    public void testUndos() throws Exception {
        undoRedoController.undoLastCommand();
        assertEquals(List.of(new Tuple<>(createProjectCommand, "DieterVH")), undoRedoController.getExecutedCommands());
        assertEquals(createTaskCommand, undoRedoController.getLastUndoneCommand());
        assertEquals(List.of(new Tuple<>(createTaskCommand, "DieterVH")), undoRedoController.getUndoneCommands());
        assertEquals(createProjectCommand, undoRedoController.getLastExecutedCommand());

        undoRedoController.redoLastUndoneCommand();
        assertEquals(List.of(new Tuple<>(createProjectCommand, "DieterVH"), new Tuple<>(createTaskCommand, "DieterVH")), undoRedoController.getExecutedCommands());
        assertEquals(createTaskCommand, undoRedoController.getLastExecutedCommand());
        assertEquals(List.of(), undoRedoController.getUndoneCommands());
        assertNull(undoRedoController.getLastUndoneCommand());
    }



}
