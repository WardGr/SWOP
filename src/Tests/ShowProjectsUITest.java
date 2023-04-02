package Tests;

import Application.Session;
import Application.SessionWrapper;
import Application.ShowProjectsController;
import Domain.*;
import UserInterface.ShowProjectsUI;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

// TODO: DEZE BOEL FAILT, OMDAT TASKS NU OP EEN ANDERE MANIER FINISHEN, DA MOET NOG GEIMPLEMENTEERD WORDEN
public class ShowProjectsUITest {
    @Test
    public void testShowProjectsUI() throws ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, FailTimeAfterSystemTimeException, IncorrectTaskStatusException, IncorrectUserException, InvalidTimeException, NewTimeBeforeSystemTimeException, ReplacedTaskNotFailedException, StartTimeBeforeAvailableException, EndTimeBeforeStartTimeException {
        Session session = new Session();
        SessionWrapper sessionWrapper = new SessionWrapper(session);
        TaskManSystem tsm = new TaskManSystem(new Time(0));
        User manager = new User("WardGr", "minecraft123", Role.PROJECTMANAGER);
        User dev = new User("OlavBl", "753", Role.DEVELOPER);

        ShowProjectsController showProjectsController = new ShowProjectsController(sessionWrapper, tsm);

        ShowProjectsUI ui = new ShowProjectsUI(showProjectsController);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        session.login(dev);
        out.reset();
        ui.showProjects();
        assertEquals("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function\n".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
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
        tsm.addTaskToProject("Project1", "Task1", "Description1", new Time(100), 5, new LinkedList<>(), dev);
        LinkedList task = new LinkedList<>();
        task.add("Task1");
        tsm.addTaskToProject("Project1", "otherTask", "This is a followup task", new Time(100), 5, task, dev);
        tsm.addTaskToProject("Project2", "Task2", "Description2", new Time(100), 5, new LinkedList<>(), dev);

        out.reset();
        System.setIn(new ByteArrayInputStream("BACK".getBytes()));
        ui.showProjects();
        assertEquals("""
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project2, status: ongoing
                Project1, status: ongoing
                Type the name of a project to see more details:
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("Project2\nBACK\nBACK".getBytes()));
        ui.showProjects();
        assertEquals("""
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project2, status: ongoing
                Project1, status: ongoing
                Type the name of a project to see more details:
                ******** PROJECT DETAILS ********
                Project Name:  Project2
                Description:   Description2
                Creation Time: 0 hours, 0 minutes
                Due Time:      1 hours, 40 minutes
                Status:        ongoing
                                
                Tasks:
                1. Task2
                                
                Type the name of a task to see more details, or type "BACK" to choose another project:
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project2, status: ongoing
                Project1, status: ongoing
                Type the name of a project to see more details:
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("HOI\nBACK\n".getBytes()));
        ui.showProjects();
        assertEquals("""
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project2, status: ongoing
                Project1, status: ongoing
                Type the name of a project to see more details:
                The given project could not be found
                                
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project2, status: ongoing
                Project1, status: ongoing
                Type the name of a project to see more details:
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("Project2\nhoi\nBACK\nBACK\n".getBytes()));
        ui.showProjects();
        assertEquals("""
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project2, status: ongoing
                Project1, status: ongoing
                Type the name of a project to see more details:
                ******** PROJECT DETAILS ********
                Project Name:  Project2
                Description:   Description2
                Creation Time: 0 hours, 0 minutes
                Due Time:      1 hours, 40 minutes
                Status:        ongoing
                                
                Tasks:
                1. Task2
                                
                Type the name of a task to see more details, or type "BACK" to choose another project:
                The given task could not be found, please try again
                
                Type the name of a task to see more details, or type "BACK" to choose another project:
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project2, status: ongoing
                Project1, status: ongoing
                Type the name of a project to see more details:     
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        tsm.startTask("Project2", "Task2", new Time(200), dev);
        tsm.advanceTime(new Time(300));

        // TODO: dit geeft een error omdat tasks nu nimeer automatisch finishen met advanceTime, moeten dus onze tests beetje aanpassen
        tsm.endTask("Project2", "Task2", Status.FINISHED, new Time(1000), dev);
        tsm.advanceTime(new Time(2000));
        System.setIn(new ByteArrayInputStream("Project2\nBACK\nBACK".getBytes()));
        ui.showProjects();
        assertEquals("""
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project2, status: finished
                Project1, status: ongoing
                Type the name of a project to see more details:
                ******** PROJECT DETAILS ********
                Project Name:  Project2
                Description:   Description2
                Creation Time: 0 hours, 0 minutes
                Due Time:      1 hours, 40 minutes
                Status:        finished
                                
                Tasks:
                1. Task2
                                
                Type the name of a task to see more details, or type "BACK" to choose another project:
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project2, status: finished
                Project1, status: ongoing
                Type the name of a project to see more details:
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("Project1\nTask1\notherTask\nBACK\nBACK".getBytes()));
        ui.showProjects();
        assertEquals("""
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project2, status: finished
                Project1, status: ongoing
                Type the name of a project to see more details:
                ******** PROJECT DETAILS ********
                Project Name:  Project1
                Description:   Description1
                Creation Time: 0 hours, 0 minutes
                Due Time:      1 hours, 40 minutes
                Status:        ongoing
                                
                Tasks:
                1. Task1
                2. otherTask
                        
                Type the name of a task to see more details, or type "BACK" to choose another project:
                ******** TASK DETAILS ********
                Task Name:          Task1
                Description:        Description1
                Estimated Duration: 1 hours, 40 minutes
                Accepted Deviation: 5.0
                Status:             available
                        
                Replacement Task:   No replacement task
                Replaces Task:      Replaces no tasks
                        
                Start Time:         Task has not started yet
                End Time:           Task has not ended yet
                        
                User:               OlavBl
                        
                Next tasks:
                1.otherTask
                Previous tasks:
                        
                Type the name of a task to see more details, or type "BACK" to choose another project:
                ******** TASK DETAILS ********
                Task Name:          otherTask
                Description:        This is a followup task
                Estimated Duration: 1 hours, 40 minutes
                Accepted Deviation: 5.0
                Status:             unavailable
                        
                Replacement Task:   No replacement task
                Replaces Task:      Replaces no tasks
                        
                Start Time:         Task has not started yet
                End Time:           Task has not ended yet
                        
                User:               OlavBl
                        
                Next tasks:
                Previous tasks:
                1.Task1
                        
                Type the name of a task to see more details, or type "BACK" to choose another project:
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project2, status: finished
                Project1, status: ongoing
                Type the name of a project to see more details:
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
        System.setIn(new ByteArrayInputStream("Project1\nTask1\notherTask\nBACK\nBACK".getBytes()));
        ui.showProjects();
        assertEquals("""
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project2, status: finished
                Project1, status: ongoing
                Type the name of a project to see more details:
                ******** PROJECT DETAILS ********
                Project Name:  Project1
                Description:   Description1
                Creation Time: 0 hours, 0 minutes
                Due Time:      1 hours, 40 minutes
                Status:        ongoing
                                
                Tasks:
                1. Task1
                2. otherTask
                                
                Type the name of a task to see more details, or type "BACK" to choose another project:
                ******** TASK DETAILS ********
                Task Name:          Task1
                Description:        Description1
                Estimated Duration: 1 hours, 40 minutes
                Accepted Deviation: 5.0
                Status:             available
                                
                Replacement Task:   No replacement task
                Replaces Task:      Replaces no tasks
                                
                Start Time:         Task has not started yet
                End Time:           Task has not ended yet
                                
                User:               OlavBl
                                
                Next tasks:
                1.otherTask
                Previous tasks:
                                
                Type the name of a task to see more details, or type "BACK" to choose another project:
                ******** TASK DETAILS ********
                Task Name:          otherTask
                Description:        This is a followup task
                Estimated Duration: 1 hours, 40 minutes
                Accepted Deviation: 5.0
                Status:             unavailable
                                
                Replacement Task:   No replacement task
                Replaces Task:      Replaces no tasks
                                
                Start Time:         Task has not started yet
                End Time:           Task has not ended yet
                                
                User:               OlavBl
                                
                Next tasks:
                Previous tasks:
                1.Task1
                                
                Type the name of a task to see more details, or type "BACK" to choose another project:
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project2, status: finished
                Project1, status: ongoing
                Type the name of a project to see more details:
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
        tsm.startTask("Project1", "Task1", new Time(88999), dev);
        tsm.advanceTime(new Time(99000));

        System.setIn(new ByteArrayInputStream("Project1\nTask1\notherTask\nBACK\nBACK".getBytes()));
        ui.showProjects();
        assertEquals("""
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project2, status: finished
                Project1, status: ongoing
                Type the name of a project to see more details:
                ******** PROJECT DETAILS ********
                Project Name:  Project1
                Description:   Description1
                Creation Time: 0 hours, 0 minutes
                Due Time:      1 hours, 40 minutes
                Status:        ongoing
                                
                Tasks:
                1. Task1
                2. otherTask
                                
                Type the name of a task to see more details, or type "BACK" to choose another project:
                ******** TASK DETAILS ********
                Task Name:          Task1
                Description:        Description1
                Estimated Duration: 1 hours, 40 minutes
                Accepted Deviation: 5.0
                Status:             executing
                                
                Replacement Task:   No replacement task
                Replaces Task:      Replaces no tasks
                                
                Start Time:         1483 hours, 19 minutes
                End Time:           No end time set
                                
                User:               OlavBl
                                
                Next tasks:
                1.otherTask
                Previous tasks:
                                
                Type the name of a task to see more details, or type "BACK" to choose another project:
                ******** TASK DETAILS ********
                Task Name:          otherTask
                Description:        This is a followup task
                Estimated Duration: 1 hours, 40 minutes
                Accepted Deviation: 5.0
                Status:             unavailable
                                
                Replacement Task:   No replacement task
                Replaces Task:      Replaces no tasks
                                
                Start Time:         Task has not started yet
                End Time:           Task has not ended yet
                                
                User:               OlavBl
                                
                Next tasks:
                Previous tasks:
                1.Task1
                                
                Type the name of a task to see more details, or type "BACK" to choose another project:
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project2, status: finished
                Project1, status: ongoing
                Type the name of a project to see more details:
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("Project1\nTask1\notherTask\nBACK\nBACK".getBytes()));
        tsm.endTask("Project1", "Task1", Status.FINISHED, new Time(89002), dev);
        tsm.advanceTime(new Time(100000));

        System.setIn(new ByteArrayInputStream("Project1\nTask1\notherTask\nBACK\nBACK".getBytes()));
        ui.showProjects();
        assertEquals("""
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project2, status: finished
                Project1, status: ongoing
                Type the name of a project to see more details:
                ******** PROJECT DETAILS ********
                Project Name:  Project1
                Description:   Description1
                Creation Time: 0 hours, 0 minutes
                Due Time:      1 hours, 40 minutes
                Status:        ongoing
                                
                Tasks:
                1. Task1
                2. otherTask
                                
                Type the name of a task to see more details, or type "BACK" to choose another project:
                ******** TASK DETAILS ********
                Task Name:          Task1
                Description:        Description1
                Estimated Duration: 1 hours, 40 minutes
                Accepted Deviation: 5.0
                Status:             finished, on time
                                                   
                Replacement Task:   No replacement task
                Replaces Task:      Replaces no tasks
                                                   
                Start Time:         1483 hours, 19 minutes
                End Time:           1483 hours, 22 minutes
                                
                User:               OlavBl
                                
                Next tasks:
                1.otherTask
                Previous tasks:
                                
                Type the name of a task to see more details, or type "BACK" to choose another project:
                ******** TASK DETAILS ********
                Task Name:          otherTask
                Description:        This is a followup task
                Estimated Duration: 1 hours, 40 minutes
                Accepted Deviation: 5.0
                Status:             available
                                
                Replacement Task:   No replacement task
                Replaces Task:      Replaces no tasks
                                
                Start Time:         Task has not started yet
                End Time:           Task has not ended yet
                                
                User:               OlavBl
                                
                Next tasks:
                Previous tasks:
                1.Task1
                                
                Type the name of a task to see more details, or type "BACK" to choose another project:
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project2, status: finished
                Project1, status: ongoing
                Type the name of a project to see more details:
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        tsm.startTask("Project1", "otherTask", new Time(89003), dev);
        tsm.advanceTime(new Time(110000));
        tsm.endTask("Project1", "otherTask", Status.FAILED, new Time(89004), dev);
        tsm.replaceTaskInProject("Project1", "replaceMentTask", "Task to replace otherTask", new Time(1000), .0001, "otherTask");

        System.setIn(new ByteArrayInputStream("Project1\nTask1\notherTask\nreplaceMentTask\nBACK\nBACK".getBytes()));
        ui.showProjects();
        assertEquals("""
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project2, status: finished
                Project1, status: ongoing
                Type the name of a project to see more details:
                ******** PROJECT DETAILS ********
                Project Name:  Project1
                Description:   Description1
                Creation Time: 0 hours, 0 minutes
                Due Time:      1 hours, 40 minutes
                Status:        ongoing
                                
                Tasks:
                1. Task1
                2. replaceMentTask
                               
                Tasks that have been replaced:
                1. otherTask, replaced by task: replaceMentTask
                                
                Type the name of a task to see more details, or type "BACK" to choose another project:
                ******** TASK DETAILS ********
                Task Name:          Task1
                Description:        Description1
                Estimated Duration: 1 hours, 40 minutes
                Accepted Deviation: 5.0
                Status:             finished, on time
                                                   
                Replacement Task:   No replacement task
                Replaces Task:      Replaces no tasks
                                                   
                Start Time:         1483 hours, 19 minutes
                End Time:           1483 hours, 22 minutes
                                
                User:               OlavBl
                                
                Next tasks:
                1.replaceMentTask
                Previous tasks:
                                
                Type the name of a task to see more details, or type "BACK" to choose another project:
                ******** TASK DETAILS ********
                Task Name:          otherTask
                Description:        This is a followup task
                Estimated Duration: 1 hours, 40 minutes
                Accepted Deviation: 5.0
                Status:             failed
                                
                Replacement Task:   replaceMentTask
                Replaces Task:      Replaces no tasks
                                
                Start Time:         1483 hours, 23 minutes
                End Time:           1483 hours, 24 minutes
                                
                User:               OlavBl
                                
                Next tasks:
                Previous tasks:
                                
                Type the name of a task to see more details, or type "BACK" to choose another project:
                ******** TASK DETAILS ********
                Task Name:          replaceMentTask
                Description:        Task to replace otherTask
                Estimated Duration: 16 hours, 40 minutes
                Accepted Deviation: 1.0E-4
                Status:             available
                                
                Replacement Task:   No replacement task
                Replaces Task:      otherTask
                                
                Start Time:         Task has not started yet
                End Time:           Task has not ended yet
                                
                User:               OlavBl
                                
                Next tasks:
                Previous tasks:
                1.Task1
                                
                Type the name of a task to see more details, or type "BACK" to choose another project:
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project2, status: finished
                Project1, status: ongoing
                Type the name of a project to see more details:
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }
}
