package Tests.CommandTests;

import Domain.*;
import Domain.Command.ReplaceTaskCommand;
import Domain.TaskStates.IllegalTaskRolesException;
import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.LoopDependencyGraphException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class ReplaceTaskCommandTest {
    TaskManSystem taskManSystem;
    User user;

    @Before
    public void setUp() throws InvalidTimeException, ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNameAlreadyInUseException, TaskNotFoundException, IllegalTaskRolesException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException, UserAlreadyAssignedToTaskException, IncorrectRoleException, EndTimeBeforeStartTimeException, IncorrectUserException {
        taskManSystem = new TaskManSystem(new Time(0));
        user = new User("TestUser", "", Set.of(Role.SYSADMIN));
        taskManSystem.createProject("Project", "", new Time(10));
        taskManSystem.addTaskToProject("Project", "Task1", "", new Time(5), 0, List.of(Role.SYSADMIN), new HashSet<>(), new HashSet<>());
        taskManSystem.startTask("Project", "Task1", user, Role.SYSADMIN);
        taskManSystem.failTask("Project", "Task1", user);
    }

    @Test
    public void testCreationCommand() throws InvalidTimeException {
        ReplaceTaskCommand command = new ReplaceTaskCommand(taskManSystem, "Project", "Replace", "test", new Time(5), 0.2, "Task1");

        assertTrue(command.undoPossible());
        assertEquals("Replace task", command.getName());
        assertEquals("Replace task Task1 by task Replace in project Project", command.getDetails());
        assertEquals(List.of("projectName", "taskName", "description", "durationTime", "deviation", "replaces"), command.getArgumentNames());
        assertEquals("Project", command.getArguments().get("projectName"));
        assertEquals("Replace", command.getArguments().get("taskName"));
        assertEquals("test", command.getArguments().get("description"));
        assertEquals("0 hour(s), 5 minute(s)", command.getArguments().get("durationTime"));
        assertEquals("0.2", command.getArguments().get("deviation"));
        assertEquals("Task1", command.getArguments().get("replaces"));

        assertEquals("Replace task", command.getCommandData().getName());
    }

    @Test
    public void testExecuteAndUndo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, EndTimeBeforeStartTimeException, IncorrectUserException, IncorrectRoleException, UserAlreadyAssignedToTaskException, TaskNameAlreadyInUseException, InvalidTimeException {
        ReplaceTaskCommand command = new ReplaceTaskCommand(taskManSystem, "Project", "Replace", "test", new Time(5), 0.2, "Task1");

        assertTrue(taskManSystem.getProjectData("Project").getActiveTasksNames().contains("Task1"));
        assertFalse(taskManSystem.getProjectData("Project").getReplacedTasksNames().contains("Task1"));
        assertFalse(taskManSystem.getProjectData("Project").getActiveTasksNames().contains("Replace"));
        assertFalse(taskManSystem.getProjectData("Project").getReplacedTasksNames().contains("Replace"));

        command.execute();

        assertFalse(taskManSystem.getProjectData("Project").getActiveTasksNames().contains("Task1"));
        assertTrue(taskManSystem.getProjectData("Project").getReplacedTasksNames().contains("Task1"));
        assertTrue(taskManSystem.getProjectData("Project").getActiveTasksNames().contains("Replace"));
        assertFalse(taskManSystem.getProjectData("Project").getReplacedTasksNames().contains("Replace"));

        command.undo();

        assertTrue(taskManSystem.getProjectData("Project").getActiveTasksNames().contains("Task1"));
        assertFalse(taskManSystem.getProjectData("Project").getReplacedTasksNames().contains("Task1"));
        assertFalse(taskManSystem.getProjectData("Project").getActiveTasksNames().contains("Replace"));
        assertFalse(taskManSystem.getProjectData("Project").getReplacedTasksNames().contains("Replace"));    }
}


