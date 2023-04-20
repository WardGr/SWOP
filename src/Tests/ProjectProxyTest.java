package Tests;

import Domain.*;
import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.IllegalTaskRolesException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class ProjectProxyTest {
    @Test
    public void test() throws InvalidTimeException, DueTimeBeforeCreationTimeException, TaskNameAlreadyInUseException, TaskNotFoundException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException, IllegalTaskRolesException, UserAlreadyAssignedToTaskException, IncorrectRoleException, EndTimeBeforeStartTimeException, IncorrectUserException {
        // Testing without replaces
        Project omer = new Project("Omer Brewery", "Process of the Omer brewery", new Time(13), new Time(20));
        List<Role> roles = new ArrayList<>();
        roles.add(Role.JAVAPROGRAMMER);
        roles.add(Role.PYTHONPROGRAMMER);
        omer.addNewTask("Hire brewer", "Hire a suitable brewer", new Time(5), .2, roles, new HashSet<>(), new HashSet<>());
        omer.addNewTask("Brew beer", "Have the brewer brew our delicious beer", new Time(30), 0.5, roles, new HashSet<>(), new HashSet<>());
        omer.addNextTask("Hire brewer", "Brew beer");
        omer.addNewTask("Sell beer", "Sell the beer", new Time(10), 0.3, roles, new HashSet<>(), new HashSet<>());
        omer.addNextTask("Brew beer", "Sell beer");
        ProjectProxy projectProxy = new ProjectProxy(omer);

        assertEquals("Omer Brewery", projectProxy.getName());
        assertEquals("Process of the Omer brewery", projectProxy.getDescription());
        assertEquals(new Time(13), projectProxy.getCreationTime());
        assertEquals(new Time(20), projectProxy.getDueTime());
        assertEquals(3, projectProxy.getActiveTasksNames().size());
        assertEquals(0, projectProxy.getReplacedTasksNames().size());

        Set javaRoles = new HashSet();
        javaRoles.add(Role.JAVAPROGRAMMER);
        User java = new User("Java", "Java", javaRoles);
        Set pythonRoles = new HashSet();
        pythonRoles.add(Role.PYTHONPROGRAMMER);
        User python = new User("Python", "Python", pythonRoles);

        assertEquals(ProjectStatus.ONGOING, projectProxy.getStatus());

        omer.startTask("Hire brewer", new Time(14), java, Role.JAVAPROGRAMMER);
        omer.startTask("Hire brewer", new Time(15), python, Role.PYTHONPROGRAMMER);
        omer.finishTask("Hire brewer", java,  new Time(16));

        assertEquals(ProjectStatus.ONGOING, projectProxy.getStatus());
        assertEquals(3, projectProxy.getActiveTasksNames().size());
        assertEquals(0, projectProxy.getReplacedTasksNames().size());

        omer.startTask("Brew beer", new Time(17), java, Role.JAVAPROGRAMMER);
        omer.startTask("Brew beer", new Time(17), python, Role.PYTHONPROGRAMMER);
        omer.finishTask("Brew beer", python,  new Time(19));

        assertEquals(ProjectStatus.ONGOING, projectProxy.getStatus());
        assertEquals(3, projectProxy.getActiveTasksNames().size());
        assertEquals(0, projectProxy.getReplacedTasksNames().size());

        omer.startTask("Sell beer", new Time(19), java, Role.JAVAPROGRAMMER);
        omer.startTask("Sell beer", new Time(19), python, Role.PYTHONPROGRAMMER);
        omer.finishTask("Sell beer", python,  new Time(20));

        assertEquals(ProjectStatus.FINISHED, projectProxy.getStatus());
        assertEquals(3, projectProxy.getActiveTasksNames().size());
        assertEquals(0, projectProxy.getReplacedTasksNames().size());


        // Testing with replaces
        Project Duvel = new Project("Duvel Moortgat", "Distillery of the Duvel Barrel aged beer", new Time(2), new Time(50));
        roles = new ArrayList<>();
        roles.add(Role.JAVAPROGRAMMER);

        Duvel.addNewTask("Hire destiller", "Hire a suitable destiller", new Time(5), .2, roles, new HashSet<>(), new HashSet<>());
        Duvel.addNewTask("Distill beer", "Have the destiller distill our delicious beer", new Time(30), 0.5, roles, new HashSet<>(), new HashSet<>());
        Duvel.addNewTask("Bottle beer", "Bottle the beer", new Time(10), 0.3, roles, new HashSet<>(), new HashSet<>());
        ProjectProxy projectProxyDuvel = new ProjectProxy(Duvel);

        assertEquals("Duvel Moortgat", projectProxyDuvel.getName());
        assertEquals("Distillery of the Duvel Barrel aged beer", projectProxyDuvel.getDescription());
        assertEquals(new Time(2), projectProxyDuvel.getCreationTime());
        assertEquals(new Time(50), projectProxyDuvel.getDueTime());
        assertEquals(3, projectProxyDuvel.getActiveTasksNames().size());
        assertEquals(0, projectProxyDuvel.getReplacedTasksNames().size());

        Duvel.startTask("Hire destiller", new Time(14), java, Role.JAVAPROGRAMMER);
        Duvel.failTask("Hire destiller", java, new Time(15));
        Duvel.replaceTask("Steal destiller", "Get chouffe destiller to work for us", new Time(3), .1, "Hire destiller");

        assertEquals(3, projectProxyDuvel.getActiveTasksNames().size());
        assertEquals(1, projectProxyDuvel.getReplacedTasksNames().size());


        assertTrue(projectProxyDuvel.getActiveTasksNames().contains("Steal destiller"));
        assertFalse(projectProxyDuvel.getActiveTasksNames().contains("Hire destiller"));
        assertTrue(projectProxyDuvel.getReplacedTasksNames().contains("Hire destiller"));

        assertEquals(ProjectStatus.ONGOING, projectProxyDuvel.getStatus());

        Duvel.startTask("Steal destiller", new Time(16), java, Role.JAVAPROGRAMMER);
        Duvel.finishTask("Steal destiller", java, new Time(19));

        assertEquals(3, projectProxyDuvel.getActiveTasksNames().size());
        assertEquals(1, projectProxyDuvel.getReplacedTasksNames().size());
        assertEquals(ProjectStatus.ONGOING, projectProxyDuvel.getStatus());

        Duvel.startTask("Distill beer", new Time(19), java, Role.JAVAPROGRAMMER);
        Duvel.finishTask("Distill beer", java, new Time(40));

        assertEquals(3, projectProxyDuvel.getActiveTasksNames().size());
        assertEquals(1, projectProxyDuvel.getReplacedTasksNames().size());
        assertEquals(ProjectStatus.ONGOING, projectProxyDuvel.getStatus());

        Duvel.startTask("Bottle beer", new Time(40), java, Role.JAVAPROGRAMMER);
        Duvel.failTask("Bottle beer", java, new Time(45)); // woops, the bottle broke!

        Duvel.replaceTask("Bottle beer 2", "Bottle beer again", new Time(10), 0.3, "Bottle beer");
        Duvel.startTask("Bottle beer 2", new Time(45), java, Role.JAVAPROGRAMMER);
        Duvel.finishTask("Bottle beer 2", java, new Time(50));

        assertEquals(3, projectProxyDuvel.getActiveTasksNames().size());
        assertEquals(2, projectProxyDuvel.getReplacedTasksNames().size());
        assertEquals(ProjectStatus.FINISHED, projectProxyDuvel.getStatus());

        assertTrue(projectProxyDuvel.getActiveTasksNames().contains("Bottle beer 2"));
        assertFalse(projectProxyDuvel.getActiveTasksNames().contains("Bottle beer"));
        assertTrue(projectProxyDuvel.getReplacedTasksNames().contains("Bottle beer"));
        assertFalse(projectProxyDuvel.getReplacedTasksNames().contains("Bottle beer 2"));
    }
}
