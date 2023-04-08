package Tests;

import Domain.IncorrectTaskStatusException;
import Domain.Role;
import Domain.Status;
import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.Task;
import Domain.TaskStates.TaskProxy;
import Domain.User;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;


import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;



@RunWith(MockitoJUnitRunner.class)
public class UserTest {

    @Mock
    private Task task;

    @Mock
    private TaskProxy taskProxy;


    // Mockito makes it so we only have to specify specific functions (Getters etc) from the mocked classes,
    // thus decoupling the individual classes from each other so we can specify one unit to test.
    @Before
    public void setUp() {
        // See startTask, taskProxy moet PENDING status returnen.
        Mockito.when(task.getTaskData()).thenReturn(taskProxy);
        Mockito.when(taskProxy.getStatus()).thenReturn(Status.EXECUTING);
    }

    @Test
    public void testUser() throws IncorrectTaskStatusException, IncorrectRoleException {

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

        assertThrows(IllegalArgumentException.class, () -> {
            new User(null, null, null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new User("Fiona", "hoi123", null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new User("Fiona", null, roles);
        });

        Set<Role> emptyRoles = new HashSet<>();
        assertThrows(IllegalArgumentException.class, () -> {
            new User("Fiona", "hoi123", emptyRoles);
        });

        assertThrows(IncorrectRoleException.class, () -> {
            jonathan.startTask(task, Role.SYSADMIN);
        });

        jonathan.startTask(task, Role.PROJECTMANAGER);
        // Jonathan is now assigned to this task, so it is not pending anymore

        assertThrows(IncorrectTaskStatusException.class, () -> {
            jonathan.startTask(task, Role.PROJECTMANAGER);
        });

    }
}