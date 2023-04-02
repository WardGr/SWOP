package Tests;

import Application.*;
import UserInterface.*;
import Domain.InvalidTimeException;
import Domain.TaskManSystem;
import Domain.Time;
import Domain.UserManager;
import UserInterface.UserInterface;
import org.junit.Test;

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
        SessionWrapper sessionWrapper = new SessionWrapper(session);

        SessionController sessionController = new SessionController(session, userManager);
        AdvanceTimeController advanceTimeController = new AdvanceTimeController(sessionWrapper, taskManSystem);
        CreateProjectController createProjectController = new CreateProjectController(sessionWrapper, taskManSystem);
        ShowProjectsController showProjectsController = new ShowProjectsController(sessionWrapper, taskManSystem);
        CreateTaskController createTaskController = new CreateTaskController(sessionWrapper, taskManSystem, userManager);
        LoadSystemController loadSystemController = new LoadSystemController(sessionWrapper, taskManSystem, userManager);
        UpdateTaskController updateTaskController = new UpdateTaskController(sessionWrapper, taskManSystem);

        SessionUI sessionUI = new SessionUI(sessionController);
        AdvanceTimeUI advanceTimeUI = new AdvanceTimeUI(advanceTimeController);
        CreateProjectUI createProjectUI = new CreateProjectUI(createProjectController);
        ShowProjectsUI showProjectsUI = new ShowProjectsUI(showProjectsController);
        CreateTaskUI createTaskUI = new CreateTaskUI(createTaskController);
        LoadSystemUI loadSystemUI = new LoadSystemUI(loadSystemController);
        UpdateTaskUI updateTaskUI = new UpdateTaskUI(updateTaskController);

        UserInterface UI = new UserInterface(
                sessionUI,
                advanceTimeUI,
                createProjectUI,
                showProjectsUI,
                createTaskUI,
                loadSystemUI,
                updateTaskUI
        );
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("help\nshutdown\n".getBytes()));
        UI.startSystem();
        assertEquals("""
                At your order! Enter 'help' for a list of commands.
                >Available commands:
                help:          Prints this message
                login:         Shows the login prompt
                logout:        Logs out
                shutdown:      Exits the system
                showprojects:  Shows a list of all current projects
                createproject: Shows the project creation prompt and creates a project
                createtask:    Shows the task creation prompt to add a task to a project
                updatetask:    Shows the update task prompt to update a tasks' information/status
                advancetime:   Allows the user to modify the system time
                loadsystem:    Allows the user to load projects and tasks into the system
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


        System.setIn(new ByteArrayInputStream("updatetask\nshutdown\n".getBytes()));
        UI.startSystem();
        assertEquals("""
                At your order! Enter 'help' for a list of commands.
                >You must be logged in with the developer role to call this function
                >""".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("advancetime\nshutdown\n".getBytes()));
        UI.startSystem();
        assertEquals("""
                At your order! Enter 'help' for a list of commands.
                >You must be logged in with the project manager or developer role to call this function
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


        // To test login from UI we need to have a persistent scanner, but that would be pretty annoying to do for just a
        // single line in a class that is basically completely tested bottom up..


    }
}
