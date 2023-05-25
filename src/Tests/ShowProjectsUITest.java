package Tests;

import Application.Session;
import Application.SessionProxy;
import Application.ShowProjectsController;
import Domain.*;
import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.IllegalTaskRolesException;
import UserInterface.ShowProjectsUI;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ShowProjectsUITest {
    @Test
    public void testShowProjectsUI() throws ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, IncorrectTaskStatusException, IncorrectUserException, InvalidTimeException, NewTimeBeforeSystemTimeException, EndTimeBeforeStartTimeException, ProjectNotOngoingException, LoopDependencyGraphException, IllegalTaskRolesException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        Session session = new Session();
        SessionProxy sessionProxy = new SessionProxy(session);
        TaskManSystem tsm = new TaskManSystem(new Time(0));
        User manager = new User("WardGr", "minecraft123", Set.of(Role.PROJECTMANAGER));
        User dev = new User("OlavBl", "753", Set.of(Role.PYTHONPROGRAMMER));

        ShowProjectsController showProjectsController = new ShowProjectsController(sessionProxy, tsm);

        ShowProjectsUI ui = new ShowProjectsUI(showProjectsController);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        session.login(dev);
        out.reset();
        ui.showProjects();
        assertEquals("""
                        You must be logged in with the project manager role to call this function\r
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        session.logout();
        session.login(manager);
        out.reset();
        System.setIn(new ByteArrayInputStream("BACK".getBytes()));
        ui.showProjects();
        assertEquals("""
                        Type "BACK" to cancel
                        ********* PROJECTS *********
                        Type the name of a project to see more details:
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        tsm.createProject("Project1", "Description1", new Time(100));
        tsm.createProject("Project2", "Description2", new Time(100));

        System.setIn(new ByteArrayInputStream("Project2\nBACK\nBACK".getBytes()));
        ui.showProjects();
        assertEquals("""
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project1, status: ongoing
                Project2, status: ongoing
                Type the name of a project to see more details:
                ******** PROJECT DETAILS ********
                Project Name:  Project2
                Description:   Description2
                Creation Time: 0 hour(s), 0 minute(s)
                Due Time:      1 hour(s), 40 minute(s)
                Status:        ongoing
                                
                Tasks:
                There are no active tasks attached to this project.
                
                Replaced Tasks:
                There are no tasks replaced in this project.
                                
                Type the name of a task to see more details, or type "BACK" to choose another project:
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project1, status: ongoing
                Project2, status: ongoing
                Type the name of a project to see more details:
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        tsm.addTaskToProject("Project1", "Task1", "Description1", new Time(100), 5, List.of(Role.PYTHONPROGRAMMER), new HashSet<>(), new HashSet<>());
        tsm.addTaskToProject("Project1", "otherTask", "This is a followup task", new Time(100), 5, List.of(Role.PYTHONPROGRAMMER) , Set.of(new Tuple<>("Project1","Task1")), new HashSet<>());
        tsm.addTaskToProject("Project2", "Task2", "Description2", new Time(100), 5, List.of(Role.PYTHONPROGRAMMER), new HashSet<>(), new HashSet<>());

        out.reset();
        System.setIn(new ByteArrayInputStream("BACK".getBytes()));
        ui.showProjects();
        assertEquals("""
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project1, status: ongoing
                Project2, status: ongoing
                Type the name of a project to see more details:
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("Project2\nBACK\nBACK".getBytes()));
        ui.showProjects();
        assertEquals("""
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project1, status: ongoing
                Project2, status: ongoing
                Type the name of a project to see more details:
                ******** PROJECT DETAILS ********
                Project Name:  Project2
                Description:   Description2
                Creation Time: 0 hour(s), 0 minute(s)
                Due Time:      1 hour(s), 40 minute(s)
                Status:        ongoing
                                
                Tasks:
                1. Task2
                
                Replaced Tasks:
                There are no tasks replaced in this project.
                                
                Type the name of a task to see more details, or type "BACK" to choose another project:
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project1, status: ongoing
                Project2, status: ongoing
                Type the name of a project to see more details:
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("HOI\nBACK\n".getBytes()));
        ui.showProjects();
        assertEquals("""
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project1, status: ongoing
                Project2, status: ongoing
                Type the name of a project to see more details:
                The given project could not be found
                                
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project1, status: ongoing
                Project2, status: ongoing
                Type the name of a project to see more details:
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        tsm.startTask("Project2", "Task2", dev, Role.PYTHONPROGRAMMER);
        tsm.advanceTime(5);
        tsm.failTask("Project2", "Task2", dev);
        tsm.replaceTaskInProject("Project2", "Task2 Replacement", "replaces Task 2", new Time(5), 0.2, "Task2");

        // Test Replacement Tasks in Project
        System.setIn(new ByteArrayInputStream("Project2\nBACK\nBACK".getBytes()));
        ui.showProjects();
        assertEquals("""
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project1, status: ongoing
                Project2, status: ongoing
                Type the name of a project to see more details:
                ******** PROJECT DETAILS ********
                Project Name:  Project2
                Description:   Description2
                Creation Time: 0 hour(s), 0 minute(s)
                Due Time:      1 hour(s), 40 minute(s)
                Status:        ongoing
                                
                Tasks:
                1. Task2 Replacement
                
                Replaced Tasks:
                1. Task2 - Replaced by: Task2 Replacement
                                
                Type the name of a task to see more details, or type "BACK" to choose another project:
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project1, status: ongoing
                Project2, status: ongoing
                Type the name of a project to see more details:
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("Project2\nhoi\nBACK\nBACK\n".getBytes()));
        ui.showProjects();
        assertEquals("""
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project1, status: ongoing
                Project2, status: ongoing
                Type the name of a project to see more details:
                ******** PROJECT DETAILS ********
                Project Name:  Project2
                Description:   Description2
                Creation Time: 0 hour(s), 0 minute(s)
                Due Time:      1 hour(s), 40 minute(s)
                Status:        ongoing
                
                Tasks:
                1. Task2 Replacement
                                
                Replaced Tasks:
                1. Task2 - Replaced by: Task2 Replacement
                                
                Type the name of a task to see more details, or type "BACK" to choose another project:
                The given task could not be found, please try again
                                
                Type the name of a task to see more details, or type "BACK" to choose another project:
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project1, status: ongoing
                Project2, status: ongoing
                Type the name of a project to see more details:
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("Project1\nTask1\notherTask\nBACK\nBACK".getBytes()));
        ui.showProjects();
        assertEquals("""
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project1, status: ongoing
                Project2, status: ongoing
                Type the name of a project to see more details:
                ******** PROJECT DETAILS ********
                Project Name:  Project1
                Description:   Description1
                Creation Time: 0 hour(s), 0 minute(s)
                Due Time:      1 hour(s), 40 minute(s)
                Status:        ongoing
                                
                Tasks:
                1. Task1
                2. otherTask
                                
                Replaced Tasks:
                There are no tasks replaced in this project.
                        
                Type the name of a task to see more details, or type "BACK" to choose another project:
                ******** TASK DETAILS ********
                Task Name:            Task1
                Belonging to project: Project1
                Description:          Description1
                Estimated Duration:   1 hour(s), 40 minute(s)
                Accepted Deviation:   5.0
                Status:               available
                        
                Replacement Task:   No replacement task
                Replaces Task:      Replaces no tasks
                        
                Start Time:         Task has not started yet
                End Time:           Task has not ended yet
                        
                Unfulfilled roles:
                - Python programmer
                                
                Committed users:
                No users are committed to this task.
                        
                Next tasks:
                1. otherTask --- Belonging to project: Project1
                Previous tasks:
                - There are no previous tasks
                        
                Type the name of a task to see more details, or type "BACK" to choose another project:
                ******** TASK DETAILS ********
                Task Name:            otherTask
                Belonging to project: Project1
                Description:          This is a followup task
                Estimated Duration:   1 hour(s), 40 minute(s)
                Accepted Deviation:   5.0
                Status:               unavailable
                        
                Replacement Task:   No replacement task
                Replaces Task:      Replaces no tasks
                        
                Start Time:         Task has not started yet
                End Time:           Task has not ended yet
                        
                Unfulfilled roles:
                - Python programmer
                                
                Committed users:
                No users are committed to this task.
                        
                Next tasks:
                - There are no next tasks
                Previous tasks:
                1. Task1 --- Belonging to project: Project1
                        
                Type the name of a task to see more details, or type "BACK" to choose another project:
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project1, status: ongoing
                Project2, status: ongoing
                Type the name of a project to see more details:
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("Project2\nTask2\nTask2 Replacement\nBACK\nBACK".getBytes()));
        ui.showProjects();
        assertEquals("""
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project1, status: ongoing
                Project2, status: ongoing
                Type the name of a project to see more details:
                ******** PROJECT DETAILS ********
                Project Name:  Project2
                Description:   Description2
                Creation Time: 0 hour(s), 0 minute(s)
                Due Time:      1 hour(s), 40 minute(s)
                Status:        ongoing
                                
                Tasks:
                1. Task2 Replacement
                                
                Replaced Tasks:
                1. Task2 - Replaced by: Task2 Replacement
                        
                Type the name of a task to see more details, or type "BACK" to choose another project:
                ******** TASK DETAILS ********
                Task Name:            Task2
                Belonging to project: Project2
                Description:          Description2
                Estimated Duration:   1 hour(s), 40 minute(s)
                Accepted Deviation:   5.0
                Status:               failed
                        
                Replacement Task:   Task2 Replacement
                Replaces Task:      Replaces no tasks
                        
                Start Time:         0 hour(s), 0 minute(s)
                End Time:           0 hour(s), 5 minute(s)
                        
                Unfulfilled roles:
                All roles are fulfilled.
                                
                Committed users:
                - OlavBl as Python programmer
                        
                Next tasks:
                - There are no next tasks
                Previous tasks:
                - There are no previous tasks
                        
                Type the name of a task to see more details, or type "BACK" to choose another project:
                ******** TASK DETAILS ********
                Task Name:            Task2 Replacement
                Belonging to project: Project2
                Description:          replaces Task 2
                Estimated Duration:   0 hour(s), 5 minute(s)
                Accepted Deviation:   0.2
                Status:               available
                        
                Replacement Task:   No replacement task
                Replaces Task:      Task2
                        
                Start Time:         Task has not started yet
                End Time:           Task has not ended yet
                        
                Unfulfilled roles:
                - Python programmer
                                
                Committed users:
                No users are committed to this task.
                        
                Next tasks:
                - There are no next tasks
                Previous tasks:
                - There are no previous tasks
                        
                Type the name of a task to see more details, or type "BACK" to choose another project:
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project1, status: ongoing
                Project2, status: ongoing
                Type the name of a project to see more details:
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        tsm.startTask("Project2", "Task2 Replacement", dev, Role.PYTHONPROGRAMMER);

        System.setIn(new ByteArrayInputStream("Project2\nTask2 Replacement\nBACK\nBACK".getBytes()));
        ui.showProjects();
        assertEquals("""
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project1, status: ongoing
                Project2, status: ongoing
                Type the name of a project to see more details:
                ******** PROJECT DETAILS ********
                Project Name:  Project2
                Description:   Description2
                Creation Time: 0 hour(s), 0 minute(s)
                Due Time:      1 hour(s), 40 minute(s)
                Status:        ongoing
                                
                Tasks:
                1. Task2 Replacement
                                
                Replaced Tasks:
                1. Task2 - Replaced by: Task2 Replacement
                
                Type the name of a task to see more details, or type "BACK" to choose another project:
                ******** TASK DETAILS ********
                Task Name:            Task2 Replacement
                Belonging to project: Project2
                Description:          replaces Task 2
                Estimated Duration:   0 hour(s), 5 minute(s)
                Accepted Deviation:   0.2
                Status:               executing
                        
                Replacement Task:   No replacement task
                Replaces Task:      Task2
                        
                Start Time:         0 hour(s), 5 minute(s)
                End Time:           Task has not ended yet
                        
                Unfulfilled roles:
                All roles are fulfilled.
                                
                Committed users:
                - OlavBl as Python programmer
                        
                Next tasks:
                - There are no next tasks
                Previous tasks:
                - There are no previous tasks
                        
                Type the name of a task to see more details, or type "BACK" to choose another project:
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project1, status: ongoing
                Project2, status: ongoing
                Type the name of a project to see more details:
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        tsm.advanceTime(15);
        tsm.finishTask("Project2", "Task2 Replacement", dev);

        System.setIn(new ByteArrayInputStream("Project2\nTask2 Replacement\nBACK\nBACK".getBytes()));
        ui.showProjects();
        assertEquals("""
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project1, status: ongoing
                Project2, status: finished
                Type the name of a project to see more details:
                ******** PROJECT DETAILS ********
                Project Name:  Project2
                Description:   Description2
                Creation Time: 0 hour(s), 0 minute(s)
                Due Time:      1 hour(s), 40 minute(s)
                Status:        finished
                                
                Tasks:
                1. Task2 Replacement
                                
                Replaced Tasks:
                1. Task2 - Replaced by: Task2 Replacement
                
                Type the name of a task to see more details, or type "BACK" to choose another project:
                ******** TASK DETAILS ********
                Task Name:            Task2 Replacement
                Belonging to project: Project2
                Description:          replaces Task 2
                Estimated Duration:   0 hour(s), 5 minute(s)
                Accepted Deviation:   0.2
                Status:               finished
                   Finished:          delayed
                        
                Replacement Task:   No replacement task
                Replaces Task:      Task2
                        
                Start Time:         0 hour(s), 5 minute(s)
                End Time:           0 hour(s), 20 minute(s)
                        
                Unfulfilled roles:
                All roles are fulfilled.
                                
                Committed users:
                - OlavBl as Python programmer
                        
                Next tasks:
                - There are no next tasks
                Previous tasks:
                - There are no previous tasks
                        
                Type the name of a task to see more details, or type "BACK" to choose another project:
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project1, status: ongoing
                Project2, status: finished
                Type the name of a project to see more details:
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

    }
}
