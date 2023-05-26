package Tests.Commands.ProjectCommandsTests;

import Application.Command.ProjectCommands.CreateProjectCommand;
import Domain.DataClasses.InvalidTimeException;
import Domain.DataClasses.Time;
import Domain.Project.ProjectData;
import Domain.Project.ProjectNameAlreadyInUseException;
import Domain.TaskManSystem.DueBeforeSystemTimeException;
import Domain.TaskManSystem.ProjectNotFoundException;
import Domain.TaskManSystem.TaskManSystem;
import Domain.TaskManSystem.TaskManSystemData;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class CreateProjectCommandTest {
    TaskManSystem taskManSystem;

    @Before
    public void setUp() throws InvalidTimeException, DueBeforeSystemTimeException, ProjectNameAlreadyInUseException {
        taskManSystem = new TaskManSystem(new Time(0));
    }

    @Test
    public void testCreationCommand() throws InvalidTimeException {
        CreateProjectCommand command = new CreateProjectCommand(taskManSystem, "Project", "test", new Time(10));

        assertTrue(command.undoPossible());
        assertEquals("Create project", command.getName());
        assertEquals("Create project Project", command.getDetails());
        assertEquals(List.of("projectName", "projectDescription", "dueTime"), command.getArgumentNames());
        assertEquals("Project", command.getArguments().get("projectName"));
        assertEquals("test", command.getArguments().get("projectDescription"));
        assertEquals("0 hour(s), 10 minute(s)", command.getArguments().get("dueTime"));
    }

    @Test
    public void testExecuteAndUndo() throws ProjectNotFoundException, InvalidTimeException, ProjectNameAlreadyInUseException, DueBeforeSystemTimeException {
        CreateProjectCommand command = new CreateProjectCommand(taskManSystem, "Project", "", new Time(0));

        TaskManSystemData taskManSystemData = taskManSystem.getTaskManSystemData();

        // Before the command no projects
        assertTrue(taskManSystemData.getProjectsData().isEmpty());
        command.execute();
        assertEquals(1, taskManSystemData.getProjectsData().size());

        ProjectData project           = taskManSystem.getProjectData("Project");
        assertTrue(taskManSystemData.getProjectsData().contains(project));

        command.undo();
        assertTrue(taskManSystemData.getProjectsData().isEmpty());
        assertFalse(taskManSystemData.getProjectsData().contains(project));
    }
}

