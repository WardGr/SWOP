package Tests.UITests;

import Application.Command.CommandManager;
import Application.Session.Session;
import Application.Session.SessionProxy;
import Application.Controllers.TaskControllers.DeleteTaskController;
import Domain.DataClasses.InvalidTimeException;
import Domain.DataClasses.Time;
import Domain.Project.ProjectNameAlreadyInUseException;
import Domain.Project.ProjectNotOngoingException;
import Domain.Project.TaskNotFoundException;
import Domain.Task.*;
import Domain.TaskManSystem.DueBeforeSystemTimeException;
import Domain.TaskManSystem.ProjectNotFoundException;
import Domain.TaskManSystem.TaskManSystem;
import Domain.User.Role;
import Domain.User.User;
import Domain.User.UserAlreadyAssignedToTaskException;
import UserInterface.TaskUIs.DeleteTaskUI;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class DeleteTaskUITest {

    private User manager;
    private User developer;
    private User developer2;
    private TaskManSystem taskManSystem;
    private DeleteTaskController managerController;
    private DeleteTaskController developerController;
    private DeleteTaskUI managerUI;
    private DeleteTaskUI developerUI;

    @Before
    public void setUp() throws InvalidTimeException, ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNameAlreadyInUseException, TaskNotFoundException, IllegalTaskRolesException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException {
        Session managerSession = new Session();
        SessionProxy managerSessionProxy = new SessionProxy(managerSession);
        Session developerSession = new Session();
        SessionProxy developerSessionProxy = new SessionProxy(developerSession);
        manager = new User("DieterVH", "computer776", Set.of(Role.PROJECTMANAGER));
        developer = new User("SamHa", "trein123", Set.of(Role.PYTHONPROGRAMMER, Role.JAVAPROGRAMMER));
        developer2 = new User("SamHa2", "trein123", Set.of(Role.PYTHONPROGRAMMER, Role.JAVAPROGRAMMER));

        managerSession.login(manager);
        developerSession.login(developer);

        taskManSystem = new TaskManSystem(new Time(0));

        CommandManager commandManager = new CommandManager();

        managerController = new DeleteTaskController(managerSessionProxy, taskManSystem, commandManager);
        developerController = new DeleteTaskController(developerSessionProxy, taskManSystem, commandManager);

        developerUI = new DeleteTaskUI(developerController);
        managerUI = new DeleteTaskUI(managerController);

        taskManSystem.createProject("TestProject","",new Time(5));
        taskManSystem.addTaskToProject("TestProject", "TestTask1", "", new Time(5), 0.2, List.of(Role.PYTHONPROGRAMMER), new HashSet<>(), new HashSet<>());
        taskManSystem.addTaskToProject("TestProject", "TestTask2", "", new Time(5), 0.2, List.of(Role.PYTHONPROGRAMMER), new HashSet<>(), new HashSet<>());

    }


    @Test
    public void testSuccessfulDeletion(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("TestProject\nTestTask2\ny\n".getBytes()));
        managerUI.deleteTask();
        assertEquals(
                """
                        Use 'BACK' to return to main menu
                                                
                         *** PROJECTS ***
                         - TestProject | Containing 2 Tasks
                        Give the project name in which you want to delete a task:
                         *** TASKS in TestProject ***
                         - TestTask1
                         - TestTask2
                                                
                        Give the task name you want to delete:
                        Successfully deleted Task
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }

    @Test
    public void testBackProject(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("BACK\n".getBytes()));
        managerUI.deleteTask();
        assertEquals(
                """
                        Use 'BACK' to return to main menu
                                                
                         *** PROJECTS ***
                         - TestProject | Containing 2 Tasks
                        Give the project name in which you want to delete a task:
                                                
                        Cancelled Task Deletion
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }

    @Test
    public void testBackTask(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("TestProject\nBACK\n".getBytes()));
        managerUI.deleteTask();
        assertEquals(
                """
                        Use 'BACK' to return to main menu
                                                
                         *** PROJECTS ***
                         - TestProject | Containing 2 Tasks
                        Give the project name in which you want to delete a task:
                         *** TASKS in TestProject ***
                         - TestTask1
                         - TestTask2
                                                
                        Give the task name you want to delete:
                                                
                        Cancelled Task Deletion
                                                
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }

    @Test
    public void testWrongProjectName(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("WRONG\nBACK\n".getBytes()));
        managerUI.deleteTask();
        assertEquals(
                """
                        Use 'BACK' to return to main menu
                                                
                         *** PROJECTS ***
                         - TestProject | Containing 2 Tasks
                        Give the project name in which you want to delete a task:
                        Given project name could not be found, try again
                                                
                         *** PROJECTS ***
                         - TestProject | Containing 2 Tasks
                        Give the project name in which you want to delete a task:
                                                
                        Cancelled Task Deletion
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }

    @Test
    public void testWrongTaskName(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("TestProject\nWRONG\nBACK\n".getBytes()));
        managerUI.deleteTask();
        assertEquals(
                """
                        Use 'BACK' to return to main menu
                                                
                         *** PROJECTS ***
                         - TestProject | Containing 2 Tasks
                        Give the project name in which you want to delete a task:
                         *** TASKS in TestProject ***
                         - TestTask1
                         - TestTask2
                                                
                        Give the task name you want to delete:
                        Given task name could not be found, try again
                                                
                         *** PROJECTS ***
                         - TestProject | Containing 2 Tasks
                        Give the project name in which you want to delete a task:
                                                
                        Cancelled Task Deletion
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }

    @Test
    public void testConfirmingTaskDeletion() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        taskManSystem.startTask("TestProject", "TestTask2", developer, Role.PYTHONPROGRAMMER);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("TestProject\nTestTask2\ny\n".getBytes()));
        managerUI.deleteTask();
        assertEquals(
                """
                        Use 'BACK' to return to main menu
                                                
                         *** PROJECTS ***
                         - TestProject | Containing 2 Tasks
                        Give the project name in which you want to delete a task:
                         *** TASKS in TestProject ***
                         - TestTask1
                         - TestTask2
                                                
                        Give the task name you want to delete:
                                                
                        Task TestTask2 has status executing
                           With users committed:\s
                        SamHa
                        Confirm you want to delete this task. (y/n)
                        Successfully deleted Task
                                                
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }

    @Test
    public void testNoConfirmingTaskDeletion() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        taskManSystem.startTask("TestProject", "TestTask2", developer, Role.PYTHONPROGRAMMER);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("TestProject\nTestTask2\nn\nBACK\n".getBytes()));
        managerUI.deleteTask();
        assertEquals(
                """
                        Use 'BACK' to return to main menu
                                                
                         *** PROJECTS ***
                         - TestProject | Containing 2 Tasks
                        Give the project name in which you want to delete a task:
                         *** TASKS in TestProject ***
                         - TestTask1
                         - TestTask2
                                                
                        Give the task name you want to delete:
                                                
                        Task TestTask2 has status executing
                           With users committed:\s
                        SamHa
                        Confirm you want to delete this task. (y/n)
                        Deleting a Pending or Executing task is not confirmed.
                                                
                         *** PROJECTS ***
                         - TestProject | Containing 2 Tasks
                        Give the project name in which you want to delete a task:
                                                
                        Cancelled Task Deletion
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }

    @Test
    public void testWrongConfirmingTaskDeletion() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        taskManSystem.startTask("TestProject", "TestTask2", developer, Role.PYTHONPROGRAMMER);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("TestProject\nTestTask2\noh\ny\n".getBytes()));
        managerUI.deleteTask();
        assertEquals(
                """
                        Use 'BACK' to return to main menu
                                                
                         *** PROJECTS ***
                         - TestProject | Containing 2 Tasks
                        Give the project name in which you want to delete a task:
                         *** TASKS in TestProject ***
                         - TestTask1
                         - TestTask2
                                                
                        Give the task name you want to delete:
                                                
                        Task TestTask2 has status executing
                           With users committed:\s
                        SamHa
                        Confirm you want to delete this task. (y/n)
                        
                        Input has to be 'y' or 'n', try again
                        Confirm you want to delete this task. (y/n)
                        Successfully deleted Task
                                                
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }
}
