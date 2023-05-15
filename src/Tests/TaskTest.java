package Tests;

import Domain.*;
import Domain.TaskStates.*;
import Domain.TaskStates.TaskData;

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

    private User sysAdmin = new User("sysAdmin", "123", Set.of(Role.SYSADMIN, Role.PYTHONPROGRAMMER));

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
    public void setUp() throws InvalidTimeException, IncorrectTaskStatusException, LoopDependencyGraphException, IllegalTaskRolesException {
        // Tasks for general task tests
        this.task = new Task("Task", "Test", new Time(100), 0.1, List.of(Role.PYTHONPROGRAMMER), Set.of(), Set.of(), "project1");

        this.prevTask = new Task("Previous Task", "Test", new Time(10), 0.1, List.of(Role.SYSADMIN, Role.PYTHONPROGRAMMER), Set.of(), Set.of(), "project1");
        this.currentTask = new Task("Current Task", "Test", new Time(100), 0.1, List.of(Role.SYSADMIN, Role.PYTHONPROGRAMMER, Role.JAVAPROGRAMMER), Set.of(prevTask), Set.of(), "project1");
        this.nextTask = new Task("Next Task", "Test", new Time(10), 0.1, List.of(Role.SYSADMIN), Set.of(currentTask), Set.of(), "project1");

        this.replacementTask = new Task("Replacement Task", "", new Time(20), 0);


        // Loop dependency check tasks
        List<Role> roles = List.of(Role.SYSADMIN, Role.JAVAPROGRAMMER);

        this.task1 = new Task("Task 1", "test", new Time(20), 0, roles, new HashSet<>(), new HashSet<>(), "project1");
        this.task2 = new Task("Task 2", "test", new Time(20), 0, roles, Set.of(task1), new HashSet<>(), "project1");
        this.task4 = new Task("Task 4", "test", new Time(20), 0, roles, new HashSet<>(), new HashSet<>(), "project1");
        this.task3 = new Task("Task 3", "test", new Time(20), 0, roles, new HashSet<>(), Set.of(task4), "project1");

    }

    @Test
    public void testTaskCreation() throws InvalidTimeException, IllegalTaskRolesException, IncorrectTaskStatusException, LoopDependencyGraphException {
        List<Role> roles = List.of(Role.SYSADMIN, Role.JAVAPROGRAMMER);
        Task creation = new Task("Creation", "test", new Time(20), 0, roles, new HashSet<>(), new HashSet<>(), "project1");
        assertEquals("Creation", creation.getName());
        assertEquals("test", creation.getDescription());
        assertEquals(new Time(20), creation.getEstimatedDuration());
        assertEquals(0, creation.getAcceptableDeviation(),0);
        assertEquals(List.of(Role.SYSADMIN, Role.JAVAPROGRAMMER), creation.getUnfulfilledRoles());
        assertEquals("project1", creation.getProjectName());

        assertEquals(0, creation.getNextTaskNames().size());
        assertEquals(0, creation.getPrevTaskNames().size());
        assertNull(creation.getReplacesTask());
        assertNull(creation.getReplacementTask());
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
    public void finishingTaskWithNext() throws InvalidTimeException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException, EndTimeBeforeStartTimeException, IncorrectUserException {
        assertEquals(Status.AVAILABLE, task1.getStatus());
        assertEquals(Status.UNAVAILABLE, task2.getStatus());

        task1.start(new Time(0), sysAdmin, Role.SYSADMIN);
        assertEquals(Status.PENDING,task1.getStatus());
        assertEquals(Status.UNAVAILABLE, task2.getStatus());
        assertNull(task1.getStartTime());

        task1.start(new Time(2), javaProg, Role.JAVAPROGRAMMER);
        assertEquals(Status.EXECUTING,task1.getStatus());
        assertEquals(Status.UNAVAILABLE, task2.getStatus());
        assertEquals(new Time(2), task1.getStartTime());
        assertNull(task1.getEndTime());

        task1.finish(javaProg, new Time(4));
        assertEquals(Status.FINISHED, task1.getStatus());
        assertEquals(Status.AVAILABLE, task2.getStatus());
        assertEquals(new Time(4), task1.getEndTime());

    }

    @Test
    public void testReplacingTask() throws InvalidTimeException, IllegalTaskRolesException, IncorrectTaskStatusException, LoopDependencyGraphException, EndTimeBeforeStartTimeException, IncorrectUserException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        task1.start(new Time(0), sysAdmin, Role.SYSADMIN);
        task1.start(new Time(2), javaProg, Role.JAVAPROGRAMMER);
        task1.finish(javaProg, new Time(4));

        Task failedTask = new Task("Failed", "", new Time(2), 0, List.of(Role.SYSADMIN), Set.of(task1), Set.of(task2), "project1");
        failedTask.start(new Time(6), sysAdmin, Role.SYSADMIN);

        Task replacementTask = new Task("Replacement", "", new Time(4), 0.1);
        assertThrows(IncorrectTaskStatusException.class, () -> failedTask.replaceTask(replacementTask));

        failedTask.fail(sysAdmin, new Time(8));
        failedTask.replaceTask(replacementTask);
        assertEquals(0, failedTask.getNextTaskNames().size());
        assertEquals(0, failedTask.getPrevTaskNames().size());

        assertEquals(Set.of(new Tuple<>("project1", "Task 1")), replacementTask.getPrevTaskNames());
        assertEquals(Set.of(new Tuple<>("project1", "Task 2")), replacementTask.getNextTaskNames());
    }

    @Test
    public void testUserSwitchTasks() throws InvalidTimeException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        task1.start(new Time(0), sysAdmin, Role.SYSADMIN);
        task3.start(new Time(2), sysAdmin, Role.SYSADMIN);
        assertEquals(0, task1.getUserNamesWithRole().size());
        task3.start(new Time(2), javaProg, Role.JAVAPROGRAMMER);
        assertEquals(2, task3.getUserNamesWithRole().size());
        assertEquals(0, task3.getUnfulfilledRoles().size());
        assertThrows(UserAlreadyAssignedToTaskException.class, () -> task1.start(new Time(5), sysAdmin, Role.SYSADMIN));
    }


    @Test
    public void testTask() throws InvalidTimeException, IncorrectTaskStatusException, IncorrectRoleException, EndTimeBeforeStartTimeException, IncorrectUserException, LoopDependencyGraphException, IllegalTaskRolesException, UserAlreadyAssignedToTaskException {

        /*
        assertThrows(IllegalTaskRolesException.class, () -> new Task("", "", new Time(0), 0, List.of(Role.PROJECTMANAGER), Set.of(), Set.of(), "project1"));

        // Test if the task states are initialised correctly
        assertEquals(Status.AVAILABLE, prevTask.getStatus());
        assertEquals(Status.UNAVAILABLE, currentTask.getStatus());
        assertEquals(Status.UNAVAILABLE, nextTask.getStatus());

        // Check removal and appending of tasks, and checks if the states are correct afterwards
        currentTask.removeNextTask(nextTask);
        assertEquals(Status.AVAILABLE, nextTask.getStatus());

        currentTask.addNextTask(nextTask);
        assertEquals("unavailable", nextTask.getStatus().toString());


        // Asserts it's impossible to add loops in the dependency graph
        assertThrows(LoopDependencyGraphException.class, () -> nextTask.addNextTask(currentTask));
        assertThrows(LoopDependencyGraphException.class, () -> new Task("", "", new Time(0), 0, List.of(Role.PYTHONPROGRAMMER), Set.of(prevTask), Set.of(prevTask), "project1"));

        Task testTask = new Task("", "", new Time(0), 0, List.of(Role.PYTHONPROGRAMMER), Set.of(), Set.of(prevTask), "project1");
        assertThrows(LoopDependencyGraphException.class, () -> new Task("", "", new Time(0), 0, List.of(Role.PYTHONPROGRAMMER), Set.of(prevTask), Set.of(currentTask, testTask, nextTask), "project1"));
        prevTask.removePrevTask(testTask);

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
        assertEquals("executing", Status.EXECUTING.toString());

        // Check if an executing task cannot be a next task for a new task, and that we cannot finish/fail a task with the wrong user
        assertThrows(IncorrectTaskStatusException.class, () -> new Task("", "", new Time(0), 0, List.of(Role.PYTHONPROGRAMMER), Set.of(), Set.of(prevTask), "project1"));
        assertThrows(IncorrectUserException.class, () -> prevTask.finish(user, new Time(10)));
        assertThrows(IncorrectUserException.class, () -> prevTask.fail(user, new Time(10)));

        // Finish the task
        prevTask.finish(sysAdmin, new Time(10));
        assertEquals("finished", prevTask.getStatus().toString());
        assertEquals("on time", prevTask.getTaskData().getFinishedStatus().toString());

        // Checks if finished state cannot start/fail/finish/unassign/replace
        assertThrows(IncorrectTaskStatusException.class, () -> prevTask.start(new Time(0), user, Role.JAVAPROGRAMMER));
        assertThrows(IncorrectUserException.class, () -> prevTask.finish(pythonProg, new Time(0)));
        assertThrows(IncorrectUserException.class, () -> prevTask.fail(pythonProg, new Time(0)));
        assertThrows(IncorrectTaskStatusException.class, () -> prevTask.unassignUser(pythonProg));
        assertThrows(IncorrectTaskStatusException.class, () -> prevTask.replaceTask(task));

        // Checks if the next task becomes available once prevTask is finished
        assertEquals(Status.AVAILABLE, currentTask.getStatus());

        // Checks if next tasks are properly added upon task creation
        Task test = new Task("", "", new Time(0), 0, List.of(Role.PYTHONPROGRAMMER), Set.of(), Set.of(currentTask), "project1");
        currentTask.removePrevTask(test);
        assertEquals(Status.AVAILABLE, currentTask.getStatus());

        // Asserts it's impossible to start a task before the end time of one of its finished previous tasks
        assertThrows(IncorrectTaskStatusException.class, () -> currentTask.start(new Time(5), sysAdmin, Role.SYSADMIN));

        // Checks removing and adding of tasks to available task
        currentTask.removePrevTask(prevTask);
        assertEquals("available", currentTask.getStatus().toString());
        currentTask.addprevTask(prevTask);
        assertEquals("available", currentTask.getStatus().toString());

        // Assign a first user to this task, ensuring it is pending
        currentTask.start(new Time(10), sysAdmin, Role.SYSADMIN);
        assertEquals("pending", currentTask.getStatus().toString());


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
        assertEquals(new HashMap<>(), currentTask.getTaskData().getUserNamesWithRole());
        assertEquals("failed", currentTask.getStatus().toString());
        assertEquals(Status.UNAVAILABLE, nextTask.getStatus());

        currentTask.replaceTask(replacementTask);

        Task replacementTask = currentTask.getReplacementTask();

        // TASK PROXY REPLACED TASK

        TaskData taskDataFailed = currentTask.getTaskData();

        assertEquals("Current Task", taskDataFailed.getName());
        assertEquals("Test", taskDataFailed.getDescription());
        assertEquals(new Time(100), taskDataFailed.getEstimatedDuration());
        assertEquals(0.1, taskDataFailed.getAcceptableDeviation(), 0.00001);
        assertEquals(Status.FAILED, taskDataFailed.getStatus());
        assertEquals("Replacement Task", taskDataFailed.getReplacementTaskName());
        assertEquals(new Time(10), taskDataFailed.getStartTime());
        assertEquals(new Time(15), taskDataFailed.getEndTime());
        assertEquals(List.of(Role.SYSADMIN, Role.PYTHONPROGRAMMER, Role.JAVAPROGRAMMER), taskDataFailed.getUnfulfilledRoles());
        assertEquals("project1", taskDataFailed.getProjectName());
        assertFalse(taskDataFailed.canSafelyAddPrevTask(new Tuple<>("project1", "Current Task")));
        assertNull(taskDataFailed.getReplacesTaskName());

        Map<String, Role> userRoleMap = new HashMap<>();

        userRoleMap.put("System Administrator", Role.SYSADMIN);
        userRoleMap.put("Python Programmer", Role.PYTHONPROGRAMMER);
        userRoleMap.put("Java Programmer", Role.JAVAPROGRAMMER);

        assertEquals(new HashMap<>(), taskDataFailed.getUserNamesWithRole());
        assertEquals(userRoleMap.values().stream().sorted().toList(), taskDataFailed.getUnfulfilledRoles().stream().sorted().toList());

        // TASK PROXY WITH REPLACEMENT TASK

        TaskData replacementProxy = replacementTask.getTaskData();

        assertNull(replacementProxy.getReplacementTaskName());
        assertEquals("Current Task", replacementProxy.getReplacesTaskName());
        assertEquals(Set.of(new Tuple<>("project1", "Previous Task")), replacementProxy.getPrevTaskNames());
        assertEquals(Set.of(new Tuple<>("project1", "Next Task")), replacementProxy.getNextTaskNames());
        assertNull(replacementProxy.getEndTime());
        assertNull(replacementProxy.getStartTime());



        assertFalse(task1.getTaskData().canSafelyAddPrevTask(new Tuple<>("project1", "Task 1")));
        assertFalse(task1.getTaskData().canSafelyAddPrevTask(new Tuple<>("project1", "Task 2")));
        assertTrue(task1.getTaskData().canSafelyAddPrevTask(new Tuple<>("project1", "Task 3")));
        assertTrue(task1.getTaskData().canSafelyAddPrevTask(new Tuple<>("project1", "Task 4")));

        assertTrue(task2.getTaskData().canSafelyAddPrevTask("Task 1"));
        assertFalse(task2.getTaskData().canSafelyAddPrevTask("Task 2"));
        assertTrue(task2.getTaskData().canSafelyAddPrevTask("Task 3"));
        assertTrue(task2.getTaskData().canSafelyAddPrevTask("Task 4"));

        assertTrue(task3.getTaskData().canSafelyAddPrevTask("Task 1"));
        assertTrue(task3.getTaskData().canSafelyAddPrevTask("Task 2"));
        assertFalse(task3.getTaskData().canSafelyAddPrevTask("Task 3"));
        assertFalse(task3.getTaskData().canSafelyAddPrevTask("Task 4"));

        assertTrue(task4.getTaskData().canSafelyAddPrevTask("Task 1"));
        assertTrue(task4.getTaskData().canSafelyAddPrevTask("Task 2"));
        assertTrue(task4.getTaskData().canSafelyAddPrevTask("Task 3"));
        assertFalse(task4.getTaskData().canSafelyAddPrevTask("Task 4"));


        task2.addNextTask(task3);
        task4.addprevTask(task1);

        TaskData taskData1 = task1.getTaskData();
        TaskData taskData2 = task2.getTaskData();
        TaskData taskData3 = task3.getTaskData();
        TaskData taskData4 = task4.getTaskData();


        assertFalse(taskData1.canSafelyAddPrevTask("Task 1"));
        assertFalse(taskData1.canSafelyAddPrevTask("Task 2"));
        assertFalse(taskData1.canSafelyAddPrevTask("Task 3"));
        assertFalse(taskData1.canSafelyAddPrevTask("Task 4"));

        assertTrue(taskData2.canSafelyAddPrevTask("Task 1"));
        assertFalse(taskData2.canSafelyAddPrevTask("Task 2"));
        assertFalse(taskData2.canSafelyAddPrevTask("Task 3"));
        assertFalse(taskData2.canSafelyAddPrevTask("Task 4"));

        assertTrue(taskData3.canSafelyAddPrevTask("Task 1"));
        assertTrue(taskData3.canSafelyAddPrevTask("Task 2"));
        assertFalse(taskData3.canSafelyAddPrevTask("Task 3"));
        assertFalse(taskData3.canSafelyAddPrevTask("Task 4"));

        assertTrue(taskData4.canSafelyAddPrevTask("Task 1"));
        assertTrue(taskData4.canSafelyAddPrevTask("Task 2"));
        assertTrue(taskData4.canSafelyAddPrevTask("Task 3"));
        assertFalse(taskData4.canSafelyAddPrevTask("Task 4"));


        // FinishedStatusTest

        Task onTime = new Task("On Time", "", new Time(10), 0.1, List.of(Role.JAVAPROGRAMMER), Set.of(), Set.of(), "project1");
        onTime.start(new Time(0), user, Role.JAVAPROGRAMMER);
        onTime.finish(user, new Time(10));
        assertEquals("on time", onTime.getTaskData().getFinishedStatus().toString());


        Task early = new Task("On Time", "", new Time(10), 0.1, List.of(Role.JAVAPROGRAMMER), Set.of(), Set.of(), "project1");
        early.start(new Time(0), user, Role.JAVAPROGRAMMER);
        early.finish(user, new Time(5));
        assertEquals("early", early.getTaskData().getFinishedStatus().toString());


        Task delayed = new Task("On Time", "", new Time(10), 0.1, List.of(Role.JAVAPROGRAMMER), Set.of(), Set.of(), "project1");
        delayed.start(new Time(0), user, Role.JAVAPROGRAMMER);
        delayed.finish(user, new Time(100));
        assertEquals("delayed", delayed.getTaskData().getFinishedStatus().toString());

         */

    }
}
