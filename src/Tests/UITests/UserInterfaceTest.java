package Tests.UITests;

import Application.ProjectControllers.ProjectController;
import Application.ProjectControllers.ShowProjectsController;
import Application.Session.Session;
import Application.Session.SessionController;
import Application.Session.SessionProxy;
import Application.SystemControllers.AdvanceTimeController;
import Application.SystemControllers.LoadSystemController;
import Application.SystemControllers.UndoRedoController;
import Application.TaskControllers.*;
import Application.Command.CommandManager;
import Domain.DataClasses.Time;
import Domain.DataClasses.InvalidTimeException;
import Domain.TaskManSystem.TaskManSystem;
import Domain.User.UserManager;
import UserInterface.ProjectUIs.ProjectUI;
import UserInterface.ProjectUIs.ShowProjectsUI;
import UserInterface.SystemUIs.AdvanceTimeUI;
import UserInterface.SystemUIs.LoadSystemUI;
import UserInterface.SystemUIs.SessionUI;
import UserInterface.SystemUIs.UndoRedoUI;
import UserInterface.TaskUIs.*;
import org.junit.Test;
import UserInterface.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class UserInterfaceTest {

    @Test
    public void test() throws InvalidTimeException {
        TaskManSystem taskManSystem = new TaskManSystem(new Time(0, 0)); // exception thrown by the new Time
        UserManager userManager = new UserManager();
        Session session = new Session();
        SessionProxy sessionProxy = new SessionProxy(session);
        CommandManager commandManager = new CommandManager();

        SessionController sessionController = new SessionController(session, userManager);
        AdvanceTimeController advanceTimeController = new AdvanceTimeController(sessionProxy, taskManSystem, commandManager);
        ProjectController createProjectController = new ProjectController(sessionProxy, taskManSystem, commandManager);
        ShowProjectsController showProjectsController = new ShowProjectsController(sessionProxy, taskManSystem);
        CreateTaskController createTaskController = new CreateTaskController(sessionProxy, taskManSystem, commandManager);
        LoadSystemController loadSystemController = new LoadSystemController(sessionProxy, taskManSystem, userManager, commandManager);
        StartTaskController startTaskController = new StartTaskController(sessionProxy, taskManSystem, commandManager);
        DeleteTaskController deleteTaskController = new DeleteTaskController(sessionProxy, taskManSystem, commandManager);
        DeleteTaskUI deleteTaskUI = new DeleteTaskUI(deleteTaskController);
        StartTaskUI startTaskUI = new StartTaskUI(startTaskController);
        EndTaskController endTaskController = new EndTaskController(sessionProxy, taskManSystem, commandManager);
        EndTaskUI endTaskUI = new EndTaskUI(endTaskController);
        UpdateDependenciesController updateDependenciesController = new UpdateDependenciesController(sessionProxy, taskManSystem, commandManager);
        UpdateDependenciesUI updateDependenciesUI = new UpdateDependenciesUI(updateDependenciesController);
        UndoRedoController undoRedoController = new UndoRedoController(sessionProxy, commandManager);
        UndoRedoUI undoRedoUI = new UndoRedoUI(undoRedoController);

        SessionUI sessionUI = new SessionUI(sessionController);
        AdvanceTimeUI advanceTimeUI = new AdvanceTimeUI(advanceTimeController);
        ProjectUI projectUI = new ProjectUI(createProjectController);
        ShowProjectsUI showProjectsUI = new ShowProjectsUI(showProjectsController);
        CreateTaskUI createTaskUI = new CreateTaskUI(createTaskController);
        LoadSystemUI loadSystemUI = new LoadSystemUI(loadSystemController);

        UserInterface UI = new UserInterface(
                sessionUI,
                advanceTimeUI,
                projectUI,
                showProjectsUI,
                createTaskUI,
                deleteTaskUI,
                loadSystemUI,
                startTaskUI,
                endTaskUI,
                updateDependenciesUI,
                undoRedoUI
        );
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("help\nshutdown\n".getBytes()));
        UI.startSystem();
        assertEquals("""
                At your order! Enter 'help' for a list of commands.
                >Available commands:
                help:               Prints this message
                login:              Shows the login prompt
                logout:             Logs out
                shutdown:           Exits the system
                showprojects:       Shows a list of all current projects
                createproject:      Shows the project creation prompt and creates a project
                deleteproject:      Allows the user to delete a project in the system
                createtask:         Shows the task creation prompt to add a task to a project
                deletetask:         Allows the user to delete a task in the system
                advancetime:        Allows the user to modify the system time
                loadsystem:         Allows the user to load projects and tasks into the system
                starttask:          Allows the user to start a task
                endtask:            Allows the user to end a task
                updatedependencies: Allows the user to update the dependencies of a task
                undo:               Allows the user to undo a previously executed command
                redo:               Allows the user to redo a previously undone command
                >""".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("logout\nshutdown\n".getBytes()));
        UI.startSystem();
        assertEquals("""
                At your order! Enter 'help' for a list of commands.
                >Already logged out.
                >""".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("showprojects\nshutdown\n".getBytes()));
        UI.startSystem();
        assertEquals("""
                At your order! Enter 'help' for a list of commands.
                >You must be logged in with the project manager role to call this function
                >""".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("createproject\nshutdown\n".getBytes()));
        UI.startSystem();
        assertEquals("""
                At your order! Enter 'help' for a list of commands.
                >You must be logged in with the project manager role to call this function
                >""".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("createtask\nshutdown\n".getBytes()));
        UI.startSystem();
        assertEquals("""
                At your order! Enter 'help' for a list of commands.
                >
                You must be logged in with the project manager role to call this function

                >""".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("starttask\nshutdown\n".getBytes()));
        UI.startSystem();
        assertEquals("""
                At your order! Enter 'help' for a list of commands.
                >ERROR: You need a developer role to call this function.
                >""".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("advancetime\nshutdown\n".getBytes()));
        UI.startSystem();
        assertEquals("""
                At your order! Enter 'help' for a list of commands.
                >You must be logged in with the project manager role or a Developer role to call this function
                >""".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("loadsystem\nshutdown\n".getBytes()));
        UI.startSystem();
        assertEquals("""
                At your order! Enter 'help' for a list of commands.
                >You must be logged in with the project manager role to call this function
                >""".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("deletetask\nshutdown\n".getBytes()));
        UI.startSystem();
        assertEquals("""
                At your order! Enter 'help' for a list of commands.
                >
                You must be logged in with the project manager role to call this function
                
                >""".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("deleteproject\nshutdown\n".getBytes()));
        UI.startSystem();
        assertEquals("""
                At your order! Enter 'help' for a list of commands.
                >You must be logged in with the project manager role to call this function
                >""".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("undo\nshutdown\n".getBytes()));
        UI.startSystem();
        assertEquals("""
                At your order! Enter 'help' for a list of commands.
                >You must be logged in with the project manager role or a Developer role to call this function
                >""".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("redo\nshutdown\n".getBytes()));
        UI.startSystem();
        assertEquals("""
                At your order! Enter 'help' for a list of commands.
                >You must be logged in with the project manager role or a Developer role to call this function
                >""".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("endtask\nshutdown\n".getBytes()));
        UI.startSystem();
        assertEquals("""
                At your order! Enter 'help' for a list of commands.
                >ERROR: You need a developer role to call this function.
                >""".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream(("updatedependencies\nshutdown\n").getBytes()));
        UI.startSystem();
        assertEquals("""
                At your order! Enter 'help' for a list of commands.
                >ERROR: You must be a project manager to call this function
                >""".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("bogus\nshutdown\n".getBytes()));
        UI.startSystem();
        assertEquals("""
                At your order! Enter 'help' for a list of commands.
                >Unknown command, type help to see available commands
                >""".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

    }
}
