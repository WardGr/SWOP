package Tests;

import Application.IncorrectPermissionException;
import Domain.DueBeforeSystemTimeException;
import Domain.InvalidTimeException;
import Domain.NewTimeBeforeSystemTimeException;
import Domain.ProjectNameAlreadyInUseException;
import org.junit.Test;

public class CreateProjectControllerTest {
    @Test
    public void testCreateProjectController() throws ProjectNameAlreadyInUseException, InvalidTimeException, DueBeforeSystemTimeException, IncorrectPermissionException, NewTimeBeforeSystemTimeException {
        /*
        User brewer = new User("OlavBl", "peer123", Role.DEVELOPER);
        User boss = new User("WardGr", "minecraft123", Role.PROJECTMANAGER);
        Session omer = new Session();
        SessionWrapper omerWrapper = new SessionWrapper(omer);
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
