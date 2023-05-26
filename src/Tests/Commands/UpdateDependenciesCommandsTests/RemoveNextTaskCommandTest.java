package Tests.Commands.UpdateDependenciesCommandsTests;

import Domain.Command.UpdateDependenciesCommands.RemoveNextTaskCommand;
import Domain.DataClasses.InvalidTimeException;
import Domain.DataClasses.Time;
import Domain.DataClasses.Tuple;
import Domain.Project.ProjectNameAlreadyInUseException;
import Domain.Project.ProjectNotOngoingException;
import Domain.Project.TaskNotFoundException;
import Domain.Task.*;
import Domain.TaskManSystem.DueBeforeSystemTimeException;
import Domain.TaskManSystem.ProjectNotFoundException;
import Domain.TaskManSystem.TaskManSystem;
import Domain.User.Role;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;


public class RemoveNextTaskCommandTest {
    TaskManSystem taskManSystem;

    @Before
    public void setUp() throws InvalidTimeException, ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNameAlreadyInUseException, TaskNotFoundException, IllegalTaskRolesException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException {
        taskManSystem = new TaskManSystem(new Time(0));
        taskManSystem.createProject("Project1", "", new Time(60));
        taskManSystem.addTaskToProject("Project1", "Task1", "", new Time(5), 0, List.of(Role.SYSADMIN), new HashSet<>(), new HashSet<>());
        taskManSystem.createProject("Project2", "", new Time(60));
        taskManSystem.addTaskToProject("Project2", "Task2", "", new Time(5), 0, List.of(Role.SYSADMIN), Set.of(new Tuple<>("Project1","Task1")), new HashSet<>());
    }

    @Test
    public void testCreationCommand(){
        RemoveNextTaskCommand command = new RemoveNextTaskCommand(taskManSystem, "Project1", "Task1", "Project2", "Task2");

        assertTrue(command.undoPossible());
        assertEquals("Remove next task", command.getName());
        assertEquals("Remove next task (Project2, Task2) from task (Project1, Task1)", command.getDetails());
        assertEquals(List.of("projectName", "taskName", "nextProjectName", "nextTaskName"), command.getArgumentNames());
        assertEquals("Project1", command.getArguments().get("projectName"));
        assertEquals("Task1", command.getArguments().get("taskName"));
        assertEquals("Project2", command.getArguments().get("nextProjectName"));
        assertEquals("Task2", command.getArguments().get("nextTaskName"));
    }

    @Test
    public void testExecuteAndUndo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
        RemoveNextTaskCommand command = new RemoveNextTaskCommand(taskManSystem, "Project1", "Task1", "Project2", "Task2");

        TaskData taskData1 = taskManSystem.getTaskData("Project1", "Task1");
        TaskData taskData2 = taskManSystem.getTaskData("Project2", "Task2");

        assertTrue(taskData1.getNextTasksData().contains(taskData2));
        assertTrue(taskData2.getPrevTasksData().contains(taskData1));

        command.execute();
        assertFalse(taskData1.getNextTasksData().contains(taskData2));
        assertFalse(taskData2.getPrevTasksData().contains(taskData1));

        command.undo();
        assertTrue(taskData1.getNextTasksData().contains(taskData2));
        assertTrue(taskData2.getPrevTasksData().contains(taskData1));
    }
}
