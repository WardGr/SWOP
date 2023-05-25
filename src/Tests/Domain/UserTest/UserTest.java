package Tests.Domain.UserTest;

import Domain.*;
import Domain.TaskStates.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;


@RunWith(MockitoJUnitRunner.class)
public class UserTest {

    @Mock
    private Task task;

    @Mock
    private Task task2;

    @Mock
    private TaskData taskData;

    @Mock
    private TaskData taskData2;

    private User user;

    @Before
    public void setUp() {
        Mockito.when(task.getTaskData()).thenReturn(taskData);
        Mockito.when(task2.getTaskData()).thenReturn(taskData2);

        user = new User("User", "123", Set.of(Role.SYSADMIN));
    }

    @Test
    public void testUserCreation(){
        User testUser = new User("Test", "123", Set.of(Role.SYSADMIN, Role.PROJECTMANAGER));
        assertEquals("Test", testUser.getUsername());
        assertEquals("123", testUser.getPassword());
        assertEquals(Set.of(Role.PROJECTMANAGER, Role.SYSADMIN), testUser.getRoles());

        assertThrows(IllegalArgumentException.class, () -> new User(null, "123", Set.of(Role.SYSADMIN)));
        assertThrows(IllegalArgumentException.class, () -> new User("Test", null, Set.of(Role.SYSADMIN)));
        assertThrows(IllegalArgumentException.class, () -> new User("Test", "123", null));
        assertThrows(IllegalArgumentException.class, () -> new User("Test", "123", new HashSet<>()));
    }

    @Test
    public void testAssignTask() throws UserAlreadyAssignedToTaskException, IncorrectRoleException {
        assertThrows(IncorrectRoleException.class, () -> user.assignTask(task, Role.JAVAPROGRAMMER));

        user.assignTask(task, Role.SYSADMIN);
        assertEquals(task.getTaskData(), user.getTaskData());
    }

    @Test
    public void testReplaceTask() throws UserAlreadyAssignedToTaskException, IncorrectRoleException {
        user.assignTask(task2, Role.SYSADMIN);
        assertEquals(task2.getTaskData(), user.getTaskData());

        user.assignTask(task, Role.SYSADMIN);
        assertEquals(task.getTaskData(), user.getTaskData());
    }

    @Test
    public void testFailedReplaceTask() throws UserAlreadyAssignedToTaskException, IncorrectRoleException, InvalidTimeException, IncorrectTaskStatusException, IllegalTaskRolesException, LoopDependencyGraphException {
        Task executingTask = new Task("Test", "Descr", new Time(5), 0.1, List.of(Role.SYSADMIN), new HashSet<>(), new HashSet<>(), "project");
        executingTask.start(new Time(0), user, Role.SYSADMIN);
        assertThrows(UserAlreadyAssignedToTaskException.class, () -> user.assignTask(task2,Role.SYSADMIN));

        assertEquals(executingTask.getTaskData(), user.getTaskData());
    }

    @Test
    public void testEndAssignedTask() throws UserAlreadyAssignedToTaskException, IncorrectRoleException {
        user.assignTask(task, Role.SYSADMIN);
        assertEquals(task.getTaskData(), user.getTaskData());

        user.endTask();
        assertNull(user.getTaskData());
    }
}