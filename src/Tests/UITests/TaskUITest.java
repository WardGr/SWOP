package Tests.UITests;

import Application.Session.Session;
import Application.Session.SessionProxy;
import Application.TaskControllers.TaskController;
import Application.Command.CommandManager;
import Domain.DataClasses.EndTimeBeforeStartTimeException;
import Domain.DataClasses.InvalidTimeException;
import Domain.DataClasses.Time;
import Domain.Project.DueTimeBeforeCreationTimeException;
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
import Domain.User.IncorrectUserException;
import Domain.User.Role;
import Domain.User.User;
import Domain.User.UserAlreadyAssignedToTaskException;
import UserInterface.TaskUIs.TaskUI;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class TaskUITest {
    private User manager;
    private User developer;
    private User developer2;
    private TaskManSystem taskManSystem;
    private TaskController managerController;
    private TaskController developerController;
    private TaskUI managerUI;
    private TaskUI developerUI;

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

        managerController = new TaskController(managerSessionProxy, taskManSystem, commandManager);
        developerController = new TaskController(developerSessionProxy, taskManSystem, commandManager);

        developerUI = new TaskUI(developerController);
        managerUI = new TaskUI(managerController);

        taskManSystem.createProject("TestProject","",new Time(5));
        taskManSystem.addTaskToProject("TestProject", "TestTask1", "", new Time(5), 0.2, List.of(Role.PYTHONPROGRAMMER), new HashSet<>(), new HashSet<>());
        taskManSystem.addTaskToProject("TestProject", "TestTask2", "", new Time(5), 0.2, List.of(Role.PYTHONPROGRAMMER), new HashSet<>(), new HashSet<>());

    }

    @Test
    public void test() throws ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, IncorrectTaskStatusException, IncorrectUserException, InvalidTimeException, NewTimeBeforeSystemTimeException, EndTimeBeforeStartTimeException, DueTimeBeforeCreationTimeException, ProjectNotOngoingException, LoopDependencyGraphException, IllegalTaskRolesException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        // Setup test environment
        taskManSystem.deleteProject("TestProject");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("BACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - No ongoing projects
                        Project name of an ongoing project to add the task to:
                        
                        Cancelled Task Creation
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        taskManSystem.createProject("SimpleProject", "Cool description", new Time(100));
        taskManSystem.addTaskToProject("SimpleProject", "SimpleTask", "Cool description", new Time(40), 0.1, List.of(Role.JAVAPROGRAMMER, Role.PYTHONPROGRAMMER), new HashSet<>(), new HashSet<>());

        developerUI.createTask();
        assertEquals(
                """
                        
                        You must be logged in with the project manager role to call this function
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("BACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                        Project name of an ongoing project to add the task to:
                        
                        Cancelled Task Creation
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("SimpleProject\nBACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                        Project name of an ongoing project to add the task to:
                        Task name:
                        
                        Cancelled Task Creation
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("SimpleProject\nNewTask\nBACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        
                        Cancelled Task Creation
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nNewTask\nCool description\nBACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        
                        Cancelled Task Creation
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nNewTask\nCool description\nnotAnInt\nBACK".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Given task duration is not an integer, please input an integer and try again
                        
                        Cancelled Task Creation
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("SimpleProject\nNewTask\nCool description\n3\nBACK".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        
                        Cancelled Task Creation
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        taskManSystem.createProject("SimpleProject2", "Cool description", new Time(100));
        System.setIn(new ByteArrayInputStream("SimpleProject\nNewTask\nCool description\n3\nnotAnInt\nBACK".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        Given task duration is not an integer, please input an integer and try again
                        
                        Cancelled Task Creation
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("SimpleProject\nNewTask\nCool description\n3\n20\nBACK".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        Task deviation:
                        
                        Cancelled Task Creation
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nNewTask\nCool description\n3\n20\nnotADouble\nBACK".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        Task deviation:
                        Given task deviation is not a double, please input an integer and try again
                        
                        Cancelled Task Creation
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nNewTask\nCool description\n3\n20\n0.3\nBACK".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        Task deviation:
                        Is this a replacement task? (y/n)
                        
                        Cancelled Task Creation
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("SimpleProject\nNewTask\nCool description\n3\n20\n0.3\ny\nBACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        Task deviation:
                        Is this a replacement task? (y/n)
                        -- Tasks that can be replaced --
                         - No replaceable tasks
                        This task is a replacement for task:
                        
                        Cancelled Task Creation
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nNewTask\nCool description\n3\n20\n0.3\nd\nBACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        Task deviation:
                        Is this a replacement task? (y/n)
                        
                        Input has to be 'y' or 'n', try again
                        Is this a replacement task? (y/n)
                        
                        Cancelled Task Creation
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nNewTask\nCool description\n3\n20\n0.3\nd\nn\nBACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        Task deviation:
                        Is this a replacement task? (y/n)
                        
                        Input has to be 'y' or 'n', try again
                        Is this a replacement task? (y/n)
                        Give developer roles needed for this task, end with a '.'
                        You can choose from: sysadmin, java programmer, python programmer
                        
                        Cancelled Task Creation
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nNewTask\nCool description\n3\n20\n0.3\nd\nn\nadmin\nBACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        Task deviation:
                        Is this a replacement task? (y/n)
                        
                        Input has to be 'y' or 'n', try again
                        Is this a replacement task? (y/n)
                        Give developer roles needed for this task, end with a '.'
                        You can choose from: sysadmin, java programmer, python programmer
                        (Unrecognized developer role)
                        
                        Cancelled Task Creation
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nNewTask\nCool description\n3\n20\n0.3\nd\nn\nsysadmin\n.\nSimpleProject / SimpleTask\n.\nBACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        Task deviation:
                        Is this a replacement task? (y/n)
                                                
                        Input has to be 'y' or 'n', try again
                        Is this a replacement task? (y/n)
                        Give developer roles needed for this task, end with a '.'
                        You can choose from: sysadmin, java programmer, python programmer
                        Give projectName and taskName of tasks that this task depends on
                        Follow the form: <projectName / taskName>, and enter '.' to stop adding new tasks:
                        Give projectName and taskName of tasks that depend on this task
                        Follow the form: <projectName / taskName>, and enter '.' to stop adding new tasks:
                        
                        Cancelled Task Creation
                                                
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nNewTask\nCool description\n3\n20\n0.3\nd\nn\nsysadmin\n.\nSimpleProject / SimpleTask\n.\n.\nBACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        Task deviation:
                        Is this a replacement task? (y/n)
                        
                        Input has to be 'y' or 'n', try again
                        Is this a replacement task? (y/n)
                        Give developer roles needed for this task, end with a '.'
                        You can choose from: sysadmin, java programmer, python programmer
                        Give projectName and taskName of tasks that this task depends on
                        Follow the form: <projectName / taskName>, and enter '.' to stop adding new tasks:
                        Give projectName and taskName of tasks that depend on this task
                        Follow the form: <projectName / taskName>, and enter '.' to stop adding new tasks:
                        
                        Task NewTask successfully added to project SimpleProject
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        taskManSystem.startTask("SimpleProject", "SimpleTask", developer, Role.PYTHONPROGRAMMER);
        taskManSystem.startTask("SimpleProject", "SimpleTask", developer2, Role.JAVAPROGRAMMER);
        taskManSystem.advanceTime(new Time(10));

        taskManSystem.failTask("SimpleProject", "SimpleTask", developer2);

        // Create replacement task
        System.setIn(new ByteArrayInputStream("SimpleProject\nReplacementTask\nCool description\n3\n20\n0.3\nb\ny\nSimpleTask\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        Task deviation:
                        Is this a replacement task? (y/n)
                        
                        Input has to be 'y' or 'n', try again
                        Is this a replacement task? (y/n)
                        -- Tasks that can be replaced --
                         - SimpleTask
                        This task is a replacement for task:
                        Task ReplacementTask successfully added to project SimpleProject as a replacement for task SimpleTask
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        // Try to create replacement task for not failed task
        System.setIn(new ByteArrayInputStream("SimpleProject\nTask\nCool description\n3\n20\n0.3\ny\nReplacementTask\nBACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        Task deviation:
                        Is this a replacement task? (y/n)
                        -- Tasks that can be replaced --
                         - No replaceable tasks
                        This task is a replacement for task:
                        ERROR: The task to replace is not failed, please try again
                                               
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        
                        Cancelled Task Creation
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        // Input non-existent project name
        System.setIn(new ByteArrayInputStream("WrongProject\nTask\nCool description\n3\n20\n0.3\ny\nSimpleTask\nBACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        Task deviation:
                        Is this a replacement task? (y/n)
                        -- Tasks that can be replaced --
                        ERROR: the given project does not exist
                                               
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        
                        Cancelled Task Creation
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        // Input wrong minute format
        System.setIn(new ByteArrayInputStream("SimpleProject\nTask\nCool description\n3\n99\n0.3\ny\nSimpleTask\nBACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        Task deviation:
                        Is this a replacement task? (y/n)
                        -- Tasks that can be replaced --
                         - No replaceable tasks
                        This task is a replacement for task:
                        ERROR: The given minutes are not of a valid format (0-59)
                                               
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        
                        Cancelled Task Creation
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        // Replace non-existent task
        System.setIn(new ByteArrayInputStream("SimpleProject\nTask\nCool description\n3\n20\n0.3\ny\nNon-Existent task\nBACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        Task deviation:
                        Is this a replacement task? (y/n)
                        -- Tasks that can be replaced --
                         - No replaceable tasks
                        This task is a replacement for task:
                        ERROR: the given task to replace does not exist
                                               
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        
                        Cancelled Task Creation
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nReplacementTask\nCool description\n3\n20\n0.3\ny\nSimpleTask\nBACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        Task deviation:
                        Is this a replacement task? (y/n)
                        -- Tasks that can be replaced --
                         - No replaceable tasks
                        This task is a replacement for task:
                        ERROR: the given task name is already in use
                                               
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        
                        Cancelled Task Creation
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nReplacementTask\nCool description\n3\n20\n0.3\ny\nSimpleTask\nBACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        Task deviation:
                        Is this a replacement task? (y/n)
                        -- Tasks that can be replaced --
                         - No replaceable tasks
                        This task is a replacement for task:
                        ERROR: the given task name is already in use
                                              
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        
                        Cancelled Task Creation
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("IncorrectProject\nNewTask\nCool description\n3\n20\n0.3\nd\nn\njava programmer\nsysadmin\npython programmer\n.\nSimpleProject / SimpleTask\n.\nBACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        Task deviation:
                        Is this a replacement task? (y/n)
                                                
                        Input has to be 'y' or 'n', try again
                        Is this a replacement task? (y/n)
                        Give developer roles needed for this task, end with a '.'
                        You can choose from: sysadmin, java programmer, python programmer
                        Give projectName and taskName of tasks that this task depends on
                        Follow the form: <projectName / taskName>, and enter '.' to stop adding new tasks:
                        Give projectName and taskName of tasks that depend on this task
                        Follow the form: <projectName / taskName>, and enter '.' to stop adding new tasks:
                        
                        Cancelled Task Creation
                                                
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("IncorrectProject\nNewTask\nCool description\n3\n20\n0.3\nd\nn\njava programmer\nsysadmin\npython programmer\n.\nSimpleProject / SimpleTask\n.\nSimpleProject / SimpleTask\nBACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        Task deviation:
                        Is this a replacement task? (y/n)
                        
                        Input has to be 'y' or 'n', try again
                        Is this a replacement task? (y/n)
                        Give developer roles needed for this task, end with a '.'
                        You can choose from: sysadmin, java programmer, python programmer
                        Give projectName and taskName of tasks that this task depends on
                        Follow the form: <projectName / taskName>, and enter '.' to stop adding new tasks:
                        Give projectName and taskName of tasks that depend on this task
                        Follow the form: <projectName / taskName>, and enter '.' to stop adding new tasks:
                                                
                        Cancelled Task Creation
                                                
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("IncorrectProject\nNewTask\nCool description\n3\n20\n0.3\nd\nn\nsysadmin\n.\nBACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        Task deviation:
                        Is this a replacement task? (y/n)
                        
                        Input has to be 'y' or 'n', try again
                        Is this a replacement task? (y/n)
                        Give developer roles needed for this task, end with a '.'
                        You can choose from: sysadmin, java programmer, python programmer
                        Give projectName and taskName of tasks that this task depends on
                        Follow the form: <projectName / taskName>, and enter '.' to stop adding new tasks:
                                             
                        Cancelled Task Creation
                                                
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("IncorrectProject\nNewTask\nCool description\n3\n20\n0.3\nd\nn\nsysadmin\n.\nSimpleProject / SimpleTask\nBACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        Task deviation:
                        Is this a replacement task? (y/n)
                                                
                        Input has to be 'y' or 'n', try again
                        Is this a replacement task? (y/n)
                        Give developer roles needed for this task, end with a '.'
                        You can choose from: sysadmin, java programmer, python programmer
                        Give projectName and taskName of tasks that this task depends on
                        Follow the form: <projectName / taskName>, and enter '.' to stop adding new tasks:
                                               
                        Cancelled Task Creation
                                                
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("IncorrectProject\nNewTask\nCool description\n3\n20\n0.3\nn\nsysadmin\n.\nSimpleProject / SimpleTask\n.\n.\nBACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        Task deviation:
                        Is this a replacement task? (y/n)
                        Give developer roles needed for this task, end with a '.'
                        You can choose from: sysadmin, java programmer, python programmer
                        Give projectName and taskName of tasks that this task depends on
                        Follow the form: <projectName / taskName>, and enter '.' to stop adding new tasks:
                        Give projectName and taskName of tasks that depend on this task
                        Follow the form: <projectName / taskName>, and enter '.' to stop adding new tasks:
                                                
                        ERROR: Given project does not exist
                                                
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                                                
                        Cancelled Task Creation
                                                
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("SimpleProject\nSimpleTask\nCool description\n3\n20\n0.3\nn\nsysadmin\n.\n.\n.\nBACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        Task deviation:
                        Is this a replacement task? (y/n)
                        Give developer roles needed for this task, end with a '.'
                        You can choose from: sysadmin, java programmer, python programmer
                        Give projectName and taskName of tasks that this task depends on
                        Follow the form: <projectName / taskName>, and enter '.' to stop adding new tasks:
                        Give projectName and taskName of tasks that depend on this task
                        Follow the form: <projectName / taskName>, and enter '.' to stop adding new tasks:
                                                
                        ERROR: The given task name is already in use
                                                
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                                                
                        Cancelled Task Creation
                                                
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("SimpleProject\nNewTask2\nCool description\n3\n80\n0.3\nn\nsysadmin\n.\n.\n.\nBACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        Task deviation:
                        Is this a replacement task? (y/n)
                        Give developer roles needed for this task, end with a '.'
                        You can choose from: sysadmin, java programmer, python programmer
                        Give projectName and taskName of tasks that this task depends on
                        Follow the form: <projectName / taskName>, and enter '.' to stop adding new tasks:
                        Give projectName and taskName of tasks that depend on this task
                        Follow the form: <projectName / taskName>, and enter '.' to stop adding new tasks:
                                                
                        ERROR: The given minutes are not of a valid format (0-59)
                                                
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                                                
                        Cancelled Task Creation
                                                
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("SimpleProject\nNewTask2\nCool description\n3\n50\n0.3\nn\nsysadmin\n.\nSimpleProject / HOI\n.\n.\nBACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        Task deviation:
                        Is this a replacement task? (y/n)
                        Give developer roles needed for this task, end with a '.'
                        You can choose from: sysadmin, java programmer, python programmer
                        Give projectName and taskName of tasks that this task depends on
                        Follow the form: <projectName / taskName>, and enter '.' to stop adding new tasks:
                        Give projectName and taskName of tasks that depend on this task
                        Follow the form: <projectName / taskName>, and enter '.' to stop adding new tasks:
                                                
                        ERROR: A given previous or next task name can't be found
                                                
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                                                
                        Cancelled Task Creation
                                                
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("SimpleProject\nTEST\nCool description\n3\n50\n0.3\nn\nsysadmin\n.\n.\nSimpleProject / SimpleTask\n.\nBACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        Task deviation:
                        Is this a replacement task? (y/n)
                        Give developer roles needed for this task, end with a '.'
                        You can choose from: sysadmin, java programmer, python programmer
                        Give projectName and taskName of tasks that this task depends on
                        Follow the form: <projectName / taskName>, and enter '.' to stop adding new tasks:
                        Give projectName and taskName of tasks that depend on this task
                        Follow the form: <projectName / taskName>, and enter '.' to stop adding new tasks:
                                                
                        ERROR: One of the next tasks is not (un)available
                                                
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                                                
                        Cancelled Task Creation
                                                
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("SimpleProject\nNewTask2\nCool description\n3\n50\n0.3\nn\nsysadmin\n.\nSimpleProject / NewTask\n.\nSimpleProject / NewTask\n.\nBACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        Task deviation:
                        Is this a replacement task? (y/n)
                        Give developer roles needed for this task, end with a '.'
                        You can choose from: sysadmin, java programmer, python programmer
                        Give projectName and taskName of tasks that this task depends on
                        Follow the form: <projectName / taskName>, and enter '.' to stop adding new tasks:
                        Give projectName and taskName of tasks that depend on this task
                        Follow the form: <projectName / taskName>, and enter '.' to stop adding new tasks:
                                                
                        ERROR: Given list of tasks introduces a loop
                                                
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                                                
                        Cancelled Task Creation
                                                
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        taskManSystem.createProject("Finished", "", new Time(500));
        taskManSystem.addTaskToProject("Finished", "Task", "", new Time(5), 0.1, List.of(Role.JAVAPROGRAMMER), new HashSet<>(), new HashSet<>());
        taskManSystem.startTask("Finished", "Task", developer, Role.JAVAPROGRAMMER);
        taskManSystem.advanceTime(5);
        taskManSystem.finishTask("Finished", "Task", developer);

        System.setIn(new ByteArrayInputStream("Finished\nNewTask\nDescription\n3\n50\n0.3\nn\nsysadmin\n.\n.\n.\nBACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Task name:
                        Task description:
                        Task duration hours:
                        Task duration minutes:
                        Task deviation:
                        Is this a replacement task? (y/n)
                        Give developer roles needed for this task, end with a '.'
                        You can choose from: sysadmin, java programmer, python programmer
                        Give projectName and taskName of tasks that this task depends on
                        Follow the form: <projectName / taskName>, and enter '.' to stop adding new tasks:
                        Give projectName and taskName of tasks that depend on this task
                        Follow the form: <projectName / taskName>, and enter '.' to stop adding new tasks:
                                                
                        ERROR: Project is already finished
                                                
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                                                
                        Cancelled Task Creation
                                                
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

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
