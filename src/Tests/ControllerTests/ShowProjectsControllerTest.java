package Tests.ControllerTests;

import Application.IncorrectPermissionException;
import Application.Session.Session;
import Application.Session.SessionProxy;
import Application.ProjectControllers.ShowProjectsController;
import Domain.DataClasses.InvalidTimeException;
import Domain.DataClasses.Time;
import Domain.Project.ProjectNameAlreadyInUseException;
import Domain.Project.ProjectNotOngoingException;
import Domain.Project.TaskNotFoundException;
import Domain.Task.IncorrectTaskStatusException;
import Domain.Task.TaskNameAlreadyInUseException;
import Domain.TaskManSystem.DueBeforeSystemTimeException;
import Domain.TaskManSystem.ProjectNotFoundException;
import Domain.TaskManSystem.TaskManSystem;
import Domain.Task.IllegalTaskRolesException;
import Domain.Task.LoopDependencyGraphException;
import Domain.User.Role;
import Domain.User.User;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class ShowProjectsControllerTest {

    private TaskManSystem taskManSystem;

    private ShowProjectsController managerController;
    private ShowProjectsController developerController;

    @Before
    public void setUp() throws Exception {
        Session managerSession = new Session();
        Session developerSession = new Session();
        User manager = new User("DieterVH", "computer776", Set.of(Role.PROJECTMANAGER));
        User developer = new User("SamHa", "trein123", Set.of(Role.JAVAPROGRAMMER));
        this.taskManSystem = new TaskManSystem(new Time(0));
        this.managerController = new ShowProjectsController(new SessionProxy(managerSession), taskManSystem);
        this.developerController = new ShowProjectsController(new SessionProxy(developerSession), taskManSystem);

        managerSession.login(manager);
        developerSession.login(developer);
        taskManSystem.createProject("SimpleProject", "Cool description", new Time(100));
        taskManSystem.addTaskToProject("SimpleProject", "SimpleTask", "Cool description", new Time(40), 0.1, List.of(Role.JAVAPROGRAMMER), Set.of(), Set.of());
    }

    @Test
    public void testPreconditions() {
        assertTrue(managerController.showProjectsPreconditions());
        assertFalse(developerController.showProjectsPreconditions());
    }

    @Test
    public void testGetProjectData() throws ProjectNotFoundException, IncorrectPermissionException {
        assertEquals("SimpleProject", managerController.getProjectData("SimpleProject").getName());
        assertThrows(IncorrectPermissionException.class, () -> developerController.getProjectData("SimpleProject"));
    }

    @Test
    public void testGetTaskData() throws ProjectNotFoundException, TaskNotFoundException, IncorrectPermissionException {
        assertEquals("SimpleTask", managerController.getTaskData("SimpleProject", "SimpleTask").getName());
        assertThrows(IncorrectPermissionException.class, () -> developerController.getTaskData("SimpleProject", "SimpleTask"));
    }

    @Test
    public void testGetTaskManSystemData() throws IncorrectPermissionException {
        assertThrows(IncorrectPermissionException.class, () -> developerController.getTaskManSystemData());
        assertEquals(taskManSystem, managerController.getTaskManSystemData());
    }
}
