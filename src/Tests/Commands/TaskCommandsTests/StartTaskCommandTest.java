package Tests.Commands.TaskCommandsTests;

import Application.Command.TaskCommands.StartTaskCommand;
import Domain.DataClasses.EndTimeBeforeStartTimeException;
import Domain.DataClasses.InvalidTimeException;
import Domain.DataClasses.Time;
import Domain.Project.ProjectNameAlreadyInUseException;
import Domain.Project.ProjectNotOngoingException;
import Domain.Project.TaskNotFoundException;
import Domain.Task.*;
import Domain.Task.IncorrectTaskStatusException;
import Domain.Task.LoopDependencyGraphException;
import Domain.Task.Status;
import Domain.TaskManSystem.DueBeforeSystemTimeException;
import Domain.TaskManSystem.ProjectNotFoundException;
import Domain.TaskManSystem.TaskManSystem;
import Domain.User.IncorrectUserException;
import Domain.User.Role;
import Domain.User.User;
import Domain.User.UserAlreadyAssignedToTaskException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class StartTaskCommandTest {
    TaskManSystem taskManSystem;
    User user;

    @Before
    public void setUp() throws InvalidTimeException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNameAlreadyInUseException, TaskNotFoundException, IllegalTaskRolesException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException, ProjectNameAlreadyInUseException, IllegalTaskRolesException, LoopDependencyGraphException, IllegalTaskRolesException, LoopDependencyGraphException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        taskManSystem = new TaskManSystem(new Time(0));
        user = new User("TestUser", "", Set.of(Role.SYSADMIN));
        taskManSystem.createProject("Project", "", new Time(10));
        taskManSystem.addTaskToProject("Project", "Pending", "", new Time(5), 0, List.of(Role.SYSADMIN, Role.SYSADMIN), new HashSet<>(), new HashSet<>());
        taskManSystem.addTaskToProject("Project", "Task1", "", new Time(5), 0, List.of(Role.SYSADMIN), new HashSet<>(), new HashSet<>());

        taskManSystem.startTask("Project", "Pending", user, Role.SYSADMIN);
    }

    @Test
    public void testCreationCommand() throws InvalidTimeException {
        StartTaskCommand command = new StartTaskCommand(taskManSystem, "Project", "Task1", user, Role.SYSADMIN);

        assertTrue(command.undoPossible());
        assertEquals("Start task", command.getName());
        assertEquals("Start task Task1 in project Project with role " + Role.SYSADMIN, command.getDetails());
        assertEquals(List.of("projectName", "taskName", "user", "role"), command.getArgumentNames());
        assertEquals("Project", command.getArguments().get("projectName"));
        assertEquals("Task1", command.getArguments().get("taskName"));
        assertEquals("TestUser", command.getArguments().get("user"));
        assertEquals(Role.SYSADMIN.toString(), command.getArguments().get("role"));

        assertEquals("Start task", command.getCommandData().getName());
    }

    @Test
    public void testExecuteAndUndo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, EndTimeBeforeStartTimeException, IncorrectUserException, IncorrectRoleException, UserAlreadyAssignedToTaskException {
        StartTaskCommand command = new StartTaskCommand(taskManSystem, "Project", "Task1", user, Role.SYSADMIN);

        assertTrue(taskManSystem.getTaskData("Project", "Pending").getUserNamesWithRole().containsKey("TestUser"));
        assertFalse(taskManSystem.getTaskData("Project", "Task1").getUserNamesWithRole().containsKey("TestUser"));
        assertEquals(Status.AVAILABLE, taskManSystem.getTaskData("Project", "Task1").getStatus());

        command.execute();

        assertFalse(taskManSystem.getTaskData("Project", "Pending").getUserNamesWithRole().containsKey("TestUser"));
        assertTrue(taskManSystem.getTaskData("Project", "Task1").getUserNamesWithRole().containsKey("TestUser"));
        assertEquals(2, taskManSystem.getTaskData("Project", "Pending").getUnfulfilledRoles().size());
        assertEquals(Status.EXECUTING, taskManSystem.getTaskData("Project", "Task1").getStatus());

        command.undo();

        assertTrue(taskManSystem.getTaskData("Project", "Pending").getUserNamesWithRole().containsKey("TestUser"));
        assertFalse(taskManSystem.getTaskData("Project", "Task1").getUserNamesWithRole().containsKey("TestUser"));
        assertEquals(Status.AVAILABLE, taskManSystem.getTaskData("Project", "Task1").getStatus());
    }
}

