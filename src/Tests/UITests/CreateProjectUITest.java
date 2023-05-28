package Tests.UITests;

import Application.Command.CommandManager;
import Domain.TaskManSystem.TaskManSystem;
import Domain.User.Role;
import Domain.User.User;
import UserInterface.ProjectUIs.CreateProjectUI;
import Application.Controllers.ProjectControllers.CreateProjectController;
import Application.Session.Session;
import Application.Session.SessionProxy;
import Domain.DataClasses.Time;
import Domain.DataClasses.InvalidTimeException;
import Domain.Project.ProjectNameAlreadyInUseException;
import Domain.Project.ProjectNotOngoingException;
import Domain.Project.TaskNotFoundException;
import Domain.Task.IncorrectTaskStatusException;
import Domain.Task.TaskNameAlreadyInUseException;
import Domain.TaskManSystem.DueBeforeSystemTimeException;
import Domain.TaskManSystem.ProjectNotFoundException;
import Domain.Task.LoopDependencyGraphException;
import Domain.Task.IllegalTaskRolesException;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CreateProjectUITest {
    private CreateProjectUI developerUI;
    private CreateProjectUI managerUI;

    @Before
    public void setUp() throws InvalidTimeException, ProjectNotFoundException, TaskNameAlreadyInUseException, TaskNotFoundException, IllegalTaskRolesException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException, ProjectNameAlreadyInUseException, DueBeforeSystemTimeException {
        // Setup test environment
        Time systemtime = new Time(70);

        Session managerSession = new Session();
        SessionProxy managerSessionProxy = new SessionProxy(managerSession);
        Session developerSession = new Session();
        SessionProxy developerSessionProxy = new SessionProxy(developerSession);


        HashSet<Role> roles = new HashSet<>();
        roles.add(Role.PROJECTMANAGER);
        User manager = new User("DieterVH", "computer776", roles);
        roles.remove(Role.PROJECTMANAGER);
        roles.add(Role.PYTHONPROGRAMMER);
        User developer = new User("SamHa", "trein123", roles); // niet zeker over Role


        managerSession.login(manager);
        developerSession.login(developer);

        TaskManSystem taskManSystem = new TaskManSystem(systemtime);
        taskManSystem.createProject("SimpleProject", "Cool description", new Time(100));
        taskManSystem.addTaskToProject("SimpleProject", "SimpleTask", "Cool description", new Time(40), 0.1, List.of(Role.PYTHONPROGRAMMER), new HashSet<>(), new HashSet<>());

        CommandManager commandManager = new CommandManager();

        CreateProjectController managerController = new CreateProjectController(managerSessionProxy, taskManSystem, commandManager);
        CreateProjectController developerController = new CreateProjectController(developerSessionProxy, taskManSystem, commandManager);

        developerUI = new CreateProjectUI(developerController);
        managerUI = new CreateProjectUI(managerController);
    }

    @Test
    public void test() throws ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, InvalidTimeException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException, IllegalTaskRolesException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        developerUI.createProject();
        assertEquals("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function\n".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("BACK\n".getBytes()));
        managerUI.createProject();
        assertEquals(
                """
                        Type BACK to cancel project creation at any time
                        *********** PROJECT CREATION FORM ***********
                        Project Name:\s
                        Project creation cancelled
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("NewProject\nBACK\n".getBytes()));
        managerUI.createProject();
        assertEquals(
                """
                        Type BACK to cancel project creation at any time
                        *********** PROJECT CREATION FORM ***********
                        Project Name:\s
                        Project Description:\s
                        Project creation cancelled
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("NewProject\nCOOL\nBACK\n".getBytes()));
        managerUI.createProject();
        assertEquals(
                """
                        Type BACK to cancel project creation at any time
                        *********** PROJECT CREATION FORM ***********
                        Project Name:\s
                        Project Description:\s
                        Project due hour:\s
                        Project creation cancelled
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("NewProject\nCOOL\nBACK\n".getBytes()));
        managerUI.createProject();
        assertEquals(
                """
                        Type BACK to cancel project creation at any time
                        *********** PROJECT CREATION FORM ***********
                        Project Name:\s
                        Project Description:\s
                        Project due hour:\s
                        Project creation cancelled
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("NewProject\nCOOL\noops\nBACK".getBytes()));
        managerUI.createProject();
        assertEquals(
                """
                        Type BACK to cancel project creation at any time
                        *********** PROJECT CREATION FORM ***********
                        Project Name:\s
                        Project Description:\s
                        Project due hour:\s
                        Given due hour is not an integer, please input an integer and try again
                        Project creation cancelled
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("NewProject\nCOOL\noops\n2\nBACK\n".getBytes()));
        managerUI.createProject();
        assertEquals(
                """
                        Type BACK to cancel project creation at any time
                        *********** PROJECT CREATION FORM ***********
                        Project Name:\s
                        Project Description:\s
                        Project due hour:\s
                        Given due hour is not an integer, please input an integer and try again
                        Project due minute:\s
                        Project creation cancelled
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("NewProject\nCOOL\noops\n2\noops\nBACK\n".getBytes()));
        managerUI.createProject();
        assertEquals(
                """
                        Type BACK to cancel project creation at any time
                        *********** PROJECT CREATION FORM ***********
                        Project Name:\s
                        Project Description:\s
                        Project due hour:\s
                        Given due hour is not an integer, please input an integer and try again
                        Project due minute:\s
                        Given due minute is not an integer, please input an integer and try again
                        Project creation cancelled
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nCOOL\n2\n20\nBACK\n".getBytes()));
        managerUI.createProject();
        assertEquals(
                """
                        Type BACK to cancel project creation at any time
                        *********** PROJECT CREATION FORM ***********
                        Project Name:\s
                        Project Description:\s
                        Project due hour:\s
                        Project due minute:\s
                        The given project name is already in use, please try again
                                        
                        Type BACK to cancel project creation at any time
                        *********** PROJECT CREATION FORM ***********
                        Project Name:\s
                        Project creation cancelled
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("SimpleProject\nCOOL\n2\n70\nBACK\n".getBytes()));
        managerUI.createProject();
        assertEquals(
                """
                        Type BACK to cancel project creation at any time
                        *********** PROJECT CREATION FORM ***********
                        Project Name:\s
                        Project Description:\s
                        Project due hour:\s
                        Project due minute:\s
                        The given due minutes are not of the correct format (0-59)
                                        
                        Type BACK to cancel project creation at any time
                        *********** PROJECT CREATION FORM ***********
                        Project Name:\s
                        Project creation cancelled
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("NewProject\nCOOL\n1\n0\nBACK\n".getBytes()));
        managerUI.createProject();
        assertEquals(
                """
                        Type BACK to cancel project creation at any time
                        *********** PROJECT CREATION FORM ***********
                        Project Name:\s
                        Project Description:\s
                        Project due hour:\s
                        Project due minute:\s
                        The given due time is before the current system time, please try again
                                        
                        Type BACK to cancel project creation at any time
                        *********** PROJECT CREATION FORM ***********
                        Project Name:\s
                        Project creation cancelled
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();


        System.setIn(new ByteArrayInputStream("NewProject\nCOOL\n2\n0\n".getBytes()));
        managerUI.createProject();
        assertEquals(
                """
                        Type BACK to cancel project creation at any time
                        *********** PROJECT CREATION FORM ***********
                        Project Name:\s
                        Project Description:\s
                        Project due hour:\s
                        Project due minute:\s
                        Project with name NewProject created!
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

    }
}