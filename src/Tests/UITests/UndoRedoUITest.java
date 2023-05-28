package Tests.UITests;

import Application.Session.Session;
import Application.Session.SessionProxy;
import Application.Controllers.SystemControllers.UndoRedoController;
import Application.Command.AdvanceTimeCommands.SetNewTimeCommand;
import Application.Command.CommandManager;
import Application.Command.EmptyCommandStackException;
import Application.Command.ProjectCommands.CreateProjectCommand;
import Application.Command.UndoNotPossibleException;
import Domain.DataClasses.InvalidTimeException;
import Domain.DataClasses.Time;
import Domain.Project.ProjectNameAlreadyInUseException;
import Domain.TaskManSystem.DueBeforeSystemTimeException;
import Domain.TaskManSystem.NewTimeBeforeSystemTimeException;
import Domain.TaskManSystem.TaskManSystem;
import Domain.User.IncorrectUserException;
import Domain.User.Role;
import Domain.User.User;
import UserInterface.SystemUIs.UndoRedoUI;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class UndoRedoUITest {
    private TaskManSystem taskManSystem;
    private CommandManager commandManager;

    private UndoRedoUI uiDev1;
    private UndoRedoUI uiDev2;
    private UndoRedoUI uiPM;
    private UndoRedoUI uiNoCommands;
    private UndoRedoUI uiImpossibleUndo;

    @Before
    public void setUp() throws InvalidTimeException, ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, UndoNotPossibleException, IncorrectUserException, EmptyCommandStackException, NewTimeBeforeSystemTimeException {
        User userDev1 = new User("Test1", "", Set.of(Role.SYSADMIN));
        User userDev2 = new User("Test2", "", Set.of(Role.SYSADMIN));
        User userPM = new User("Test3", "", Set.of(Role.PROJECTMANAGER));

        Session sessionDev1 = new Session();
        sessionDev1.login(userDev1);
        Session sessionDev2 = new Session();
        sessionDev2.login(userDev2);
        Session sessionPM = new Session();
        sessionPM.login(userPM);

        commandManager = new CommandManager();
        taskManSystem = new TaskManSystem(new Time(0));

        UndoRedoController controllerDev1 = new UndoRedoController(new SessionProxy(sessionDev1), commandManager);
        UndoRedoController controllerDev2 = new UndoRedoController(new SessionProxy(sessionDev2), commandManager);
        UndoRedoController controllerPM = new UndoRedoController(new SessionProxy(sessionPM), commandManager);

        uiDev1 = new UndoRedoUI(controllerDev1);
        uiDev2 = new UndoRedoUI(controllerDev2);
        uiPM = new UndoRedoUI(controllerPM);

        CreateProjectCommand command1 = new CreateProjectCommand(taskManSystem, "Project 1", "", new Time(10));
        CreateProjectCommand command2 = new CreateProjectCommand(taskManSystem, "Project 2", "", new Time(10));
        CreateProjectCommand command3 = new CreateProjectCommand(taskManSystem, "Project 3", "", new Time(10));
        CreateProjectCommand command4 = new CreateProjectCommand(taskManSystem, "Project 4", "", new Time(10));
        command1.execute();
        command2.execute();
        command3.execute();
        command4.execute();
        commandManager.addExecutedCommand(command1,userDev1);
        commandManager.addExecutedCommand(command2,userDev1);
        commandManager.addExecutedCommand(command3,userDev1);
        commandManager.addExecutedCommand(command4,userDev1);

        commandManager.undoLastCommand(userDev1);
        commandManager.undoLastCommand(userDev1);


        UndoRedoController controllerNoCommands = new UndoRedoController(new SessionProxy(sessionPM), new CommandManager());
        uiNoCommands = new UndoRedoUI(controllerNoCommands);

        CommandManager noUndoCM = new CommandManager();
        SetNewTimeCommand setNewTimeCommand = new SetNewTimeCommand(taskManSystem, new Time(0));
        setNewTimeCommand.execute();
        noUndoCM.addExecutedCommand(setNewTimeCommand, userDev1);
        UndoRedoController controllerNoUndo = new UndoRedoController(new SessionProxy(sessionDev1), noUndoCM);
        uiImpossibleUndo = new UndoRedoUI(controllerNoUndo);

    }

    @Test
    public void testNotLoggedIn(){
        UndoRedoController noSessionController = new UndoRedoController(new SessionProxy(new Session()), commandManager);
        UndoRedoUI noSessionUI = new UndoRedoUI(noSessionController);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("BACK\n".getBytes()));
        noSessionUI.undo();
        assertEquals(
                """
                        You must be logged in with the project manager role or a Developer role to call this function
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("BACK\n".getBytes()));
        noSessionUI.redo();
        assertEquals(
                """
                        You must be logged in with the project manager role or a Developer role to call this function
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }

    @Test
    public void testNoAvailableCommands(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("BACK\n".getBytes()));
        uiNoCommands.undo();
        assertEquals(
                """
                        There are no actions that can be undone; Cancelled undo
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("BACK\n".getBytes()));
        uiNoCommands.redo();
        assertEquals(
                """
                        There are no undone actions that can be redone; Cancelled redo
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }

    @Test
    public void testSuccessfulUndo(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("y\n".getBytes()));
        uiDev1.undo();
        assertEquals(
                """
                         ***** EXECUTED ACTIONS *****
                         ----- Oldest Action -----
                         - Create project Project 1 --- Executed By: Test1
                         - Create project Project 2 --- Executed By: Test1
                         ----- Most Recent Action -----
                                                
                        Are you sure that you want to undo the last action? Create project Project 2 (y/n)
                        Last action successfully undone
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }

    @Test
    public void testNoConfirmationUndo(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("n\n".getBytes()));
        uiDev1.undo();
        assertEquals(
                """
                         ***** EXECUTED ACTIONS *****
                         ----- Oldest Action -----
                         - Create project Project 1 --- Executed By: Test1
                         - Create project Project 2 --- Executed By: Test1
                         ----- Most Recent Action -----
                                                
                        Are you sure that you want to undo the last action? Create project Project 2 (y/n)
                        Cancelled undo
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }

    @Test
    public void testDoubleConfirmationUndo(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("dvh\nn\n".getBytes()));
        uiDev1.undo();
        assertEquals(
                """
                         ***** EXECUTED ACTIONS *****
                         ----- Oldest Action -----
                         - Create project Project 1 --- Executed By: Test1
                         - Create project Project 2 --- Executed By: Test1
                         ----- Most Recent Action -----
                                                
                        Are you sure that you want to undo the last action? Create project Project 2 (y/n)
                                                
                        Input has to be 'y' or 'n', try again
                        Are you sure that you want to undo the last action? Create project Project 2 (y/n)
                        Cancelled undo
                                                
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }

    @Test
    public void testWrongUserUndo(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("y\n".getBytes()));
        uiDev2.undo();
        assertEquals(
                """
                         ***** EXECUTED ACTIONS *****
                         ----- Oldest Action -----
                         - Create project Project 1 --- Executed By: Test1
                         - Create project Project 2 --- Executed By: Test1
                         ----- Most Recent Action -----
                                                
                        Are you sure that you want to undo the last action? Create project Project 2 (y/n)
                        ERROR: The current user is not allowed to undo the last executed action
                                                                                      
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }

    @Test
    public void testBACKUndo(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("huh?\nBACK\n".getBytes()));
        uiDev1.undo();
        assertEquals(
                """
                         ***** EXECUTED ACTIONS *****
                         ----- Oldest Action -----
                         - Create project Project 1 --- Executed By: Test1
                         - Create project Project 2 --- Executed By: Test1
                         ----- Most Recent Action -----
                        
                        Are you sure that you want to undo the last action? Create project Project 2 (y/n)
                                                
                        Input has to be 'y' or 'n', try again                        
                        Are you sure that you want to undo the last action? Create project Project 2 (y/n)
                        Cancelled undo
                                                                                                              
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }

    @Test
    public void testImpossibleUndo(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("y\n".getBytes()));
        uiImpossibleUndo.undo();
        assertEquals(
                """
                         ***** EXECUTED ACTIONS *****
                         ----- Oldest Action -----
                         - Set new time 0 hour(s), 0 minute(s) --- Executed By: Test1 --- CANNOT BE UNDONE
                         ----- Most Recent Action -----
                                                
                        Are you sure that you want to undo the last action? Set new time 0 hour(s), 0 minute(s) (y/n)
                        Last executed action can't be undone
                                                                                                              
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }



    //
    //
    //      REDO
    //
    //

    @Test
    public void testSuccessfulRedo(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("y\n".getBytes()));
        uiDev1.redo();
        assertEquals(
                """
                         ***** UNDONE ACTIONS *****
                         ----- Oldest Undone Action -----
                         - Create project Project 4 --- Executed By: Test1
                         - Create project Project 3 --- Executed By: Test1
                         ----- Most Recent Undone Action -----
                                                
                        Confirm that you want to redo the last undone action: Create project Project 3 (y/n)
                        Last undone action successfully redone
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }

    @Test
    public void testNoConfirmationRedo(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("n\n".getBytes()));
        uiDev1.redo();
        assertEquals(
                """
                         ***** UNDONE ACTIONS *****
                         ----- Oldest Undone Action -----
                         - Create project Project 4 --- Executed By: Test1
                         - Create project Project 3 --- Executed By: Test1
                         ----- Most Recent Undone Action -----
                                                
                        Confirm that you want to redo the last undone action: Create project Project 3 (y/n)
                        Cancelled redo
                                                
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }

    @Test
    public void testDoubleConfirmationRedo(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("dvh\nn\n".getBytes()));
        uiDev1.redo();
        assertEquals(
                """
                         ***** UNDONE ACTIONS *****
                         ----- Oldest Undone Action -----
                         - Create project Project 4 --- Executed By: Test1
                         - Create project Project 3 --- Executed By: Test1
                         ----- Most Recent Undone Action -----
                                                
                        Confirm that you want to redo the last undone action: Create project Project 3 (y/n)
                                                
                        Input has to be 'y' or 'n', try again
                        Confirm that you want to redo the last undone action: Create project Project 3 (y/n)
                        Cancelled redo
                                                
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }

    @Test
    public void testWrongUserRedo(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("y\n".getBytes()));
        uiDev2.redo();
        assertEquals(
                """
                         ***** UNDONE ACTIONS *****
                         ----- Oldest Undone Action -----
                         - Create project Project 4 --- Executed By: Test1
                         - Create project Project 3 --- Executed By: Test1
                         ----- Most Recent Undone Action -----
                                                
                        Confirm that you want to redo the last undone action: Create project Project 3 (y/n)
                        ERROR: The current user is not allowed to redo the last undone action
                                                                                      
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }

    @Test
    public void testBACKRedo(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("BACK\n".getBytes()));
        uiDev1.redo();
        assertEquals(
                """
                         ***** UNDONE ACTIONS *****
                         ----- Oldest Undone Action -----
                         - Create project Project 4 --- Executed By: Test1
                         - Create project Project 3 --- Executed By: Test1
                         ----- Most Recent Undone Action -----
                                                
                        Confirm that you want to redo the last undone action: Create project Project 3 (y/n)
                        Cancelled redo
                                                                                                              
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }
}
