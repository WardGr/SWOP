package Tests.CommandTests;

import Domain.*;
import Domain.Command.AddNextTaskCommand;
import Domain.TaskStates.IllegalTaskRolesException;
import Domain.TaskStates.LoopDependencyGraphException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;


public class AddNextTaskCommandTest {
    TaskManSystem taskManSystem;

    @Before
    public void setUp() throws InvalidTimeException, ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNameAlreadyInUseException, TaskNotFoundException, IllegalTaskRolesException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException {
        taskManSystem = new TaskManSystem(new Time(0));
        taskManSystem.createProject("Project1", "", new Time(60));
        taskManSystem.addTaskToProject("Project1", "Task1", "", new Time(5), 0, List.of(Role.SYSADMIN), new HashSet<>(), new HashSet<>());
        taskManSystem.createProject("Project2", "", new Time(60));
        taskManSystem.addTaskToProject("Project2", "Task2", "", new Time(5), 0, List.of(Role.SYSADMIN), new HashSet<>(), new HashSet<>());
    }

    @Test
    public void testCreationCommand(){
        AddNextTaskCommand command = new AddNextTaskCommand(taskManSystem, "Project1", "Task1", "Project2", "Task2");

        assertTrue(command.undoPossible());
        assertEquals("Add next task", command.getInformation());
        assertEquals("Add next task (Project2, Task2) to task (Project1, Task1)", command.getExtendedInformation());
        assertEquals(List.of("projectName", "taskName", "nextProjectName", "nextTaskName"), command.getArgumentNames());
        assertEquals("Project1", command.getArguments().get("projectName"));
        assertEquals("Task1", command.getArguments().get("taskName"));
        assertEquals("Project2", command.getArguments().get("nextProjectName"));
        assertEquals("Task2", command.getArguments().get("nextTaskName"));
    }

    @Test
    public void testExecuteAndUndo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
        AddNextTaskCommand command = new AddNextTaskCommand(taskManSystem, "Project1", "Task1", "Project2", "Task2");

        assertFalse(taskManSystem.getTaskData("Project1", "Task1").getNextTaskNames().contains(new Tuple<>("Project2", "Task2")));
        assertFalse(taskManSystem.getTaskData("Project2", "Task2").getPrevTaskNames().contains(new Tuple<>("Project1", "Task1")));

        command.execute();
        assertTrue(taskManSystem.getTaskData("Project1", "Task1").getNextTaskNames().contains(new Tuple<>("Project2", "Task2")));
        assertTrue(taskManSystem.getTaskData("Project2", "Task2").getPrevTaskNames().contains(new Tuple<>("Project1", "Task1")));

        command.undo();
        assertFalse(taskManSystem.getTaskData("Project1", "Task1").getNextTaskNames().contains(new Tuple<>("Project2", "Task2")));
        assertFalse(taskManSystem.getTaskData("Project2", "Task2").getPrevTaskNames().contains(new Tuple<>("Project1", "Task1")));
    }
}
