package Tests.Domain.ProjectTests;

import Domain.DataClasses.EndTimeBeforeStartTimeException;
import Domain.DataClasses.InvalidTimeException;
import Domain.Project.DueTimeBeforeCreationTimeException;
import Domain.Project.ProjectNotOngoingException;
import Domain.Project.TaskNotFoundException;
import Domain.Task.*;
import Domain.Task.IncorrectTaskStatusException;
import Domain.Task.LoopDependencyGraphException;
import Domain.User.IncorrectUserException;
import Domain.User.UserAlreadyAssignedToTaskException;
import org.junit.Test;

public class ProjectDataTest {
    @Test
    public void test() throws InvalidTimeException, DueTimeBeforeCreationTimeException, TaskNameAlreadyInUseException, TaskNotFoundException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException, IllegalTaskRolesException, UserAlreadyAssignedToTaskException, IncorrectRoleException, EndTimeBeforeStartTimeException, IncorrectUserException {
        /*
        // Testing without replaces
        Project omer = new Project("Omer Brewery", "Process of the Omer brewery", new Time(13), new Time(20));
        List<Role> roles = new ArrayList<>();
        roles.add(Role.JAVAPROGRAMMER);
        roles.add(Role.PYTHONPROGRAMMER);
        omer.addNewTask("Hire brewer", "Hire a suitable brewer", new Time(5), .2, roles, new HashSet<>(), new HashSet<>());
        omer.addNewTask("Brew beer", "Have the brewer brew our delicious beer", new Time(30), 0.5, roles, new HashSet<>(), new HashSet<>());
        omer.addNextTask("Hire brewer", "Brew beer");
        omer.addNewTask("Sell beer", "Sell the beer", new Time(10), 0.3, roles, new HashSet<>(), new HashSet<>());
        omer.addNextTask("Brew beer", "Sell beer");
        ProjectData projectData = new ProjectData(omer);

        assertEquals("Omer Brewery", projectData.getName());
        assertEquals("Process of the Omer brewery", projectData.getDescription());
        assertEquals(new Time(13), projectData.getCreationTime());
        assertEquals(new Time(20), projectData.getDueTime());
        assertEquals(3, projectData.getActiveTasksNames().size());
        assertEquals(0, projectData.getReplacedTasksData().size());

        Set javaRoles = new HashSet();
        javaRoles.add(Role.JAVAPROGRAMMER);
        User java = new User("Java", "Java", javaRoles);
        Set pythonRoles = new HashSet();
        pythonRoles.add(Role.PYTHONPROGRAMMER);
        User python = new User("Python", "Python", pythonRoles);

        assertEquals(ProjectStatus.ONGOING, projectData.getStatus());

        omer.startTask("Hire brewer", new Time(14), java, Role.JAVAPROGRAMMER);
        omer.startTask("Hire brewer", new Time(15), python, Role.PYTHONPROGRAMMER);
        omer.finishTask("Hire brewer", java,  new Time(16));

        assertEquals(ProjectStatus.ONGOING, projectData.getStatus());
        assertEquals(3, projectData.getActiveTasksNames().size());
        assertEquals(0, projectData.getReplacedTasksData().size());

        omer.startTask("Brew beer", new Time(17), java, Role.JAVAPROGRAMMER);
        omer.startTask("Brew beer", new Time(17), python, Role.PYTHONPROGRAMMER);
        omer.finishTask("Brew beer", python,  new Time(19));

        assertEquals(ProjectStatus.ONGOING, projectData.getStatus());
        assertEquals(3, projectData.getActiveTasksNames().size());
        assertEquals(0, projectData.getReplacedTasksData().size());

        omer.startTask("Sell beer", new Time(19), java, Role.JAVAPROGRAMMER);
        omer.startTask("Sell beer", new Time(19), python, Role.PYTHONPROGRAMMER);
        omer.finishTask("Sell beer", python,  new Time(20));

        assertEquals(ProjectStatus.FINISHED, projectData.getStatus());
        assertEquals(3, projectData.getActiveTasksNames().size());
        assertEquals(0, projectData.getReplacedTasksData().size());


        // Testing with replaces
        Project Duvel = new Project("Duvel Moortgat", "Distillery of the Duvel Barrel aged beer", new Time(2), new Time(50));
        roles = new ArrayList<>();
        roles.add(Role.JAVAPROGRAMMER);

        Duvel.addNewTask("Hire destiller", "Hire a suitable destiller", new Time(5), .2, roles, new HashSet<>(), new HashSet<>());
        Duvel.addNewTask("Distill beer", "Have the destiller distill our delicious beer", new Time(30), 0.5, roles, new HashSet<>(), new HashSet<>());
        Duvel.addNewTask("Bottle beer", "Bottle the beer", new Time(10), 0.3, roles, new HashSet<>(), new HashSet<>());
        ProjectData projectDataDuvel = new ProjectData(Duvel);

        assertEquals("Duvel Moortgat", projectDataDuvel.getName());
        assertEquals("Distillery of the Duvel Barrel aged beer", projectDataDuvel.getDescription());
        assertEquals(new Time(2), projectDataDuvel.getCreationTime());
        assertEquals(new Time(50), projectDataDuvel.getDueTime());
        assertEquals(3, projectDataDuvel.getActiveTasksNames().size());
        assertEquals(0, projectDataDuvel.getReplacedTasksData().size());

        Duvel.startTask("Hire destiller", new Time(14), java, Role.JAVAPROGRAMMER);
        Duvel.failTask("Hire destiller", java, new Time(15));
        Duvel.replaceTask("Steal destiller", "Get chouffe destiller to work for us", new Time(3), .1, "Hire destiller");

        assertEquals(3, projectDataDuvel.getActiveTasksNames().size());
        assertEquals(1, projectDataDuvel.getReplacedTasksData().size());


        assertTrue(projectDataDuvel.getTasksData().stream().map(TaskData::getName).toList().contains("Steal destiller"));
        assertFalse(projectDataDuvel.getTasksData().stream().map(TaskData::getName).toList().contains("Hire destiller"));
        assertTrue(projectDataDuvel.getReplacedTasksData().stream().map(TaskData::getName).toList().contains("Hire destiller"));

        assertEquals(ProjectStatus.ONGOING, projectDataDuvel.getStatus());

        Duvel.startTask("Steal destiller", new Time(16), java, Role.JAVAPROGRAMMER);
        Duvel.finishTask("Steal destiller", java, new Time(19));

        assertEquals(3, projectDataDuvel.getActiveTasksNames().size());
        assertEquals(1, projectDataDuvel.getReplacedTasksData().size());
        assertEquals(ProjectStatus.ONGOING, projectDataDuvel.getStatus());

        Duvel.startTask("Distill beer", new Time(19), java, Role.JAVAPROGRAMMER);
        Duvel.finishTask("Distill beer", java, new Time(40));

        assertEquals(3, projectDataDuvel.getActiveTasksNames().size());
        assertEquals(1, projectDataDuvel.getReplacedTasksData().size());
        assertEquals(ProjectStatus.ONGOING, projectDataDuvel.getStatus());

        Duvel.startTask("Bottle beer", new Time(40), java, Role.JAVAPROGRAMMER);
        Duvel.failTask("Bottle beer", java, new Time(45)); // woops, the bottle broke!

        Duvel.replaceTask("Bottle beer 2", "Bottle beer again", new Time(10), 0.3, "Bottle beer");
        Duvel.startTask("Bottle beer 2", new Time(45), java, Role.JAVAPROGRAMMER);
        Duvel.finishTask("Bottle beer 2", java, new Time(50));

        assertEquals(3, projectDataDuvel.getActiveTasksNames().size());
        assertEquals(2, projectDataDuvel.getReplacedTasksData().size());
        assertEquals(ProjectStatus.FINISHED, projectDataDuvel.getStatus());

        assertTrue(projectDataDuvel.getTasksData().stream().map(TaskData::getName).toList().contains("Bottle beer 2"));
        assertFalse(projectDataDuvel.getTasksData().stream().map(TaskData::getName).toList().contains("Bottle beer"));
        assertTrue(projectDataDuvel.getReplacedTasksData().stream().map(TaskData::getName).toList().contains("Bottle beer"));
        assertFalse(projectDataDuvel.getReplacedTasksData().stream().map(TaskData::getName).toList().contains("Bottle beer 2"));

         */
    }
}
