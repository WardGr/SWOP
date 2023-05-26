package Tests.Commands.UpdateDependenciesCommandsTests;

import Domain.Command.UpdateDependenciesCommands.AddNextTaskCommand;
import Domain.DataClasses.InvalidTimeException;
import Domain.DataClasses.Time;
import Domain.Project.ProjectNameAlreadyInUseException;
import Domain.Project.ProjectNotOngoingException;
import Domain.Project.TaskNotFoundException;
import Domain.Task.*;
import Domain.Task.IncorrectTaskStatusException;
import Domain.Task.LoopDependencyGraphException;
import Domain.TaskManSystem.DueBeforeSystemTimeException;
import Domain.TaskManSystem.ProjectNotFoundException;
import Domain.TaskManSystem.TaskManSystem;
import Domain.User.Role;
import org.junit.Before;
import org.junit.Test;

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
        assertEquals("Add next task", command.getName());
        assertEquals("Add next task (Project2, Task2) to task (Project1, Task1)", command.getDetails());
        assertEquals(List.of("projectName", "taskName", "nextProjectName", "nextTaskName"), command.getArgumentNames());
        assertEquals("Project1", command.getArguments().get("projectName"));
        assertEquals("Task1", command.getArguments().get("taskName"));
        assertEquals("Project2", command.getArguments().get("nextProjectName"));
        assertEquals("Task2", command.getArguments().get("nextTaskName"));
    }

    @Test
    public void testExecuteAndUndo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
        AddNextTaskCommand command = new AddNextTaskCommand(taskManSystem, "Project1", "Task1", "Project2", "Task2");

        TaskData taskData1 = taskManSystem.getTaskData("Project1", "Task1");
        TaskData taskData2 = taskManSystem.getTaskData("Project2", "Task2");

        assertFalse(taskData1.getNextTasksData().contains(taskData2));
        assertFalse(taskData2.getPrevTasksData().contains(taskData1));

        command.execute();
        assertTrue(taskData1.getNextTasksData().contains(taskData2));
        assertTrue(taskData2.getPrevTasksData().contains(taskData1));

        command.undo();
        assertFalse(taskData1.getNextTasksData().contains(taskData2));
        assertFalse(taskData2.getPrevTasksData().contains(taskData1));
    }
}
