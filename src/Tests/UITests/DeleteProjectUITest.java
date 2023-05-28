package Tests.UITests;

import Application.IncorrectPermissionException;
import Application.Command.CommandManager;
import Application.Controllers.ProjectControllers.DeleteProjectController;
import Domain.TaskManSystem.TaskManSystem;
import Domain.User.Role;
import Domain.User.User;
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
import UserInterface.ProjectUIs.DeleteProjectUI;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DeleteProjectUITest {
    private DeleteProjectUI developerUI;
    private DeleteProjectUI managerUI;

    private DeleteProjectController developerController;
    private DeleteProjectController managerController;

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

        managerController = new DeleteProjectController(managerSessionProxy, taskManSystem, commandManager);
        developerController = new DeleteProjectController(developerSessionProxy, taskManSystem, commandManager);

        developerUI = new DeleteProjectUI(developerController);
        managerUI = new DeleteProjectUI(managerController);
    }

    @Test
    public void testSuccessfulDeletion(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("SimpleProject\n".getBytes()));
        managerUI.deleteProject();
        assertEquals(
                """
                         *** PROJECT LIST ***
                        - SimpleProject --- Containing 1 Task(s)
                                                
                        Project Name to Delete (type 'BACK' to return):\s
                        Project successfully deleted
                        
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }

    @Test
    public void testNoTasksInSystem() throws ProjectNotFoundException, IncorrectPermissionException {
        managerController.deleteProject("SimpleProject");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("SimpleProject\nBACK\n".getBytes()));
        managerUI.deleteProject();
        assertEquals(
                """
                         *** PROJECT LIST ***
                        There are currently no projects in the system.
                                                
                        Project Name to Delete (type 'BACK' to return):\s
                                                
                        WARNING: Project couldn't be found, try again
                                                
                         *** PROJECT LIST ***
                        There are currently no projects in the system.
                                                
                        Project Name to Delete (type 'BACK' to return):\s
                        Project deletion cancelled
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }

    @Test
    public void testIncorrectPermission(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        System.setIn(new ByteArrayInputStream("BACK\n".getBytes()));
        developerUI.deleteProject();
        assertEquals(
                """
                        You must be logged in with the project manager role to call this function
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }


}
