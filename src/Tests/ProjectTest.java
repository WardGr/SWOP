package Tests;

import Domain.*;
import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.IllegalTaskRolesException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectTest {

    @Mock
    private User user;
    private Project project;
    private List<Role> roles;

    @Before
    public void setUp() throws InvalidTimeException, DueTimeBeforeCreationTimeException {
        this.project = new Project("Project 1", "", new Time(0), new Time(100));
        this.roles = List.of(Role.PYTHONPROGRAMMER);
    }

    @Test
    public void testProject() throws InvalidTimeException, TaskNameAlreadyInUseException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException, IllegalTaskRolesException, ProjectNotOngoingException, EndTimeBeforeStartTimeException, IncorrectUserException, UserAlreadyAssignedToTaskException, IncorrectRoleException {

        /*
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
        assertThrows(TaskNotFoundException.class, () -> project.addPrevTask("", ""));
        assertThrows(TaskNotFoundException.class, () -> project.removePrevTask("", ""));
        assertThrows(TaskNotFoundException.class, () -> project.removeNextTask("", ""));

        assertThrows(DueTimeBeforeCreationTimeException.class, () -> new Project("", "", new Time(10), new Time(0)));


        project.addNewTask("Previous Task", "", new Time(10), 0, roles, Set.of(), Set.of());

        assertThrows(TaskNameAlreadyInUseException.class, () -> project.addNewTask("Previous Task", "", new Time(10), 0, roles, Set.of(), Set.of()));
        assertThrows(TaskNotFoundException.class, () -> project.addNewTask("Task", "", new Time(10), 0, roles, Set.of(""), Set.of()));
        assertThrows(TaskNotFoundException.class, () -> project.addNewTask("Task", "", new Time(10), 0, roles, Set.of(), Set.of("")));

        project.addNewTask("Next Task", "", new Time(0), 0, roles, Set.of(), Set.of());
        project.addNewTask("Current Task", "", new Time(10), 0, roles, Set.of("Previous Task"), Set.of("Next Task"));

        assertEquals("ongoing", project.getStatus().toString());

        assertNotNull(project.getStatus("Previous Task"));

        project.startTask("Previous Task", new Time(0), user, Role.PYTHONPROGRAMMER);
        assertEquals(Status.EXECUTING, project.getStatus("Previous Task"));
        project.finishTask("Previous Task", user, new Time(20));
        assertEquals(Status.FINISHED, project.getStatus("Previous Task"));
        assertEquals(Status.AVAILABLE, project.getStatus("Current Task"));

        project.startTask("Current Task", new Time(20), user, Role.PYTHONPROGRAMMER);
        assertEquals(Status.EXECUTING, project.getStatus("Current Task"));
        project.finishTask("Current Task", user, new Time(40));
        assertEquals(Status.FINISHED, project.getStatus("Current Task"));
        assertEquals(Status.AVAILABLE, project.getStatus("Next Task"));

        project.startTask("Next Task", new Time(40), user, Role.PYTHONPROGRAMMER);
        assertEquals(Status.EXECUTING, project.getStatus("Next Task"));
        project.finishTask("Next Task", user, new Time(60));
        assertEquals(Status.FINISHED, project.getStatus("Next Task"));

        assertEquals("finished", project.getStatus().toString());

        assertThrows(ProjectNotOngoingException.class, () -> project.addNewTask("", "", new Time(10), 0, List.of(), Set.of(), Set.of()));

        // PROJECT PROXY TEST

        ProjectData projectData = project.getProjectData();

        assertEquals(project.getName(), projectData.getName());
        assertEquals(project.getStatus(), projectData.getStatus());
        assertEquals(project.getReplacedTasksNames(), projectData.getReplacedTasksNames());
        assertEquals(project.getActiveTasksNames(), projectData.getActiveTasksNames());
        assertEquals(project.getReplacedTasksNames(), projectData.getReplacedTasksNames());
        assertEquals(project.getCreationTime(), projectData.getCreationTime());
        assertEquals(project.getDueTime(), projectData.getDueTime());
        assertEquals(project.getDescription(), projectData.getDescription());

         */


    }
}
