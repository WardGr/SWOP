package Tests.ControllerTests;

import Application.IncorrectPermissionException;
import Application.Session.Session;
import Application.Session.SessionProxy;
import Domain.DataClasses.InvalidTimeException;
import Domain.DataClasses.Time;
import Domain.TaskManSystem.DueBeforeSystemTimeException;
import Domain.Project.ProjectNameAlreadyInUseException;
import Domain.TaskManSystem.NewTimeBeforeSystemTimeException;
import Domain.TaskManSystem.TaskManSystem;
import Domain.User.Role;
import Domain.User.User;
import Application.ProjectControllers.ProjectController;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProjectControllerTest {
    @Test
    public void testCreateProjectController() throws ProjectNameAlreadyInUseException, InvalidTimeException, DueBeforeSystemTimeException, IncorrectPermissionException, NewTimeBeforeSystemTimeException {
        /*
        Session wardsSession = new Session();
        SessionProxy omerWrapper = new SessionProxy(wardsSession);
        Set wardsRoles = new HashSet();
        wardsRoles.add(Role.PROJECTMANAGER);
        wardsRoles.add(Role.JAVAPROGRAMMER);
        User ward = new User("WardGr", "peer123", wardsRoles);
        wardsSession.login(ward);
        TaskManSystem tms = new TaskManSystem(new Time(12));

        ProjectController projectController = new ProjectController(omerWrapper, tms);

        assertTrue(projectController.createProjectPreconditions());

        wardsSession.logout();
        assertFalse(projectController.createProjectPreconditions());

        Set falseRoles = new HashSet();
        falseRoles.add(Role.SYSADMIN);
        User false1 = new User("false1", "false1", falseRoles);
        wardsSession.login(false1);
        assertFalse(projectController.createProjectPreconditions());
        assertThrows(IncorrectPermissionException.class, () -> projectController.createProject("Omer", "Brew omer beer", new Time(30)));

        wardsSession.logout();

        falseRoles = new HashSet();
        falseRoles.add(Role.JAVAPROGRAMMER);
        User false2 = new User("false2", "false2", falseRoles);
        wardsSession.login(false2);
        assertFalse(projectController.createProjectPreconditions());
        assertThrows(IncorrectPermissionException.class, () -> projectController.createProject("Omer", "Brew omer beer", new Time(30)));

        wardsSession.logout();

        falseRoles = new HashSet();
        falseRoles.add(Role.PYTHONPROGRAMMER);
        User false3 = new User("false3", "false3", falseRoles);
        wardsSession.login(false3);
        assertFalse(projectController.createProjectPreconditions());
        assertThrows(IncorrectPermissionException.class, () -> projectController.createProject("Omer", "Brew omer beer", new Time(30)));

        ;
        wardsSession.login(ward);
        assertTrue(projectController.createProjectPreconditions());
        projectController.createProject("Omer", "Brew omer beer", new Time(30));
        projectController.createProject("LeFort", "Brew Tripel LeFort", new Time(60));

        assertThrows(ProjectNameAlreadyInUseException.class, () -> projectController.createProject("Omer", "Brew omer beer", new Time(40)));
        assertThrows(DueBeforeSystemTimeException.class, () -> projectController.createProject("Duvel", "Brew duvel beer", new Time(11)));
        assertThrows(DueBeforeSystemTimeException.class, () -> projectController.createProject("Duvel", "Brew duvel beer", new Time(10)));
        assertThrows(ProjectNameAlreadyInUseException.class, () -> projectController.createProject("LeFort", "Drink a LeFort Tripel ;)", new Time(22)));





        User brewer = new User("OlavBl", "peer123", Role.DEVELOPER);
        User boss = new User("WardGr", "minecraft123", Role.PROJECTMANAGER);
        Session omer = new Session();
        SessionProxy omerWrapper = new SessionProxy(omer);
        omer.login(brewer);

        TaskManSystem tms = new TaskManSystem(new Time(0));

        CreateProjectController cpc = new CreateProjectController(omerWrapper, tms);
        assertFalse(cpc.createProjectPreconditions());
        assertThrows(IncorrectPermissionException.class, () -> {
            cpc.createProject("Omer", "Brew omer beer", 2, 0);
        });
        assertFalse(cpc.createProjectPreconditions());
        omer.logout();
        assertThrows(IncorrectPermissionException.class, () -> {
            cpc.createProject("Omer", "Brew omer beer", 2, 0);
        });
        omer.login(boss);
        assertTrue(cpc.createProjectPreconditions());
        cpc.createProject("Omer", "Brew omer beer", 32, 0);
        cpc.createProject("LeFort", "Brew Tripel LeFort", 65, 0);
        assertThrows(ProjectNameAlreadyInUseException.class, () -> {
            cpc.createProject("Omer", "Brew omer beer", 2, 30);
        });
        assertThrows(ProjectNameAlreadyInUseException.class, () -> {
            cpc.createProject("LeFort", "Brew Tripel LeFort", 2, 30);
        });


        taskManSystem.advanceTime(new Time(2000));
        assertThrows(DueBeforeSystemTimeException.class, () -> {
            cpc.createProject("Bockor", "Brew Bockor beer", 2, 30);
        });
        assertThrows(DueBeforeSystemTimeException.class, () -> {
            cpc.createProject("Blauw", "Brew Blauw export beer", 17, 30);
        });

        assertThrows(InvalidTimeException.class, () -> {
            cpc.createProject("Roodbruin", "Brew Vanderghinste Roodbruin beer", 2, -1);
        });

        omer.logout();
        assertFalse(cpc.createProjectPreconditions());
        */
    }
}
