package Tests.CommandTests;

import Domain.*;
import Domain.Command.DeleteTaskCommand;
import Domain.Command.UndoNotPossibleException;
import Domain.TaskStates.IllegalTaskRolesException;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.TaskData;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

public class DeleteTaskCommandTest {
    TaskManSystem taskManSystem;

    @Before
    public void setUp() throws InvalidTimeException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNameAlreadyInUseException, TaskNotFoundException, IllegalTaskRolesException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException, ProjectNameAlreadyInUseException {
        taskManSystem = new TaskManSystem(new Time(0));
        taskManSystem.createProject("Project", "", new Time(50));
        taskManSystem.addTaskToProject("Project", "Task", "", new Time(5), 0, List.of(Role.SYSADMIN), new HashSet<>(), new HashSet<>());
    }

    @Test
    public void testCreationCommand(){
        DeleteTaskCommand command = new DeleteTaskCommand(taskManSystem, "Project", "Task");

        assertFalse(command.undoPossible());
        assertEquals("Delete task", command.getName());
        assertEquals("Delete task (Project, Task)", command.getDetails());
        assertEquals(List.of("projectName", "taskName"), command.getArgumentNames());
        assertEquals("Project", command.getArguments().get("projectName"));
        assertEquals("Task", command.getArguments().get("taskName"));

    }

    @Test
    public void testExecuteAndUndo() throws ProjectNotFoundException, TaskNotFoundException {
        DeleteTaskCommand command = new DeleteTaskCommand(taskManSystem, "Project", "Task");

        ProjectData       projectData       = taskManSystem.getProjectData("Project");
        TaskData          taskData          = taskManSystem.getTaskData("Project", "Task");

        assertTrue(projectData.getTasksData().contains(taskData));
        command.execute();

        assertFalse(projectData.getTasksData().contains(taskData));
        assertThrows(UndoNotPossibleException.class, command::undo);
    }
}
