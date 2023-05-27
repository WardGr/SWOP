package Tests.ControllerTests;

import Application.Command.CommandManager;
import Application.IncorrectPermissionException;
import Application.ProjectControllers.CreateProjectController;
import Application.Session.Session;
import Application.Session.SessionProxy;
import Domain.DataClasses.Time;
import Domain.Project.ProjectData;
import Domain.TaskManSystem.TaskManSystem;
import Domain.User.Role;
import Domain.User.User;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class CreateProjectControllerTest {

    private Session session;
    private User incorrect;
    private CreateProjectController createProjectController;

    @Before
    public void setUp() throws Exception {
        this.session = new Session();
        User projectManager = new User("DieterVH", "computer776", Set.of(Role.PROJECTMANAGER));
        this.incorrect = new User("OlavBl", "peer123", Set.of(Role.PYTHONPROGRAMMER));
        TaskManSystem taskManSystem = new TaskManSystem(new Time(0));
        this.createProjectController = new CreateProjectController(new SessionProxy(session), taskManSystem, new CommandManager());
        session.login(projectManager);

    }

    @Test
    public void testPreconditions() {
        assertTrue(createProjectController.projectPreconditions());
        session.logout();
        assertFalse(createProjectController.projectPreconditions());
        session.login(incorrect);
        assertFalse(createProjectController.projectPreconditions());
    }

    @Test
    public void testIncorrectPermissions() {
        session.logout();
        assertFalse(createProjectController.projectPreconditions());
        assertThrows(IncorrectPermissionException.class, () -> createProjectController.createProject("Omer","Brew omer beer", new Time(30)));
        assertThrows(IncorrectPermissionException.class, () -> createProjectController.getTaskManSystemData());
        assertThrows(IncorrectPermissionException.class, () -> createProjectController.getProjectData("Omer"));

        session.login(incorrect);
        assertFalse(createProjectController.projectPreconditions());
        assertThrows(IncorrectPermissionException.class, () -> createProjectController.createProject("Omer","Brew omer beer", new Time(30)));
        assertThrows(IncorrectPermissionException.class, () -> createProjectController.getTaskManSystemData());
        assertThrows(IncorrectPermissionException.class, () -> createProjectController.getProjectData("Omer"));
    }
    @Test
    public void testCreateProject() throws Exception {
        createProjectController.createProject("Omer","Brew omer beer", new Time(30));
        assertTrue(createProjectController.getTaskManSystemData().getProjectsData().stream().map(ProjectData::getName).toList().contains("Omer"));
        assertEquals(1, createProjectController.getTaskManSystemData().getProjectsData().size());

        createProjectController. createProject("LeFort", "Brew Tripel LeFort", new Time(60));
        assertTrue(createProjectController.getTaskManSystemData().getProjectsData().stream().map(ProjectData::getName).toList().contains("LeFort"));
        assertEquals(2, createProjectController.getTaskManSystemData().getProjectsData().size());
    }
    @Test
    public void testGetters() throws Exception {
        createProjectController.createProject("Omer","Brew omer beer", new Time(30));
        assertTrue(createProjectController.getTaskManSystemData().getProjectsData().stream().map(ProjectData::getName).toList().contains("Omer"));
        assertEquals("Omer", createProjectController.getProjectData("Omer").getName());

        createProjectController. createProject("LeFort", "Brew Tripel LeFort", new Time(60));
        assertTrue(createProjectController.getTaskManSystemData().getProjectsData().stream().map(ProjectData::getName).toList().contains("LeFort"));
        assertEquals("LeFort", createProjectController.getProjectData("LeFort").getName());

        assertEquals(2, createProjectController.getTaskManSystemData().getProjectsData().size());
    }
    /*
    @Test
    public void testCreateProjectController() throws ProjectNameAlreadyInUseException, InvalidTimeException, DueBeforeSystemTimeException, IncorrectPermissionException, NewTimeBeforeSystemTimeException {
        Session wardsSession = new Session();
        SessionProxy omerWrapper = new SessionProxy(wardsSession);
        Set wardsRoles = new HashSet();
        wardsRoles.add(Role.PROJECTMANAGER);
        wardsRoles.add(Role.JAVAPROGRAMMER);
        User ward = new User("WardGr", "peer123", wardsRoles);
        wardsSession.login(ward);
        TaskManSystem tms = new TaskManSystem(new Time(12));
        CommandManager cmd = new CommandManager();

        CreateProjectController projectController = new CreateProjectController(omerWrapper, tms, cmd);

        assertTrue(projectController.projectPreconditions());

        wardsSession.logout();
        assertFalse(projectController.projectPreconditions());

        Set falseRoles = new HashSet();
        falseRoles.add(Role.SYSADMIN);
        User false1 = new User("false1", "false1", falseRoles);
        wardsSession.login(false1);
        assertFalse(projectController.projectPreconditions());
        assertThrows(IncorrectPermissionException.class, () -> projectController.createProject("Omer", "Brew omer beer", new Time(30)));

        wardsSession.logout();

        falseRoles = new HashSet();
        falseRoles.add(Role.JAVAPROGRAMMER);
        User false2 = new User("false2", "false2", falseRoles);
        wardsSession.login(false2);
        assertFalse(projectController.projectPreconditions());
        assertThrows(IncorrectPermissionException.class, () -> projectController.createProject("Omer", "Brew omer beer", new Time(30)));

        wardsSession.logout();

        falseRoles = new HashSet();
        falseRoles.add(Role.PYTHONPROGRAMMER);
        User false3 = new User("false3", "false3", falseRoles);
        wardsSession.login(false3);
        assertFalse(projectController.projectPreconditions());
        assertThrows(IncorrectPermissionException.class, () -> projectController.createProject("Omer", "Brew omer beer", new Time(30)));

        ;
        wardsSession.login(ward);
        assertTrue(projectController.projectPreconditions());
        projectController.createProject("Omer", "Brew omer beer", new Time(30));
        projectController.createProject("LeFort", "Brew Tripel LeFort", new Time(60));

        assertThrows(ProjectNameAlreadyInUseException.class, () -> projectController.createProject("Omer", "Brew omer beer", new Time(40)));
        assertThrows(DueBeforeSystemTimeException.class, () -> projectController.createProject("Duvel", "Brew duvel beer", new Time(11)));
        assertThrows(DueBeforeSystemTimeException.class, () -> projectController.createProject("Duvel", "Brew duvel beer", new Time(10)));
        assertThrows(ProjectNameAlreadyInUseException.class, () -> projectController.createProject("LeFort", "Drink a LeFort Tripel ;)", new Time(22)));



        //vgm hier nieuwe functie
        Set r = new HashSet<>();
        r.add(Role.PYTHONPROGRAMMER);
        User brewer = new User("OlavBl", "peer123", r);
        r.add(Role.JAVAPROGRAMMER);
        User boss = new User("WardGr", "minecraft123", r);
        Session omer = new Session();
        SessionProxy omerWrapper = new SessionProxy(omer);
        omer.login(brewer);

        TaskManSystem tms = new TaskManSystem(new Time(0));

        CreateProjectController cpc = new CreateProjectController(omerWrapper, tms);
        assertFalse(cpc.projectPreconditions());
        assertThrows(IncorrectPermissionException.class, () -> {
            cpc.createProject("Omer", "Brew omer beer", 2, 0);
        });
        assertFalse(cpc.projectPreconditions());
        omer.logout();
        assertThrows(IncorrectPermissionException.class, () -> {
            cpc.createProject("Omer", "Brew omer beer", 2, 0);
        });
        omer.login(boss);
        assertTrue(cpc.projectPreconditions());
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
        assertFalse(cpc.projectPreconditions());
    }

     */
}
