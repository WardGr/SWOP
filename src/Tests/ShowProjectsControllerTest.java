package Tests;

import Application.IncorrectPermissionException;
import Application.Session;
import Application.SessionProxy;
import Application.ShowProjectsController;
import Domain.*;
import Domain.TaskStates.IllegalTaskRolesException;
import Domain.TaskStates.LoopDependencyGraphException;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class ShowProjectsControllerTest {

    @Test
    public void test() throws DueBeforeSystemTimeException, ProjectNotFoundException, IncorrectPermissionException, ProjectNameAlreadyInUseException, TaskNotFoundException, TaskNameAlreadyInUseException, InvalidTimeException, IllegalTaskRolesException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException {
        Time systemtime = new Time(0);

        Session managerSession = new Session();
        SessionProxy managerSessionWrapper = new SessionProxy(managerSession);
        Session developerSession = new Session();
        SessionProxy developerSessionWrapper = new SessionProxy(developerSession);
        User manager = new User("DieterVH", "computer776", Set.of(Role.PROJECTMANAGER));
        User developer = new User("SamHa", "trein123", Set.of(Role.JAVAPROGRAMMER));

        managerSession.login(manager);
        developerSession.login(developer);

        TaskManSystem taskManSystem = new TaskManSystem(systemtime);
        taskManSystem.createProject("SimpleProject", "Cool description", new Time(100));
        taskManSystem.addTaskToProject("SimpleProject", "SimpleTask", "Cool description", new Time(40), 0.1, List.of(Role.JAVAPROGRAMMER), Set.of(), Set.of());

        ShowProjectsController managerController = new ShowProjectsController(managerSessionWrapper, taskManSystem);
        ShowProjectsController developerController = new ShowProjectsController(developerSessionWrapper, taskManSystem);

        assertTrue(managerController.showProjectsPreconditions());
        assertFalse(developerController.showProjectsPreconditions());

        assertThrows(IncorrectPermissionException.class, () -> developerController.getProjectData("hoi"));
        assertThrows(ProjectNotFoundException.class, () -> managerController.getProjectData("HOI"));

        assertNotNull(managerController.getProjectData("SimpleProject")); // Beetje belachelijk om heel die string hier te hardcoden...

        assertThrows(IncorrectPermissionException.class, () -> developerController.getTaskData("SimpleProject", "SimpleTask"));
        assertThrows(ProjectNotFoundException.class, () -> managerController.getTaskData("gwrf", "regtt"));
        assertThrows(TaskNotFoundException.class, () -> managerController.getTaskData("SimpleProject", "regtt"));

        assertNotNull(managerController.getTaskData("SimpleProject", "SimpleTask"));

        assertThrows(IncorrectPermissionException.class, developerController::getTaskManSystemData);

    }
}
