package Tests.Domain.ProjectTests;

import Domain.*;
import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.IllegalTaskRolesException;
import Domain.TaskStates.TaskData;
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
public class ProjectTest {

    @Mock
    private User user;
    private Project project1;
    private Project project2;

    @Before
    public void setUp() throws InvalidTimeException, DueTimeBeforeCreationTimeException, TaskNameAlreadyInUseException, TaskNotFoundException, IllegalTaskRolesException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException {
        this.project1 = new Project("Project 1", "", new Time(0), new Time(100));
        this.project2 = new Project("Project 2", "", new Time(0), new Time(100));
        project1.addNewTask("Task1", "", new Time(0), 0, List.of(Role.SYSADMIN), new HashSet<>(), new HashSet<>());
        project1.addNewTask("Task3", "", new Time(0), 0, List.of(Role.SYSADMIN), new HashSet<>(), new HashSet<>());
    }

    @Test
    public void testCreationProject() throws InvalidTimeException, DueTimeBeforeCreationTimeException {
        Project testProject = new Project("Project", "test", new Time(2), new Time(5));
        assertEquals("Project", testProject.getName());
        assertEquals("test", testProject.getDescription());
        assertEquals(new Time(2), testProject.getCreationTime());
        assertEquals(new Time(5), testProject.getDueTime());

        assertEquals(0, testProject.getActiveTasksNames().size());
        assertEquals(0, testProject.getReplacedTasksNames().size());
        assertEquals(ProjectStatus.ONGOING, testProject.getStatus());
        assertThrows(TaskNotFoundException.class, () -> testProject.getTaskData("Test"));

        assertThrows(DueTimeBeforeCreationTimeException.class, () -> new Project("","", new Time(5), new Time(4)));
    }

    @Test
    public void testAddTask() throws InvalidTimeException, TaskNameAlreadyInUseException, TaskNotFoundException, IllegalTaskRolesException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException {
        project1.addNewTask("Task", "", new Time(5), 0.5, List.of(Role.SYSADMIN), new HashSet<>(), new HashSet<>());
        assertTrue(project1.getActiveTasksNames().contains("Task"));

        assertThrows(TaskNameAlreadyInUseException.class, () -> project1.addNewTask("Task", "", new Time(0), 0, List.of(Role.SYSADMIN), new HashSet<>(), new HashSet<>()));

        assertThrows(TaskNotFoundException.class, () -> project1.deleteTask("Test"));
        project1.deleteTask("Task");
        assertFalse(project1.getActiveTasksNames().contains("Task"));
    }

    @Test
    public void testDeleteTask() throws TaskNotFoundException {
        assertThrows(TaskNotFoundException.class, () -> project1.deleteTask("test"));

        project1.deleteTask("Task1");
        assertFalse(project1.getActiveTasksNames().contains("Task1"));
        assertEquals(0, project1.getReplacedTasksNames().size());
    }

    @Test
    public void testCreationPreviousAndNextTasks() throws InvalidTimeException, TaskNameAlreadyInUseException, TaskNotFoundException, IllegalTaskRolesException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException {
        assertThrows(TaskNotFoundException.class, () -> project2.addNewTask("Task2", "", new Time(0), 0, List.of(Role.SYSADMIN), Set.of(new Tuple<>(project1, "Task")), new HashSet<>()));
        assertThrows(TaskNotFoundException.class, () -> project2.addNewTask("Task2", "", new Time(0), 0, List.of(Role.SYSADMIN), new HashSet<>(), Set.of(new Tuple<>(project1, "Task"))));

        project2.addNewTask("Task2", "", new Time(0), 0, List.of(Role.SYSADMIN), Set.of(new Tuple<>(project1, "Task1")), Set.of(new Tuple<>(project1, "Task3")));

        assertTrue(project2.getTaskData("Task2").getPrevTasksData().contains(project1.getTaskData("Task1")));
        assertTrue(project2.getTaskData("Task2").getNextTasksData().contains(project1.getTaskData("Task3")));
    }

    @Test
    public void testAddingRemovingPreviousAndNextTasks() throws InvalidTimeException, TaskNameAlreadyInUseException, TaskNotFoundException, IllegalTaskRolesException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException {
        project2.addNewTask("Task2", "", new Time(0), 0, List.of(Role.SYSADMIN), Set.of(new Tuple<>(project1, "Task1")), Set.of(new Tuple<>(project1, "Task3")));

        assertThrows(TaskNotFoundException.class, () -> project2.removePrevTask("Task", project1, "Task1"));
        assertThrows(TaskNotFoundException.class, () -> project2.removeNextTask("Task", project1, "Task1"));
        assertThrows(TaskNotFoundException.class, () -> project2.addPrevTask("Task", project1, "Task1"));
        assertThrows(TaskNotFoundException.class, () -> project2.addNextTask("Task", project1, "Task1"));


        TaskData task1 = project1.getTaskData("Task1");
        TaskData task2 = project2.getTaskData("Task2");
        TaskData task3 = project1.getTaskData("Task3");

        project2.removePrevTask("Task2", project1, "Task1");
        project2.removeNextTask("Task2", project1, "Task3");

        assertTrue(task2.getPrevTasksData().isEmpty());
        assertTrue(task2.getNextTasksData().isEmpty());

        project2.addPrevTask("Task2", project1, "Task3");
        project2.addNextTask("Task2", project1, "Task1");

        assertTrue(task2.getPrevTasksData().contains(task3));
        assertTrue(task2.getNextTasksData().contains(task1));
    }

