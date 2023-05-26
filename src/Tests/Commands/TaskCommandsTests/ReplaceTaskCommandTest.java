package Tests.Commands.TaskCommandsTests;

import Domain.Command.TaskCommands.ReplaceTaskCommand;
import Domain.DataClasses.EndTimeBeforeStartTimeException;
import Domain.DataClasses.InvalidTimeException;
import Domain.DataClasses.Time;
import Domain.Project.ProjectData;
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

        TaskData taskData1 = taskManSystem.getTaskData("Project", "Task1");
        ProjectData projectData = taskManSystem.getProjectData("Project");


        assertTrue(projectData.getTasksData().contains(taskData1));
        assertFalse(projectData.getReplacedTasksData().contains(taskData1));
        assertEquals(1, projectData.getTasksData().size());
        assertTrue(projectData.getReplacedTasksData().isEmpty());

        command.execute();
        assertEquals(1, projectData.getReplacedTasksData().size());
        assertEquals(1, projectData.getTasksData().size());

        TaskData taskReplace = taskManSystem.getTaskData("Project", "Replace");

        assertFalse(projectData.getTasksData().contains(taskData1));
        assertTrue(projectData.getReplacedTasksData().contains(taskData1));
        assertTrue(projectData.getTasksData().contains(taskReplace));
        assertFalse(projectData.getReplacedTasksData().contains(taskReplace));

        command.undo();
        assertEquals(1, projectData.getTasksData().size());
        assertTrue(projectData.getReplacedTasksData().isEmpty());

        assertTrue(projectData.getTasksData().contains(taskData1));
        assertFalse(projectData.getReplacedTasksData().contains(taskData1));
        assertFalse(projectData.getTasksData().contains(taskReplace));
        assertFalse(projectData.getReplacedTasksData().contains(taskReplace));
    }
}


