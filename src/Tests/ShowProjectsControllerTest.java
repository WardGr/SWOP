package Tests;

import Application.IncorrectPermissionException;
import Application.Session;
import Application.SessionWrapper;
import Application.ShowProjectsController;
import Domain.*;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Map;

import static org.junit.Assert.*;

public class ShowProjectsControllerTest {

    @Test
    public void test() throws DueBeforeSystemTimeException, ProjectNotFoundException, IncorrectPermissionException, ProjectNameAlreadyInUseException, TaskNotFoundException, TaskNameAlreadyInUseException {
        /*
        Time systemtime = new Time(0);

        Session managerSession = new Session();
        SessionWrapper managerSessionWrapper = new SessionWrapper(managerSession);
        Session developerSession = new Session();
        SessionWrapper developerSessionWrapper = new SessionWrapper(developerSession);
        User manager = new User("DieterVH", "computer776", Role.PROJECTMANAGER);
        User developer = new User("SamHa", "trein123", Role.DEVELOPER);

        managerSession.login(manager);
        developerSession.login(developer);

        TaskManSystem taskManSystem = new TaskManSystem(systemtime);
        taskManSystem.createProject("SimpleProject", "Cool description", systemtime, new Time(100));
        taskManSystem.addTaskToProject("SimpleProject", "SimpleTask", "Cool description", new Time(40), 0.1, new LinkedList<>(), developer);

        ShowProjectsController managerController = new ShowProjectsController(managerSessionWrapper, taskManSystem);
        ShowProjectsController developerController = new ShowProjectsController(developerSessionWrapper, taskManSystem);

        assertTrue(managerController.showProjectsPreconditions());
        assertFalse(developerController.showProjectsPreconditions());

        assertThrows(IncorrectPermissionException.class, () -> developerController.showProject("hoi"));
        assertThrows(ProjectNotFoundException.class, () -> managerController.showProject("HOI"));

        assertNotNull(managerController.showProject("SimpleProject")); // Beetje belachelijk om heel die string hier te hardcoden...

        assertThrows(IncorrectPermissionException.class, () -> developerController.showTask("SimpleProject", "SimpleTask"));
        assertThrows(ProjectNotFoundException.class, () -> managerController.showTask("gwrf", "regtt"));
        assertThrows(TaskNotFoundException.class, () -> managerController.showTask("SimpleProject", "regtt"));

        assertNotNull(managerController.showTask("SimpleProject", "SimpleTask"));

        assertThrows(IncorrectPermissionException.class, developerController::getProjectNamesWithStatus);

        Map<String, String> statuses = managerController.getProjectNamesWithStatus();
        assertEquals("ongoing", statuses.get("SimpleProject"));

        */

    }
}
