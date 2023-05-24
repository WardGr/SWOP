package Tests.CommandTests;

import Domain.*;
import Domain.Command.CreateTaskCommand;
import Domain.TaskStates.IllegalTaskRolesException;
import Domain.TaskStates.LoopDependencyGraphException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class CreateTaskCommandTest {
    TaskManSystem taskManSystem;

    @Before
    public void setUp() throws InvalidTimeException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNameAlreadyInUseException, TaskNotFoundException, IllegalTaskRolesException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException, ProjectNameAlreadyInUseException, IllegalTaskRolesException, LoopDependencyGraphException {
        taskManSystem = new TaskManSystem(new Time(0));
        taskManSystem.createProject("Project", "", new Time(10));
        taskManSystem.addTaskToProject("Project", "Task1", "", new Time(5), 0, List.of(Role.SYSADMIN), new HashSet<>(), new HashSet<>());
    }

    @Test
    public void testCreationCommand() throws InvalidTimeException {
        CreateTaskCommand command = new CreateTaskCommand(taskManSystem, "Project", "Task", "test", new Time(10), 0.2, List.of(Role.SYSADMIN), Set.of(new Tuple<>("Project", "Task1")), new HashSet<>());

        assertTrue(command.undoPossible());
        assertEquals("Create task", command.getName());
        assertEquals("Create task (Project, Task)", command.getDetails());
        assertEquals(List.of("projectName", "taskName", "description", "durationTime", "deviation", "roles", "previousTasks", "nextTasks"), command.getArgumentNames());
        assertEquals("Project", command.getArguments().get("projectName"));
        assertEquals("Task", command.getArguments().get("taskName"));
        assertEquals("test", command.getArguments().get("description"));
        assertEquals("0 hour(s), 10 minute(s)", command.getArguments().get("durationTime"));
        assertEquals("0.2", command.getArguments().get("deviation"));
        assertEquals("[system administration developer]", command.getArguments().get("roles"));
        assertEquals("[(Project, Task1)]", command.getArguments().get("previousTasks"));
        assertEquals("[]", command.getArguments().get("nextTasks"));

        assertEquals("Create task", command.getCommandData().getName());
    }

    @Test
    public void testExecuteAndUndo() throws ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, IllegalTaskRolesException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException, InvalidTimeException {
        CreateTaskCommand command = new CreateTaskCommand(taskManSystem, "Project", "Task", "test", new Time(10), 0.2, List.of(Role.SYSADMIN), Set.of(new Tuple<>("Project", "Task1")), new HashSet<>());

        assertFalse(taskManSystem.getProjectData("Project").getActiveTasksNames().contains("Task"));

        command.execute();

        assertTrue(taskManSystem.getProjectData("Project").getActiveTasksNames().contains("Task"));

        command.undo();

        assertFalse(taskManSystem.getProjectData("Project").getActiveTasksNames().contains("Task"));
    }
}

