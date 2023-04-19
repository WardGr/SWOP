package Tests;

import Application.Session;
import Application.SessionWrapper;
import Application.UpdateDependenciesController;
import Domain.*;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.NonDeveloperRoleException;
import UserInterface.UpdateDependenciesUI;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class UpdateDependenciesUITest {
    @Test
    public void test() throws ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, InvalidTimeException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException, NonDeveloperRoleException {
        // Setup test environment

        Time systemtime = new Time(0);

        Session managerSession = new Session();
        SessionWrapper managerSessionWrapper = new SessionWrapper(managerSession);
        Session developerSession = new Session();
        SessionWrapper developerSessionWrapper = new SessionWrapper(developerSession);
        Session wrongSession = new Session();
        SessionWrapper wrongSessionWrapper = new SessionWrapper(wrongSession);

        User manager = new User("DieterVH", "computer776", Set.of(Role.PROJECTMANAGER));
        User developer = new User("SamHa", "trein123", Set.of(Role.PYTHONPROGRAMMER));
        User wrongUser = new User("HOI", "HOI", Set.of(Role.PYTHONPROGRAMMER));

        managerSession.login(manager);
        developerSession.login(developer);
        wrongSession.login(wrongUser);

        TaskManSystem taskManSystem = new TaskManSystem(systemtime);

        UpdateDependenciesController developerController = new UpdateDependenciesController(developerSessionWrapper, taskManSystem);
        UpdateDependenciesController wrongController = new UpdateDependenciesController(wrongSessionWrapper, taskManSystem);
        UpdateDependenciesController managerController = new UpdateDependenciesController(managerSessionWrapper, taskManSystem);

        UpdateDependenciesUI developerUI = new UpdateDependenciesUI(developerController);
        UpdateDependenciesUI wrongUI = new UpdateDependenciesUI(wrongController);
        UpdateDependenciesUI managerUI = new UpdateDependenciesUI(managerController);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("BACK\n".getBytes()));
        managerUI.updateDependencies();
        assertEquals(
                """
                        You can always use 'BACK' to return to previous menu
                        
                        ***** UNFINISHED PROJECTS *****
                         --- There are no unfinished projects in the system
                        
                        Give the name of the project you want to edit:
                        Quiting updating task dependencies
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        taskManSystem.createProject("Project 1", "Description 1", new Time(100));
        taskManSystem.addTaskToProject("Project 1", "Task 1", "Description 1", new Time(40), 0.1, List.of(Role.PYTHONPROGRAMMER), new HashSet<>(), new HashSet<>());
        taskManSystem.addTaskToProject("Project 1", "Task 2", "Description 2", new Time(200), 0.2, List.of(Role.PYTHONPROGRAMMER), new HashSet<>(), new HashSet<>());

        developerUI.updateDependencies();
        assertEquals("ERROR: You must be a project manager to call this function\n"
                .replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("BACK\n".getBytes()));
        managerUI.updateDependencies();
        assertEquals(
                """
                        You can always use 'BACK' to return to previous menu
                        
                        ***** UNFINISHED PROJECTS *****
                         - Project 1
                        
                        Give the name of the project you want to edit:
                        Quiting updating task dependencies
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                        out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("HOI\nBACK\n".getBytes()));
        managerUI.updateDependencies();
        assertEquals(
                """
                        You can always use 'BACK' to return to previous menu
                        
                        ***** UNFINISHED PROJECTS *****
                         - Project 1
                        
                        Give the name of the project you want to edit:
                        ERROR: The given project name could not be found.
                        You can always use 'BACK' to return to previous menu
                        
                        ***** UNFINISHED PROJECTS *****
                         - Project 1
                        
                        Give the name of the project you want to edit:
                        Quiting updating task dependencies
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("Project 1\nHOI\nBACK\nBACK\n".getBytes()));
        managerUI.updateDependencies();
        assertEquals(
                """
                        You can always use 'BACK' to return to previous menu
                        
                        ***** UNFINISHED PROJECTS *****
                         - Project 1
                        
                        Give the name of the project you want to edit:
                        ***** (UN)AVAILABLE TASKS *****
                         - Task 1 with status: available
                         - Task 2 with status: available
                        
                        Give the name of the task you want to edit:
                        ERROR: Given task name could not be found, try again.
                        ***** (UN)AVAILABLE TASKS *****
                         - Task 1 with status: available
                         - Task 2 with status: available
                        
                        Give the name of the task you want to edit:
                        Returning to project menu...
                        You can always use 'BACK' to return to previous menu
                                                
                        ***** UNFINISHED PROJECTS *****
                         - Project 1
                                                
                        Give the name of the project you want to edit:
                        Quiting updating task dependencies
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("Project 1\nTask 1\naddnext Task 2\nBACK\nBACK\nBACK\n".getBytes()));
        managerUI.updateDependencies();
        assertEquals(
                """
                        You can always use 'BACK' to return to previous menu
                                                
                        ***** UNFINISHED PROJECTS *****
                         - Project 1
                                                
                        Give the name of the project you want to edit:
                        ***** (UN)AVAILABLE TASKS *****
                         - Task 1 with status: available
                         - Task 2 with status: available
                                                
                        Give the name of the task you want to edit:
                        Previous tasks: There are no previous tasks.
                        Next tasks: There are no next tasks.
                        Possible previous tasks: Task 2
                        Possible next tasks: Task 2
                        Please put in the desired command:\s
                           addprev/addnext/removeprev/removenext <taskName>
                        Previous tasks: There are no previous tasks.
                        Next tasks: Task 2
                        Possible previous tasks: There are no possible previous tasks to add.
                        Possible next tasks: There are no possible next tasks to add.
                        Please put in the desired command:\s
                           addprev/addnext/removeprev/removenext <taskName>
                        Returning to task menu...
                        ***** (UN)AVAILABLE TASKS *****
                         - Task 1 with status: available
                         - Task 2 with status: unavailable
                                                
                        Give the name of the task you want to edit:
                        Returning to project menu...
                        You can always use 'BACK' to return to previous menu
                                                
                        ***** UNFINISHED PROJECTS *****
                         - Project 1
                                                
                        Give the name of the project you want to edit:
                        Quiting updating task dependencies
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        /*


        System.setIn(new ByteArrayInputStream("dfgs\ndsa\nBACK\n".getBytes()));
        developerUI.updateTaskStatus();
        assertEquals(
                """
                        *** AVAILABLE TASKS ***
                        Project: SimpleProject --- SimpleTask
                                        
                        *** EXECUTING TASKS ***
                                        
                        Type BACK to cancel updating the task any time
                        Name of the project you want to update:
                        Name of the task you want to update:
                        ERROR: the given project does not exist, please try again
                                        
                        *** AVAILABLE TASKS ***
                        Project: SimpleProject --- SimpleTask
                                        
                        *** EXECUTING TASKS ***
                                        
                        Type BACK to cancel updating the task any time
                        Name of the project you want to update:
                        Cancelled updating task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\ndsa\nBACK\n".getBytes()));
        developerUI.updateTaskStatus();
        assertEquals(
                """
                        *** AVAILABLE TASKS ***
                        Project: SimpleProject --- SimpleTask
                                        
                        *** EXECUTING TASKS ***
                                        
                        Type BACK to cancel updating the task any time
                        Name of the project you want to update:
                        Name of the task you want to update:
                        ERROR: the given task does not exist, please try again
                                        
                        *** AVAILABLE TASKS ***
                        Project: SimpleProject --- SimpleTask
                                        
                        *** EXECUTING TASKS ***
                                        
                        Type BACK to cancel updating the task any time
                        Name of the project you want to update:
                        Cancelled updating task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nSimpleTask\nn\n0\n0\nBACK\n".getBytes()));
        wrongUI.updateTaskStatus();
        assertEquals(
                """
                        *** AVAILABLE TASKS ***
                        Project: SimpleProject --- SimpleTask
                                                
                        *** EXECUTING TASKS ***
                                                
                        Type BACK to cancel updating the task any time
                        Name of the project you want to update:
                        Name of the task you want to update:
                        Task Name:          SimpleTask
                        Description:        Cool description
                        Estimated Duration: 0 hours, 40 minutes
                        Accepted Deviation: 0.1
                        Status:             available
                                                
                        Replacement Task:   No replacement task
                        Replaces Task:      Replaces no tasks
                                                
                        Start Time:         Task has not started yet
                        End Time:           Task has not ended yet
                                                
                        User:               SamHa
                                                
                        Next tasks:
                        Previous tasks:
                                                
                        -- Possible Next Statuses --
                        - executing
                        - pending
                        Give the start time for the task:
                        Do you want to use system time (0:0)? (y/n)
                        Give start hour:
                        Give start minute:
                        ERROR: you are not allowed to change this task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nBACK\n".getBytes()));
        developerUI.updateTaskStatus();
        assertEquals(
                """
                        *** AVAILABLE TASKS ***
                        Project: SimpleProject --- SimpleTask
                                        
                        *** EXECUTING TASKS ***
                                        
                        Type BACK to cancel updating the task any time
                        Name of the project you want to update:
                        Name of the task you want to update:
                        Cancelled updating task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString());
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nSimpleTask\nb\nBACK".getBytes()));
        developerUI.updateTaskStatus();
        assertEquals(
                """
                        *** AVAILABLE TASKS ***
                        Project: SimpleProject --- SimpleTask
                                                
                        *** EXECUTING TASKS ***
                                                
                        Type BACK to cancel updating the task any time
                        Name of the project you want to update:
                        Name of the task you want to update:
                        Task Name:          SimpleTask
                        Description:        Cool description
                        Estimated Duration: 0 hours, 40 minutes
                        Accepted Deviation: 0.1
                        Status:             available
                                                
                        Replacement Task:   No replacement task
                        Replaces Task:      Replaces no tasks
                                                
                        Start Time:         Task has not started yet
                        End Time:           Task has not ended yet
                                                
                        User:               SamHa
                                                
                        Next tasks:
                        Previous tasks:
                                                
                        -- Possible Next Statuses --
                        - executing
                        - pending
                        Give the start time for the task:
                        Do you want to use system time (0:0)? (y/n)
                        Do you want to use system time (0:0)? (y/n)
                        Cancelled updating task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nSimpleTask\nn\nBACK".getBytes()));
        developerUI.updateTaskStatus();
        assertEquals(
                """
                        *** AVAILABLE TASKS ***
                        Project: SimpleProject --- SimpleTask
                                                
                        *** EXECUTING TASKS ***
                                                
                        Type BACK to cancel updating the task any time
                        Name of the project you want to update:
                        Name of the task you want to update:
                        Task Name:          SimpleTask
                        Description:        Cool description
                        Estimated Duration: 0 hours, 40 minutes
                        Accepted Deviation: 0.1
                        Status:             available
                                                
                        Replacement Task:   No replacement task
                        Replaces Task:      Replaces no tasks
                                                
                        Start Time:         Task has not started yet
                        End Time:           Task has not ended yet
                                                
                        User:               SamHa
                                                
                        Next tasks:
                        Previous tasks:
                                                
                        -- Possible Next Statuses --
                        - executing
                        - pending
                        Give the start time for the task:
                        Do you want to use system time (0:0)? (y/n)
                        Give start hour:
                        Cancelled updating task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nSimpleTask\nn\nsad\nBACK\n".getBytes()));
        developerUI.updateTaskStatus();
        assertEquals(
                """
                        *** AVAILABLE TASKS ***
                        Project: SimpleProject --- SimpleTask
                                                
                        *** EXECUTING TASKS ***
                                                
                        Type BACK to cancel updating the task any time
                        Name of the project you want to update:
                        Name of the task you want to update:
                        Task Name:          SimpleTask
                        Description:        Cool description
                        Estimated Duration: 0 hours, 40 minutes
                        Accepted Deviation: 0.1
                        Status:             available
                                                
                        Replacement Task:   No replacement task
                        Replaces Task:      Replaces no tasks
                                                
                        Start Time:         Task has not started yet
                        End Time:           Task has not ended yet
                                                
                        User:               SamHa
                                                
                        Next tasks:
                        Previous tasks:
                                                
                        -- Possible Next Statuses --
                        - executing
                        - pending
                        Give the start time for the task:
                        Do you want to use system time (0:0)? (y/n)
                        Give start hour:
                        Given start hour is not an integer, please try again
                        Cancelled updating task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nSimpleTask\nn\n3\nBACK\n".getBytes()));
        developerUI.updateTaskStatus();
        assertEquals(
                """
                        *** AVAILABLE TASKS ***
                        Project: SimpleProject --- SimpleTask
                                                
                        *** EXECUTING TASKS ***
                                                
                        Type BACK to cancel updating the task any time
                        Name of the project you want to update:
                        Name of the task you want to update:
                        Task Name:          SimpleTask
                        Description:        Cool description
                        Estimated Duration: 0 hours, 40 minutes
                        Accepted Deviation: 0.1
                        Status:             available
                                                
                        Replacement Task:   No replacement task
                        Replaces Task:      Replaces no tasks
                                                
                        Start Time:         Task has not started yet
                        End Time:           Task has not ended yet
                                                
                        User:               SamHa
                                                
                        Next tasks:
                        Previous tasks:
                                                
                        -- Possible Next Statuses --
                        - executing
                        - pending
                        Give the start time for the task:
                        Do you want to use system time (0:0)? (y/n)
                        Give start hour:
                        Give start minute:
                        Cancelled updating task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nSimpleTask\nn\n2\nsdf\nBACK\n".getBytes()));
        developerUI.updateTaskStatus();
        assertEquals(
                """
                        *** AVAILABLE TASKS ***
                        Project: SimpleProject --- SimpleTask
                                                
                        *** EXECUTING TASKS ***
                                                
                        Type BACK to cancel updating the task any time
                        Name of the project you want to update:
                        Name of the task you want to update:
                        Task Name:          SimpleTask
                        Description:        Cool description
                        Estimated Duration: 0 hours, 40 minutes
                        Accepted Deviation: 0.1
                        Status:             available
                                                
                        Replacement Task:   No replacement task
                        Replaces Task:      Replaces no tasks
                                                
                        Start Time:         Task has not started yet
                        End Time:           Task has not ended yet
                                                
                        User:               SamHa
                                                
                        Next tasks:
                        Previous tasks:
                                                
                        -- Possible Next Statuses --
                        - executing
                        - pending
                        Give the start time for the task:
                        Do you want to use system time (0:0)? (y/n)
                        Give start hour:
                        Give start minute:
                        Given start minute is not an integer, please try again
                        Cancelled updating task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nSimpleTask\nn\n2\n90\nBACK\n".getBytes()));
        developerUI.updateTaskStatus();
        assertEquals(
                """
                        *** AVAILABLE TASKS ***
                        Project: SimpleProject --- SimpleTask
                                                
                        *** EXECUTING TASKS ***
                                                
                        Type BACK to cancel updating the task any time
                        Name of the project you want to update:
                        Name of the task you want to update:
                        Task Name:          SimpleTask
                        Description:        Cool description
                        Estimated Duration: 0 hours, 40 minutes
                        Accepted Deviation: 0.1
                        Status:             available
                                                
                        Replacement Task:   No replacement task
                        Replaces Task:      Replaces no tasks
                                                
                        Start Time:         Task has not started yet
                        End Time:           Task has not ended yet
                                                
                        User:               SamHa
                                                
                        Next tasks:
                        Previous tasks:
                                                
                        -- Possible Next Statuses --
                        - executing
                        - pending
                        Give the start time for the task:
                        Do you want to use system time (0:0)? (y/n)
                        Give start hour:
                        Give start minute:
                        ERROR: the given minute is not of a valid format (0-59), please try again!
                                                
                        Give the start time for the task:
                        Do you want to use system time (0:0)? (y/n)
                        Cancelled updating task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nSimpleTask\nn\n0\n0\nBACK\n".getBytes()));
        developerUI.updateTaskStatus();
        assertEquals(
                """
                        *** AVAILABLE TASKS ***
                        Project: SimpleProject --- SimpleTask
                                                
                        *** EXECUTING TASKS ***
                                                
                        Type BACK to cancel updating the task any time
                        Name of the project you want to update:
                        Name of the task you want to update:
                        Task Name:          SimpleTask
                        Description:        Cool description
                        Estimated Duration: 0 hours, 40 minutes
                        Accepted Deviation: 0.1
                        Status:             available
                                                
                        Replacement Task:   No replacement task
                        Replaces Task:      Replaces no tasks
                                                
                        Start Time:         Task has not started yet
                        End Time:           Task has not ended yet
                                                
                        User:               SamHa
                                                
                        Next tasks:
                        Previous tasks:
                                                
                        -- Possible Next Statuses --
                        - executing
                        - pending
                        Give the start time for the task:
                        Do you want to use system time (0:0)? (y/n)
                        Give start hour:
                        Give start minute:
                        Task SimpleTask successfully updated
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nSimpleTask\nBACK\n".getBytes()));
        developerUI.updateTaskStatus();
        assertEquals(
                """
                        *** AVAILABLE TASKS ***
                                                
                        *** EXECUTING TASKS ***
                        Project: SimpleProject --- SimpleTask
                                                
                        Type BACK to cancel updating the task any time
                        Name of the project you want to update:
                        Name of the task you want to update:
                        Task Name:          SimpleTask
                        Description:        Cool description
                        Estimated Duration: 0 hours, 40 minutes
                        Accepted Deviation: 0.1
                        Status:             executing
                                                
                        Replacement Task:   No replacement task
                        Replaces Task:      Replaces no tasks
                                                
                        Start Time:         0 hours, 0 minutes
                        End Time:           No end time set
                                                
                        User:               SamHa
                                                
                        Next tasks:
                        Previous tasks:
                                                
                        -- Possible Next Statuses --
                        - failed
                        - finished
                        Give the end time for the task:
                        Do you want to use system time (0:0)? (y/n)
                        Cancelled updating task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nSimpleTask\nfdas\nBACK\n".getBytes()));
        developerUI.updateTaskStatus();
        assertEquals(
                """
                        *** AVAILABLE TASKS ***
                                                
                        *** EXECUTING TASKS ***
                        Project: SimpleProject --- SimpleTask
                                                
                        Type BACK to cancel updating the task any time
                        Name of the project you want to update:
                        Name of the task you want to update:
                        Task Name:          SimpleTask
                        Description:        Cool description
                        Estimated Duration: 0 hours, 40 minutes
                        Accepted Deviation: 0.1
                        Status:             executing
                                                
                        Replacement Task:   No replacement task
                        Replaces Task:      Replaces no tasks
                                                
                        Start Time:         0 hours, 0 minutes
                        End Time:           No end time set
                                                
                        User:               SamHa
                                                
                        Next tasks:
                        Previous tasks:
                                                
                        -- Possible Next Statuses --
                        - failed
                        - finished
                        Give the end time for the task:
                        Do you want to use system time (0:0)? (y/n)
                        Do you want to use system time (0:0)? (y/n)
                        Cancelled updating task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nSimpleTask\ny\nBACK\n".getBytes()));
        developerUI.updateTaskStatus();
        assertEquals(
                """
                        *** AVAILABLE TASKS ***
                                                
                        *** EXECUTING TASKS ***
                        Project: SimpleProject --- SimpleTask
                                                
                        Type BACK to cancel updating the task any time
                        Name of the project you want to update:
                        Name of the task you want to update:
                        Task Name:          SimpleTask
                        Description:        Cool description
                        Estimated Duration: 0 hours, 40 minutes
                        Accepted Deviation: 0.1
                        Status:             executing
                                                
                        Replacement Task:   No replacement task
                        Replaces Task:      Replaces no tasks
                                                
                        Start Time:         0 hours, 0 minutes
                        End Time:           No end time set
                                                
                        User:               SamHa
                                                
                        Next tasks:
                        Previous tasks:
                                                
                        -- Possible Next Statuses --
                        - failed
                        - finished
                        Give the end time for the task:
                        Do you want to use system time (0:0)? (y/n)
                        Do you want to finish or fail this task? (finish/fail)
                        Cancelled updating task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nSimpleTask\nn\nBACK\n".getBytes()));
        developerUI.updateTaskStatus();
        assertEquals(
                """
                        *** AVAILABLE TASKS ***
                                                
                        *** EXECUTING TASKS ***
                        Project: SimpleProject --- SimpleTask
                                                
                        Type BACK to cancel updating the task any time
                        Name of the project you want to update:
                        Name of the task you want to update:
                        Task Name:          SimpleTask
                        Description:        Cool description
                        Estimated Duration: 0 hours, 40 minutes
                        Accepted Deviation: 0.1
                        Status:             executing
                                                
                        Replacement Task:   No replacement task
                        Replaces Task:      Replaces no tasks
                                                
                        Start Time:         0 hours, 0 minutes
                        End Time:           No end time set
                                                
                        User:               SamHa
                                                
                        Next tasks:
                        Previous tasks:
                                                
                        -- Possible Next Statuses --
                        - failed
                        - finished
                        Give the end time for the task:
                        Do you want to use system time (0:0)? (y/n)
                        Give end hour:
                        Cancelled updating task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nSimpleTask\nn\nff\nBACK\n".getBytes()));
        developerUI.updateTaskStatus();
        assertEquals(
                """
                        *** AVAILABLE TASKS ***
                                                
                        *** EXECUTING TASKS ***
                        Project: SimpleProject --- SimpleTask
                                                
                        Type BACK to cancel updating the task any time
                        Name of the project you want to update:
                        Name of the task you want to update:
                        Task Name:          SimpleTask
                        Description:        Cool description
                        Estimated Duration: 0 hours, 40 minutes
                        Accepted Deviation: 0.1
                        Status:             executing
                                                
                        Replacement Task:   No replacement task
                        Replaces Task:      Replaces no tasks
                                                
                        Start Time:         0 hours, 0 minutes
                        End Time:           No end time set
                                                
                        User:               SamHa
                                                
                        Next tasks:
                        Previous tasks:
                                                
                        -- Possible Next Statuses --
                        - failed
                        - finished
                        Give the end time for the task:
                        Do you want to use system time (0:0)? (y/n)
                        Give end hour:
                        Given end hour is not an integer, please try again
                        Cancelled updating task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nSimpleTask\nn\n3\nBACK\n".getBytes()));
        developerUI.updateTaskStatus();
        assertEquals(
                """
                        *** AVAILABLE TASKS ***
                                                
                        *** EXECUTING TASKS ***
                        Project: SimpleProject --- SimpleTask
                                                
                        Type BACK to cancel updating the task any time
                        Name of the project you want to update:
                        Name of the task you want to update:
                        Task Name:          SimpleTask
                        Description:        Cool description
                        Estimated Duration: 0 hours, 40 minutes
                        Accepted Deviation: 0.1
                        Status:             executing
                                                
                        Replacement Task:   No replacement task
                        Replaces Task:      Replaces no tasks
                                                
                        Start Time:         0 hours, 0 minutes
                        End Time:           No end time set
                                                
                        User:               SamHa
                                                
                        Next tasks:
                        Previous tasks:
                                                
                        -- Possible Next Statuses --
                        - failed
                        - finished
                        Give the end time for the task:
                        Do you want to use system time (0:0)? (y/n)
                        Give end hour:
                        Give end minute:
                        Cancelled updating task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nSimpleTask\nn\n3\nasdf\n20\nBACK\n".getBytes()));
        developerUI.updateTaskStatus();
        assertEquals(
                """
                        *** AVAILABLE TASKS ***
                                                
                        *** EXECUTING TASKS ***
                        Project: SimpleProject --- SimpleTask
                                                
                        Type BACK to cancel updating the task any time
                        Name of the project you want to update:
                        Name of the task you want to update:
                        Task Name:          SimpleTask
                        Description:        Cool description
                        Estimated Duration: 0 hours, 40 minutes
                        Accepted Deviation: 0.1
                        Status:             executing
                                                
                        Replacement Task:   No replacement task
                        Replaces Task:      Replaces no tasks
                                                
                        Start Time:         0 hours, 0 minutes
                        End Time:           No end time set
                                                
                        User:               SamHa
                                                
                        Next tasks:
                        Previous tasks:
                                                
                        -- Possible Next Statuses --
                        - failed
                        - finished
                        Give the end time for the task:
                        Do you want to use system time (0:0)? (y/n)
                        Give end hour:
                        Give end minute:
                        Given end minute is not an integer, please try again
                        Do you want to finish or fail this task? (finish/fail)
                        Cancelled updating task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nSimpleTask\nn\n3\nasdf\n80\nfinish\nBACK\n".getBytes()));
        developerUI.updateTaskStatus();
        assertEquals(
                """
                        *** AVAILABLE TASKS ***
                                                
                        *** EXECUTING TASKS ***
                        Project: SimpleProject --- SimpleTask
                                                
                        Type BACK to cancel updating the task any time
                        Name of the project you want to update:
                        Name of the task you want to update:
                        Task Name:          SimpleTask
                        Description:        Cool description
                        Estimated Duration: 0 hours, 40 minutes
                        Accepted Deviation: 0.1
                        Status:             executing
                                                
                        Replacement Task:   No replacement task
                        Replaces Task:      Replaces no tasks
                                                
                        Start Time:         0 hours, 0 minutes
                        End Time:           No end time set
                                                
                        User:               SamHa
                                                
                        Next tasks:
                        Previous tasks:
                                                
                        -- Possible Next Statuses --
                        - failed
                        - finished
                        Give the end time for the task:
                        Do you want to use system time (0:0)? (y/n)
                        Give end hour:
                        Give end minute:
                        Given end minute is not an integer, please try again
                        Do you want to finish or fail this task? (finish/fail)
                        ERROR: the given minute is not of a valid format (0-59), please try again!
                                                
                        Give the end time for the task:
                        Do you want to use system time (0:0)? (y/n)
                        Cancelled updating task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        taskManSystem.addTaskToProject("SimpleProject", "NewTask", "Cool description", new Time(40), 0.1, new LinkedList<>(), developer);

        System.setIn(new ByteArrayInputStream("SimpleProject\nNewTask\ny\nfail\n".getBytes()));
        developerUI.updateTaskStatus();
        assertEquals(
                """
                        *** AVAILABLE TASKS ***
                        Project: SimpleProject --- NewTask
                                                
                        *** EXECUTING TASKS ***
                        Project: SimpleProject --- SimpleTask
                                                
                        Type BACK to cancel updating the task any time
                        Name of the project you want to update:
                        Name of the task you want to update:
                        Task Name:          NewTask
                        Description:        Cool description
                        Estimated Duration: 0 hours, 40 minutes
                        Accepted Deviation: 0.1
                        Status:             available
                                                
                        Replacement Task:   No replacement task
                        Replaces Task:      Replaces no tasks
                                                
                        Start Time:         Task has not started yet
                        End Time:           Task has not ended yet
                                                
                        User:               SamHa
                                                
                        Next tasks:
                        Previous tasks:
                                                
                        -- Possible Next Statuses --
                        - executing
                        - pending
                        Give the start time for the task:
                        Do you want to use system time (0:0)? (y/n)
                        Task NewTask successfully updated
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nNewTask\ny\nBACK\n".getBytes()));
        developerUI.updateTaskStatus();
        assertEquals(
                """
                        *** AVAILABLE TASKS ***
                                                
                        *** EXECUTING TASKS ***
                        Project: SimpleProject --- SimpleTask
                        Project: SimpleProject --- NewTask
                                                
                        Type BACK to cancel updating the task any time
                        Name of the project you want to update:
                        Name of the task you want to update:
                        Task Name:          NewTask
                        Description:        Cool description
                        Estimated Duration: 0 hours, 40 minutes
                        Accepted Deviation: 0.1
                        Status:             executing
                                                
                        Replacement Task:   No replacement task
                        Replaces Task:      Replaces no tasks
                                                
                        Start Time:         0 hours, 0 minutes
                        End Time:           No end time set
                                                
                        User:               SamHa
                                                
                        Next tasks:
                        Previous tasks:
                                                
                        -- Possible Next Statuses --
                        - failed
                        - finished
                        Give the end time for the task:
                        Do you want to use system time (0:0)? (y/n)
                        Do you want to finish or fail this task? (finish/fail)
                        Cancelled updating task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nNewTask\nn\n2\n20\nefre\nBACK\n".getBytes()));
        developerUI.updateTaskStatus();
        assertEquals(
                """
                        *** AVAILABLE TASKS ***
                                                
                        *** EXECUTING TASKS ***
                        Project: SimpleProject --- SimpleTask
                        Project: SimpleProject --- NewTask
                                                
                        Type BACK to cancel updating the task any time
                        Name of the project you want to update:
                        Name of the task you want to update:
                        Task Name:          NewTask
                        Description:        Cool description
                        Estimated Duration: 0 hours, 40 minutes
                        Accepted Deviation: 0.1
                        Status:             executing
                                                
                        Replacement Task:   No replacement task
                        Replaces Task:      Replaces no tasks
                                                
                        Start Time:         0 hours, 0 minutes
                        End Time:           No end time set
                                                
                        User:               SamHa
                                                
                        Next tasks:
                        Previous tasks:
                                                
                        -- Possible Next Statuses --
                        - failed
                        - finished
                        Give the end time for the task:
                        Do you want to use system time (0:0)? (y/n)
                        Give end hour:
                        Give end minute:
                        Do you want to finish or fail this task? (finish/fail)
                        Do you want to finish or fail this task? (finish/fail)
                        Cancelled updating task    
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nNewTask\nn\n2\n20\nfail\nBACK\n".getBytes()));
        developerUI.updateTaskStatus();
        assertEquals(
                """
                        *** AVAILABLE TASKS ***
                                                
                        *** EXECUTING TASKS ***
                        Project: SimpleProject --- SimpleTask
                        Project: SimpleProject --- NewTask
                                                
                        Type BACK to cancel updating the task any time
                        Name of the project you want to update:
                        Name of the task you want to update:
                        Task Name:          NewTask
                        Description:        Cool description
                        Estimated Duration: 0 hours, 40 minutes
                        Accepted Deviation: 0.1
                        Status:             executing
                                                
                        Replacement Task:   No replacement task
                        Replaces Task:      Replaces no tasks
                                                
                        Start Time:         0 hours, 0 minutes
                        End Time:           No end time set
                                                
                        User:               SamHa
                                                
                        Next tasks:
                        Previous tasks:
                                                
                        -- Possible Next Statuses --
                        - failed
                        - finished
                        Give the end time for the task:
                        Do you want to use system time (0:0)? (y/n)
                        Give end hour:
                        Give end minute:
                        Do you want to finish or fail this task? (finish/fail)
                        ERROR: the fail time is after the system time
                                                
                        Give the end time for the task:
                        Do you want to use system time (0:0)? (y/n)
                        Cancelled updating task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nNewTask\ny\nfail\n".getBytes()));
        developerUI.updateTaskStatus();
        assertEquals(
                """
                        *** AVAILABLE TASKS ***
                                                
                        *** EXECUTING TASKS ***
                        Project: SimpleProject --- SimpleTask
                        Project: SimpleProject --- NewTask
                                                
                        Type BACK to cancel updating the task any time
                        Name of the project you want to update:
                        Name of the task you want to update:
                        Task Name:          NewTask
                        Description:        Cool description
                        Estimated Duration: 0 hours, 40 minutes
                        Accepted Deviation: 0.1
                        Status:             executing
                                                
                        Replacement Task:   No replacement task
                        Replaces Task:      Replaces no tasks
                                                
                        Start Time:         0 hours, 0 minutes
                        End Time:           No end time set
                                                
                        User:               SamHa
                                                
                        Next tasks:
                        Previous tasks:
                                                
                        -- Possible Next Statuses --
                        - failed
                        - finished
                        Give the end time for the task:
                        Do you want to use system time (0:0)? (y/n)
                        Do you want to finish or fail this task? (finish/fail)
                        Task NewTask successfully updated
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nNewTask\ny\nfail\n".getBytes()));
        developerUI.updateTaskStatus();
        assertEquals(
                """
                        *** AVAILABLE TASKS ***
                                                
                        *** EXECUTING TASKS ***
                        Project: SimpleProject --- SimpleTask
                                                
                        Type BACK to cancel updating the task any time
                        Name of the project you want to update:
                        Name of the task you want to update:
                        Task Name:          NewTask
                        Description:        Cool description
                        Estimated Duration: 0 hours, 40 minutes
                        Accepted Deviation: 0.1
                        Status:             failed
                                                
                        Replacement Task:   No replacement task
                        Replaces Task:      Replaces no tasks
                                                
                        Start Time:         0 hours, 0 minutes
                        End Time:           0 hours, 0 minutes
                                                
                        User:               SamHa
                                                
                        Next tasks:
                        Previous tasks:
                                                
                        -- Possible Next Statuses --
                        ERROR: Task status doesn't allow an update.
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        */

    }
}
