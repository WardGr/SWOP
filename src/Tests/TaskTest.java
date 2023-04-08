package Tests;

import Domain.*;
import Domain.TaskStates.*;
import Domain.TaskStates.Task;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class TaskTest {
    @Mock
    private Project project1;


    @Before
    public void setUp() {
        // TODO
    }


    @Test
    public void testTask() throws IncorrectUserException, IncorrectTaskStatusException, InvalidTimeException, DueBeforeSystemTimeException, LoopDependencyGraphException, NonDeveloperRoleException, TaskNotFoundException, UserAlreadyExecutingTaskException, IncorrectRoleException, TaskNameAlreadyInUseException {
        Project project1 = new Project("Project 1", "test", new Time(0,0), new Time(50,0));
        // TODO: waarom start ge de tasks via de project dieter
        List<Role> roles = new LinkedList<>();
        roles.add(Role.JAVAPROGRAMMER);
        roles.add(Role.JAVAPROGRAMMER);
        roles.add(Role.SYSADMIN);
        project1.addNewTask("Task 1", "test", new Time(10, 20), 0.2, roles, new HashSet<>(), new HashSet<>());
        Task task1 = project1.getTask("Task 1");


        roles.add(Role.PROJECTMANAGER);
        assertEquals(3,task1.getTaskData().getRequiredRoles().size());
        assertThrows(NonDeveloperRoleException.class, () -> new Task("TEST", "test", new Time(10,20), 0.2, roles, new HashSet<>(), new HashSet<>(), project1));


        // Test TaskProxy
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(Role.JAVAPROGRAMMER);
        User user = new User("Dieter", "123", userRoles);
        task1.start(new Time(15), user, Role.JAVAPROGRAMMER);
        task1.start(new Time(15), user, Role.JAVAPROGRAMMER);
        task1.start(new Time(15), user, Role.JAVAPROGRAMMER);
        assertEquals(2,task1.getTaskData().getRequiredRoles().size());
        assertEquals(1,task1.getTaskData().getUserNamesWithRole().size());
        assertEquals(Status.PENDING, task1.getTaskData().getStatus());
        assertEquals(Status.PENDING, user.getTaskData().getStatus());



        User user2 = new User("Dieter2", "123", userRoles);
        //project1.startTask("Task 1", new Time(15), user, Role.SYSADMIN); -> role not in user
        project1.startTask("Task 1", new Time(15), user2, Role.JAVAPROGRAMMER);
        //project1.startTask("Task 1", new Time(15), user, Role.JAVAPROGRAMMER); -> role not needed
        assertEquals(Status.PENDING, task1.getTaskData().getStatus());

        userRoles.add(Role.SYSADMIN);
        User user3 = new User("Dieter3", "123", userRoles);
        project1.startTask("Task 1", new Time(15), user3, Role.SYSADMIN);
        assertEquals(Status.EXECUTING, task1.getTaskData().getStatus());
        assertEquals(0, task1.getTaskData().getRequiredRoles().size());
        assertEquals(Status.EXECUTING, user.getTaskData().getStatus());
        assertEquals(Status.EXECUTING, user2.getTaskData().getStatus());
        assertEquals(Status.EXECUTING, user3.getTaskData().getStatus());

        project1.failTask("Task 1", user3, new Time(30));
        assertNull(user.getTaskData());
        assertNull(user2.getTaskData());
        assertNull(user3.getTaskData());
        assertEquals(Status.FAILED, task1.getTaskData().getStatus());
        assertEquals(3, task1.getTaskData().getRequiredRoles().size());
        assertEquals(0, task1.getTaskData().getUserNamesWithRole().size());








    }
}
