package Tests.UITests;

import Application.Session.Session;
import Application.Session.SessionProxy;
import Application.Controllers.TaskControllers.StartTaskController;
import Application.Command.CommandManager;
import Domain.DataClasses.EndTimeBeforeStartTimeException;
import Domain.DataClasses.InvalidTimeException;
import Domain.DataClasses.Time;
import Domain.Project.ProjectNameAlreadyInUseException;
import Domain.Project.ProjectNotOngoingException;
import Domain.Project.TaskNotFoundException;
import Domain.Task.*;
import Domain.Task.IncorrectTaskStatusException;
import Domain.Task.LoopDependencyGraphException;
import Domain.TaskManSystem.DueBeforeSystemTimeException;
import Domain.TaskManSystem.ProjectNotFoundException;
import Domain.TaskManSystem.TaskManSystem;
import Domain.User.IncorrectUserException;
import Domain.User.Role;
import Domain.User.User;
import Domain.User.UserAlreadyAssignedToTaskException;
import UserInterface.TaskUIs.StartTaskUI;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class StartTaskUITest {
    @Test
    public void test() throws InvalidTimeException, ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNameAlreadyInUseException, TaskNotFoundException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException, IllegalTaskRolesException, UserAlreadyAssignedToTaskException, IncorrectRoleException, EndTimeBeforeStartTimeException, IncorrectUserException {
        Session session = new Session();
        SessionProxy sessionProxy = new SessionProxy(session);

        TaskManSystem taskManSystem = new TaskManSystem(new Time(0));

        CommandManager commandManager = new CommandManager();

        StartTaskController controller = new StartTaskController(sessionProxy, taskManSystem, commandManager);
        StartTaskUI startTaskUI = new StartTaskUI(controller);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        startTaskUI.startTask();
        assertEquals(
                """
                        ERROR: You need a developer role to call this function.
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        User developer = new User("OlavBl", "123", Set.of(Role.PYTHONPROGRAMMER));
        session.login(developer);

        taskManSystem.createProject("Project 1", "Description 1", new Time(6));
        taskManSystem.addTaskToProject("Project 1", "Task 1", "Description 1", new Time(50), 0.1, List.of(Role.PYTHONPROGRAMMER, Role.PYTHONPROGRAMMER), new HashSet<>(), new HashSet<>());
        taskManSystem.addTaskToProject("Project 1", "Task 2", "Description 2", new Time(20), 0.1, List.of(Role.PYTHONPROGRAMMER), new HashSet<>(), new HashSet<>());

        System.setIn(new ByteArrayInputStream("Project 1\nTask 2\nsysadmin\ny\nBACK\n".getBytes()));
        startTaskUI.startTask();
        assertEquals(
                """
                        ***** LIST OF AVAILABLE OR PENDING TASKS *****
                         - Task: Task 1, belonging to Project: Project 1
                         - Task: Task 2, belonging to Project: Project 1
                        
                        Please give the project name you want to start working in:
                        Please give the task name you want to start working on in project Project 1:
                        You have roles: Python programmer
                        Task requires roles: Python programmer
                        Give the role you want to fulfill in task Task 2:
                        ******** TASK DETAILS ********
                        Task Name:            Task 2
                        Belonging to project: Project 1
                        Description:          Description 2
                        Estimated Duration:   0 hour(s), 20 minute(s)
                        Status:               available
                       
                        Replaces Task:      Replaces no tasks
                         
                        Required roles:
                        - Python programmer
                       
                        Committed users:
                        No users are committed to this task.
                       
                        Start working on this task as a system administration developer at the current system time: 0 hour(s), 0 minute(s)
                        Confirm? (y/n)
                       
                        ERROR: Given role is not required in the task
                        ***** LIST OF AVAILABLE OR PENDING TASKS *****
                         - Task: Task 1, belonging to Project: Project 1
                         - Task: Task 2, belonging to Project: Project 1
                       
                        Please give the project name you want to start working in:
                        Cancelled starting task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        taskManSystem.startTask("Project 1", "Task 2", developer, Role.PYTHONPROGRAMMER);

        System.setIn(new ByteArrayInputStream("Project 1\nTask 2\npython programmer\ny\nBACK\n".getBytes()));
        startTaskUI.startTask();
        assertEquals(
                """
                       ***** LIST OF AVAILABLE OR PENDING TASKS *****
                        - Task: Task 1, belonging to Project: Project 1
                       
                       Please give the project name you want to start working in:
                       Please give the task name you want to start working on in project Project 1:
                       You have roles: Python programmer
                       Task requires roles: The task doesn't require any roles
                       Give the role you want to fulfill in task Task 2:
                       ******** TASK DETAILS ********
                       Task Name:            Task 2
                       Belonging to project: Project 1
                       Description:          Description 2
                       Estimated Duration:   0 hour(s), 20 minute(s)
                       Status:               executing
                       
                       Replaces Task:      Replaces no tasks
                       
                       Required roles:
                       All roles are filled in.
                       
                       Committed users:
                       - OlavBl as Python programmer
                       
                       Start working on this task as a Python programmer at the current system time: 0 hour(s), 0 minute(s)
                       Confirm? (y/n)
                       
                       ERROR: Given task does not have the right status to start
                       ***** LIST OF AVAILABLE OR PENDING TASKS *****
                        - Task: Task 1, belonging to Project: Project 1
                       
                       Please give the project name you want to start working in:
                       Cancelled starting task
                       """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("Project 1\nTask 1\npython programmer\ny\nBACK\n".getBytes()));
        startTaskUI.startTask();
        assertEquals(
                """
                         ***** LIST OF AVAILABLE OR PENDING TASKS *****
                          - Task: Task 1, belonging to Project: Project 1
                                                
                         Please give the project name you want to start working in:
                         Please give the task name you want to start working on in project Project 1:
                         You have roles: Python programmer
                         Task requires roles: Python programmer, Python programmer
                         Give the role you want to fulfill in task Task 1:
                         ******** TASK DETAILS ********
                         Task Name:            Task 1
                         Belonging to project: Project 1
                         Description:          Description 1
                         Estimated Duration:   0 hour(s), 50 minute(s)
                         Status:               available
                                                
                         Replaces Task:      Replaces no tasks
                                                
                         Required roles:
                         - Python programmer
                         - Python programmer
                                                
                         Committed users:
                         No users are committed to this task.
                                                
                         Start working on this task as a Python programmer at the current system time: 0 hour(s), 0 minute(s)
                         Confirm? (y/n)
                                                
                         ERROR: User is already executing a task
                         ***** LIST OF AVAILABLE OR PENDING TASKS *****
                          - Task: Task 1, belonging to Project: Project 1
                                                
                         Please give the project name you want to start working in:
                         Cancelled starting task
                         """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        taskManSystem.failTask("Project 1", "Task 2", developer);

        // Testing BACK
        System.setIn(new ByteArrayInputStream("BACK\n".getBytes()));
        startTaskUI.startTask();
        assertEquals(
                """
                        ***** LIST OF AVAILABLE OR PENDING TASKS *****
                         - Task: Task 1, belonging to Project: Project 1
                        
                        Please give the project name you want to start working in:
                        Cancelled starting task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        // Testing BACK
        System.setIn(new ByteArrayInputStream("Project 1\nBACK\n".getBytes()));
        startTaskUI.startTask();
        assertEquals(
                """
                        ***** LIST OF AVAILABLE OR PENDING TASKS *****
                         - Task: Task 1, belonging to Project: Project 1
                        
                        Please give the project name you want to start working in:
                        Please give the task name you want to start working on in project Project 1:
                        Cancelled starting task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        // Testing non available/pending task
        System.setIn(new ByteArrayInputStream("Project 1\nTask 2\nBACK".getBytes()));
        startTaskUI.startTask();
        assertEquals(
                """
                        ***** LIST OF AVAILABLE OR PENDING TASKS *****
                         - Task: Task 1, belonging to Project: Project 1
                                                
                        Please give the project name you want to start working in:
                        Please give the task name you want to start working on in project Project 1:
                        You have roles: Python programmer
                        Task requires roles: The task doesn't require any roles
                        Give the role you want to fulfill in task Task 2:
                        Cancelled starting task Task 2
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        // Testing normal behavior
        System.setIn(new ByteArrayInputStream("Project 1\nTask 1\npython programmer\ny\n".getBytes()));
        startTaskUI.startTask();
        assertEquals(
                """
                        ***** LIST OF AVAILABLE OR PENDING TASKS *****
                         - Task: Task 1, belonging to Project: Project 1
                        
                        Please give the project name you want to start working in:
                        Please give the task name you want to start working on in project Project 1:
                        You have roles: Python programmer
                        Task requires roles: Python programmer, Python programmer
                        Give the role you want to fulfill in task Task 1:
                        ******** TASK DETAILS ********
                        Task Name:            Task 1
                        Belonging to project: Project 1
                        Description:          Description 1
                        Estimated Duration:   0 hour(s), 50 minute(s)
                        Status:               available
                        
                        Replaces Task:      Replaces no tasks
                        
                        Required roles:
                        - Python programmer
                        - Python programmer
                        
                        Committed users:
                        No users are committed to this task.
                        
                        Start working on this task as a Python programmer at the current system time: 0 hour(s), 0 minute(s)
                        Confirm? (y/n)
                        
                        Successfully started working on task Task 1 in project Project 1 as Python programmer
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        // Testing wrong confirm input
        System.setIn(new ByteArrayInputStream("Project 1\nTask 1\npython programmer\ny\nx\ny\n".getBytes()));
        startTaskUI.startTask();
        assertEquals(
                """
                        ***** LIST OF AVAILABLE OR PENDING TASKS *****
                         - Task: Task 1, belonging to Project: Project 1
                                                
                        Please give the project name you want to start working in:
                        Please give the task name you want to start working on in project Project 1:
                        You have roles: Python programmer
                        Task requires roles: Python programmer
                        Give the role you want to fulfill in task Task 1:
                        ******** TASK DETAILS ********
                        Task Name:            Task 1
                        Belonging to project: Project 1
                        Description:          Description 1
                        Estimated Duration:   0 hour(s), 50 minute(s)
                        Status:               pending
                                                
                        Replaces Task:      Replaces no tasks
                                                
                        Required roles:
                        - Python programmer
                                                
                        Committed users:
                        - OlavBl as Python programmer
                                                
                        Start working on this task as a Python programmer at the current system time: 0 hour(s), 0 minute(s)
                        Confirm? (y/n)
                                                
                        You are already pending for task Task 1
                        Confirm you want to stop pending for task Task 1 and start working on task Task 1? (y/n)
                        
                        Input has to be 'y' or 'n', try again
                        Confirm you want to stop pending for task Task 1 and start working on task Task 1? (y/n)
                        
                        Successfully started working on task Task 1 in project Project 1 as Python programmer
                                                
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        // Testing BACK
        System.setIn(new ByteArrayInputStream("Project 1\nTask 1\npython programmer\nBACK\n".getBytes()));
        startTaskUI.startTask();
        assertEquals(
                """
                        ***** LIST OF AVAILABLE OR PENDING TASKS *****
                         - Task: Task 1, belonging to Project: Project 1
                                                
                        Please give the project name you want to start working in:
                        Please give the task name you want to start working on in project Project 1:
                        You have roles: Python programmer
                        Task requires roles: Python programmer
                        Give the role you want to fulfill in task Task 1:
                        ******** TASK DETAILS ********
                        Task Name:            Task 1
                        Belonging to project: Project 1
                        Description:          Description 1
                        Estimated Duration:   0 hour(s), 50 minute(s)
                        Status:               pending
                                                
                        Replaces Task:      Replaces no tasks
                                                
                        Required roles:
                        - Python programmer
                                                
                        Committed users:
                        - OlavBl as Python programmer
                                                
                        Start working on this task as a Python programmer at the current system time: 0 hour(s), 0 minute(s)
                        Confirm? (y/n)
                        
                        Cancelled starting task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        // Testing BACK
        System.setIn(new ByteArrayInputStream("Project 1\nTask 1\npython programmer\nx\ny\nBACK\n".getBytes()));
        startTaskUI.startTask();
        assertEquals(
                """
                        ***** LIST OF AVAILABLE OR PENDING TASKS *****
                         - Task: Task 1, belonging to Project: Project 1
                                                
                        Please give the project name you want to start working in:
                        Please give the task name you want to start working on in project Project 1:
                        You have roles: Python programmer
                        Task requires roles: Python programmer
                        Give the role you want to fulfill in task Task 1:
                        ******** TASK DETAILS ********
                        Task Name:            Task 1
                        Belonging to project: Project 1
                        Description:          Description 1
                        Estimated Duration:   0 hour(s), 50 minute(s)
                        Status:               pending
                                                
                        Replaces Task:      Replaces no tasks
                                                
                        Required roles:
                        - Python programmer
                                                
                        Committed users:
                        - OlavBl as Python programmer
                                                
                        Start working on this task as a Python programmer at the current system time: 0 hour(s), 0 minute(s)
                        Confirm? (y/n)
                        
                        Input has to be 'y' or 'n', try again
                        Start working on this task as a Python programmer at the current system time: 0 hour(s), 0 minute(s)
                        Confirm? (y/n)
                        
                        You are already pending for task Task 1
                        Confirm you want to stop pending for task Task 1 and start working on task Task 1? (y/n)
                        
                        Cancelled starting task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        // Testing BACK
        System.setIn(new ByteArrayInputStream("Project 1\nTask 1\npython programmer\ny\nx\nBACK\n".getBytes()));
        startTaskUI.startTask();
        assertEquals(
                """
                        ***** LIST OF AVAILABLE OR PENDING TASKS *****
                         - Task: Task 1, belonging to Project: Project 1
                                                
                        Please give the project name you want to start working in:
                        Please give the task name you want to start working on in project Project 1:
                        You have roles: Python programmer
                        Task requires roles: Python programmer
                        Give the role you want to fulfill in task Task 1:
                        ******** TASK DETAILS ********
                        Task Name:            Task 1
                        Belonging to project: Project 1
                        Description:          Description 1
                        Estimated Duration:   0 hour(s), 50 minute(s)
                        Status:               pending
                                                
                        Replaces Task:      Replaces no tasks
                                                
                        Required roles:
                        - Python programmer
                                                
                        Committed users:
                        - OlavBl as Python programmer
                                                
                        Start working on this task as a Python programmer at the current system time: 0 hour(s), 0 minute(s)
                        Confirm? (y/n)
                        
                        You are already pending for task Task 1
                        Confirm you want to stop pending for task Task 1 and start working on task Task 1? (y/n)
                        
                        Input has to be 'y' or 'n', try again
                        Confirm you want to stop pending for task Task 1 and start working on task Task 1? (y/n)
                        
                        Cancelled starting task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        // Testing no confirm
        System.setIn(new ByteArrayInputStream("Project 1\nTask 1\npython programmer\ny\nn\nBACK\n".getBytes()));
        startTaskUI.startTask();
        assertEquals(
                """
                        ***** LIST OF AVAILABLE OR PENDING TASKS *****
                         - Task: Task 1, belonging to Project: Project 1
                                                
                        Please give the project name you want to start working in:
                        Please give the task name you want to start working on in project Project 1:
                        You have roles: Python programmer
                        Task requires roles: Python programmer
                        Give the role you want to fulfill in task Task 1:
                        ******** TASK DETAILS ********
                        Task Name:            Task 1
                        Belonging to project: Project 1
                        Description:          Description 1
                        Estimated Duration:   0 hour(s), 50 minute(s)
                        Status:               pending
                                                
                        Replaces Task:      Replaces no tasks
                                                
                        Required roles:
                        - Python programmer
                                                
                        Committed users:
                        - OlavBl as Python programmer
                                                
                        Start working on this task as a Python programmer at the current system time: 0 hour(s), 0 minute(s)
                        Confirm? (y/n)
                                                
                        You are already pending for task Task 1
                        Confirm you want to stop pending for task Task 1 and start working on task Task 1? (y/n)
                                                
                        ERROR: Starting of the task needs confirmation and isn't confirmed
                        ***** LIST OF AVAILABLE OR PENDING TASKS *****
                         - Task: Task 1, belonging to Project: Project 1
                                                
                        Please give the project name you want to start working in:
                        Cancelled starting task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        // Testing BACK
        System.setIn(new ByteArrayInputStream("Project 1\nTask 1\npython programmer\nx\nBACK\n".getBytes()));
        startTaskUI.startTask();
        assertEquals(
                """
                        ***** LIST OF AVAILABLE OR PENDING TASKS *****
                         - Task: Task 1, belonging to Project: Project 1
                                                
                        Please give the project name you want to start working in:
                        Please give the task name you want to start working on in project Project 1:
                        You have roles: Python programmer
                        Task requires roles: Python programmer
                        Give the role you want to fulfill in task Task 1:
                        ******** TASK DETAILS ********
                        Task Name:            Task 1
                        Belonging to project: Project 1
                        Description:          Description 1
                        Estimated Duration:   0 hour(s), 50 minute(s)
                        Status:               pending
                                                
                        Replaces Task:      Replaces no tasks
                                                
                        Required roles:
                        - Python programmer
                                                
                        Committed users:
                        - OlavBl as Python programmer
                                                
                        Start working on this task as a Python programmer at the current system time: 0 hour(s), 0 minute(s)
                        Confirm? (y/n)
                        
                        Input has to be 'y' or 'n', try again
                        Start working on this task as a Python programmer at the current system time: 0 hour(s), 0 minute(s)
                        Confirm? (y/n)
                        
                        Cancelled starting task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        // Testing no confirm
        System.setIn(new ByteArrayInputStream("Project 1\nTask 1\npython programmer\nn\n".getBytes()));
        startTaskUI.startTask();
        assertEquals(
                """
                        ***** LIST OF AVAILABLE OR PENDING TASKS *****
                         - Task: Task 1, belonging to Project: Project 1
                                                
                        Please give the project name you want to start working in:
                        Please give the task name you want to start working on in project Project 1:
                        You have roles: Python programmer
                        Task requires roles: Python programmer
                        Give the role you want to fulfill in task Task 1:
                        ******** TASK DETAILS ********
                        Task Name:            Task 1
                        Belonging to project: Project 1
                        Description:          Description 1
                        Estimated Duration:   0 hour(s), 50 minute(s)
                        Status:               pending
                                                
                        Replaces Task:      Replaces no tasks
                                                
                        Required roles:
                        - Python programmer
                                                
                        Committed users:
                        - OlavBl as Python programmer
                                                
                        Start working on this task as a Python programmer at the current system time: 0 hour(s), 0 minute(s)
                        Confirm? (y/n)
                        
                        Cancelled starting task Task 1
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        // Testing BACK
        System.setIn(new ByteArrayInputStream("Project 1\nTask 1\nBACK\n".getBytes()));
        startTaskUI.startTask();
        assertEquals(
                """
                        ***** LIST OF AVAILABLE OR PENDING TASKS *****
                         - Task: Task 1, belonging to Project: Project 1
                        
                        Please give the project name you want to start working in:
                        Please give the task name you want to start working on in project Project 1:
                        You have roles: Python programmer
                        Task requires roles: Python programmer
                        Give the role you want to fulfill in task Task 1:
                        Cancelled starting task Task 1
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        // Testing roles
        System.setIn(new ByteArrayInputStream("Project 1\nTask 1\nsysadmin\nBACK\n".getBytes()));
        startTaskUI.startTask();
        assertEquals(
                """
                        ***** LIST OF AVAILABLE OR PENDING TASKS *****
                         - Task: Task 1, belonging to Project: Project 1
                                                
                        Please give the project name you want to start working in:
                        Please give the task name you want to start working on in project Project 1:
                        You have roles: Python programmer
                        Task requires roles: Python programmer
                        Give the role you want to fulfill in task Task 1:
                        ******** TASK DETAILS ********
                        Task Name:            Task 1
                        Belonging to project: Project 1
                        Description:          Description 1
                        Estimated Duration:   0 hour(s), 50 minute(s)
                        Status:               pending
                                                
                        Replaces Task:      Replaces no tasks
                                                
                        Required roles:
                        - Python programmer
                                                
                        Committed users:
                        - OlavBl as Python programmer
                                                
                        Start working on this task as a system administration developer at the current system time: 0 hour(s), 0 minute(s)
                        Confirm? (y/n)
                                                
                        Cancelled starting task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        // Testing roles
        System.setIn(new ByteArrayInputStream("Project 1\nTask 1\njava programmer\nBACK\n".getBytes()));
        startTaskUI.startTask();
        assertEquals(
                """
                        ***** LIST OF AVAILABLE OR PENDING TASKS *****
                         - Task: Task 1, belonging to Project: Project 1
                                                
                        Please give the project name you want to start working in:
                        Please give the task name you want to start working on in project Project 1:
                        You have roles: Python programmer
                        Task requires roles: Python programmer
                        Give the role you want to fulfill in task Task 1:
                        ******** TASK DETAILS ********
                        Task Name:            Task 1
                        Belonging to project: Project 1
                        Description:          Description 1
                        Estimated Duration:   0 hour(s), 50 minute(s)
                        Status:               pending
                                                
                        Replaces Task:      Replaces no tasks
                                                
                        Required roles:
                        - Python programmer
                                                
                        Committed users:
                        - OlavBl as Python programmer
                                                
                        Start working on this task as a Java programmer at the current system time: 0 hour(s), 0 minute(s)
                        Confirm? (y/n)
                                                
                        Cancelled starting task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        // Testing roles
        System.setIn(new ByteArrayInputStream("Project 1\nTask 1\nprogrammer\nBACK\n".getBytes()));
        startTaskUI.startTask();
        assertEquals(
                """
                        ***** LIST OF AVAILABLE OR PENDING TASKS *****
                         - Task: Task 1, belonging to Project: Project 1
                        
                        Please give the project name you want to start working in:
                        Please give the task name you want to start working on in project Project 1:
                        You have roles: Python programmer
                        Task requires roles: Python programmer
                        Give the role you want to fulfill in task Task 1:
                        Unrecognized developer role
                        Give the role you want to fulfill in task Task 1:
                        Cancelled starting task Task 1
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        // Testing wrong project name
        System.setIn(new ByteArrayInputStream("Project x\nTask 1\nBACK\n".getBytes()));
        startTaskUI.startTask();
        assertEquals(
                """
                        ***** LIST OF AVAILABLE OR PENDING TASKS *****
                         - Task: Task 1, belonging to Project: Project 1
                        
                        Please give the project name you want to start working in:
                        Please give the task name you want to start working on in project Project x:
                        ERROR: Given project could not be found
                        ***** LIST OF AVAILABLE OR PENDING TASKS *****
                         - Task: Task 1, belonging to Project: Project 1
                        
                        Please give the project name you want to start working in:
                        Cancelled starting task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        // Testing wrong task name
        System.setIn(new ByteArrayInputStream("Project 1\nTask x\nBACK\n".getBytes()));
        startTaskUI.startTask();
        assertEquals(
                """
                        ***** LIST OF AVAILABLE OR PENDING TASKS *****
                         - Task: Task 1, belonging to Project: Project 1
                        
                        Please give the project name you want to start working in:
                        Please give the task name you want to start working on in project Project 1:
                        ERROR: Given task could not be found
                        ***** LIST OF AVAILABLE OR PENDING TASKS *****
                         - Task: Task 1, belonging to Project: Project 1
                        
                        Please give the project name you want to start working in:
                        Cancelled starting task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        taskManSystem.replaceTaskInProject("Project 1", "Task 2 Repl", "Test", new Time(5), 0.1, "Task 2");

        // Testing replacement task
        System.setIn(new ByteArrayInputStream("Project 1\nTask 2 Repl\npython programmer\nBACK\n".getBytes()));
        startTaskUI.startTask();
        assertEquals(
                """
                        ***** LIST OF AVAILABLE OR PENDING TASKS *****
                         - Task: Task 1, belonging to Project: Project 1
                         - Task: Task 2 Repl, belonging to Project: Project 1
                        
                        Please give the project name you want to start working in:
                        Please give the task name you want to start working on in project Project 1:
                        You have roles: Python programmer
                        Task requires roles: Python programmer
                        Give the role you want to fulfill in task Task 2 Repl:
                        ******** TASK DETAILS ********
                        Task Name:            Task 2 Repl
                        Belonging to project: Project 1
                        Description:          Test
                        Estimated Duration:   0 hour(s), 5 minute(s)
                        Status:               available
                        
                        Replaces Task:      Task 2
                        
                        Required roles:
                        - Python programmer
                        
                        Committed users:
                        No users are committed to this task.
                        
                        Start working on this task as a Python programmer at the current system time: 0 hour(s), 0 minute(s)
                        Confirm? (y/n)
                        
                        Cancelled starting task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }
}