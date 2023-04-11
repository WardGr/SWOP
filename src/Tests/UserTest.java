package Tests;

import Domain.*;
import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.Task;
import Domain.TaskStates.TaskProxy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;


@RunWith(MockitoJUnitRunner.class)
public class UserTest {

    @Mock
    private Task task;

    @Mock
    private Task task2;

    @Mock
    private TaskProxy taskProxy;

    @Mock
    private TaskProxy taskProxy2;

    @Before
    public void setUp() {
        // See startTask, taskProxy moet PENDING status returnen.
        Mockito.when(task.getTaskProxy()).thenReturn(taskProxy);
        Mockito.when(task2.getTaskProxy()).thenReturn(taskProxy2);
        Mockito.when(taskProxy.getStatus()).thenReturn(Status.EXECUTING);
    }

    @Test
    public void testUser() throws IncorrectTaskStatusException, IncorrectRoleException, UserAlreadyAssignedToTaskException {

        Set<Role> roles = new HashSet<>();
        roles.add(Role.PROJECTMANAGER);
        User thomas = new User("Thomas", "banaan123", roles);
        assertEquals("Thomas", thomas.getUsername());
        assertEquals("banaan123", thomas.getPassword());
        assertEquals(roles, thomas.getRoles());
        assertNotEquals("Thomas", thomas.getPassword());
        assertNotEquals("banaan123", thomas.getUsername());
        roles.add(Role.PYTHONPROGRAMMER);
        assertNotSame(roles, thomas.getRoles());
        assertNotSame("banaan1234", thomas.getPassword());

        roles.add(Role.JAVAPROGRAMMER);
        User jonathan = new User("Jonathan", "perzik789", roles);
        assertEquals("Jonathan", jonathan.getUsername());
        assertEquals("perzik789", jonathan.getPassword());
        assertNotEquals("perzik7890", jonathan.getPassword());
        assertNotEquals("thomas", jonathan.getUsername());
        assertEquals(roles, jonathan.getRoles());
        assertFalse(jonathan.getRoles().contains(Role.SYSADMIN));

        assertThrows(IllegalArgumentException.class, () -> new User(null, null, null));

        assertThrows(IllegalArgumentException.class, () -> new User("Fiona", "hoi123", null));

        assertThrows(IllegalArgumentException.class, () -> new User("Fiona", null, roles));

        Set<Role> emptyRoles = new HashSet<>();
        assertThrows(IllegalArgumentException.class, () -> new User("Fiona", "hoi123", emptyRoles));

        assertThrows(IncorrectRoleException.class, () -> jonathan.assignTask(task, Role.SYSADMIN));

        jonathan.assignTask(task, Role.PROJECTMANAGER);
        // Jonathan is now assigned to this task, so it is not pending anymore

        assertThrows(UserAlreadyAssignedToTaskException.class, () -> jonathan.assignTask(task, Role.PROJECTMANAGER));

        Mockito.when(taskProxy.getStatus()).thenReturn(Status.PENDING);
        jonathan.assignTask(task2, Role.PROJECTMANAGER);
        // This should unassign jonathan to the pending task, and assign task as its current task

        assertEquals(taskProxy2, jonathan.getTaskData());


        jonathan.endTask();
        assertNull(jonathan.getTaskData());

    }
}