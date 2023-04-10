package Tests;

import Domain.*;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.NonDeveloperRoleException;
import Domain.TaskStates.Task;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class ProjectTest {

    @Mock
    private User user;
    private Project project;
    private List<Role> roles;

    @Before
    public void setUp() throws InvalidTimeException, DueTimeBeforeCreationTimeException {
        this.project = new Project("Project 1", "", new Time(0), new Time(100));
        this.roles = List.of(Role.JAVAPROGRAMMER, Role.PYTHONPROGRAMMER);

    }

    @Test
    public void testProject() throws InvalidTimeException, TaskNameAlreadyInUseException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException, NonDeveloperRoleException, ProjectNotOngoingException {

        assertEquals("Project 1", project.getName());
        assertEquals("", project.getDescription());
        assertEquals(new Time(0), project.getCreationTime());
        assertEquals(new Time(100), project.getDueTime());
        assertEquals(ProjectStatus.ONGOING, project.getStatus());
        assertEquals(List.of(), project.getTasks());
        assertEquals(List.of(), project.getReplacedTasksNames());
        assertEquals(List.of(), project.getActiveTasksNames());
        assertNotNull(project.getProjectData());

        assertThrows(TaskNotFoundException.class, () -> project.getTaskData(""));
        assertThrows(TaskNotFoundException.class, () -> project.getStatus(""));
        assertThrows(TaskNotFoundException.class, () -> project.startTask("", new Time(0), user, Role.PROJECTMANAGER));
        assertThrows(TaskNotFoundException.class, () -> project.finishTask("", user, new Time(0)));
        assertThrows(TaskNotFoundException.class, () -> project.failTask("", user, new Time(0)));
        assertThrows(TaskNotFoundException.class, () -> project.addNextTask("", ""));
        assertThrows(TaskNotFoundException.class, () -> project.addPreviousTask("", ""));
        assertThrows(TaskNotFoundException.class, () -> project.removePreviousTask("", ""));
        assertThrows(TaskNotFoundException.class, () -> project.removeNextTask("", ""));

        assertThrows(DueTimeBeforeCreationTimeException.class, () -> new Project("", "", new Time(10), new Time(0)));


        project.addNewTask("Previous Task", "", new Time(10), 0, roles, Set.of(), Set.of());

        assertThrows(TaskNameAlreadyInUseException.class, () -> project.addNewTask("Previous Task", "", new Time(10), 0, roles, Set.of(), Set.of()));
        assertThrows(TaskNotFoundException.class, () -> project.addNewTask("Task", "", new Time(10), 0, roles, Set.of(""), Set.of()));
        assertThrows(TaskNotFoundException.class, () -> project.addNewTask("Task", "", new Time(10), 0, roles, Set.of(), Set.of("")));

        project.addNewTask("Next Task", "", new Time(0), 0, roles, Set.of(), Set.of());
        project.addNewTask("Current Task", "", new Time(10), 0, roles, Set.of("Previous Task"), Set.of("Next Task"));

        assertNotNull(project.getStatus("Previous Task"));
    }
}
