package Tests.ControllerTests;

import Application.Command.CommandManager;
import Application.IncorrectPermissionException;
import Application.Controllers.ProjectControllers.DeleteProjectController;
import Application.Session.Session;
import Application.Session.SessionProxy;
import Domain.Project.ProjectData;
import Domain.TaskManSystem.TaskManSystem;
import Domain.User.Role;
import Domain.User.User;
import Domain.DataClasses.Time;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class DeleteProjectControllerTest {

    private Session session;
    private User incorrect;
    private DeleteProjectController deleteProjectController;

    @Before
    public void setUp() throws Exception {
        this.session = new Session();
        User projectManager = new User("DieterVH", "computer776", Set.of(Role.PROJECTMANAGER));
        this.incorrect = new User("OlavBl", "peer123", Set.of(Role.PYTHONPROGRAMMER));
        TaskManSystem taskManSystem = new TaskManSystem(new Time(0));
        this.deleteProjectController = new DeleteProjectController(new SessionProxy(session), taskManSystem, new CommandManager());
        session.login(projectManager);

        taskManSystem.createProject("Omer", "Brew omer beer", new Time(30));
        taskManSystem.createProject("Duvel", "Brew duvel beer", new Time(30));
    }

    @Test
    public void testPreconditions() {
        assertTrue(deleteProjectController.deleteProjectPreconditions());
        session.logout();
        assertFalse(deleteProjectController.deleteProjectPreconditions());
        session.login(incorrect);
        assertFalse(deleteProjectController.deleteProjectPreconditions());
    }

    @Test
    public void testIncorrectPermissions() {
        session.logout();
        assertFalse(deleteProjectController.deleteProjectPreconditions());
        assertThrows(IncorrectPermissionException.class, () -> deleteProjectController.deleteProject("Omer"));
        assertThrows(IncorrectPermissionException.class, () -> deleteProjectController.getTaskManSystemData());
        assertThrows(IncorrectPermissionException.class, () -> deleteProjectController.getProjectData("Omer"));

        session.login(incorrect);
        assertFalse(deleteProjectController.deleteProjectPreconditions());
        assertThrows(IncorrectPermissionException.class, () -> deleteProjectController.deleteProject("Omer"));
        assertThrows(IncorrectPermissionException.class, () -> deleteProjectController.getTaskManSystemData());
        assertThrows(IncorrectPermissionException.class, () -> deleteProjectController.getProjectData("Omer"));
    }

    @Test
    public void testGetters() throws Exception {
        assertTrue(deleteProjectController.getTaskManSystemData().getProjectsData().stream().map(ProjectData::getName).toList().contains("Omer"));
        assertEquals("Omer", deleteProjectController.getProjectData("Omer").getName());

        assertTrue(deleteProjectController.getTaskManSystemData().getProjectsData().stream().map(ProjectData::getName).toList().contains("Duvel"));
        assertEquals("Duvel", deleteProjectController.getProjectData("Duvel").getName());

        assertEquals(2, deleteProjectController.getTaskManSystemData().getProjectsData().size());
    }

    @Test
    public void testDeleteProject() throws Exception {
        deleteProjectController.deleteProject("Omer");
        assertFalse(deleteProjectController.getTaskManSystemData().getProjectsData().stream().map(ProjectData::getName).toList().contains("Omer"));
        assertEquals(1, deleteProjectController.getTaskManSystemData().getProjectsData().size());

        deleteProjectController.deleteProject("Duvel");
        assertFalse(deleteProjectController.getTaskManSystemData().getProjectsData().stream().map(ProjectData::getName).toList().contains("Duvel"));
        assertEquals(0, deleteProjectController.getTaskManSystemData().getProjectsData().size());
    }
}
