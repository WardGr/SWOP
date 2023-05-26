package Tests.ControllerTests;

import Application.*;
import Domain.DataClasses.EndTimeBeforeStartTimeException;
import Domain.DataClasses.InvalidTimeException;
import Domain.Project.DueTimeBeforeCreationTimeException;
import Domain.Project.ProjectNameAlreadyInUseException;
import Domain.Project.ProjectNotOngoingException;
import Domain.Project.TaskNotFoundException;
import Domain.Task.IncorrectTaskStatusException;
import Domain.Task.TaskNameAlreadyInUseException;
import Domain.TaskManSystem.DueBeforeSystemTimeException;
import Domain.TaskManSystem.NewTimeBeforeSystemTimeException;
import Domain.TaskManSystem.ProjectNotFoundException;
import Domain.Task.LoopDependencyGraphException;
import Domain.Task.IllegalTaskRolesException;
import Domain.User.IncorrectUserException;
import org.junit.Test;

public class UpdateDependenciesControllerTest {

    @Test
    public void testUpdateDependenciesController() throws InvalidTimeException, IncorrectPermissionException, ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, IncorrectTaskStatusException, IncorrectUserException, NewTimeBeforeSystemTimeException, EndTimeBeforeStartTimeException, DueTimeBeforeCreationTimeException, LoopDependencyGraphException, ProjectNotOngoingException, IllegalTaskRolesException {
        /*
        Session omer = new Session();
        SessionProxy omerWrapper = new SessionProxy(omer);
        TaskManSystem tms = new TaskManSystem(new Time(11));
        User ward = new User("Ward", "ward123", Set.of(Role.JAVAPROGRAMMER, Role.PROJECTMANAGER));

        UpdateDependenciesController udc = new UpdateDependenciesController(omerWrapper, tms);


        User incorrectUserOne = new User("IncorrectOne", "incorrect123", Set.of(Role.JAVAPROGRAMMER));
        User incorrectUserTwo = new User("IncorrectTwo", "incorrect123", Set.of(Role.JAVAPROGRAMMER, Role.SYSADMIN));
        User incorrectUserThree = new User("IncorrectThree", "incorrect123", Set.of(Role.JAVAPROGRAMMER, Role.SYSADMIN, Role.PYTHONPROGRAMMER));

        omer.login(incorrectUserOne);
        assertFalse(udc.updateDependenciesPreconditions());

        omer.logout();
        omer.login(incorrectUserTwo);
        assertFalse(udc.updateDependenciesPreconditions());
        assertThrows(IncorrectPermissionException.class, () -> udc.addPrevTask("Project", "Task", "PreviousTask"));
        assertThrows(IncorrectPermissionException.class, () -> udc.addNextTask("Project", "Task", "PreviousTask"));
        assertThrows(IncorrectPermissionException.class, () -> udc.getProjectData("Project"));
        assertThrows(IncorrectPermissionException.class, () -> udc.getTaskData("Project", "Task"));
        assertThrows(IncorrectPermissionException.class, () -> udc.getTaskManSystemData());
        assertThrows(IncorrectPermissionException.class, () -> udc.removeNextTask("Project", "Task", "PreviousTask"));
        assertThrows(IncorrectPermissionException.class, () -> udc.removePrevTask("Project", "Task", "PreviousTask"));



        omer.logout();
        omer.login(incorrectUserThree);
        assertFalse(udc.updateDependenciesPreconditions());
        assertThrows(IncorrectPermissionException.class, () -> udc.addPrevTask("Project", "Task", "PreviousTask"));
        assertThrows(IncorrectPermissionException.class, () -> udc.getProjectData("Project"));
        assertThrows(IncorrectPermissionException.class, () -> udc.getTaskData("Project", "Task"));
        assertThrows(IncorrectPermissionException.class, () -> udc.getTaskManSystemData());
        assertThrows(IncorrectPermissionException.class, () -> udc.addNextTask("Project", "Task", "PreviousTask"));
        assertThrows(IncorrectPermissionException.class, () -> udc.removeNextTask("Project", "Task", "PreviousTask"));
        assertThrows(IncorrectPermissionException.class, () -> udc.removePrevTask("Project", "Task", "PreviousTask"));



        omer.logout();
        List<Role> languages = new LinkedList<>();
        languages.add(Role.JAVAPROGRAMMER);
        languages.add(Role.PYTHONPROGRAMMER);
        omer.login(ward);
        taskManSystem.createProject("Omer", "Omer brewery project", new Time(20));
        taskManSystem.addTaskToProject("Omer", "Brew", "Brewing beer", new Time(10), 3, languages, new HashSet<>(), new HashSet<>());
        taskManSystem.addTaskToProject("Omer", "Sell", "Selling beer", new Time(10), 3, languages, new HashSet<>(), new HashSet<>());
        taskManSystem.addTaskToProject("Omer", "Clean", "Cleaning beer", new Time(10), 3, languages, new HashSet<>(), new HashSet<>());
        udc.addNextTask("Omer", "Brew", "Sell");
        udc.addPrevTask("Omer", "Clean", "Sell");

        assertEquals(udc.getProjectData("Omer").getName(), "Omer");
        assertEquals(udc.getProjectData("Omer").getCreationTime(), new Time(11));
        assertEquals(udc.getProjectData("Omer").getDueTime(), new Time(20));
        assertEquals(udc.getProjectData("Omer").getDescription(), "Omer brewery project");
        assertTrue(udc.updateDependenciesPreconditions());
        assertEquals(0, udc.getTaskData("Omer", "Brew").getPrevTaskNames().size());
        assertEquals(1, udc.getTaskData("Omer", "Brew").getNextTasksNames().size());
        assertEquals(1, udc.getTaskData("Omer", "Sell").getPrevTaskNames().size());
        assertEquals(1, udc.getTaskData("Omer", "Sell").getNextTasksNames().size());
        assertEquals(1, udc.getTaskData("Omer", "Clean").getPrevTaskNames().size());
        assertEquals(0, udc.getTaskData("Omer", "Clean").getNextTasksNames().size());

        assertTrue(udc.getTaskData("Omer", "Brew").getNextTasksNames().contains("Sell"));
        assertTrue(udc.getTaskData("Omer", "Sell").getPrevTaskNames().contains("Brew"));
        assertTrue(udc.getTaskData("Omer", "Clean").getPrevTaskNames().contains("Sell"));
        assertTrue(udc.getTaskData("Omer", "Sell").getNextTasksNames().contains("Clean"));

        udc.removeNextTask("Omer", "Brew", "Sell");
        assertEquals(0, udc.getTaskData("Omer", "Brew").getNextTasksNames().size());
        assertEquals(0, udc.getTaskData("Omer", "Sell").getPrevTaskNames().size());
        assertEquals(1, udc.getTaskData("Omer", "Clean").getPrevTaskNames().size());
        assertEquals(0, udc.getTaskData("Omer", "Clean").getNextTasksNames().size());

        udc.removePrevTask("Omer", "Clean", "Sell");
        assertEquals(0, udc.getTaskData("Omer", "Brew").getNextTasksNames().size());
        assertEquals(0, udc.getTaskData("Omer", "Sell").getPrevTaskNames().size());
        assertEquals(0, udc.getTaskData("Omer", "Clean").getPrevTaskNames().size());
        assertEquals(0, udc.getTaskData("Omer", "Clean").getNextTasksNames().size());

        assertEquals(1, udc.getTaskManSystemData().getProjectNames().size());
        taskManSystem.createProject("Omer2", "Omer brewery project", new Time(20));
        assertEquals(2, udc.getTaskManSystemData().getProjectNames().size());
        assertEquals(new Time(11), udc.getTaskManSystemData().getSystemTime());
        taskManSystem.advanceTime(new Time(12));
        assertEquals(new Time(12), udc.getTaskManSystemData().getSystemTime());

        omer.logout();
        assertThrows(IncorrectPermissionException.class, () -> udc.getProjectData("Project"));
        assertThrows(IncorrectPermissionException.class, () -> udc.getTaskData("Project", "Task"));
        assertThrows(IncorrectPermissionException.class, () -> udc.getTaskManSystemData());
        assertThrows(IncorrectPermissionException.class, () -> udc.removeNextTask("Project", "Task", "PreviousTask"));
        assertThrows(IncorrectPermissionException.class, () -> udc.removePrevTask("Project", "Task", "PreviousTask"));

        omer.login(ward);
        taskManSystem.createProject("Duvel", "Duvel brewery project", new Time(20));
        taskManSystem.addTaskToProject("Duvel", "Brew", "Brewing beer", new Time(10), 3, languages, new HashSet<>(), new HashSet<>());
        taskManSystem.addTaskToProject("Duvel", "Sell", "Selling beer", new Time(10), 3, languages, new HashSet<>(), new HashSet<>());
        taskManSystem.addTaskToProject("Duvel", "Clean", "Cleaning beer", new Time(10), 3, languages, new HashSet<>(), new HashSet<>());
        taskManSystem.addTaskToProject("Duvel", "Hire brewer", "Hiring a brewer", new Time(10), 3, languages, new HashSet<>(), new HashSet<>());
        taskManSystem.addTaskToProject("Duvel", "Hire cleaner", "Hiring a cleaner", new Time(10), 3, languages, new HashSet<>(), new HashSet<>());
        taskManSystem.addTaskToProject("Duvel", "Hire seller", "Hiring a seller", new Time(10), 3, languages, new HashSet<>(), new HashSet<>());
        udc.addNextTask("Duvel", "Brew", "Sell");
        udc.addNextTask("Duvel", "Hire brewer", "Hire cleaner");
        udc.addPrevTask("Duvel", "Brew", "Hire brewer");
        udc.addPrevTask("Duvel", "Clean", "Hire cleaner");
        udc.addPrevTask("Duvel", "Sell", "Hire seller");
        udc.addNextTask("Duvel", "Brew", "Clean");

        assertEquals(udc.getTaskData("Duvel", "Brew").getPrevTaskNames().size(), 1);
        assertEquals(udc.getTaskData("Duvel", "Brew").getNextTasksNames().size(), 2);
        assertEquals(udc.getTaskData("Duvel", "Sell").getPrevTaskNames().size(), 2);
        assertEquals(udc.getTaskData("Duvel", "Sell").getNextTasksNames().size(), 0);
        assertEquals(udc.getTaskData("Duvel", "Clean").getPrevTaskNames().size(), 2);
        assertEquals(udc.getTaskData("Duvel", "Clean").getNextTasksNames().size(), 0);
        assertEquals(udc.getTaskData("Duvel", "Hire brewer").getPrevTaskNames().size(), 0);
        assertEquals(udc.getTaskData("Duvel", "Hire brewer").getNextTasksNames().size(), 2);
        assertEquals(udc.getTaskData("Duvel", "Hire cleaner").getPrevTaskNames().size(), 1);
        assertEquals(udc.getTaskData("Duvel", "Hire cleaner").getNextTasksNames().size(), 1);
        assertEquals(udc.getTaskData("Duvel", "Hire seller").getPrevTaskNames().size(), 0);
        assertEquals(udc.getTaskData("Duvel", "Hire seller").getNextTasksNames().size(), 1);

         */

    }

}
