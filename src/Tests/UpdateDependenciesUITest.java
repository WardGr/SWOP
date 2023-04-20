package Tests;

import Application.Session;
import Application.SessionProxy;
import Application.UpdateDependenciesController;
import Domain.*;
import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.IllegalTaskRolesException;
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
    public void test() throws ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, InvalidTimeException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException, IllegalTaskRolesException, UserAlreadyAssignedToTaskException, IncorrectRoleException, NewTimeBeforeSystemTimeException, EndTimeBeforeStartTimeException, IncorrectUserException {
        // Setup test environment

        Time systemtime = new Time(0);

        Session managerSession = new Session();
        SessionProxy managerSessionProxy = new SessionProxy(managerSession);
        Session developerSession = new Session();
        SessionProxy developerSessionProxy = new SessionProxy(developerSession);

        User manager = new User("DieterVH", "computer776", Set.of(Role.PROJECTMANAGER));
        User developer = new User("SamHa", "trein123", Set.of(Role.PYTHONPROGRAMMER));

        managerSession.login(manager);
        developerSession.login(developer);

        TaskManSystem taskManSystem = new TaskManSystem(systemtime);

        UpdateDependenciesController developerController = new UpdateDependenciesController(developerSessionProxy, taskManSystem);
        UpdateDependenciesController managerController = new UpdateDependenciesController(managerSessionProxy, taskManSystem);

        UpdateDependenciesUI developerUI = new UpdateDependenciesUI(developerController);
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
                        
                        Give the name of the project you want to update:
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
                        
                        Give the name of the project you want to update:
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
                        
                        Give the name of the project you want to update:
                        ERROR: The given project name could not be found.
                        You can always use 'BACK' to return to previous menu
                        
                        ***** UNFINISHED PROJECTS *****
                         - Project 1
                        
                        Give the name of the project you want to update:
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
                        
                        Give the name of the project you want to update:
                        ***** (UN)AVAILABLE TASKS *****
                         - Task 1 with status: available
                         - Task 2 with status: available
                        
                        Give the name of the task you want to update:
                        ERROR: Given task name could not be found, try again.
                        ***** (UN)AVAILABLE TASKS *****
                         - Task 1 with status: available
                         - Task 2 with status: available
                        
                        Give the name of the task you want to update:
                        Returning to project menu...
                        You can always use 'BACK' to return to previous menu
                                                
                        ***** UNFINISHED PROJECTS *****
                         - Project 1
                                                
                        Give the name of the project you want to update:
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
                                                
                        Give the name of the project you want to update:
                        ***** (UN)AVAILABLE TASKS *****
                         - Task 1 with status: available
                         - Task 2 with status: available
                                                
                        Give the name of the task you want to update:
                        Previous tasks: There are no previous tasks.
                        Next tasks: There are no next tasks.
                        Possible previous tasks: Task 2
                        Possible next tasks: Task 2
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <taskName>
                        Previous tasks: There are no previous tasks.
                        Next tasks: Task 2
                        Possible previous tasks: There are no possible previous tasks to add.
                        Possible next tasks: There are no possible next tasks to add.
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <taskName>
                        Returning to task menu...
                        ***** (UN)AVAILABLE TASKS *****
                         - Task 1 with status: available
                         - Task 2 with status: unavailable
                                                
                        Give the name of the task you want to update:
                        Returning to project menu...
                        You can always use 'BACK' to return to previous menu
                                                
                        ***** UNFINISHED PROJECTS *****
                         - Project 1
                                                
                        Give the name of the project you want to update:
                        Quiting updating task dependencies
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("Project 1\nTask 1\nremovenext Task 2\nBACK\nBACK\nBACK\n".getBytes()));
        managerUI.updateDependencies();
        assertEquals(
                """
                        You can always use 'BACK' to return to previous menu
                        
                        ***** UNFINISHED PROJECTS *****
                         - Project 1
                        
                        Give the name of the project you want to update:
                        ***** (UN)AVAILABLE TASKS *****
                         - Task 1 with status: available
                         - Task 2 with status: unavailable
                        
                        Give the name of the task you want to update:
                        Previous tasks: There are no previous tasks.
                        Next tasks: Task 2
                        Possible previous tasks: There are no possible previous tasks to add.
                        Possible next tasks: There are no possible next tasks to add.
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <taskName>
                        Previous tasks: There are no previous tasks.
                        Next tasks: There are no next tasks.
                        Possible previous tasks: Task 2
                        Possible next tasks: Task 2
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <taskName>
                        Returning to task menu...
                        ***** (UN)AVAILABLE TASKS *****
                         - Task 1 with status: available
                         - Task 2 with status: available
                        
                        Give the name of the task you want to update:
                        Returning to project menu...
                        You can always use 'BACK' to return to previous menu
                        
                        ***** UNFINISHED PROJECTS *****
                         - Project 1
                        
                        Give the name of the project you want to update:
                        Quiting updating task dependencies
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("Project 1\nTask 2\naddprev Task 1\nBACK\nBACK\nBACK\n".getBytes()));
        managerUI.updateDependencies();
        assertEquals(
                """
                        You can always use 'BACK' to return to previous menu
                        
                        ***** UNFINISHED PROJECTS *****
                         - Project 1
                        
                        Give the name of the project you want to update:
                        ***** (UN)AVAILABLE TASKS *****
                         - Task 1 with status: available
                         - Task 2 with status: available
                        
                        Give the name of the task you want to update:
                        Previous tasks: There are no previous tasks.
                        Next tasks: There are no next tasks.
                        Possible previous tasks: Task 1
                        Possible next tasks: Task 1
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <taskName>
                        Previous tasks: Task 1
                        Next tasks: There are no next tasks.
                        Possible previous tasks: There are no possible previous tasks to add.
                        Possible next tasks: There are no possible next tasks to add.
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <taskName>
                        Returning to task menu...
                        ***** (UN)AVAILABLE TASKS *****
                         - Task 1 with status: available
                         - Task 2 with status: unavailable
                        
                        Give the name of the task you want to update:
                        Returning to project menu...
                        You can always use 'BACK' to return to previous menu
                        
                        ***** UNFINISHED PROJECTS *****
                         - Project 1
                        
                        Give the name of the project you want to update:
                        Quiting updating task dependencies
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("Project 1\nTask 2\naddnext Task 1\naddnext HOI\nremoveprev HOI\nremovenext HOI\nh\nh e\nBACK\nBACK\nBACK\n".getBytes()));
        managerUI.updateDependencies();
        assertEquals(
                """
                        You can always use 'BACK' to return to previous menu
                        
                        ***** UNFINISHED PROJECTS *****
                         - Project 1
                        
                        Give the name of the project you want to update:
                        ***** (UN)AVAILABLE TASKS *****
                         - Task 1 with status: available
                         - Task 2 with status: unavailable
                        
                        Give the name of the task you want to update:
                        Previous tasks: Task 1
                        Next tasks: There are no next tasks.
                        Possible previous tasks: There are no possible previous tasks to add.
                        Possible next tasks: There are no possible next tasks to add.
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <taskName>
                        ERROR: The given task could not safely be added/removed, try again.
                        Previous tasks: Task 1
                        Next tasks: There are no next tasks.
                        Possible previous tasks: There are no possible previous tasks to add.
                        Possible next tasks: There are no possible next tasks to add.
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <taskName>
                        ERROR: The given task could not be found, try again.
                        Previous tasks: Task 1
                        Next tasks: There are no next tasks.
                        Possible previous tasks: There are no possible previous tasks to add.
                        Possible next tasks: There are no possible next tasks to add.
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <taskName>
                        ERROR: Given task name is not present in previous tasks, try again.
                        Previous tasks: Task 1
                        Next tasks: There are no next tasks.
                        Possible previous tasks: There are no possible previous tasks to add.
                        Possible next tasks: There are no possible next tasks to add.
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <taskName>
                        ERROR: Given task name is not present in next tasks, try again.
                        Previous tasks: Task 1
                        Next tasks: There are no next tasks.
                        Possible previous tasks: There are no possible previous tasks to add.
                        Possible next tasks: There are no possible next tasks to add.
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <taskName>
                        ERROR: Unrecognized command, try again.
                        Previous tasks: Task 1
                        Next tasks: There are no next tasks.
                        Possible previous tasks: There are no possible previous tasks to add.
                        Possible next tasks: There are no possible next tasks to add.
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <taskName>
                        ERROR: Unrecognized command, try again.
                        Previous tasks: Task 1
                        Next tasks: There are no next tasks.
                        Possible previous tasks: There are no possible previous tasks to add.
                        Possible next tasks: There are no possible next tasks to add.
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <taskName>
                        Returning to task menu...
                        ***** (UN)AVAILABLE TASKS *****
                         - Task 1 with status: available
                         - Task 2 with status: unavailable
                        
                        Give the name of the task you want to update:
                        Returning to project menu...
                        You can always use 'BACK' to return to previous menu
                        
                        ***** UNFINISHED PROJECTS *****
                         - Project 1
                        
                        Give the name of the project you want to update:
                        Quiting updating task dependencies
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("Project 1\nTask 2\nremoveprev Task 1\nBACK\nBACK\nBACK\n".getBytes()));
        managerUI.updateDependencies();
        assertEquals(
                """
                        You can always use 'BACK' to return to previous menu
                        
                        ***** UNFINISHED PROJECTS *****
                         - Project 1
                        
                        Give the name of the project you want to update:
                        ***** (UN)AVAILABLE TASKS *****
                         - Task 1 with status: available
                         - Task 2 with status: unavailable
                        
                        Give the name of the task you want to update:
                        Previous tasks: Task 1
                        Next tasks: There are no next tasks.
                        Possible previous tasks: There are no possible previous tasks to add.
                        Possible next tasks: There are no possible next tasks to add.
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <taskName>
                        Previous tasks: There are no previous tasks.
                        Next tasks: There are no next tasks.
                        Possible previous tasks: Task 1
                        Possible next tasks: Task 1
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <taskName>
                        Returning to task menu...
                        ***** (UN)AVAILABLE TASKS *****
                         - Task 1 with status: available
                         - Task 2 with status: available
                        
                        Give the name of the task you want to update:
                        Returning to project menu...
                        You can always use 'BACK' to return to previous menu
                        
                        ***** UNFINISHED PROJECTS *****
                         - Project 1
                        
                        Give the name of the project you want to update:
                        Quiting updating task dependencies
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        taskManSystem.startTask("Project 1", "Task 1", developer, Role.PYTHONPROGRAMMER);
        taskManSystem.advanceTime(10);
        taskManSystem.finishTask("Project 1", "Task 1", developer);
        taskManSystem.startTask("Project 1", "Task 2", developer, Role.PYTHONPROGRAMMER);
        taskManSystem.advanceTime(20);

        System.setIn(new ByteArrayInputStream("Project 1\nTask 2\nBACK\nBACK\n".getBytes()));
        managerUI.updateDependencies();
        assertEquals(
                """
                        You can always use 'BACK' to return to previous menu
                                                
                        ***** UNFINISHED PROJECTS *****
                         - Project 1
                                                
                        Give the name of the project you want to update:
                        ***** (UN)AVAILABLE TASKS *****
                        There are no (un)available tasks in this project
                        
                        Give the name of the task you want to update:
                        ERROR: Chosen task is not (un)available
                        ***** (UN)AVAILABLE TASKS *****
                        There are no (un)available tasks in this project
                                                
                        Give the name of the task you want to update:
                        Returning to project menu...
                        You can always use 'BACK' to return to previous menu
                                                
                        ***** UNFINISHED PROJECTS *****
                         - Project 1
                                                
                        Give the name of the project you want to update:
                        Quiting updating task dependencies
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        taskManSystem.finishTask("Project 1", "Task 2", developer);

    }
}
