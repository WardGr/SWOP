package Tests.UITests;

import Application.Session.Session;
import Application.Session.SessionProxy;
import Application.Controllers.TaskControllers.UpdateDependenciesController;
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
import Domain.TaskManSystem.NewTimeBeforeSystemTimeException;
import Domain.TaskManSystem.ProjectNotFoundException;
import Domain.TaskManSystem.TaskManSystem;
import Domain.User.IncorrectUserException;
import Domain.User.Role;
import Domain.User.User;
import Domain.User.UserAlreadyAssignedToTaskException;
import UserInterface.TaskUIs.UpdateDependenciesUI;
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

        CommandManager commandManager = new CommandManager();

        UpdateDependenciesController developerController = new UpdateDependenciesController(developerSessionProxy, taskManSystem, commandManager);
        UpdateDependenciesController managerController = new UpdateDependenciesController(managerSessionProxy, taskManSystem, commandManager);

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
                        ***** TASKS in Project 1 *****
                         - Task 1 --- Status: available
                         - Task 2 --- Status: available
                        
                        Give the name of the task you want to update:
                        ERROR: Given task name could not be found, try again.
                        ***** TASKS in Project 1 *****
                         - Task 1 --- Status: available
                         - Task 2 --- Status: available
                        
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

        System.setIn(new ByteArrayInputStream("Project 1\nTask 1\naddnext Project 1, Task 2\nBACK\nBACK\nBACK\n".getBytes()));
        managerUI.updateDependencies();
        assertEquals(
                """
                        You can always use 'BACK' to return to previous menu
                                                
                        ***** UNFINISHED PROJECTS *****
                         - Project 1
                                                
                        Give the name of the project you want to update:
                        ***** TASKS in Project 1 *****
                         - Task 1 --- Status: available
                         - Task 2 --- Status: available
                                                
                        Give the name of the task you want to update:
                                                
                        Previous tasks: There are no previous tasks.
                        Next tasks: There are no next tasks.
                        Possible tasks to add as previous task:  - Task "Task 2" in project "Project 1"
                        Possible tasks to add as next task:  - Task "Task 2" in project "Project 1"
                                                
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <projectName, taskName>
                                                
                        Previous tasks: There are no previous tasks.
                        Next tasks: Task "Task 2" in project "Project 1"Possible tasks to add as previous task: There are no possible previous tasks to add.
                        Possible tasks to add as next task: There are no possible next tasks to add.
                                                
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <projectName, taskName>
                        Returning to task menu...
                        ***** TASKS in Project 1 *****
                         - Task 1 --- Status: available
                         - Task 2 --- Status: unavailable
                                                
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

        System.setIn(new ByteArrayInputStream("Project 1\nTask 1\nremovenext Project 1, Task 2\nBACK\nBACK\nBACK\n".getBytes()));
        managerUI.updateDependencies();
        assertEquals(
                """
                        You can always use 'BACK' to return to previous menu
                                                
                        ***** UNFINISHED PROJECTS *****
                         - Project 1
                                                
                        Give the name of the project you want to update:
                        ***** TASKS in Project 1 *****
                         - Task 1 --- Status: available
                         - Task 2 --- Status: unavailable
                         
                        Give the name of the task you want to update:
                         
                        Previous tasks: There are no previous tasks.
                        Next tasks: Task "Task 2" in project "Project 1"Possible tasks to add as previous task: There are no possible previous tasks to add.
                        Possible tasks to add as next task: There are no possible next tasks to add.
                         
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <projectName, taskName>
                         
                        Previous tasks: There are no previous tasks.
                        Next tasks: There are no next tasks.
                        Possible tasks to add as previous task:  - Task "Task 2" in project "Project 1"
                        Possible tasks to add as next task:  - Task "Task 2" in project "Project 1"
                         
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <projectName, taskName>
                        Returning to task menu...
                        ***** TASKS in Project 1 *****
                         - Task 1 --- Status: available
                         - Task 2 --- Status: available
                         
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

        System.setIn(new ByteArrayInputStream("Project 1\nTask 2\naddprev Project 1, Task 1\nBACK\nBACK\nBACK\n".getBytes()));
        managerUI.updateDependencies();
        assertEquals(
                """
                        You can always use 'BACK' to return to previous menu
                                                
                        ***** UNFINISHED PROJECTS *****
                         - Project 1
                                                
                        Give the name of the project you want to update:
                        ***** TASKS in Project 1 *****
                         - Task 1 --- Status: available
                         - Task 2 --- Status: available
                                                
                        Give the name of the task you want to update:
                                                
                        Previous tasks: There are no previous tasks.
                        Next tasks: There are no next tasks.
                        Possible tasks to add as previous task:  - Task "Task 1" in project "Project 1"
                        Possible tasks to add as next task:  - Task "Task 1" in project "Project 1"
                                                
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <projectName, taskName>
                                                
                        Previous tasks: Task "Task 1" in project "Project 1"Next tasks: There are no next tasks.
                        Possible tasks to add as previous task: There are no possible previous tasks to add.
                        Possible tasks to add as next task: There are no possible next tasks to add.
                                                
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <projectName, taskName>
                        Returning to task menu...
                        ***** TASKS in Project 1 *****
                         - Task 1 --- Status: available
                         - Task 2 --- Status: unavailable
                                                
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

        System.setIn(new ByteArrayInputStream("Project 1\nTask 2\naddnext Project 1, Task 1\naddnext Project 1, HOI\nremoveprev HOI, Task 1\nremoveprev HOI\nh\nh e, c\nBACK\nBACK\nBACK\n".getBytes()));
        managerUI.updateDependencies();
        assertEquals(
                """
                        You can always use 'BACK' to return to previous menu
                                                
                        ***** UNFINISHED PROJECTS *****
                         - Project 1
                                                
                        Give the name of the project you want to update:
                        ***** TASKS in Project 1 *****
                         - Task 1 --- Status: available
                         - Task 2 --- Status: unavailable
                                                
                        Give the name of the task you want to update:
                                                
                        Previous tasks: Task "Task 1" in project "Project 1"Next tasks: There are no next tasks.
                        Possible tasks to add as previous task: There are no possible previous tasks to add.
                        Possible tasks to add as next task: There are no possible next tasks to add.
                                                
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <projectName, taskName>
                        ERROR: The given task could not safely be added, try again.
                                                
                        Previous tasks: Task "Task 1" in project "Project 1"Next tasks: There are no next tasks.
                        Possible tasks to add as previous task: There are no possible previous tasks to add.
                        Possible tasks to add as next task: There are no possible next tasks to add.
                                                
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <projectName, taskName>
                        ERROR: The given task could not be found, try again.
                                                
                        Previous tasks: Task "Task 1" in project "Project 1"Next tasks: There are no next tasks.
                        Possible tasks to add as previous task: There are no possible previous tasks to add.
                        Possible tasks to add as next task: There are no possible next tasks to add.
                                                
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <projectName, taskName>
                        ERROR: The given task could not be found, try again.
                                                
                        Previous tasks: Task "Task 1" in project "Project 1"Next tasks: There are no next tasks.
                        Possible tasks to add as previous task: There are no possible previous tasks to add.
                        Possible tasks to add as next task: There are no possible next tasks to add.
                                                
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <projectName, taskName>
                                                
                        The given project and task names are not in the correct form, try again.
                                                
                                                
                        Previous tasks: Task "Task 1" in project "Project 1"Next tasks: There are no next tasks.
                        Possible tasks to add as previous task: There are no possible previous tasks to add.
                        Possible tasks to add as next task: There are no possible next tasks to add.
                                                
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <projectName, taskName>
                        ERROR: Unrecognized command, try again.
                                                
                        Previous tasks: Task "Task 1" in project "Project 1"Next tasks: There are no next tasks.
                        Possible tasks to add as previous task: There are no possible previous tasks to add.
                        Possible tasks to add as next task: There are no possible next tasks to add.
                                                
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <projectName, taskName>
                        ERROR: Unrecognized command, try again.
                                                
                        Previous tasks: Task "Task 1" in project "Project 1"Next tasks: There are no next tasks.
                        Possible tasks to add as previous task: There are no possible previous tasks to add.
                        Possible tasks to add as next task: There are no possible next tasks to add.
                                                
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <projectName, taskName>
                        Returning to task menu...
                        ***** TASKS in Project 1 *****
                         - Task 1 --- Status: available
                         - Task 2 --- Status: unavailable
                                                
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

        System.setIn(new ByteArrayInputStream("Project 1\nTask 2\naddnext Project 1, Task 1\nBACK\nBACK\nBACK\n".getBytes()));
        managerUI.updateDependencies();
        assertEquals(
                """
                        You can always use 'BACK' to return to previous menu
                                                
                        ***** UNFINISHED PROJECTS *****
                         - Project 1
                                                
                        Give the name of the project you want to update:
                        ***** TASKS in Project 1 *****
                         - Task 1 --- Status: available
                         - Task 2 --- Status: unavailable
                                                
                        Give the name of the task you want to update:
                                                
                        Previous tasks: Task "Task 1" in project "Project 1"Next tasks: There are no next tasks.
                        Possible tasks to add as previous task: There are no possible previous tasks to add.
                        Possible tasks to add as next task: There are no possible next tasks to add.
                                                
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <projectName, taskName>
                        ERROR: The given task could not safely be added, try again.
                                                
                        Previous tasks: Task "Task 1" in project "Project 1"Next tasks: There are no next tasks.
                        Possible tasks to add as previous task: There are no possible previous tasks to add.
                        Possible tasks to add as next task: There are no possible next tasks to add.
                                                
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <projectName, taskName>
                        Returning to task menu...
                        ***** TASKS in Project 1 *****
                         - Task 1 --- Status: available
                         - Task 2 --- Status: unavailable
                                                
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

        System.setIn(new ByteArrayInputStream("Project 1\nTask 2\nremoveprev Project 1, Task 1\nBACK\nBACK\nBACK\n".getBytes()));
        managerUI.updateDependencies();
        assertEquals(
                """
                        You can always use 'BACK' to return to previous menu
                                                
                        ***** UNFINISHED PROJECTS *****
                         - Project 1
                                                
                        Give the name of the project you want to update:
                        ***** TASKS in Project 1 *****
                         - Task 1 --- Status: available
                         - Task 2 --- Status: unavailable
                                                
                        Give the name of the task you want to update:
                                                
                        Previous tasks: Task "Task 1" in project "Project 1"Next tasks: There are no next tasks.
                        Possible tasks to add as previous task: There are no possible previous tasks to add.
                        Possible tasks to add as next task: There are no possible next tasks to add.
                                                
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <projectName, taskName>
                                                
                        Previous tasks: There are no previous tasks.
                        Next tasks: There are no next tasks.
                        Possible tasks to add as previous task:  - Task "Task 1" in project "Project 1"
                        Possible tasks to add as next task:  - Task "Task 1" in project "Project 1"
                                                
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <projectName, taskName>
                        Returning to task menu...
                        ***** TASKS in Project 1 *****
                         - Task 1 --- Status: available
                         - Task 2 --- Status: available
                                                
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

        System.setIn(new ByteArrayInputStream("Project 1\nTask 2\nremoveprev WRONG, Task 1\nBACK\nBACK\nBACK\n".getBytes()));
        managerUI.updateDependencies();
        assertEquals(
                """
                        You can always use 'BACK' to return to previous menu
                                                
                        ***** UNFINISHED PROJECTS *****
                         - Project 1
                                                
                        Give the name of the project you want to update:
                        ***** TASKS in Project 1 *****
                         - Task 1 --- Status: available
                         - Task 2 --- Status: available
                                                
                        Give the name of the task you want to update:
                                                
                        Previous tasks: There are no previous tasks.
                        Next tasks: There are no next tasks.
                        Possible tasks to add as previous task:  - Task "Task 1" in project "Project 1"
                        Possible tasks to add as next task:  - Task "Task 1" in project "Project 1"
                                                
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <projectName, taskName>
                        ERROR: The given task could not be found, try again.
                                                
                        Previous tasks: There are no previous tasks.
                        Next tasks: There are no next tasks.
                        Possible tasks to add as previous task:  - Task "Task 1" in project "Project 1"
                        Possible tasks to add as next task:  - Task "Task 1" in project "Project 1"
                                                
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <projectName, taskName>
                        Returning to task menu...
                        ***** TASKS in Project 1 *****
                         - Task 1 --- Status: available
                         - Task 2 --- Status: available
                                                
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

        System.setIn(new ByteArrayInputStream("Project 1\nTask 2\nBACK\nBACK\nBACK\n".getBytes()));
        managerUI.updateDependencies();
        assertEquals(
                """
                        You can always use 'BACK' to return to previous menu
                                                
                        ***** UNFINISHED PROJECTS *****
                         - Project 1
                                                
                        Give the name of the project you want to update:
                        ***** TASKS in Project 1 *****
                         - Task 1 --- Status: finished
                         - Task 2 --- Status: executing
                                                
                        Give the name of the task you want to update:
                                                
                        Previous tasks: There are no previous tasks.
                        Next tasks: There are no next tasks.
                        Possible tasks to add as previous task: There are no possible previous tasks to add.
                        Possible tasks to add as next task: There are no possible next tasks to add.
                                                
                        Please put in the desired command:
                           addprev/addnext/removeprev/removenext <projectName, taskName>
                        Returning to task menu...
                        ***** TASKS in Project 1 *****
                         - Task 1 --- Status: finished
                         - Task 2 --- Status: executing
                                                
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
