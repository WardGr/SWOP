package Tests.Commands.TaskCommandsTests;

import Domain.Command.TaskCommands.FailTaskCommand;
import Domain.DataClasses.EndTimeBeforeStartTimeException;
import Domain.DataClasses.InvalidTimeException;
import Domain.DataClasses.Time;
import Domain.Project.ProjectNameAlreadyInUseException;
import Domain.Project.ProjectNotOngoingException;
import Domain.Project.TaskNotFoundException;
import Domain.Task.*;
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

public class FailTaskCommandTest {
    TaskManSystem taskManSystem;
    User user;

    @Before
    public void setUp() throws InvalidTimeException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNameAlreadyInUseException, TaskNotFoundException, IllegalTaskRolesException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException, ProjectNameAlreadyInUseException, IllegalTaskRolesException, LoopDependencyGraphException, IllegalTaskRolesException, LoopDependencyGraphException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        taskManSystem = new TaskManSystem(new Time(0));
        user = new User("TestUser", "", Set.of(Role.SYSADMIN));
        taskManSystem.createProject("Project", "", new Time(10));
        taskManSystem.addTaskToProject("Project", "Task1", "", new Time(5), 0, List.of(Role.SYSADMIN), new HashSet<>(), new HashSet<>());
        taskManSystem.startTask("Project", "Task1", user, Role.SYSADMIN);
    }

    @Test
    public void testCreationCommand() throws InvalidTimeException {
        FailTaskCommand command = new FailTaskCommand(taskManSystem, "Project", "Task1", user);

        assertTrue(command.undoPossible());
        assertEquals("Fail task", command.getName());
        assertEquals("Fail task Task1 in project Project", command.getDetails());
        assertEquals(List.of("projectName", "taskName", "user"), command.getArgumentNames());
        assertEquals("Project", command.getArguments().get("projectName"));
        assertEquals("Task1", command.getArguments().get("taskName"));
        assertEquals("TestUser", command.getArguments().get("user"));

        assertEquals("Fail task", command.getCommandData().getName());
    }

    @Test
    public void testExecuteAndUndo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, EndTimeBeforeStartTimeException, IncorrectUserException, IncorrectRoleException, UserAlreadyAssignedToTaskException {
        FailTaskCommand command = new FailTaskCommand(taskManSystem, "Project", "Task1", user);

        assertEquals(Status.EXECUTING, taskManSystem.getTaskData("Project", "Task1").getStatus());

        command.execute();

        assertEquals(Status.FAILED, taskManSystem.getTaskData("Project", "Task1").getStatus());

        command.undo();

        assertEquals(Status.EXECUTING, taskManSystem.getTaskData("Project", "Task1").getStatus());
    }
}

