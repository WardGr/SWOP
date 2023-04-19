package Tests;

import Domain.*;
import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.NonDeveloperRoleException;
import org.junit.Test;
import Application.*;
import UserInterface.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class CreateTaskUITest {

    @Test
    public void test() throws ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, IncorrectTaskStatusException, IncorrectUserException, InvalidTimeException, NewTimeBeforeSystemTimeException, EndTimeBeforeStartTimeException, DueTimeBeforeCreationTimeException, ProjectNotOngoingException, LoopDependencyGraphException, NonDeveloperRoleException, UserAlreadyAssignedToTaskException, IncorrectRoleException {

        // Setup test environment

        Time systemtime = new Time(0);

        Session managerSession = new Session();
        SessionWrapper managerSessionWrapper = new SessionWrapper(managerSession);
        Session developerSession = new Session();
        SessionWrapper developerSessionWrapper = new SessionWrapper(developerSession);
        Set roleMan = new HashSet();
        roleMan.add(Role.PROJECTMANAGER);
        Set roleDev = new HashSet();
        roleDev.add(Role.JAVAPROGRAMMER);
        roleDev.add(Role.PYTHONPROGRAMMER);
        User manager = new User("DieterVH", "computer776", roleMan);
        User developer = new User("SamHa", "trein123", roleDev);
        User developer2 = new User("SamHa2", "trein123", roleDev);

        managerSession.login(manager);
        developerSession.login(developer);

        TaskManSystem taskManSystem = new TaskManSystem(systemtime);
        taskManSystem.createProject("SimpleProject", "Cool description", systemtime, new Time(100));
        List rolesDev = new LinkedList();
        rolesDev.add(Role.JAVAPROGRAMMER);
        rolesDev.add(Role.PYTHONPROGRAMMER);
        taskManSystem.addTaskToProject("SimpleProject", "SimpleTask", "Cool description", new Time(40), 0.1, rolesDev, new HashSet<>(), new HashSet<>());

        UserManager userManager = new UserManager();

        CreateTaskController managerController = new CreateTaskController(managerSessionWrapper, taskManSystem, userManager);
        CreateTaskController developerController = new CreateTaskController(developerSessionWrapper, taskManSystem, userManager);

        CreateTaskUI developerUI = new CreateTaskUI(developerController);
        CreateTaskUI managerUI = new CreateTaskUI(managerController);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

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

        taskManSystem.createProject("SimpleProject2", "Cool description", systemtime, new Time(100));
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


        System.setIn(new ByteArrayInputStream("IncorrectProject\nNewTask\nCool description\n3\n20\n0.3\nd\nn\njava programmer\nsysadmin\n.\nSimpleTask\n.\nBACK\n".getBytes()));
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

    }
}