    @Test
    public void testStartTask() throws InvalidTimeException, TaskNotFoundException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException, IncorrectUserException {
        assertThrows(TaskNotFoundException.class, () -> project1.startTask("Test", new Time(0), user, Role.SYSADMIN));
        project1.startTask("Task1", new Time(0), user, Role.SYSADMIN);
        assertEquals(Status.EXECUTING, project1.getTaskData("Task1").getStatus());

        assertThrows(TaskNotFoundException.class, () -> project1.stopTask("Test", user));
        project1.stopTask("Task1", user);
        assertEquals(Status.AVAILABLE, project1.getTaskData("Task1").getStatus());
    }

    @Test
    public void testFailFinishTask() throws InvalidTimeException, TaskNotFoundException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException, EndTimeBeforeStartTimeException, IncorrectUserException, TaskNameAlreadyInUseException, IllegalTaskRolesException, ProjectNotOngoingException, LoopDependencyGraphException {
        project2.addNewTask("Task", "", new Time(0), 0, List.of(Role.SYSADMIN), new HashSet<>(), new HashSet<>());
        project2.startTask("Task", new Time(5), user, Role.SYSADMIN);

        assertThrows(TaskNotFoundException.class, () -> project2.failTask("Test", user, new Time(10)));
        assertThrows(TaskNotFoundException.class, () -> project2.finishTask("Test", user, new Time(10)));

        project2.failTask("Task", user, new Time(5));
        assertEquals(Status.FAILED, project2.getTaskData("Task").getStatus());
        assertEquals(ProjectStatus.ONGOING, project1.getStatus());

        assertThrows(TaskNotFoundException.class, () -> project2.restartTask("Test"));
        project2.restartTask("Task");
        assertEquals(Status.EXECUTING, project2.getTaskData("Task").getStatus());
        assertEquals(ProjectStatus.ONGOING, project2.getStatus());

        project2.finishTask("Task", user, new Time(10));
        assertEquals(Status.FINISHED, project2.getTaskData("Task").getStatus());
        assertEquals(ProjectStatus.FINISHED, project2.getStatus());

        project2.restartTask("Task");
        assertEquals(Status.EXECUTING, project2.getTaskData("Task").getStatus());
        assertEquals(ProjectStatus.ONGOING, project2.getStatus());
    }

    @Test
    public void testReplaceTask() throws InvalidTimeException, TaskNotFoundException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException, EndTimeBeforeStartTimeException, IncorrectUserException, TaskNameAlreadyInUseException {
        project1.startTask("Task1", new Time(0), user, Role.SYSADMIN);
        project1.failTask("Task1", user, new Time(5));

        assertThrows(TaskNameAlreadyInUseException.class, () -> project1.replaceTask("Task1", "", new Time(0), 0, "Task1"));
        assertThrows(TaskNotFoundException.class, () -> project1.replaceTask("Replace", "", new Time(0), 0, "Test"));
        project1.replaceTask("Replace", "", new Time(5), 0, "Task1");


        assertTrue(project1.getReplacedTasksNames().contains("Task1"));
        assertTrue(project1.getActiveTasksNames().contains("Replace"));
        assertFalse(project1.getReplacedTasksNames().contains("Replace"));
        assertFalse(project1.getActiveTasksNames().contains("Task1"));
        assertEquals(ProjectStatus.ONGOING, project1.getStatus());

        project1.deleteTask("Task3");
        project1.startTask("Replace", new Time(10), user, Role.SYSADMIN);
        project1.finishTask("Replace", user, new Time(15));
        assertEquals(ProjectStatus.FINISHED, project1.getStatus());

        assertThrows(ProjectNotOngoingException.class, () -> project1.addNewTask("","", new Time(0), 0, List.of(Role.SYSADMIN), new HashSet<>(), new HashSet<>()));

        project1.deleteTask("Replace");

        assertTrue(project1.getActiveTasksNames().contains("Task1"));
        assertFalse(project1.getReplacedTasksNames().contains("Task1"));
    }

    @Test
    public void testClearTasks() throws InvalidTimeException, TaskNotFoundException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException, EndTimeBeforeStartTimeException, IncorrectUserException, TaskNameAlreadyInUseException {
        project1.startTask("Task1", new Time(0), user, Role.SYSADMIN);
        project1.failTask("Task1", user, new Time(5));
        project1.replaceTask("Replace", "", new Time(5), 0, "Task1");
        project1.startTask("Replace", new Time(10), user, Role.SYSADMIN);
        project1.finishTask("Replace", user, new Time(15));
        project1.startTask("Task3", new Time(20), user, Role.SYSADMIN);
        project1.finishTask("Task3", user, new Time(25));
        assertEquals(ProjectStatus.FINISHED, project1.getStatus());

        project1.clearTasks();
        assertEquals(0, project1.getActiveTasksNames().size());
        assertEquals(0, project1.getReplacedTasksNames().size());
        assertEquals(ProjectStatus.ONGOING, project1.getStatus());

    }

    @Test
    public void testProjectState(){
        assertEquals("ongoing", ProjectStatus.ONGOING.toString());
        assertEquals("finished", ProjectStatus.FINISHED.toString());
    }
}
