package Tests;

import Domain.*;
import Domain.TaskStates.Task;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.TaskProxy;
import Domain.TaskStates.NonDeveloperRoleException;
import Domain.TaskStates.IncorrectRoleException;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class TaskTest {
    @Mock
    private Project project1;

    @Mock
    private User user;

    @Mock
    private User sysAdmin;

    @Mock
    private User pythonProg;

    @Mock
    private User javaProg;

    private Task task;

    private Task prevTask;
    private Task currentTask;
    private Task nextTask;

    private Task task1;
    private Task task2;
    private Task task3;
    private Task task4;
    private Task replacementTask;


    @Before
    public void setUp() throws InvalidTimeException, IncorrectTaskStatusException, LoopDependencyGraphException, NonDeveloperRoleException {
        // Tasks for general task tests
        this.task = new Task("Task", "Test", new Time(100), 0.1, List.of(Role.PYTHONPROGRAMMER), Set.of(), Set.of(), project1);

        this.prevTask = new Task("Previous Task", "Test", new Time(10), 0.1, List.of(Role.SYSADMIN, Role.PYTHONPROGRAMMER), Set.of(), Set.of(), project1);
        this.currentTask = new Task("Current Task", "Test", new Time(100), 0.1, List.of(Role.SYSADMIN, Role.PYTHONPROGRAMMER, Role.JAVAPROGRAMMER), Set.of(prevTask), Set.of(), project1);
        this.nextTask = new Task("Next Task", "Test", new Time(10), 0.1, List.of(Role.SYSADMIN), Set.of(currentTask), Set.of(), project1);

        this.replacementTask = new Task("Replacement Task", "", new Time(20), 0);


        // Loop dependency check tasks
        List<Role> roles = List.of(Role.SYSADMIN, Role.JAVAPROGRAMMER);

        this.task1 = new Task("Task 1", "test", new Time(20), 0, roles, new HashSet<>(), new HashSet<>(), project1);
        this.task2 = new Task("Task 2", "test", new Time(20), 0, roles, Set.of(task1), new HashSet<>(), project1);
        this.task4 = new Task("Task 4", "test", new Time(20), 0, roles, new HashSet<>(), new HashSet<>(), project1);
        this.task3 = new Task("Task 3", "test", new Time(20), 0, roles, new HashSet<>(), Set.of(task4), project1);

        // Set stubs
        Mockito.when(project1.getName()).thenReturn("Project 1");
        Mockito.when(pythonProg.getUsername()).thenReturn("Python Programmer");
        Mockito.when(javaProg.getUsername()).thenReturn("Java Programmer");
        Mockito.when(sysAdmin.getUsername()).thenReturn("System Administrator");
    }


    @Test
    public void testTask() throws InvalidTimeException, IncorrectTaskStatusException, IncorrectRoleException, EndTimeBeforeStartTimeException, IncorrectUserException, LoopDependencyGraphException, NonDeveloperRoleException, UserAlreadyAssignedToTaskException {

        assertThrows(NonDeveloperRoleException.class, () -> new Task("", "", new Time(0), 0, List.of(Role.PROJECTMANAGER), Set.of(), Set.of(), project1));

        // Test if the task states are initialised correctly
        assertEquals(Status.AVAILABLE, prevTask.getStatus());
        assertEquals(Status.UNAVAILABLE, currentTask.getStatus());
        assertEquals(Status.UNAVAILABLE, nextTask.getStatus());

        // Check removal and appending of tasks, and checks if the states are correct afterwards
        currentTask.removeNextTask(nextTask);
        assertEquals(Status.AVAILABLE, nextTask.getStatus());

        currentTask.addNextTask(nextTask);
        assertEquals(Status.UNAVAILABLE, nextTask.getStatus());


        // Asserts it's impossible to add loops in the dependency graph
        assertThrows(LoopDependencyGraphException.class, () -> nextTask.addNextTask(currentTask));
        assertThrows(LoopDependencyGraphException.class, () -> new Task("", "", new Time(0), 0, List.of(), Set.of(prevTask), Set.of(prevTask), project1));

        Task testTask = new Task("", "", new Time(0), 0, List.of(), Set.of(), Set.of(prevTask), project1);
        assertThrows(LoopDependencyGraphException.class, () -> new Task("", "", new Time(0), 0, List.of(Role.PYTHONPROGRAMMER), Set.of(prevTask), Set.of(currentTask, testTask, nextTask), project1));
        prevTask.removePreviousTask(testTask);

        // Check basic getters
        assertEquals("Task", task.getName());
        assertEquals("Test", task.getDescription());
        assertEquals(task.getStatus(), Status.AVAILABLE);

        assertNull(task.getReplacesTask());
        assertNull(task.getReplacementTask());

        // Assert unavailable tasks cant be started, and tasks cannot be started with irrelevant roles
        assertThrows(IncorrectTaskStatusException.class, () -> currentTask.start(new Time(0), sysAdmin, Role.SYSADMIN));
        assertThrows(IncorrectRoleException.class, () -> prevTask.start(new Time(0), sysAdmin, Role.JAVAPROGRAMMER));
        assertThrows(IncorrectTaskStatusException.class, () -> currentTask.start(new Time(0), sysAdmin, Role.SYSADMIN));


        // Checks start and pending status
        task.start(new Time(0), pythonProg, Role.PYTHONPROGRAMMER);
        assertEquals(task.getStatus(), Status.EXECUTING);

        prevTask.start(new Time(0), pythonProg, Role.PYTHONPROGRAMMER);
        assertEquals(prevTask.getStatus(), Status.PENDING);


        // Checks unassigning users
        prevTask.unassignUser(pythonProg);
        assertEquals(prevTask.getStatus(), Status.AVAILABLE);

        prevTask.start(new Time(0), pythonProg, Role.PYTHONPROGRAMMER);
        assertEquals(prevTask.getStatus(), Status.PENDING);

        // Checks if pending tasks can't be started with the wrong roles
        assertThrows(IncorrectRoleException.class, () -> prevTask.start(new Time(0), pythonProg, Role.JAVAPROGRAMMER));

        // Start executing the pending task
        prevTask.start(new Time(0), sysAdmin, Role.SYSADMIN);
        assertEquals(prevTask.getStatus(), Status.EXECUTING);

        // Check if an executing task cannot be a next task for a new task, and that we cannot finish/fail a task with the wrong user
        assertThrows(IncorrectTaskStatusException.class, () -> new Task("", "", new Time(0), 0, List.of(), Set.of(), Set.of(prevTask), project1));
        assertThrows(IncorrectUserException.class, () -> prevTask.finish(user, new Time(10)));
        assertThrows(IncorrectUserException.class, () -> prevTask.fail(user, new Time(10)));

        // Finish the task
        prevTask.finish(sysAdmin, new Time(10));
        assertEquals(Status.FINISHED, prevTask.getStatus());

        // Checks if finished state cannot start/fail/finish/unassign/replace
        assertThrows(IncorrectTaskStatusException.class, () -> prevTask.start(new Time(0), user, Role.JAVAPROGRAMMER));
        assertThrows(IncorrectTaskStatusException.class, () -> prevTask.finish(pythonProg, new Time(0)));
        assertThrows(IncorrectTaskStatusException.class, () -> prevTask.fail(pythonProg, new Time(0)));
        assertThrows(IncorrectTaskStatusException.class, () -> prevTask.unassignUser(pythonProg));
        assertThrows(IncorrectTaskStatusException.class, () -> prevTask.replaceTask(task));

        // Checks if the next task becomes available once prevTask is finished
        assertEquals(Status.AVAILABLE, currentTask.getStatus());

        // Checks if next tasks are properly added upon task creation
        Task test = new Task("", "", new Time(0), 0, List.of(), Set.of(), Set.of(currentTask), project1);
        currentTask.removePreviousTask(test);
        assertEquals(Status.AVAILABLE, currentTask.getStatus());

        // Asserts it's impossible to start a task before the end time of one of its finished previous tasks
        assertThrows(IncorrectTaskStatusException.class, () -> currentTask.start(new Time(5), sysAdmin, Role.SYSADMIN));

        // Checks removing and adding of tasks to available task
        currentTask.removePreviousTask(prevTask);
        assertEquals(Status.AVAILABLE, currentTask.getStatus());
        currentTask.addPreviousTask(prevTask);
        assertEquals(Status.AVAILABLE, currentTask.getStatus());

        // Assign a first user to this task, ensuring it is pending
        currentTask.start(new Time(10), sysAdmin, Role.SYSADMIN);
        assertEquals(Status.PENDING, currentTask.getStatus());


        // Make sure you cannot assign a user to a pending task with start time before the end time of any of its previous tasks
        assertThrows(IncorrectTaskStatusException.class, () -> currentTask.start(new Time(5), pythonProg, Role.PYTHONPROGRAMMER));

        // Assign a second user to this task, ensuring it is still pending
        currentTask.start(new Time(10), pythonProg, Role.PYTHONPROGRAMMER);
        assertEquals(Status.PENDING, currentTask.getStatus());

        // Assign the final user to this task, ensuring it is executing
        currentTask.start(new Time(10), javaProg, Role.JAVAPROGRAMMER);
        assertEquals(Status.EXECUTING, currentTask.getStatus());

        // Fail the current task
        currentTask.fail(pythonProg, new Time(15));
        assertEquals(Status.FAILED, currentTask.getStatus());
        assertEquals(Status.UNAVAILABLE, nextTask.getStatus());

        currentTask.replaceTask(replacementTask);

        Task replacementTask = currentTask.getReplacementTask();

        // TASK PROXY REPLACED TASK

        TaskProxy taskProxyFailed = currentTask.getTaskProxy();

        assertEquals("Current Task", taskProxyFailed.getName());
        assertEquals("Test", taskProxyFailed.getDescription());
        assertEquals(new Time(100), taskProxyFailed.getEstimatedDuration());
        assertEquals(0.1, taskProxyFailed.getAcceptableDeviation(), 0.00001);
        assertEquals(Status.FAILED, taskProxyFailed.getStatus());
        assertEquals("Replacement Task", taskProxyFailed.getReplacementTaskName());
        assertEquals(new Time(10), taskProxyFailed.getStartTime());
        assertEquals(new Time(15), taskProxyFailed.getEndTime());
        assertEquals(List.of(), taskProxyFailed.getRequiredRoles());
        assertEquals("Project 1", taskProxyFailed.getProjectName());
        assertFalse(taskProxyFailed.canSafelyAddPrevTask("Current Task"));
        assertNull(taskProxyFailed.getReplacesTaskName());

        Map<String, Role> userRoleMap = new HashMap<>();

        userRoleMap.put("System Administrator", Role.SYSADMIN);
        userRoleMap.put("Python Programmer", Role.PYTHONPROGRAMMER);
        userRoleMap.put("Java Programmer", Role.JAVAPROGRAMMER);

        assertEquals(userRoleMap, taskProxyFailed.getUserNamesWithRole());

        // TASK PROXY WITH REPLACEMENT TASK

        TaskProxy replacementProxy = replacementTask.getTaskProxy();

        assertNull(replacementProxy.getReplacementTaskName());
        assertEquals("Current Task", replacementProxy.getReplacesTaskName());
        assertEquals(List.of("Previous Task"), replacementProxy.getPreviousTasksNames());
        assertEquals(List.of("Next Task"), replacementProxy.getNextTasksNames());
        assertNull(replacementProxy.getEndTime());
        assertNull(replacementProxy.getStartTime());


        task2.addNextTask(task3);
        task4.addPreviousTask(task1);

        TaskProxy taskProxy1 = task1.getTaskProxy();
        TaskProxy taskProxy2 = task2.getTaskProxy();
        TaskProxy taskProxy3 = task3.getTaskProxy();
        TaskProxy taskProxy4 = task4.getTaskProxy();


        assertFalse(taskProxy1.canSafelyAddPrevTask("Task 1"));
        assertFalse(taskProxy1.canSafelyAddPrevTask("Task 2"));
        assertFalse(taskProxy1.canSafelyAddPrevTask("Task 3"));
        assertFalse(taskProxy1.canSafelyAddPrevTask("Task 4"));

        assertTrue(taskProxy2.canSafelyAddPrevTask("Task 1"));
        assertFalse(taskProxy2.canSafelyAddPrevTask("Task 2"));
        assertFalse(taskProxy2.canSafelyAddPrevTask("Task 3"));
        assertFalse(taskProxy2.canSafelyAddPrevTask("Task 4"));

        assertTrue(taskProxy3.canSafelyAddPrevTask("Task 1"));
        assertTrue(taskProxy3.canSafelyAddPrevTask("Task 2"));
        assertFalse(taskProxy3.canSafelyAddPrevTask("Task 3"));
        assertFalse(taskProxy3.canSafelyAddPrevTask("Task 4"));

        assertTrue(taskProxy4.canSafelyAddPrevTask("Task 1"));
        assertTrue(taskProxy4.canSafelyAddPrevTask("Task 2"));
        assertTrue(taskProxy4.canSafelyAddPrevTask("Task 3"));
        assertFalse(taskProxy4.canSafelyAddPrevTask("Task 4"));

    }
}
