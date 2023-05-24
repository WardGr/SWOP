package Tests.CommandTests;

import Domain.*;
import Domain.Command.FinishTaskCommand;
import Domain.TaskStates.IllegalTaskRolesException;
import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.LoopDependencyGraphException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FinishTaskCommandTest {
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
        FinishTaskCommand command = new FinishTaskCommand(taskManSystem, "Project", "Task1", user);

        assertTrue(command.undoPossible());
        assertEquals("Finish task", command.getName());
        assertEquals("Finish task Task1 in project Project", command.getDetails());
        assertEquals(List.of("projectName", "taskName", "user"), command.getArgumentNames());
        assertEquals("Project", command.getArguments().get("projectName"));
        assertEquals("Task1", command.getArguments().get("taskName"));
        assertEquals("TestUser", command.getArguments().get("user"));

        assertEquals("Finish task", command.getCommandData().getName());
    }

    @Test
    public void testExecuteAndUndo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, EndTimeBeforeStartTimeException, IncorrectUserException, IncorrectRoleException, UserAlreadyAssignedToTaskException {
        FinishTaskCommand command = new FinishTaskCommand(taskManSystem, "Project", "Task1", user);

        assertEquals(Status.EXECUTING, taskManSystem.getTaskData("Project", "Task1").getStatus());

        command.execute();

        assertEquals(Status.FINISHED, taskManSystem.getTaskData("Project", "Task1").getStatus());

        command.undo();

        assertEquals(Status.EXECUTING, taskManSystem.getTaskData("Project", "Task1").getStatus());
    }
}

