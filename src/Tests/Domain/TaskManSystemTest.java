package Tests.Domain;

import Domain.*;
import Domain.TaskStates.IllegalTaskRolesException;
import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.LoopDependencyGraphException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class TaskManSystemTest {

    @Mock
    private User user;

    private TaskManSystem taskManSystem;

    @Before
    public void setUp() throws InvalidTimeException, ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNameAlreadyInUseException, TaskNotFoundException, IllegalTaskRolesException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException {
        this.taskManSystem = new TaskManSystem(new Time(10));
        taskManSystem.createProject("New Project", "Description", new Time(100));
        taskManSystem.addTaskToProject("New Project", "New Task", "", new Time(20), 0.2, List.of(Role.SYSADMIN), Set.of(), Set.of());
        taskManSystem.createProject("Second Project", "Description", new Time(200));
        taskManSystem.addTaskToProject("Second Project", "Second Task", "", new Time(20), 0.2, List.of(Role.SYSADMIN), Set.of(), Set.of());
    }

    @Test
    public void testCreateProject() throws ProjectNotFoundException, InvalidTimeException, ProjectNameAlreadyInUseException, DueBeforeSystemTimeException {
        assertThrows(ProjectNameAlreadyInUseException.class, () -> taskManSystem.createProject("New Project", "", new Time(100)));
        assertEquals("New Project", taskManSystem.getProjectData("New Project").getName());
        assertEquals(List.of("New Project", "Second Project"), taskManSystem.getProjectNames());

        taskManSystem.createProject("Third Project", "", new Time(20));
        assertEquals(List.of("New Project", "Second Project", "Third Project"), taskManSystem.getProjectNames());

        assertThrows(DueBeforeSystemTimeException.class, () -> taskManSystem.createProject("", "", new Time(0)));
    }

    @Test
    public void testDeleteProject() throws ProjectNotFoundException {
        taskManSystem.deleteProject("New Project");
        assertThrows(ProjectNotFoundException.class, () -> taskManSystem.getProjectData("New Project"));
        assertThrows(ProjectNotFoundException.class, () -> taskManSystem.deleteProject("hello"));
    }

    @Test
    public void testGettersInitial() throws InvalidTimeException {
        assertNotNull(taskManSystem.getTaskManSystemData());
        assertEquals(new Time(10), taskManSystem.getSystemTime());
        assertEquals(List.of("New Project", "Second Project"), taskManSystem.getProjectNames());

        // getProjectData
        assertThrows(ProjectNotFoundException.class, () -> taskManSystem.getProjectData(""));

        // getTaskData
        assertThrows(ProjectNotFoundException.class, () -> taskManSystem.getTaskData("", ""));
    }

    @Test
    public void testAddTaskToProject() throws InvalidTimeException, ProjectNotFoundException, TaskNameAlreadyInUseException, TaskNotFoundException, IllegalTaskRolesException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException {
        // Add task in second project depending on task in first project
        taskManSystem.addTaskToProject("Second Project", "Dependent", "", new Time(40), 0.2, List.of(Role.SYSADMIN), Set.of(new Tuple<>("New Project", "New Task")), Set.of());

        assertEquals(1, taskManSystem.getTaskData("Second Project", "Dependent").getPrevTasksData().size());
        assertEquals(Status.AVAILABLE, taskManSystem.getTaskData("New Project", "New Task").getStatus());
        assertEquals(Status.UNAVAILABLE, taskManSystem.getTaskData("Second Project", "Dependent").getStatus());
        taskManSystem.deleteProject("Second Project");

        // Test if you can't add task of a non-existent project as dependant of new task
        assertThrows(ProjectNotFoundException.class, () -> taskManSystem.addTaskToProject("Second Project", "d", "", new Time(40), 0.2, List.of(Role.SYSADMIN), Set.of(new Tuple<>("non-existent project", "")), Set.of()));

        assertThrows(ProjectNotFoundException.class, () -> taskManSystem.addTaskToProject("", "", "",new Time(20), 0.2, List.of(Role.SYSADMIN), Set.of(), Set.of()));
    }

    @Test
    public void testDeleteTask() throws ProjectNotFoundException, TaskNotFoundException {
        taskManSystem.deleteTask("New Project", "New Task");
        assertThrows(TaskNotFoundException.class, () -> taskManSystem.getTaskData("New Project", "New Task"));
        assertThrows(ProjectNotFoundException.class, () -> taskManSystem.deleteTask("", ""));
    }

    @Test
    public void testStart() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        taskManSystem.startTask("New Project", "New Task", user, Role.SYSADMIN);
        assertEquals(Status.EXECUTING, taskManSystem.getTaskData("New Project", "New Task").getStatus());

        assertThrows(ProjectNotFoundException.class, () -> taskManSystem.startTask("", "", user, Role.SYSADMIN));
    }

    @Test
    public void testFinishTask() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException, EndTimeBeforeStartTimeException, IncorrectUserException {
        taskManSystem.startTask("New Project", "New Task", user, Role.SYSADMIN);
        taskManSystem.finishTask("New Project", "New Task", user);
        assertEquals(Status.FINISHED, taskManSystem.getTaskData("New Project", "New Task").getStatus());

        assertThrows(ProjectNotFoundException.class, () -> taskManSystem.finishTask("", "", user));
    }

    @Test
    public void testFailTask() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException, EndTimeBeforeStartTimeException, IncorrectUserException {
        taskManSystem.startTask("New Project", "New Task", user, Role.SYSADMIN);

        taskManSystem.failTask("New Project", "New Task", user);
        assertEquals(Status.FAILED, taskManSystem.getTaskData("New Project", "New Task").getStatus());

        assertThrows(ProjectNotFoundException.class, () -> taskManSystem.failTask("", "", user));
    }

    @Test
    public void testRestartTask() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException, EndTimeBeforeStartTimeException, IncorrectUserException {
        taskManSystem.startTask("New Project", "New Task", user, Role.SYSADMIN);

        // Restart finished task
        taskManSystem.finishTask("New Project", "New Task", user);
        taskManSystem.undoEndTask("New Project", "New Task");
        assertEquals(Status.EXECUTING, taskManSystem.getTaskData("New Project", "New Task").getStatus());

        // Restart failed task
        taskManSystem.failTask("New Project", "New Task", user);
        taskManSystem.undoEndTask("New Project", "New Task");
        assertEquals(Status.EXECUTING, taskManSystem.getTaskData("New Project", "New Task").getStatus());

        assertThrows(ProjectNotFoundException.class, () -> taskManSystem.undoEndTask("", ""));
    }


    @Test
    public void testStopTask() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException, IncorrectUserException {
        taskManSystem.startTask("New Project", "New Task", user, Role.SYSADMIN);

        taskManSystem.undoStartTask("New Project", "New Task", user);
        assertEquals(Status.AVAILABLE, taskManSystem.getTaskData("New Project", "New Task").getStatus());

        assertThrows(ProjectNotFoundException.class, () -> taskManSystem.undoStartTask("", "", user));
    }

    @Test
    public void testAdvancetime() throws InvalidTimeException, NewTimeBeforeSystemTimeException {
        // Advance with new timestamp
        taskManSystem.advanceTime(new Time(15));
        assertEquals(new Time(15), taskManSystem.getSystemTime());

        // Advance with amount of minutes
        taskManSystem.advanceTime(10);
        assertEquals(new Time(25), taskManSystem.getSystemTime());

        // Test invalid advances
        assertThrows(NewTimeBeforeSystemTimeException.class, () -> taskManSystem.advanceTime(new Time(10)));
        assertThrows(NewTimeBeforeSystemTimeException.class, () -> taskManSystem.advanceTime(-10));
    }

    @Test
    public void testReplaceTask() throws ProjectNotFoundException, EndTimeBeforeStartTimeException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectUserException, InvalidTimeException, TaskNameAlreadyInUseException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        taskManSystem.startTask("New Project", "New Task", user, Role.SYSADMIN);
        taskManSystem.failTask("New Project", "New Task", user);

        taskManSystem.replaceTaskInProject("New Project", "Replacement", "", new Time(20), 0.2, "New Task");
        assertEquals("Replacement", taskManSystem.getTaskData("New Project", "New Task").getReplacementTaskName());
        assertEquals("New Task", taskManSystem.getTaskData("New Project", "Replacement").getReplacesTaskName());
    }

    @Test
    public void addRemoveNextTask() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
        taskManSystem.addNextTaskToProject("New Project", "New Task", "Second Project", "Second Task");
        assertEquals(1, taskManSystem.getTaskData("New Project", "New Task").getNextTasksData().size());
        assertEquals(1, taskManSystem.getTaskData("Second Project", "Second Task").getPrevTasksData().size());

        taskManSystem.removeNextTaskFromProject("New Project", "New Task", "Second Project", "Second Task");
        assertTrue(taskManSystem.getTaskData("New Project", "New Task").getNextTasksData().isEmpty());
        assertTrue(taskManSystem.getTaskData("Second Project", "Second Task").getPrevTasksData().isEmpty());

        assertThrows(ProjectNotFoundException.class, () -> taskManSystem.addNextTaskToProject("", "", "", ""));
        assertThrows(ProjectNotFoundException.class, () -> taskManSystem.removeNextTaskFromProject("", "", "", ""));
    }

    @Test
    public void addRemovePrevTask() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
        taskManSystem.addPrevTaskToProject("New Project", "New Task", "Second Project", "Second Task");
        assertEquals(1, taskManSystem.getTaskData("New Project", "New Task").getPrevTasksData().size());
        assertEquals(1, taskManSystem.getTaskData("Second Project", "Second Task").getNextTasksData().size());

        taskManSystem.removePrevTaskFromProject("New Project", "New Task", "Second Project", "Second Task");
        assertTrue(taskManSystem.getTaskData("New Project", "New Task").getPrevTasksData().isEmpty());
        assertTrue(taskManSystem.getTaskData("Second Project", "Second Task").getNextTasksData().isEmpty());

        assertThrows(ProjectNotFoundException.class, () -> taskManSystem.addPrevTaskToProject("", "", "", ""));
        assertThrows(ProjectNotFoundException.class, () -> taskManSystem.removePrevTaskFromProject("", "", "", ""));
    }


    @After
    public void testReset() throws InvalidTimeException {
        taskManSystem.reset();
        assertEquals(List.of(), taskManSystem.getProjectNames());
        assertEquals(new Time(0), taskManSystem.getSystemTime());
    }
}
