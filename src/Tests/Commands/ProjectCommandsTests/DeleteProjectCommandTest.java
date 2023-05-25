package Tests.Commands.ProjectCommandsTests;

import Domain.*;
import Domain.Command.DeleteProjectCommand;
import Domain.Command.UndoNotPossibleException;
import Domain.TaskStates.IllegalTaskRolesException;
import Domain.TaskStates.LoopDependencyGraphException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

public class DeleteProjectCommandTest {
    TaskManSystem taskManSystem;

    @Before
    public void setUp() throws InvalidTimeException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNameAlreadyInUseException, TaskNotFoundException, IllegalTaskRolesException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException, ProjectNameAlreadyInUseException {
        taskManSystem = new TaskManSystem(new Time(0));
        taskManSystem.createProject("Project", "", new Time(50));
        taskManSystem.addTaskToProject("Project", "Task", "", new Time(5), 0, List.of(Role.SYSADMIN), new HashSet<>(), new HashSet<>());
    }

    @Test
    public void testCreationCommand(){
        DeleteProjectCommand command = new DeleteProjectCommand(taskManSystem, "Project");

        assertFalse(command.undoPossible());
        assertEquals("Delete project", command.getName());
        assertEquals("Delete project Project", command.getDetails());
        assertEquals(List.of("projectName"), command.getArgumentNames());
        assertEquals("Project", command.getArguments().get("projectName"));

    }

    @Test
    public void testExecuteAndUndo() throws ProjectNotFoundException {
        DeleteProjectCommand command = new DeleteProjectCommand(taskManSystem, "Project");

        TaskManSystemData taskManSystemData = taskManSystem.getTaskManSystemData();
        ProjectData       projectData       = taskManSystem.getProjectData("Project");


        assertTrue(taskManSystemData.getProjectsData().contains(projectData));
        command.execute();

        assertFalse(taskManSystem.getProjectsData().contains(projectData));
        assertThrows(UndoNotPossibleException.class, command::undo);
    }
}
