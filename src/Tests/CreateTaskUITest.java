package Tests;

import Domain.*;
import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.IllegalTaskRolesException;
import org.junit.Test;
import Application.*;
import UserInterface.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class CreateTaskUITest {

    @Test
    public void test() throws ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, IncorrectTaskStatusException, IncorrectUserException, InvalidTimeException, NewTimeBeforeSystemTimeException, EndTimeBeforeStartTimeException, DueTimeBeforeCreationTimeException, ProjectNotOngoingException, LoopDependencyGraphException, IllegalTaskRolesException, UserAlreadyAssignedToTaskException, IncorrectRoleException {

        // Setup test environment
        Session managerSession = new Session();
        SessionProxy managerSessionProxy = new SessionProxy(managerSession);
        Session developerSession = new Session();
        SessionProxy developerSessionProxy = new SessionProxy(developerSession);
        User manager = new User("DieterVH", "computer776", Set.of(Role.PROJECTMANAGER));
        User developer = new User("SamHa", "trein123", Set.of(Role.PYTHONPROGRAMMER, Role.JAVAPROGRAMMER));
        User developer2 = new User("SamHa2", "trein123", Set.of(Role.PYTHONPROGRAMMER, Role.JAVAPROGRAMMER));

        managerSession.login(manager);
        developerSession.login(developer);

        TaskManSystem taskManSystem = new TaskManSystem(new Time(0));

        CreateTaskController managerController = new CreateTaskController(managerSessionProxy, taskManSystem);
        CreateTaskController developerController = new CreateTaskController(developerSessionProxy, taskManSystem);

        CreateTaskUI developerUI = new CreateTaskUI(developerController);
        CreateTaskUI managerUI = new CreateTaskUI(managerController);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("BACK\n".getBytes()));
        managerUI.createTask();
        assertEquals(
                """
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - There is no ongoing project in the system.
                        Project name of an ongoing project to add the task to:
                        Cancelled task creation
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        taskManSystem.createProject("SimpleProject", "Cool description", new Time(100));
        taskManSystem.addTaskToProject("SimpleProject", "SimpleTask", "Cool description", new Time(40), 0.1, List.of(Role.JAVAPROGRAMMER, Role.PYTHONPROGRAMMER), new HashSet<>(), new HashSet<>());

        developerUI.createTask();
        assertEquals("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function\n".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString());
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
                        Cancelled task creation
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
                        Cancelled task creation
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
                        Cancelled task creation
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
                        Cancelled task creation
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
                        Cancelled task creation
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
                        Cancelled task creation
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
                        Cancelled task creation
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
                        Cancelled task creation
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
                        Cancelled task creation
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
                        Cancelled task creation
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
                        This task is a replacement for task:
                        Cancelled task creation
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
                        Is this a replacement task? (y/n)
                        Cancelled task creation
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
                        Is this a replacement task? (y/n)
                        Give developer roles needed for this task, end with a '.'
                        You can choose from: sysadmin, java programmer, python programmer
                        Cancelled task creation
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
                        Is this a replacement task? (y/n)
                        Give developer roles needed for this task, end with a '.'
                        You can choose from: sysadmin, java programmer, python programmer
                        (Unrecognized developer role)
                        Cancelled task creation
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nNewTask\nCool description\n3\n20\n0.3\nd\nn\nsysadmin\n.\nSimpleTask\n.\nBACK\n".getBytes()));
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
                        Is this a replacement task? (y/n)
                        Give developer roles needed for this task, end with a '.'
                        You can choose from: sysadmin, java programmer, python programmer
                        Tasks that this task depends on, enter '.' to stop adding new tasks:
                        Tasks that depend on this task, enter '.' to stop adding new tasks:
                        Cancelled task creation
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nNewTask\nCool description\n3\n20\n0.3\nd\nn\nsysadmin\n.\nSimpleTask\n.\n.\nBACK\n".getBytes()));
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
                        Is this a replacement task? (y/n)
                        Give developer roles needed for this task, end with a '.'
                        You can choose from: sysadmin, java programmer, python programmer
                        Tasks that this task depends on, enter '.' to stop adding new tasks:
                        Tasks that depend on this task, enter '.' to stop adding new tasks:
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
                        This task is a replacement for task:
                        ERROR: The task to replace is not failed, please try again
                                               
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Cancelled task creation
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
                        Cancelled task creation
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
                        This task is a replacement for task:
                        ERROR: The given minutes are not of a valid format (0-59)
                                               
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Cancelled task creation
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
                        This task is a replacement for task:
                        ERROR: the given task to replace does not exist
                                               
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Cancelled task creation
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
                        This task is a replacement for task:
                        ERROR: the given task name is already in use
                                               
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Cancelled task creation
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
                        This task is a replacement for task:
                        ERROR: the given task name is already in use
                                              
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Cancelled task creation
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("IncorrectProject\nNewTask\nCool description\n3\n20\n0.3\nd\nn\njava programmer\nsysadmin\npython programmer\n.\nSimpleTask\n.\nBACK\n".getBytes()));
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
                        Is this a replacement task? (y/n)
                        Give developer roles needed for this task, end with a '.'
                        You can choose from: sysadmin, java programmer, python programmer
                        Tasks that this task depends on, enter '.' to stop adding new tasks:
                        Tasks that depend on this task, enter '.' to stop adding new tasks:
                        Cancelled task creation
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("IncorrectProject\nNewTask\nCool description\n3\n20\n0.3\nd\nn\njava programmer\nsysadmin\npython programmer\n.\nSimpleTask\n.\nSimpleTask\nBACK\n".getBytes()));
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
                        Is this a replacement task? (y/n)
                        Give developer roles needed for this task, end with a '.'
                        You can choose from: sysadmin, java programmer, python programmer
                        Tasks that this task depends on, enter '.' to stop adding new tasks:
                        Tasks that depend on this task, enter '.' to stop adding new tasks:
                        Cancelled task creation
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
                        Is this a replacement task? (y/n)
                        Give developer roles needed for this task, end with a '.'
                        You can choose from: sysadmin, java programmer, python programmer
                        Tasks that this task depends on, enter '.' to stop adding new tasks:
                        Cancelled task creation
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("IncorrectProject\nNewTask\nCool description\n3\n20\n0.3\nd\nn\nsysadmin\n.\nSimpleTask\nBACK\n".getBytes()));
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
                        Is this a replacement task? (y/n)
                        Give developer roles needed for this task, end with a '.'
                        You can choose from: sysadmin, java programmer, python programmer
                        Tasks that this task depends on, enter '.' to stop adding new tasks:
                        Cancelled task creation
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("IncorrectProject\nNewTask\nCool description\n3\n20\n0.3\nn\nsysadmin\n.\nSimpleTask\n.\n.\nBACK\n".getBytes()));
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
                        Tasks that this task depends on, enter '.' to stop adding new tasks:
                        Tasks that depend on this task, enter '.' to stop adding new tasks:
                        ERROR: Given project does not exist
                                                
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Cancelled task creation
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
                        Tasks that this task depends on, enter '.' to stop adding new tasks:
                        Tasks that depend on this task, enter '.' to stop adding new tasks:
                        ERROR: The given task name is already in use
                        
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Cancelled task creation
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
                        Tasks that this task depends on, enter '.' to stop adding new tasks:
                        Tasks that depend on this task, enter '.' to stop adding new tasks:
                        ERROR: The given minutes are not of a valid format (0-59)
                        
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Cancelled task creation
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("SimpleProject\nNewTask2\nCool description\n3\n50\n0.3\nn\nsysadmin\n.\nHOI\n.\n.\nBACK\n".getBytes()));
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
                        Tasks that this task depends on, enter '.' to stop adding new tasks:
                        Tasks that depend on this task, enter '.' to stop adding new tasks:
                        ERROR: A given previous or next task name can't be found
                        
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Cancelled task creation
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("SimpleProject\nNewTask2\nCool description\n3\n50\n0.3\nn\nsysadmin\n.\n.\nSimpleTask\n.\nBACK\n".getBytes()));
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
                        Tasks that this task depends on, enter '.' to stop adding new tasks:
                        Tasks that depend on this task, enter '.' to stop adding new tasks:
                        ERROR: One of the next tasks is not (un)available
                        
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Cancelled task creation
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("SimpleProject\nNewTask2\nCool description\n3\n50\n0.3\nn\nsysadmin\n.\nNewTask\n.\nNewTask\n.\nBACK\n".getBytes()));
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
                        Tasks that this task depends on, enter '.' to stop adding new tasks:
                        Tasks that depend on this task, enter '.' to stop adding new tasks:
                        ERROR: Given list of tasks introduces a loop
                        
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Cancelled task creation
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
                        Tasks that this task depends on, enter '.' to stop adding new tasks:
                        Tasks that depend on this task, enter '.' to stop adding new tasks:
                        ERROR: Project is already finished
                        
                        Type BACK to cancel task creation at any time
                        *********** TASK CREATION FORM ***********
                        -- Ongoing Projects --
                         - SimpleProject
                         - SimpleProject2
                        Project name of an ongoing project to add the task to:
                        Cancelled task creation
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

    }
}
