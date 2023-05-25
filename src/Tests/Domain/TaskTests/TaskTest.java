package Tests.Domain.TaskTests;

import Domain.*;
import Domain.TaskStates.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class TaskTest {

    @Mock
    private User user;

    private User sysAdmin;

    private User pythonProg;
    private User javaProg;

    private Task task;

    private Task prevTask;
    private Task currentTask;
    private Task nextTask;
    private Task multipleRolesTask;

    private Task task1;
    private Task task2;
    private Task task3;
    private Task task4;
    private Task replacementTask;


    @Before
    public void setUp() throws InvalidTimeException, IncorrectTaskStatusException, LoopDependencyGraphException, IllegalTaskRolesException {
        this.sysAdmin = new User("sysAdmin", "123", Set.of(Role.SYSADMIN, Role.PYTHONPROGRAMMER));
        this.javaProg = new User("javaProg", "123", Set.of(Role.JAVAPROGRAMMER));
        this.pythonProg = new User("pythonProg", "123", Set.of(Role.PYTHONPROGRAMMER));

        // Tasks for general task tests
        this.task = new Task("Task", "Test", new Time(100), 0.1, List.of(Role.PYTHONPROGRAMMER), Set.of(), Set.of(), "project1");

        this.prevTask = new Task("Previous Task", "Test", new Time(10), 0.1, List.of(Role.SYSADMIN), Set.of(), Set.of(), "project1");
        this.currentTask = new Task("Current Task", "Test", new Time(100), 0.1, List.of(Role.SYSADMIN, Role.JAVAPROGRAMMER), Set.of(prevTask), Set.of(), "project1");
        this.nextTask = new Task("Next Task", "Test", new Time(10), 0.1, List.of(Role.SYSADMIN), Set.of(currentTask), Set.of(), "project1");

        this.replacementTask = new Task("Replacement Task", "", new Time(20), 0);
        this.multipleRolesTask = new Task("MR", "test", new Time(20), 0.2, List.of(Role.SYSADMIN, Role.PYTHONPROGRAMMER, Role.JAVAPROGRAMMER), new HashSet<>(), new HashSet<>(), "project1");

        // Loop dependency check tasks
        List<Role> roles = List.of(Role.SYSADMIN);

        this.task1 = new Task("Task 1", "test", new Time(20), 0, roles, new HashSet<>(), new HashSet<>(), "project1");
        this.task2 = new Task("Task 2", "test", new Time(20), 0, roles, Set.of(task1), new HashSet<>(), "project2");
        this.task4 = new Task("Task 4", "test", new Time(20), 0, roles, new HashSet<>(), new HashSet<>(), "project4");
        this.task3 = new Task("Task 3", "test", new Time(20), 0, roles, new HashSet<>(), Set.of(task4), "project3");

    }

    @Test
    public void testTaskCreation() throws InvalidTimeException, IllegalTaskRolesException, IncorrectTaskStatusException, LoopDependencyGraphException {
        List<Role> roles = List.of(Role.SYSADMIN, Role.JAVAPROGRAMMER);
        Task creation = new Task("Creation", "test", new Time(20), 0, roles, new HashSet<>(), new HashSet<>(), "project1");
        assertEquals("Creation", creation.getName());
        assertEquals("Creation", creation.getTaskData().getName());
        assertEquals("test", creation.getDescription());
        assertEquals(new Time(20), creation.getEstimatedDuration());
        assertEquals(0, creation.getAcceptableDeviation(),0);
        assertEquals(List.of(Role.SYSADMIN, Role.JAVAPROGRAMMER), creation.getUnfulfilledRoles());
        assertEquals("project1", creation.getProjectName());
        assertEquals("(project1, Creation)", creation.toString());

        assertTrue(creation.getNextTasksData().isEmpty());
        assertTrue(creation.getPrevTasksData().isEmpty());
        assertNull(creation.getReplacesTaskName());
        assertNull(creation.getReplacementTaskName());
        assertNull(creation.getStartTime());
        assertNull(creation.getEndTime());
    }

    @Test
    public void testTaskCreationExceptions() throws InvalidTimeException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException, IllegalTaskRolesException, LoopDependencyGraphException {
        assertThrows(IllegalTaskRolesException.class, () -> new Task("Creation", "test", new Time(20), 0, new LinkedList<>(), new HashSet<>(), new HashSet<>(), "project1"));
        List<Role> projectManagerRole = List.of(Role.PROJECTMANAGER, Role.SYSADMIN);
        assertThrows(IllegalTaskRolesException.class, () -> new Task("Creation", "test", new Time(20), 0, projectManagerRole, new HashSet<>(), new HashSet<>(), "project1"));

        Task executingTask = new Task("Executing", "", new Time(0),0, List.of(Role.SYSADMIN), new HashSet<>(), new HashSet<>(), "project1");
        executingTask.start(new Time(0), sysAdmin, Role.SYSADMIN);
        assertThrows(IncorrectTaskStatusException.class, () -> new Task("Creation", "test", new Time(20), 0, List.of(Role.SYSADMIN), new HashSet<>(), Set.of(executingTask), "project1"));
        assertThrows(LoopDependencyGraphException.class, () -> new Task("Creation", "test", new Time(20), 0, List.of(Role.SYSADMIN), Set.of(task1), Set.of(task1), "project1"));

    }

    @Test
    public void testStartTask() throws InvalidTimeException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException, EndTimeBeforeStartTimeException, IncorrectUserException {
        assertEquals(Status.AVAILABLE, task1.getStatus());
        assertEquals(Status.UNAVAILABLE, task2.getStatus());

        assertThrows(IncorrectRoleException.class, () -> task1.start(new Time(0), javaProg, Role.JAVAPROGRAMMER));

        task1.start(new Time(0), sysAdmin, Role.SYSADMIN);
        assertEquals(Status.EXECUTING,task1.getStatus());
        assertEquals(Status.UNAVAILABLE, task2.getStatus());
        assertEquals(new Time(0), task1.getStartTime());
        assertNull(task1.getEndTime());

        assertThrows(IncorrectTaskStatusException.class, () -> currentTask.start(new Time(0), sysAdmin, Role.SYSADMIN));

        task1.finish(sysAdmin, new Time(4));
        assertEquals(Status.FINISHED, task1.getStatus());
        assertEquals(Status.AVAILABLE, task2.getStatus());
        assertEquals(new Time(4), task1.getEndTime());
    }

    @Test
    public void testUserSwitchTasks() throws InvalidTimeException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        multipleRolesTask.start(new Time(0), sysAdmin, Role.SYSADMIN);
        task3.start(new Time(2), sysAdmin, Role.SYSADMIN);
        assertEquals(0, task1.getUserNamesWithRole().size());
        assertEquals(1, task3.getUserNamesWithRole().size());
        assertEquals(0, task3.getUnfulfilledRoles().size());
        assertThrows(UserAlreadyAssignedToTaskException.class, () -> task1.start(new Time(5), sysAdmin, Role.SYSADMIN));
    }

    @Test
    public void testStartingPendingTask() throws InvalidTimeException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        multipleRolesTask.start(new Time(0), sysAdmin, Role.SYSADMIN);
        assertNull(multipleRolesTask.getStartTime());
        assertEquals(Status.PENDING, multipleRolesTask.getStatus());

        assertThrows(IncorrectRoleException.class, () -> multipleRolesTask.start(new Time(1), javaProg, Role.SYSADMIN));
        multipleRolesTask.start(new Time(1), javaProg, Role.JAVAPROGRAMMER);
        assertNull(multipleRolesTask.getStartTime());
        assertEquals(Status.PENDING, multipleRolesTask.getStatus());

        multipleRolesTask.start(new Time(2), pythonProg, Role.PYTHONPROGRAMMER);
        assertEquals(new Time(2), multipleRolesTask.getStartTime());
        assertEquals(Status.EXECUTING, multipleRolesTask.getStatus());
    }

    @Test
    public void testFinishingTask() throws InvalidTimeException, EndTimeBeforeStartTimeException, IncorrectTaskStatusException, IncorrectUserException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        task1.start(new Time(5), sysAdmin, Role.SYSADMIN);
        assertNull(task1.getEndTime());
        assertThrows(IncorrectUserException.class, () -> task1.finish(javaProg, new Time(10)));
        assertThrows(EndTimeBeforeStartTimeException.class, () -> task1.finish(sysAdmin, new Time(0)));

        assertEquals(Status.EXECUTING, task1.getStatus());
        task1.finish(sysAdmin, new Time(10));
        assertEquals(Status.FINISHED, task1.getStatus());

        multipleRolesTask.start(new Time(0), sysAdmin, Role.SYSADMIN);
        assertThrows(IncorrectTaskStatusException.class, () -> multipleRolesTask.finish(sysAdmin, new Time(0)));
        multipleRolesTask.start(new Time(0), javaProg, Role.JAVAPROGRAMMER);
        multipleRolesTask.start(new Time(0), pythonProg, Role.PYTHONPROGRAMMER);
        multipleRolesTask.finish(sysAdmin, new Time(10));
        assertEquals(Status.FINISHED, multipleRolesTask.getStatus());
    }

    @Test
    public void testStartTimeBeforeAvailableTime() throws InvalidTimeException, EndTimeBeforeStartTimeException, IncorrectTaskStatusException, IncorrectUserException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        assertEquals(Status.UNAVAILABLE, currentTask.getStatus());
        prevTask.start(new Time(10), sysAdmin, Role.SYSADMIN);
        prevTask.finish(sysAdmin, new Time(20));

        assertEquals(Status.AVAILABLE, currentTask.getStatus());
        assertThrows(IncorrectTaskStatusException.class, () -> currentTask.start(new Time(15), sysAdmin, Role.SYSADMIN));
        assertEquals(Status.AVAILABLE, currentTask.getStatus());
        currentTask.start(new Time(25), sysAdmin, Role.SYSADMIN);
        assertThrows(IncorrectTaskStatusException.class, () -> currentTask.start(new Time(15), javaProg, Role.JAVAPROGRAMMER));
        currentTask.start(new Time(25), javaProg, Role.JAVAPROGRAMMER);
    }

    @Test
    public void testFailTask() throws InvalidTimeException, EndTimeBeforeStartTimeException, IncorrectTaskStatusException, IncorrectUserException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        multipleRolesTask.start(new Time(0), sysAdmin, Role.SYSADMIN);
        assertThrows(IncorrectTaskStatusException.class, () -> multipleRolesTask.fail(sysAdmin, new Time(0)));

        prevTask.start(new Time(5), sysAdmin, Role.SYSADMIN);
        assertThrows(EndTimeBeforeStartTimeException.class, () -> prevTask.fail(sysAdmin, new Time(0)));
        assertThrows(IncorrectUserException.class, () -> prevTask.fail(javaProg, new Time(10)));
        prevTask.fail(sysAdmin, new Time(10));
        assertEquals(Status.FAILED, prevTask.getStatus());
        assertEquals(Status.UNAVAILABLE, currentTask.getStatus());
    }

    @Test
    public void testReplacingTask() throws InvalidTimeException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException, EndTimeBeforeStartTimeException, IncorrectUserException {
        prevTask.start(new Time(0), sysAdmin, Role.SYSADMIN);
        assertThrows(IncorrectTaskStatusException.class, () -> prevTask.replaceTask(replacementTask));

        prevTask.finish(sysAdmin, new Time(0));
        currentTask.start(new Time(0), sysAdmin, Role.SYSADMIN);
        currentTask.start(new Time(0), javaProg, Role.JAVAPROGRAMMER);
        currentTask.fail(sysAdmin, new Time(10));
        assertNull(currentTask.getReplacesTaskName());
        assertNull(currentTask.getReplacementTaskName());

        currentTask.replaceTask(replacementTask);
        assertThrows(IncorrectTaskStatusException.class, () -> currentTask.replaceTask(nextTask));
        assertEquals("Current Task", replacementTask.getReplacesTaskName());
        assertEquals("Replacement Task", currentTask.getReplacementTaskName());
        assertEquals(Status.AVAILABLE, replacementTask.getStatus());

        assertEquals(1, replacementTask.getNextTasksData().size());
        assertEquals(1, replacementTask.getPrevTasksData().size());
        assertTrue(currentTask.getNextTasksData().isEmpty());
        assertTrue(currentTask.getPrevTasksData().isEmpty());
    }

    @Test
    public void testPreviousDependencies() throws IncorrectTaskStatusException, LoopDependencyGraphException, InvalidTimeException, UserAlreadyAssignedToTaskException, IncorrectRoleException {


        assertFalse(prevTask.canSafelyAddPrevTask(nextTask.getTaskData()));
        assertFalse(currentTask.canSafelyAddPrevTask(currentTask.getTaskData()));
        assertTrue(prevTask.canSafelyAddPrevTask(task2.getTaskData()));
        assertTrue(nextTask.canSafelyAddPrevTask(currentTask.getTaskData()));

        assertThrows(LoopDependencyGraphException.class, () -> prevTask.addPrevTask(nextTask));
        assertThrows(LoopDependencyGraphException.class, () -> currentTask.addPrevTask(currentTask));

        nextTask.addPrevTask(currentTask);
        currentTask.removePrevTask(nextTask);

        assertEquals(Status.UNAVAILABLE, currentTask.getStatus());
        currentTask.removePrevTask(prevTask);
        assertEquals(Status.AVAILABLE, currentTask.getStatus());
        currentTask.addPrevTask(prevTask);
        assertEquals(Status.UNAVAILABLE, currentTask.getStatus());
        assertEquals(List.of(prevTask), currentTask.getPrevTasksData());

        currentTask.removePrevTask(prevTask);
        currentTask.start(new Time(0), sysAdmin, Role.SYSADMIN);
        assertFalse(currentTask.canSafelyAddPrevTask(prevTask.getTaskData()));
        assertThrows(IncorrectTaskStatusException.class, () -> currentTask.addPrevTask(prevTask));
    }

    @Test
    public void testNextDependencies() throws IncorrectTaskStatusException, LoopDependencyGraphException, InvalidTimeException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        assertThrows(LoopDependencyGraphException.class, () -> nextTask.addNextTask(prevTask));

        currentTask.removeNextTask(prevTask);

        assertEquals(Status.UNAVAILABLE, currentTask.getStatus());
        prevTask.removeNextTask(currentTask);
        assertEquals(Status.AVAILABLE, currentTask.getStatus());
        prevTask.addNextTask(currentTask);
        assertEquals(Status.UNAVAILABLE, currentTask.getStatus());
        assertEquals(1, prevTask.getNextTasksData().size());

        prevTask.removeNextTask(currentTask);
        currentTask.start(new Time(0), sysAdmin, Role.SYSADMIN);
        assertThrows(IncorrectTaskStatusException.class, () -> prevTask.addNextTask(currentTask));
    }



    @Test
    public void testDeletingReplacedTask() throws IncorrectTaskStatusException, InvalidTimeException, UserAlreadyAssignedToTaskException, IncorrectRoleException, EndTimeBeforeStartTimeException, IncorrectUserException {
        prevTask.start(new Time(0), sysAdmin, Role.SYSADMIN);
        prevTask.fail(sysAdmin, new Time(2));
        prevTask.replaceTask(replacementTask);

        prevTask.clearTask();
        assertNull(prevTask.getReplacementTaskName());
        assertNull(prevTask.getReplacesTaskName());
        assertNull(replacementTask.getReplacesTaskName());

    }

    @Test
    public void testDeletingReplacingTask() throws IncorrectTaskStatusException, InvalidTimeException, UserAlreadyAssignedToTaskException, IncorrectRoleException, EndTimeBeforeStartTimeException, IncorrectUserException {
        prevTask.start(new Time(0), sysAdmin, Role.SYSADMIN);
        prevTask.fail(sysAdmin, new Time(2));
        prevTask.replaceTask(replacementTask);

        replacementTask.clearTask();
        assertNull(replacementTask.getReplacementTaskName());
        assertNull(replacementTask.getReplacesTaskName());
        assertNull(prevTask.getReplacementTaskName());

    }

    @Test
    public void testDeletingCurrentTask(){
        currentTask.clearTask();
        assertTrue(currentTask.getPrevTasksData().isEmpty());
        assertTrue(currentTask.getNextTasksData().isEmpty());
        assertTrue(nextTask.getPrevTasksData().isEmpty());
        assertTrue(nextTask.getNextTasksData().isEmpty());
    }

    @Test
    public void testDeletingUsers() throws InvalidTimeException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        prevTask.start(new Time(0), sysAdmin, Role.SYSADMIN);
        prevTask.clearTask();

        assertNull(sysAdmin.getTaskData());
        assertEquals(List.of(Role.SYSADMIN), prevTask.getUnfulfilledRoles());
        assertEquals(0, prevTask.getUserNamesWithRole().size());
        assertEquals(Status.AVAILABLE, prevTask.getStatus());
    }

    @Test
    public void testDeletingFinishedCurrentTask() throws InvalidTimeException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException, EndTimeBeforeStartTimeException, IncorrectUserException {
        prevTask.start(new Time(0), sysAdmin, Role.SYSADMIN);
        prevTask.finish(sysAdmin, new Time(0));
        currentTask.start(new Time(0), javaProg, Role.JAVAPROGRAMMER);
        currentTask.start(new Time(0), sysAdmin, Role.SYSADMIN);
        currentTask.finish(sysAdmin, new Time(0));

        currentTask.clearTask();
        assertEquals(Status.AVAILABLE, currentTask.getStatus());
    }

    @Test
    public void testEarlyFinishedState() throws InvalidTimeException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException, EndTimeBeforeStartTimeException, IncorrectUserException {
        prevTask.start(new Time(0), sysAdmin, Role.SYSADMIN);
        assertThrows(IncorrectTaskStatusException.class, () -> prevTask.getFinishedStatus());

        prevTask.finish(sysAdmin, new Time(8));
        assertEquals(FinishedStatus.EARLY, prevTask.getFinishedStatus());
        assertEquals("early", prevTask.getFinishedStatus().toString());
    }

    @Test
    public void testOnTimeFinishedState() throws InvalidTimeException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException, EndTimeBeforeStartTimeException, IncorrectUserException {
        prevTask.start(new Time(0), sysAdmin, Role.SYSADMIN);
        assertThrows(IncorrectTaskStatusException.class, () -> prevTask.getFinishedStatus());

        prevTask.finish(sysAdmin, new Time(10));
        assertEquals(FinishedStatus.ON_TIME, prevTask.getFinishedStatus());
        assertEquals("on time", prevTask.getFinishedStatus().toString());
    }

    @Test
    public void testDelayedFinishedState() throws InvalidTimeException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException, EndTimeBeforeStartTimeException, IncorrectUserException {
        prevTask.start(new Time(0), sysAdmin, Role.SYSADMIN);
        assertThrows(IncorrectTaskStatusException.class, () -> prevTask.getFinishedStatus());

        prevTask.finish(sysAdmin, new Time(12));
        assertEquals(FinishedStatus.DELAYED, prevTask.getFinishedStatus());
        assertEquals("delayed", prevTask.getFinishedStatus().toString());
    }

    @Test
    public void testStopTaskWithMoreUsers() throws InvalidTimeException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException, IncorrectUserException {
        multipleRolesTask.start(new Time(0), sysAdmin, Role.SYSADMIN);
        multipleRolesTask.start(new Time(2), javaProg, Role.JAVAPROGRAMMER);
        multipleRolesTask.start(new Time(4), pythonProg, Role.PYTHONPROGRAMMER);

        assertEquals(Status.EXECUTING, multipleRolesTask.getStatus());
        assertEquals(0, multipleRolesTask.getUnfulfilledRoles().size());
        assertEquals(new Time(4), multipleRolesTask.getStartTime());

        multipleRolesTask.undoStart(pythonProg);

        assertEquals(Status.PENDING, multipleRolesTask.getStatus());
        assertEquals(List.of(Role.PYTHONPROGRAMMER), multipleRolesTask.getUnfulfilledRoles());
        assertNull(multipleRolesTask.getStartTime());

        assertThrows(IncorrectUserException.class, () -> multipleRolesTask.undoStart(pythonProg));

        multipleRolesTask.undoStart(sysAdmin);

        assertEquals(Status.PENDING, multipleRolesTask.getStatus());
        assertTrue(multipleRolesTask.getUnfulfilledRoles().contains(Role.PYTHONPROGRAMMER));
        assertTrue(multipleRolesTask.getUnfulfilledRoles().contains(Role.SYSADMIN));

        multipleRolesTask.undoStart(javaProg);

        assertEquals(Status.AVAILABLE, multipleRolesTask.getStatus());
        assertTrue(multipleRolesTask.getUnfulfilledRoles().contains(Role.SYSADMIN));
        assertTrue(multipleRolesTask.getUnfulfilledRoles().contains(Role.PYTHONPROGRAMMER));
        assertTrue(multipleRolesTask.getUnfulfilledRoles().contains(Role.JAVAPROGRAMMER));
    }

    @Test
    public void testStopTaskWithOneUser() throws InvalidTimeException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException, IncorrectUserException, EndTimeBeforeStartTimeException {
        prevTask.start(new Time(0), sysAdmin, Role.SYSADMIN);
        assertEquals(Status.EXECUTING, prevTask.getStatus());
        assertEquals(0, prevTask.getUnfulfilledRoles().size());
        assertEquals(new Time(0), prevTask.getStartTime());

        prevTask.undoStart(sysAdmin);
        assertEquals(Status.AVAILABLE, prevTask.getStatus());
        assertEquals(List.of(Role.SYSADMIN), prevTask.getUnfulfilledRoles());
        assertNull(prevTask.getStartTime());

        prevTask.start(new Time(0), sysAdmin, Role.SYSADMIN);
        prevTask.finish(sysAdmin, new Time(2));

        assertThrows(IncorrectTaskStatusException.class, () -> prevTask.undoStart(sysAdmin));
        assertEquals(Status.FINISHED, prevTask.getStatus());
        assertEquals(Status.AVAILABLE, currentTask.getStatus());
    }

    @Test
    public void testRestartingFinishedMultipleRolesTask() throws InvalidTimeException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException, EndTimeBeforeStartTimeException, IncorrectUserException {
        assertThrows(IncorrectTaskStatusException.class, () -> multipleRolesTask.undoEnd());

        multipleRolesTask.start(new Time(0), sysAdmin, Role.SYSADMIN);
        multipleRolesTask.start(new Time(2), javaProg, Role.JAVAPROGRAMMER);

        assertThrows(IncorrectTaskStatusException.class, () -> multipleRolesTask.undoEnd());

        multipleRolesTask.start(new Time(4), pythonProg, Role.PYTHONPROGRAMMER);
        multipleRolesTask.finish(sysAdmin, new Time(6));

        multipleRolesTask.undoEnd();
        assertEquals(Status.EXECUTING, multipleRolesTask.getStatus());
        assertEquals(0, multipleRolesTask.getUnfulfilledRoles().size());
        assertNull(multipleRolesTask.getEndTime());
        assertEquals(new Time(4), multipleRolesTask.getStartTime());

        assertThrows(IncorrectTaskStatusException.class, () -> multipleRolesTask.undoEnd());
    }

    @Test
    public void testRestartingFinishedPreviousTask() throws InvalidTimeException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException, EndTimeBeforeStartTimeException, IncorrectUserException {
        prevTask.start(new Time(0), sysAdmin, Role.SYSADMIN);
        prevTask.finish(sysAdmin, new Time(2));
        assertEquals(Status.AVAILABLE, currentTask.getStatus());

        prevTask.undoEnd();
        assertEquals(Status.EXECUTING, prevTask.getStatus());
        assertEquals(0, prevTask.getUnfulfilledRoles().size());
        assertNull(prevTask.getEndTime());
        assertEquals(new Time(0), prevTask.getStartTime());
        assertEquals(Status.UNAVAILABLE, currentTask.getStatus());
    }

    @Test
    public void testRestartingFailedTask() throws InvalidTimeException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException, EndTimeBeforeStartTimeException, IncorrectUserException {
        prevTask.start(new Time(0), sysAdmin, Role.SYSADMIN);
        prevTask.fail(sysAdmin, new Time(2));

        prevTask.undoEnd();
        assertEquals(Status.EXECUTING, prevTask.getStatus());
        assertEquals(0, prevTask.getUnfulfilledRoles().size());
        assertNull(prevTask.getEndTime());
        assertEquals(new Time(0), prevTask.getStartTime());
    }
}
