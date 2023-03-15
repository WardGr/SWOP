import org.junit.Test;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class TaskManSystemTest {
    @Test
    public void testTaskManSystem() throws InvalidTimeException, ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, NewTimeBeforeSystemTimeException, UserNotAllowedToChangeTaskException, IncorrectTaskStatusException, FailTimeAfterSystemTimeException {
        Time time = new Time(320);
        TaskManSystem taskManSystem = new TaskManSystem(time);
        assertEquals(5, taskManSystem.getSystemHour());
        assertEquals(20, taskManSystem.getSystemMinute());
        assertEquals(taskManSystem.getProjectNamesWithStatus(), new LinkedList<>());
        Exception exception = assertThrows(ProjectNotFoundException.class, () -> {
            taskManSystem.showProject("car");
        });
        exception = assertThrows(ProjectNotFoundException.class, () -> {
            taskManSystem.showProject("house");
        });
        exception = assertThrows(DueBeforeSystemTimeException.class, () -> {
            taskManSystem.createProject("car", "Make a Honda Civic 2020", 2, 3);
        });
        exception = assertThrows(DueBeforeSystemTimeException.class, () -> { // Zelfde tijd!
            taskManSystem.createProject("house", "Make a house", 5, 20);
        });
        exception = assertThrows(InvalidTimeException.class, () -> {
            taskManSystem.createProject("car", "Make a Honda Civic 2020", 24, 60);
        });
        taskManSystem.createProject("car", "Make a Honda Civic 2020", 63, 20);
        assertEquals(taskManSystem.getProjectNamesWithStatus().size(), 1);
        assertEquals(taskManSystem.getProjectNamesWithStatus().get(0).getKey(), "car");
        assertEquals(taskManSystem.getProjectNamesWithStatus().get(0).getValue(), "ongoing");
        assertEquals(taskManSystem.showProject("car"), "Project Name:  car\n" +
                "Description:   Make a Honda Civic 2020\n" +
                "Creation Time: 5 hours, 20 minutes\n" +
                "Due Time:      63 hours, 20 minutes\n" +
                "\nTasks:\n");
        exception = assertThrows(ProjectNotFoundException.class, () -> {
            taskManSystem.showProject("house");
        });

        taskManSystem.createProject("house", "Make a house", 50, 43);
        assertEquals(taskManSystem.getProjectNamesWithStatus().size(), 2);
        assertEquals(taskManSystem.getProjectNamesWithStatus().get(1).getKey(), "house");
        assertEquals(taskManSystem.getProjectNamesWithStatus().get(1).getValue(), "ongoing");
        exception = assertThrows(ProjectNameAlreadyInUseException.class, () -> {
            taskManSystem.createProject("car", "Make a Honda Civic 2020", 50, 20);
        });
        exception = assertThrows(ProjectNameAlreadyInUseException.class, () -> {
            taskManSystem.createProject("house", "Make a house", 50, 20);
        });

        // TODO taskNotFoundException
        User mechanic = new User("mechanic", "honda123", Role.DEVELOPER);
        List prev = new LinkedList<>();
        taskManSystem.addTaskToProject("car", "Engine", "Get Honda to deliver engine", 7, 3, 10, new LinkedList<>(),  mechanic);
        taskManSystem.addTaskToProject("car", "Wheels", "Get Honda to deliver wheels", 7, 3, 10, new LinkedList<>(),  mechanic);
        prev.add("Wheels");
        taskManSystem.addTaskToProject("car", "Body", "Get Honda to deliver body", 7, 3, 10, prev,  mechanic);
        prev.add("Body");
        taskManSystem.addTaskToProject("car", "Paint", "Get Honda to deliver paint", 7, 3, 10, prev,  mechanic);
        exception = assertThrows(TaskNameAlreadyInUseException.class, () -> {
            taskManSystem.addTaskToProject("car", "Engine", "Get Honda to deliver engine", 7, 3, 10, new LinkedList<>(),  mechanic);
        });
        exception = assertThrows(TaskNameAlreadyInUseException.class, () -> {
            taskManSystem.addTaskToProject("car", "Wheels", "Get Honda to deliver wheels", 7, 3, 10, new LinkedList<>(),  mechanic);
        });
        exception = assertThrows(TaskNameAlreadyInUseException.class, () -> {
            taskManSystem.addTaskToProject("car", "Body", "Get Honda to deliver body", 7, 3, 10, new LinkedList<>(),  mechanic);
        });
        exception = assertThrows(ProjectNotFoundException.class, () -> {
            taskManSystem.addTaskToProject("minecraft", "Engine", "Get Honda to deliver engine", 7, 3, 10, new LinkedList<>(),  mechanic);
        });
        exception = assertThrows(ProjectNotFoundException.class, () -> {
            taskManSystem.addTaskToProject("minecraft", "Wheels", "Get Honda to deliver wheels", 7, 3, 10, new LinkedList<>(),  mechanic);
        });
        exception = assertThrows(InvalidTimeException.class, () -> {
            taskManSystem.addTaskToProject("car", "Engine", "Get Honda to deliver engine", 24, 60, 10, new LinkedList<>(),  mechanic);
        });
        exception = assertThrows(InvalidTimeException.class, () -> {
            taskManSystem.addTaskToProject("car", "Wheels", "Get Honda to deliver wheels", 24, -3, 10, new LinkedList<>(),  mechanic);
        });
        List exception_list = new ArrayList();
        exception_list.add("Honda");
        exception = assertThrows(TaskNotFoundException.class, () -> {
            taskManSystem.addTaskToProject("car", "Fail", "This will fail", 7, 3, 10, exception_list,  mechanic);
        });

        User builder = new User("thomas", "builder123", Role.DEVELOPER);
        taskManSystem.addTaskToProject("house", "Walls", "Make walls out of concrete", 7, 3, 10, new LinkedList<>(),  builder);

        List tasks = new ArrayList();


        assertEquals(taskManSystem.showAvailableTasks().get(0), new AbstractMap.SimpleEntry<>("car", "Engine"));
        assertEquals(taskManSystem.showAvailableTasks().get(1), new AbstractMap.SimpleEntry<>("car", "Wheels"));
        assertTrue(taskManSystem.showAvailableTasks().size() == 3);



        assertEquals(3, taskManSystem.showAvailableTasks().size());
        assertEquals(0, taskManSystem.showExecutingTasks().size());
        assertEquals(Status.AVAILABLE, taskManSystem.getStatus("car", "Engine"));
        assertEquals(Status.AVAILABLE, taskManSystem.getStatus("car", "Wheels"));
        assertEquals(Status.AVAILABLE, taskManSystem.getStatus("house", "Walls"));

        assertEquals(1, taskManSystem.getNextStatuses("car", "Engine").size());
        assertEquals(1, taskManSystem.getNextStatuses("car", "Wheels").size());
        assertEquals(1, taskManSystem.getNextStatuses("house", "Walls").size());

        taskManSystem.startTask("car", "Engine", 4, 34, mechanic);
        assertEquals(1, taskManSystem.showExecutingTasks().size());
        taskManSystem.startTask("car", "Wheels", 4, 34, mechanic);
        assertEquals(2, taskManSystem.showExecutingTasks().size());
        taskManSystem.startTask("house", "Walls", 4, 35, builder);
        assertEquals(3, taskManSystem.showExecutingTasks().size());

        assertEquals(2, taskManSystem.getNextStatuses("car", "Engine").size());
        assertEquals(2, taskManSystem.getNextStatuses("car", "Wheels").size());
        assertEquals(2, taskManSystem.getNextStatuses("house", "Walls").size());

        assertEquals(Status.EXECUTING, taskManSystem.getStatus("car", "Engine"));
        assertEquals(Status.EXECUTING, taskManSystem.getStatus("car", "Wheels"));
        assertEquals(Status.EXECUTING, taskManSystem.getStatus("house", "Walls"));
        assertEquals(Status.UNAVAILABLE, taskManSystem.getStatus("car", "Body"));
        assertEquals(Status.UNAVAILABLE, taskManSystem.getStatus("car", "Paint"));



        taskManSystem.endTask("car", "Engine", Status.FINISHED, 6, 35, mechanic);
        taskManSystem.endTask("car", "Wheels", Status.FINISHED, 6, 35, mechanic);
        taskManSystem.endTask("house", "Walls", Status.FINISHED, 6, 35, builder);


        taskManSystem.advanceTime(6, 22);
        assertEquals(taskManSystem.getSystemHour(), 6);
        assertEquals(taskManSystem.getSystemMinute(), 22);
        taskManSystem.advanceTime(7, 33);
        assertEquals(taskManSystem.getSystemHour(), 7);
        assertEquals(taskManSystem.getSystemMinute(), 33);

        assertEquals(0, taskManSystem.getNextStatuses("car", "Engine").size());
        assertEquals(0, taskManSystem.getNextStatuses("car", "Wheels").size());
        assertEquals(0, taskManSystem.getNextStatuses("house", "Walls").size());

        exception = assertThrows(NewTimeBeforeSystemTimeException.class, () -> {
            taskManSystem.advanceTime(6, 22);
        });
        exception = assertThrows(NewTimeBeforeSystemTimeException.class, () -> {
            taskManSystem.advanceTime(7, 32);
        });
        /*exception = assertThrows(NewTimeBeforeSystemTimeException.class, () -> {
            taskManSystem.advanceTime(7, 33);
        });*/ // TODO dit zou moeten werken, niet?

        assertEquals(Status.AVAILABLE, taskManSystem.getStatus("car", "Body"));
        assertEquals(Status.UNAVAILABLE, taskManSystem.getStatus("car", "Paint"));
        assertEquals(Status.FINISHED, taskManSystem.getStatus("car", "Engine"));
        assertEquals(Status.FINISHED, taskManSystem.getStatus("car", "Wheels"));
        assertEquals(Status.FINISHED, taskManSystem.getStatus("house", "Walls"));
        assertEquals(1, taskManSystem.getNextStatuses("car", "Body").size());
        assertEquals(0, taskManSystem.getNextStatuses("car", "Paint").size());
        assertEquals(1, taskManSystem.showAvailableTasks().size());
        assertEquals(1, taskManSystem.showAvailableTasks().size());
        assertEquals(0, taskManSystem.showExecutingTasks().size());
        taskManSystem.startTask("car", "Body", 8, 34, mechanic);
        taskManSystem.advanceTime(8, 40);
        assertEquals(2, taskManSystem.getNextStatuses("car", "Body").size());
        assertEquals(0, taskManSystem.showAvailableTasks().size());
        assertEquals(1, taskManSystem.showExecutingTasks().size());
        taskManSystem.endTask("car", "Body", Status.FINISHED, 12, 35, mechanic);
        taskManSystem.advanceTime(14, 33);
        assertEquals(0, taskManSystem.showExecutingTasks().size());
        assertEquals(1, taskManSystem.showAvailableTasks().size());
        taskManSystem.startTask("car", "Paint", 15, 34, mechanic);
        taskManSystem.advanceTime(15, 40);
        assertEquals(0, taskManSystem.showAvailableTasks().size());
        assertEquals(1, taskManSystem.showExecutingTasks().size());
        exception = assertThrows(FailTimeAfterSystemTimeException.class, () -> {
            taskManSystem.endTask("car", "Paint", Status.FAILED, 568, 3,  mechanic);
        });
        taskManSystem.endTask("car", "Paint", Status.FINISHED, 16, 35, mechanic);
        taskManSystem.advanceTime(17, 33);

        assertEquals(Status.FINISHED, taskManSystem.getStatus("car", "Engine"));
        assertEquals(Status.FINISHED, taskManSystem.getStatus("car", "Wheels"));
        assertEquals(Status.FINISHED, taskManSystem.getStatus("house", "Walls"));
        assertEquals(Status.FINISHED, taskManSystem.getStatus("car", "Body"));
        assertEquals(Status.FINISHED, taskManSystem.getStatus("car", "Paint"));

        exception = assertThrows(ProjectNotFoundException.class, () -> {
            taskManSystem.addTaskToProject("minecraft", "Engine", "Get Honda to deliver engine", 7, 3, 10, new LinkedList<>(),  mechanic);
        });
        exception = assertThrows(TaskNotFoundException.class, () -> {
            taskManSystem.addTaskToProject("car", "Fail", "This will fail", 7, 3, 10, exception_list,  mechanic);
        });
        exception = assertThrows(InvalidTimeException.class, () -> {
            taskManSystem.addTaskToProject("car", "Windows", "Install windows", 7, 60, 10, new LinkedList<>(),  mechanic);
        });
        exception = assertThrows(InvalidTimeException.class, () -> {
            taskManSystem.addTaskToProject("car", "Wheels", "Get Honda to deliver wheels", 7, -3, 10, new LinkedList<>(),  mechanic);
        });
        exception = assertThrows(UserNotAllowedToChangeTaskException.class, () -> {
            taskManSystem.startTask("car", "Engine", 4, 34, builder);
        });
        exception = assertThrows(UserNotAllowedToChangeTaskException.class, () -> {
            taskManSystem.endTask("car", "Engine", Status.FINISHED, 6, 35, builder);
        });
        exception = assertThrows(IncorrectTaskStatusException.class, () -> {
            taskManSystem.endTask("car", "Engine", null, 4, 34, mechanic);
        });

        assertEquals(0, taskManSystem.getNextStatuses("car", "Engine").size());
        assertEquals(0, taskManSystem.getNextStatuses("car", "Wheels").size());
        assertEquals(0, taskManSystem.getNextStatuses("house", "Walls").size());
        assertEquals(0, taskManSystem.getNextStatuses("car", "Body").size());
        assertEquals(0, taskManSystem.getNextStatuses("car", "Paint").size());



    }
}
