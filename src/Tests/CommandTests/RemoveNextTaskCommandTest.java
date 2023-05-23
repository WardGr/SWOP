package Tests.CommandTests;

import Domain.*;
import Domain.Command.RemoveNextTaskCommand;
import Domain.TaskStates.IllegalTaskRolesException;
import Domain.TaskStates.LoopDependencyGraphException;
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
        assertEquals("Remove next task", command.getInformation());
        assertEquals("Remove next task (Project2, Task2) from task (Project1, Task1)", command.getExtendedInformation());
        assertEquals(List.of("projectName", "taskName", "nextProjectName", "nextTaskName"), command.getArgumentNames());
        assertEquals("Project1", command.getArguments().get("projectName"));
        assertEquals("Task1", command.getArguments().get("taskName"));
        assertEquals("Project2", command.getArguments().get("nextProjectName"));
        assertEquals("Task2", command.getArguments().get("nextTaskName"));
    }

    @Test
    public void testExecuteAndUndo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
        RemoveNextTaskCommand command = new RemoveNextTaskCommand(taskManSystem, "Project1", "Task1", "Project2", "Task2");

        assertTrue(taskManSystem.getTaskData("Project1", "Task1").getNextTaskNames().contains(new Tuple<>("Project2", "Task2")));
        assertTrue(taskManSystem.getTaskData("Project2", "Task2").getPrevTaskNames().contains(new Tuple<>("Project1", "Task1")));

        command.execute();
        assertFalse(taskManSystem.getTaskData("Project1", "Task1").getNextTaskNames().contains(new Tuple<>("Project2", "Task2")));
        assertFalse(taskManSystem.getTaskData("Project2", "Task2").getPrevTaskNames().contains(new Tuple<>("Project1", "Task1")));

        command.undo();
        assertTrue(taskManSystem.getTaskData("Project1", "Task1").getNextTaskNames().contains(new Tuple<>("Project2", "Task2")));
        assertTrue(taskManSystem.getTaskData("Project2", "Task2").getPrevTaskNames().contains(new Tuple<>("Project1", "Task1")));
    }
}
