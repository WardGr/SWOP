package Tests;

import Application.ProjectController;
import Application.IncorrectPermissionException;
import Application.Session;
import Application.SessionProxy;
import Domain.DueBeforeSystemTimeException;
import Domain.*;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class CreateProjectControllerTest {
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

        CreateProjectController cpc = new CreateProjectController(omerWrapper, tms);

        assertTrue(cpc.createProjectPreconditions());

        wardsSession.logout();
        assertFalse(cpc.createProjectPreconditions());

        Set falseRoles = new HashSet();
        falseRoles.add(Role.SYSADMIN);
        User false1 = new User("false1", "false1", falseRoles);
        wardsSession.login(false1);
        assertFalse(cpc.createProjectPreconditions());
        assertThrows(IncorrectPermissionException.class, () -> cpc.createProject("Omer", "Brew omer beer", new Time(30)));

        wardsSession.logout();

        falseRoles = new HashSet();
        falseRoles.add(Role.JAVAPROGRAMMER);
        User false2 = new User("false2", "false2", falseRoles);
        wardsSession.login(false2);
        assertFalse(cpc.createProjectPreconditions());
        assertThrows(IncorrectPermissionException.class, () -> cpc.createProject("Omer", "Brew omer beer", new Time(30)));

        wardsSession.logout();

        falseRoles = new HashSet();
        falseRoles.add(Role.PYTHONPROGRAMMER);
        User false3 = new User("false3", "false3", falseRoles);
        wardsSession.login(false3);
        assertFalse(cpc.createProjectPreconditions());
        assertThrows(IncorrectPermissionException.class, () -> cpc.createProject("Omer", "Brew omer beer", new Time(30)));

        ;
        wardsSession.login(ward);
        assertTrue(cpc.createProjectPreconditions());
        cpc.createProject("Omer", "Brew omer beer", new Time(30));
        cpc.createProject("LeFort", "Brew Tripel LeFort", new Time(60));

        assertThrows(ProjectNameAlreadyInUseException.class, () -> cpc.createProject("Omer", "Brew omer beer", new Time(40)));
        assertThrows(DueBeforeSystemTimeException.class, () -> cpc.createProject("Duvel", "Brew duvel beer", new Time(11)));
        assertThrows(DueBeforeSystemTimeException.class, () -> cpc.createProject("Duvel", "Brew duvel beer", new Time(10)));
        assertThrows(ProjectNameAlreadyInUseException.class, () -> cpc.createProject("LeFort", "Drink a LeFort Tripel ;)", new Time(22)));




        /*
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


        tms.advanceTime(new Time(2000));
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
