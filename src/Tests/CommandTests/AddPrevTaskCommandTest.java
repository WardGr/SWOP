package Tests.CommandTests;

import Domain.*;
import Domain.Command.AddPrevTaskCommand;
import Domain.TaskStates.IllegalTaskRolesException;
import Domain.TaskStates.LoopDependencyGraphException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;


public class AddPrevTaskCommandTest {
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
        AddPrevTaskCommand command = new AddPrevTaskCommand(taskManSystem, "Project2", "Task2", "Project1", "Task1");

        assertTrue(command.undoPossible());
        assertEquals("Add previous task", command.getInformation());
        assertEquals("Add previous task (Project1, Task1) to task (Project2, Task2)", command.getExtendedInformation());
        assertEquals(List.of("projectName", "taskName", "previousProjectName", "previousTaskName"), command.getArgumentNames());
        assertEquals("Project2", command.getArguments().get("projectName"));
        assertEquals("Task2", command.getArguments().get("taskName"));
        assertEquals("Project1", command.getArguments().get("previousProjectName"));
        assertEquals("Task1", command.getArguments().get("previousTaskName"));
    }

    @Test
    public void testExecuteAndUndo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
        AddPrevTaskCommand command = new AddPrevTaskCommand(taskManSystem, "Project2", "Task2", "Project1", "Task1");

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
