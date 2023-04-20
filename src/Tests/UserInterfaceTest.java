package Tests;

import Domain.InvalidTimeException;
import org.junit.Test;
import Domain.*;
import Application.*;
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

        SessionController sessionController = new SessionController(session, userManager);
        AdvanceTimeController advanceTimeController = new AdvanceTimeController(sessionProxy, taskManSystem);
        CreateProjectController createProjectController = new CreateProjectController(sessionProxy, taskManSystem);
        ShowProjectsController showProjectsController = new ShowProjectsController(sessionProxy, taskManSystem);
        CreateTaskController createTaskController = new CreateTaskController(sessionProxy, taskManSystem, userManager);
        LoadSystemController loadSystemController = new LoadSystemController(sessionProxy, taskManSystem, userManager);
        StartTaskController startTaskController = new StartTaskController(sessionProxy, taskManSystem);
        StartTaskUI startTaskUI = new StartTaskUI(startTaskController);
        EndTaskController endTaskController = new EndTaskController(sessionProxy, taskManSystem);
        EndTaskUI endTaskUI = new EndTaskUI(endTaskController);
        UpdateDependenciesController updateDependenciesController = new UpdateDependenciesController(sessionProxy, taskManSystem);
        UpdateDependenciesUI updateDependenciesUI = new UpdateDependenciesUI(updateDependenciesController);

        SessionUI sessionUI = new SessionUI(sessionController);
        AdvanceTimeUI advanceTimeUI = new AdvanceTimeUI(advanceTimeController);
        CreateProjectUI createProjectUI = new CreateProjectUI(createProjectController);
        ShowProjectsUI showProjectsUI = new ShowProjectsUI(showProjectsController);
        CreateTaskUI createTaskUI = new CreateTaskUI(createTaskController);
        LoadSystemUI loadSystemUI = new LoadSystemUI(loadSystemController);

        UserInterface UI = new UserInterface(
                sessionUI,
                advanceTimeUI,
                createProjectUI,
                showProjectsUI,
                createTaskUI,
                loadSystemUI,
                startTaskUI,
                endTaskUI,
                updateDependenciesUI
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
                createtask:         Shows the task creation prompt to add a task to a project
                advancetime:        Allows the user to modify the system time
                loadsystem:         Allows the user to load projects and tasks into the system
                starttask:          Allows the user to start a task
                endtask:            Allows the user to end a task
                updatedependencies: Allows the user to update the dependencies of a task
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
                >You must be logged in with the project manager role to call this function
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


        System.setIn(new ByteArrayInputStream("bogus\nshutdown\n".getBytes()));
        UI.startSystem();
        assertEquals("""
                At your order! Enter 'help' for a list of commands.
                >Unknown command, type help to see available commands
                >""".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }
}
