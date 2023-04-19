package Tests;

import Domain.*;
import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.NonDeveloperRoleException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
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
    public void setUp() throws InvalidTimeException {
        this.taskManSystem = new TaskManSystem(new Time(0));

        // Mockito.when(user.getRoles()).thenReturn(Set.of(Role.JAVAPROGRAMMER));
    }

    @Test
    public void testTaskManSystem() throws InvalidTimeException, NewTimeBeforeSystemTimeException, ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNameAlreadyInUseException, TaskNotFoundException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException, NonDeveloperRoleException, UserAlreadyAssignedToTaskException, IncorrectRoleException, EndTimeBeforeStartTimeException, IncorrectUserException {

        assertNotNull(taskManSystem.getTaskManSystemData());
        assertEquals(new Time(0), taskManSystem.getSystemTime());

        assertThrows(ProjectNotFoundException.class, () -> taskManSystem.getProjectData(""));
        assertThrows(ProjectNotFoundException.class, () -> taskManSystem.getTaskData("", ""));
        assertThrows(ProjectNotFoundException.class, () -> taskManSystem.getTaskData("", ""));
        assertThrows(ProjectNotFoundException.class, () -> taskManSystem.finishTask("", "", user));
        assertThrows(ProjectNotFoundException.class, () -> taskManSystem.failTask("", "", user));
        assertThrows(ProjectNotFoundException.class, () -> taskManSystem.startTask("", "", user, Role.SYSADMIN));
        assertThrows(ProjectNotFoundException.class, () -> taskManSystem.addTaskToProject("", "", "", new Time(0), 0, List.of(), Set.of(), Set.of()));
        assertThrows(ProjectNotFoundException.class, () -> taskManSystem.addNextTaskToProject("", "", ""));
        assertThrows(ProjectNotFoundException.class, () -> taskManSystem.addPreviousTaskToProject("", "", ""));
        assertThrows(ProjectNotFoundException.class, () -> taskManSystem.removeNextTaskFromProject("", "", ""));
        assertThrows(ProjectNotFoundException.class, () -> taskManSystem.removePreviousTaskFromProject("", "", ""));
        assertThrows(ProjectNotFoundException.class, () -> taskManSystem.replaceTaskInProject("", "", "", new Time(0), 0, ""));

        assertThrows(NewTimeBeforeSystemTimeException.class, () -> taskManSystem.advanceTime(-1));
        taskManSystem.advanceTime(10);
        assertThrows(NewTimeBeforeSystemTimeException.class, () -> taskManSystem.advanceTime(new Time(5)));

        assertThrows(DueBeforeSystemTimeException.class, () -> taskManSystem.createProject("", "", new Time(5)));

        taskManSystem.createProject("Project 1", "Test", new Time(15));
        assertEquals(List.of("Project 1"), taskManSystem.getProjectNames());
        assertNotNull(taskManSystem.getProjectData("Project 1"));
        assertThrows(ProjectNameAlreadyInUseException.class, () -> taskManSystem.createProject("Project 1", "Test", new Time(15)));

        taskManSystem.createProject("Project 2", "Test", new Time(15));
        assertEquals(List.of("Project 1", "Project 2"), taskManSystem.getProjectNames());


        taskManSystem.addTaskToProject("Project 1", "Task 1", "Test Task", new Time(10), 0, List.of(Role.JAVAPROGRAMMER), Set.of(), Set.of());
        assertNotNull(taskManSystem.getTaskData("Project 1", "Task 1"));
        assertEquals(Status.AVAILABLE, taskManSystem.getTaskData("Project 1", "Task 1").getStatus());

        taskManSystem.startTask("Project 1", "Task 1", user, Role.JAVAPROGRAMMER);
        assertEquals(Status.EXECUTING, taskManSystem.getTaskData("Project 1", "Task 1").getStatus());
        taskManSystem.advanceTime(10);
        taskManSystem.failTask("Project 1", "Task 1", user);
        assertEquals(Status.FAILED, taskManSystem.getTaskData("Project 1", "Task 1").getStatus());

        assertEquals(List.of("Task 1"), taskManSystem.getProjectData("Project 1").getActiveTasksNames());
        assertEquals(List.of(), taskManSystem.getProjectData("Project 1").getReplacedTasksNames());

        taskManSystem.replaceTaskInProject("Project 1", "Replacement Task", "Test", new Time(10), 0, "Task 1");
        assertEquals(Status.AVAILABLE, taskManSystem.getTaskData("Project 1", "Replacement Task").getStatus());
        assertEquals(List.of("Replacement Task"), taskManSystem.getProjectData("Project 1").getActiveTasksNames());
        assertEquals(List.of("Task 1"), taskManSystem.getProjectData("Project 1").getReplacedTasksNames());

        taskManSystem.addTaskToProject("Project 1", "Next Task", "Test", new Time(0), 0, List.of(), Set.of(), Set.of());
        assertEquals(List.of("Replacement Task", "Next Task"), taskManSystem.getProjectData("Project 1").getActiveTasksNames());
        taskManSystem.addNextTaskToProject("Project 1", "Task 1", "Next Task");
        assertEquals(Status.UNAVAILABLE, taskManSystem.getTaskData("Project 1", "Next Task").getStatus());

        taskManSystem.removeNextTaskFromProject("Project 1", "Task 1", "Next Task");
        assertEquals(Status.AVAILABLE, taskManSystem.getTaskData("Project 1", "Replacement Task").getStatus());
        assertEquals(Status.AVAILABLE, taskManSystem.getTaskData("Project 1", "Next Task").getStatus());
        assertEquals(List.of("Replacement Task", "Next Task"), taskManSystem.getProjectData("Project 1").getActiveTasksNames());
        assertEquals(List.of("Task 1"), taskManSystem.getProjectData("Project 1").getReplacedTasksNames());

        taskManSystem.addPreviousTaskToProject("Project 1", "Next Task", "Task 1");
        assertEquals(Status.UNAVAILABLE, taskManSystem.getTaskData("Project 1", "Next Task").getStatus());

        taskManSystem.removePreviousTaskFromProject("Project 1", "Next Task", "Task 1");
        assertEquals(Status.AVAILABLE, taskManSystem.getTaskData("Project 1", "Replacement Task").getStatus());
        assertEquals(Status.AVAILABLE, taskManSystem.getTaskData("Project 1", "Next Task").getStatus());
        assertEquals(List.of("Replacement Task", "Next Task"), taskManSystem.getProjectData("Project 1").getActiveTasksNames());
        assertEquals(List.of("Task 1"), taskManSystem.getProjectData("Project 1").getReplacedTasksNames());

        //  TODO: fix dit
        taskManSystem.startTask("Project 1", "Replacement Task", user, Role.JAVAPROGRAMMER);
        assertEquals(Status.EXECUTING, taskManSystem.getTaskData("Project 1", "Replacement Task").getStatus());
        taskManSystem.finishTask("Project 1", "Replacement Task", user);
        assertEquals(Status.FINISHED, taskManSystem.getTaskData("Project 1", "Replacement Task").getStatus());
        assertEquals(Status.AVAILABLE, taskManSystem.getTaskData("Project 1", "Next Task").getStatus());


        assertEquals(new Time(20), taskManSystem.getTaskManSystemData().getSystemTime());
        assertEquals(List.of("Project 1", "Project 2"), taskManSystem.getTaskManSystemData().getProjectNames());

        taskManSystem.clear();
        assertEquals(List.of(), taskManSystem.getProjectNames());

    }
}
