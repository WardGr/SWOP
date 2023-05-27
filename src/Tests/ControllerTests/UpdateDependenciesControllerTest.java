package Tests.ControllerTests;

import Application.*;
import Application.Command.CommandManager;
import Application.Session.Session;
import Application.Session.SessionProxy;
import Application.TaskControllers.UpdateDependenciesController;
import Domain.DataClasses.EndTimeBeforeStartTimeException;
import Domain.DataClasses.InvalidTimeException;
import Domain.DataClasses.Time;
import Domain.Project.DueTimeBeforeCreationTimeException;
import Domain.Project.ProjectNameAlreadyInUseException;
import Domain.Project.ProjectNotOngoingException;
import Domain.Project.TaskNotFoundException;
import Domain.Task.*;
import Domain.TaskManSystem.DueBeforeSystemTimeException;
import Domain.TaskManSystem.NewTimeBeforeSystemTimeException;
import Domain.TaskManSystem.ProjectNotFoundException;
import Domain.TaskManSystem.TaskManSystem;
import Domain.User.IncorrectUserException;
import Domain.User.Role;
import Domain.User.User;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class UpdateDependenciesControllerTest {

    private UpdateDependenciesController udc;
    private TaskManSystem taskManSystem;
    private Session omer;
    private User ward;
    private User incorrectUserOne;
    private User incorrectUserTwo;
    private User incorrectUserThree;
    private List<Role> languages;


    @Before
    public void setUp() throws Exception {
        this.omer = new Session();
        this.ward = new User("Ward", "ward123", Set.of(Role.JAVAPROGRAMMER, Role.PROJECTMANAGER));

        this.taskManSystem = new TaskManSystem(new Time(11));
        SessionProxy omerWrapper = new SessionProxy(omer);
        this.udc = new UpdateDependenciesController(omerWrapper, taskManSystem, new CommandManager());

        this.incorrectUserOne = new User("IncorrectOne", "incorrect123", Set.of(Role.JAVAPROGRAMMER));
        this.incorrectUserTwo = new User("IncorrectTwo", "incorrect123", Set.of(Role.JAVAPROGRAMMER, Role.SYSADMIN));
        this.incorrectUserThree = new User("IncorrectThree", "incorrect123", Set.of(Role.JAVAPROGRAMMER, Role.SYSADMIN, Role.PYTHONPROGRAMMER));

        this.languages = new LinkedList<>();
        languages.add(Role.JAVAPROGRAMMER);
        languages.add(Role.PYTHONPROGRAMMER);

        omer.login(ward);
        taskManSystem.createProject("Omer", "Omer brewery project", new Time(20));
        taskManSystem.addTaskToProject("Omer", "Brew", "Brewing beer", new Time(10), 3, languages, new HashSet<>(), new HashSet<>());
        taskManSystem.addTaskToProject("Omer", "Sell", "Selling beer", new Time(10), 3, languages, new HashSet<>(), new HashSet<>());
        taskManSystem.addTaskToProject("Omer", "Clean", "Cleaning beer", new Time(10), 3, languages, new HashSet<>(), new HashSet<>());

        taskManSystem.createProject("Ward", "Ward brewery project", new Time(20));
        taskManSystem.addTaskToProject("Ward", "Brew", "Brewing beer", new Time(10), 3, languages, new HashSet<>(), new HashSet<>());
        taskManSystem.addTaskToProject("Ward", "Sell", "Selling beer", new Time(10), 3, languages, new HashSet<>(), new HashSet<>());
        taskManSystem.addTaskToProject("Ward", "Clean", "Cleaning beer", new Time(10), 3, languages, new HashSet<>(), new HashSet<>());
    }

    @Test
    public void testIncorrectPermissions() {
        omer.login(incorrectUserOne);
        assertFalse(udc.updateDependenciesPreconditions());

        omer.logout();
        omer.login(incorrectUserTwo);
        assertFalse(udc.updateDependenciesPreconditions());
        assertThrows(IncorrectPermissionException.class, () -> udc.addPrevTask("Project", "Task", "Project", "PreviousTask"));
        assertThrows(IncorrectPermissionException.class, () -> udc.addNextTask("Project", "Task", "Project", "PreviousTask"));
        assertThrows(IncorrectPermissionException.class, () -> udc.getProjectData("Project"));
        assertThrows(IncorrectPermissionException.class, () -> udc.getTaskData("Project", "Task"));
        assertThrows(IncorrectPermissionException.class, () -> udc.getTaskManSystemData());
        assertThrows(IncorrectPermissionException.class, () -> udc.removeNextTask("Project", "Task", "Project", "PreviousTask"));
        assertThrows(IncorrectPermissionException.class, () -> udc.removePrevTask("Project", "Task", "Project", "PreviousTask"));



        omer.logout();
        omer.login(incorrectUserThree);
        assertFalse(udc.updateDependenciesPreconditions());
        assertThrows(IncorrectPermissionException.class, () -> udc.addPrevTask("Project", "Task", "Project","PreviousTask"));
        assertThrows(IncorrectPermissionException.class, () -> udc.getProjectData("Project"));
        assertThrows(IncorrectPermissionException.class, () -> udc.getTaskData("Project", "Task"));
        assertThrows(IncorrectPermissionException.class, () -> udc.getTaskManSystemData());
        assertThrows(IncorrectPermissionException.class, () -> udc.addNextTask("Project", "Task", "Project", "PreviousTask"));
        assertThrows(IncorrectPermissionException.class, () -> udc.removeNextTask("Project", "Task", "Project", "PreviousTask"));
        assertThrows(IncorrectPermissionException.class, () -> udc.removePrevTask("Project", "Task", "Project", "PreviousTask"));

        omer.logout();
        assertThrows(IncorrectPermissionException.class, () -> udc.getProjectData("Project"));
        assertThrows(IncorrectPermissionException.class, () -> udc.getTaskData("Project", "Task"));
        assertThrows(IncorrectPermissionException.class, () -> udc.getTaskManSystemData());
        assertThrows(IncorrectPermissionException.class, () -> udc.removeNextTask("Project", "Task", "Project", "PreviousTask"));
        assertThrows(IncorrectPermissionException.class, () -> udc.removePrevTask("Project", "Task", "Project", "PreviousTask"));

    }

    @Test
    public void testGetters() throws Exception {
        assertEquals(udc.getProjectData("Omer"), taskManSystem.getProjectData("Omer"));
        assertEquals(udc.getTaskData("Omer", "Brew"), taskManSystem.getTaskData("Omer", "Brew"));
        assertEquals(udc.getTaskManSystemData(), taskManSystem.getTaskManSystemData());
    }


    @Test
    public void testAddRemoveNextTaskSameProject() throws Exception {
        udc.addNextTask("Omer", "Brew", "Omer", "Sell");
        assertTrue(udc.getTaskData("Omer", "Brew").getNextTasksData().stream().map(TaskData::getName).toList().contains("Sell"));
        assertTrue(udc.getTaskData("Omer", "Sell").getPrevTasksData().stream().map(TaskData::getName).toList().contains("Brew"));

        udc.removeNextTask("Omer", "Brew", "Omer", "Sell");
        assertFalse(udc.getTaskData("Omer", "Brew").getNextTasksData().stream().map(TaskData::getName).toList().contains("Sell"));
        assertFalse(udc.getTaskData("Omer", "Sell").getPrevTasksData().stream().map(TaskData::getName).toList().contains("Brew"));
    }

    @Test
    public void testAddRemoveNextTaskDifferentProject() throws Exception {
        udc.addNextTask("Omer", "Brew", "Ward", "Sell");
        assertTrue(udc.getTaskData("Omer", "Brew").getNextTasksData().stream().map(TaskData::getName).toList().contains("Sell"));
        assertTrue(udc.getTaskData("Ward", "Sell").getPrevTasksData().stream().map(TaskData::getName).toList().contains("Brew"));

        udc.removeNextTask("Omer", "Brew", "Ward", "Sell");
        assertFalse(udc.getTaskData("Omer", "Brew").getNextTasksData().stream().map(TaskData::getName).toList().contains("Sell"));
        assertFalse(udc.getTaskData("Ward", "Sell").getPrevTasksData().stream().map(TaskData::getName).toList().contains("Brew"));
    }

    @Test
    public void testAddRemovePrevTaskSameProject() throws Exception {
        udc.addPrevTask("Omer", "Brew", "Omer", "Sell");
        assertTrue(udc.getTaskData("Omer", "Brew").getPrevTasksData().stream().map(TaskData::getName).toList().contains("Sell"));
        assertTrue(udc.getTaskData("Omer", "Sell").getNextTasksData().stream().map(TaskData::getName).toList().contains("Brew"));

        udc.removePrevTask("Omer", "Brew", "Omer", "Sell");
        assertFalse(udc.getTaskData("Omer", "Brew").getPrevTasksData().stream().map(TaskData::getName).toList().contains("Sell"));
        assertFalse(udc.getTaskData("Omer", "Sell").getNextTasksData().stream().map(TaskData::getName).toList().contains("Brew"));
    }

    @Test
    public void testAddRemovePrevTaskDifferentProject() throws Exception {
        udc.addPrevTask("Omer", "Brew", "Ward", "Sell");
        assertTrue(udc.getTaskData("Omer", "Brew").getPrevTasksData().stream().map(TaskData::getName).toList().contains("Sell"));
        assertTrue(udc.getTaskData("Ward", "Sell").getNextTasksData().stream().map(TaskData::getName).toList().contains("Brew"));

        udc.removePrevTask("Omer", "Brew", "Ward", "Sell");
        assertFalse(udc.getTaskData("Omer", "Brew").getPrevTasksData().stream().map(TaskData::getName).toList().contains("Sell"));
        assertFalse(udc.getTaskData("Ward", "Sell").getNextTasksData().stream().map(TaskData::getName).toList().contains("Brew"));
    }


    @Test
    public void integrationTest() throws InvalidTimeException, IncorrectPermissionException, ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, IncorrectTaskStatusException, IncorrectUserException, NewTimeBeforeSystemTimeException, EndTimeBeforeStartTimeException, DueTimeBeforeCreationTimeException, LoopDependencyGraphException, ProjectNotOngoingException, IllegalTaskRolesException {

        udc.addNextTask("Omer", "Brew", "Omer","Sell");
        udc.addPrevTask("Omer", "Clean", "Omer", "Sell");

        assertEquals(udc.getProjectData("Omer").getName(), "Omer");
        assertEquals(udc.getProjectData("Omer").getCreationTime(), new Time(11));
        assertEquals(udc.getProjectData("Omer").getDueTime(), new Time(20));
        assertEquals(udc.getProjectData("Omer").getDescription(), "Omer brewery project");
        assertTrue(udc.updateDependenciesPreconditions());
        assertEquals(0, udc.getTaskData("Omer", "Brew").getPrevTasksData().size());
        assertEquals(1, udc.getTaskData("Omer", "Brew").getNextTasksData().size());
        assertEquals(1, udc.getTaskData("Omer", "Sell").getPrevTasksData().size());
        assertEquals(1, udc.getTaskData("Omer", "Sell").getNextTasksData().size());
        assertEquals(1, udc.getTaskData("Omer", "Clean").getPrevTasksData().size());
        assertEquals(0, udc.getTaskData("Omer", "Clean").getNextTasksData().size());

        assertTrue(udc.getTaskData("Omer", "Brew").getNextTasksData().stream().map(TaskData::getName).toList().contains("Sell"));
        assertTrue(udc.getTaskData("Omer", "Sell").getPrevTasksData().stream().map(TaskData::getName).toList().contains("Brew"));
        assertTrue(udc.getTaskData("Omer", "Clean").getPrevTasksData().stream().map(TaskData::getName).toList().contains("Sell"));
        assertTrue(udc.getTaskData("Omer", "Sell").getNextTasksData().stream().map(TaskData::getName).toList().contains("Clean"));

        udc.removeNextTask("Omer", "Brew", "Omer", "Sell");
        assertEquals(0, udc.getTaskData("Omer", "Brew").getNextTasksData().size());
        assertEquals(0, udc.getTaskData("Omer", "Sell").getPrevTasksData().size());
        assertEquals(1, udc.getTaskData("Omer", "Clean").getPrevTasksData().size());
        assertEquals(0, udc.getTaskData("Omer", "Clean").getNextTasksData().size());

        udc.removePrevTask("Omer", "Clean", "Omer", "Sell");
        assertEquals(0, udc.getTaskData("Omer", "Brew").getNextTasksData().size());
        assertEquals(0, udc.getTaskData("Omer", "Sell").getPrevTasksData().size());
        assertEquals(0, udc.getTaskData("Omer", "Clean").getPrevTasksData().size());
        assertEquals(0, udc.getTaskData("Omer", "Clean").getNextTasksData().size());

        assertEquals(2, udc.getTaskManSystemData().getProjectsData().size());
        taskManSystem.createProject("Omer2", "Omer brewery project", new Time(20));
        assertEquals(3, udc.getTaskManSystemData().getProjectsData().size());
        assertEquals(new Time(11), udc.getTaskManSystemData().getSystemTime());
        taskManSystem.advanceTime(new Time(12));
        assertEquals(new Time(12), udc.getTaskManSystemData().getSystemTime());

        omer.login(ward);
        taskManSystem.createProject("Duvel", "Duvel brewery project", new Time(20));
        taskManSystem.addTaskToProject("Duvel", "Brew", "Brewing beer", new Time(10), 3, languages, new HashSet<>(), new HashSet<>());
        taskManSystem.addTaskToProject("Duvel", "Sell", "Selling beer", new Time(10), 3, languages, new HashSet<>(), new HashSet<>());
        taskManSystem.addTaskToProject("Duvel", "Clean", "Cleaning beer", new Time(10), 3, languages, new HashSet<>(), new HashSet<>());
        taskManSystem.addTaskToProject("Duvel", "Hire brewer", "Hiring a brewer", new Time(10), 3, languages, new HashSet<>(), new HashSet<>());
        taskManSystem.addTaskToProject("Duvel", "Hire cleaner", "Hiring a cleaner", new Time(10), 3, languages, new HashSet<>(), new HashSet<>());
        taskManSystem.addTaskToProject("Duvel", "Hire seller", "Hiring a seller", new Time(10), 3, languages, new HashSet<>(), new HashSet<>());
        udc.addNextTask("Duvel", "Brew", "Duvel","Sell");
        udc.addNextTask("Duvel", "Hire brewer", "Duvel", "Hire cleaner");
        udc.addPrevTask("Duvel", "Brew", "Duvel","Hire brewer");
        udc.addPrevTask("Duvel", "Clean", "Duvel","Hire cleaner");
        udc.addPrevTask("Duvel", "Sell", "Duvel","Hire seller");
        udc.addNextTask("Duvel", "Brew", "Duvel","Clean");

        assertEquals(udc.getTaskData("Duvel", "Brew").getPrevTasksData().size(), 1);
        assertEquals(udc.getTaskData("Duvel", "Brew").getNextTasksData().size(), 2);
        assertEquals(udc.getTaskData("Duvel", "Sell").getPrevTasksData().size(), 2);
        assertEquals(udc.getTaskData("Duvel", "Sell").getNextTasksData().size(), 0);
        assertEquals(udc.getTaskData("Duvel", "Clean").getPrevTasksData().size(), 2);
        assertEquals(udc.getTaskData("Duvel", "Clean").getNextTasksData().size(), 0);
        assertEquals(udc.getTaskData("Duvel", "Hire brewer").getPrevTasksData().size(), 0);
        assertEquals(udc.getTaskData("Duvel", "Hire brewer").getNextTasksData().size(), 2);
        assertEquals(udc.getTaskData("Duvel", "Hire cleaner").getPrevTasksData().size(), 1);
        assertEquals(udc.getTaskData("Duvel", "Hire cleaner").getNextTasksData().size(), 1);
        assertEquals(udc.getTaskData("Duvel", "Hire seller").getPrevTasksData().size(), 0);
        assertEquals(udc.getTaskData("Duvel", "Hire seller").getNextTasksData().size(), 1);

    }

}
