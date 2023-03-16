import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class ProjectTest {

    @Test
    public void testProject() throws DueBeforeSystemTimeException, InvalidTimeException, TaskNotFoundException, TaskNameAlreadyInUseException, ReplacedTaskNotFailedException, FailTimeAfterSystemTimeException, UserNotAllowedToChangeTaskException, IncorrectTaskStatusException {
        Time minecraft_begin = new Time(0, 0);
        Time minecraft_end = new Time(3000);
        Exception exception = assertThrows(DueBeforeSystemTimeException.class, () -> {
            Project minecraft = new Project("Minecraft", "Build a game", minecraft_end, minecraft_begin);
        });
        Project minecraft = new Project("Minecraft", "Build a game", minecraft_begin, minecraft_end);

        Time car_begin = new Time(2520);
        Time car_end = new Time(3782);
        exception = assertThrows(DueBeforeSystemTimeException.class, () -> {
            Project car = new Project("Car", "Build a car", car_end, car_begin);
        });
        Project car = new Project("Car", "Build a car", car_begin, car_end);

        Time house_begin = new Time(12, 55);
        Time house_end = new Time(11, 0);
        exception = assertThrows(DueBeforeSystemTimeException.class, () -> {
            Project house = new Project("House", "Build a house", house_begin, house_end);
        });
        exception = assertThrows(DueBeforeSystemTimeException.class, () -> {
            Project house = new Project("House", "Build a house", house_begin, house_begin);
        });

        Time house_end2 = new Time(3058, 55);
        Project house = new Project("House", "Build a house", house_begin, house_end2);

        assertEquals("Minecraft", minecraft.getName());
        assertEquals("Build a game", minecraft.getDescription());
        assertEquals(minecraft_begin, minecraft.getCreationTime());
        assertEquals(minecraft_end, minecraft.getDueTime());
        assertEquals(minecraft.getTasks(), new LinkedList<>()); // start with empty list
        assertEquals(minecraft.getStatus(), "ongoing");
        assertNull(minecraft.getTask("Make render"));
        assertEquals("Project Name:  Minecraft\nDescription:   Build a game\nCreation Time: " + minecraft_begin.toString()
                + "\nDue Time:      " + minecraft_end.toString() + "\n\nTasks:\n", minecraft.toString());

        assertEquals("Car", car.getName());
        assertEquals("Build a car", car.getDescription());
        assertEquals(car_begin, car.getCreationTime());
        assertEquals(car_end, car.getDueTime());
        assertEquals(car.getTasks(), new LinkedList<>()); // start with empty list
        assertEquals(car.getStatus(), "ongoing");
        assertNull(car.getTask("Design engine with Honda"));
        assertEquals("Project Name:  Car\nDescription:   Build a car\nCreation Time: " + car_begin.toString()
                + "\nDue Time:      " + car_end.toString() + "\n\nTasks:\n", car.toString());

        assertEquals("House", house.getName());
        assertEquals("Build a house", house.getDescription());
        assertEquals(house_begin, house.getCreationTime());
        assertEquals(house_end2, house.getDueTime());
        assertEquals(house.getTasks(), new LinkedList<>()); // start with empty list
        assertEquals(house.getStatus(), "ongoing");
        assertNull(house.getTask("Talk with architect"));
        assertEquals("Project Name:  House\nDescription:   Build a house\nCreation Time: " + house_begin.toString()
                + "\nDue Time:      " + house_end2.toString() + "\n\nTasks:\n", house.toString());

        User ward = new User("Ward", "minecraftDev123", Role.DEVELOPER);
        User manager = new User("Manager", "minecraftManager123", Role.PROJECTMANAGER);
        Time renderDuration = new Time(0, 30);
        minecraft.addTask("Make Render", "Make a render of the game", renderDuration, 300, new LinkedList<>(), ward);
        exception = assertThrows(TaskNameAlreadyInUseException.class, () -> {
            minecraft.addTask("Make Render", "Make a render of the game", renderDuration, 300, new LinkedList<>(), ward);
        });
        assertEquals(1, minecraft.getTasks().size());
        assertEquals(minecraft.getStatus(), "ongoing");
        minecraft.getTask("Make Render").start(new Time(0, 15), new Time(0, 15), ward);
        minecraft.getTask("Make Render").end(Status.FAILED, new Time(0, 15), new Time(0, 15), ward);
        minecraft.replaceTask("Purchase Render", "Purchase a render of the game", renderDuration, 300, "Make Render");
        exception = assertThrows(TaskNameAlreadyInUseException.class, () -> {
            minecraft.replaceTask("Purchase Render", "Purchase a render of the game", renderDuration, 300, "Make Render");
        });
        exception = assertThrows(TaskNotFoundException.class, () -> {
            minecraft.replaceTask("Make Mobs", "Adds creepers and skeletons to the game", renderDuration, 300, "Make Render");
        });
        assertNull(minecraft.getTask("Make Mobs"));
        assertNull(minecraft.getTask("Make Render"));
        assertTrue(minecraft.getTasks().contains(minecraft.getTask("Purchase Render")));
        List<String> previousTasksMinecraft = new LinkedList<String>();
        previousTasksMinecraft.add("Purchase Render");
        minecraft.addTask("Make Mobs", "Adds creepers and skeletons to the game", renderDuration, 300, previousTasksMinecraft, manager);
        //minecraft.addTask("Make Mobs", "Adds creepers and skeletons to the game", renderDuration, 300, previousTasksMinecraft, ward);
        assertEquals(2, minecraft.getTasks().size());
        assertEquals(minecraft.getStatus(), "ongoing");
        assertEquals(minecraft.getTask("Make Mobs").getName(), "Make Mobs");
        assertEquals(minecraft.getTask("Purchase Render").getNextTasks().size(), 1 );
        List<Task> next = minecraft.getTask("Purchase Render").getNextTasks();
        for (Task task : next) {
            assertEquals(task.getName(), "Make Mobs");
        }

        assertEquals(0, minecraft.showExecutingTasks().size());
        assertEquals(1, minecraft.showAvailableTasks().size());
        minecraft.getTask("Purchase Render").start(new Time(0, 2), new Time(0, 2), ward);
        assertEquals(1, minecraft.showExecutingTasks().size());
        assertEquals(0, minecraft.showAvailableTasks().size());
        minecraft.getTask("Purchase Render").end(Status.FINISHED, new Time(0, 15), new Time(0, 15), ward);
        assertEquals(0, minecraft.showExecutingTasks().size());
        assertEquals(1, minecraft.showAvailableTasks().size());
        assertEquals(minecraft.getStatus(), "ongoing");
        minecraft.getTask("Make Mobs").start(new Time(0, 2), new Time(0, 2), manager);
        assertEquals(1, minecraft.showExecutingTasks().size());
        assertEquals(0, minecraft.showAvailableTasks().size());
        minecraft.getTask("Make Mobs").end(Status.FINISHED, new Time(0, 15), new Time(0, 15), manager);
        assertEquals(0, minecraft.showExecutingTasks().size());
        assertEquals(0, minecraft.showAvailableTasks().size());
        assertEquals(minecraft.getStatus(), "finished");

        assertEquals(0, car.getTasks().size());
        assertEquals(0, car.showAvailableTasks().size());
        assertEquals(0, house.getTasks().size());
        assertEquals(0, house.showAvailableTasks().size());

        // TODO getNExtStatuses testen

        User mechanic = new User("Mechanic", "carMechanic123", Role.DEVELOPER);
        User engineer = new User("Engineer", "carEngineer123", Role.DEVELOPER);
        LinkedList<String> prevMech = new LinkedList<>();
        LinkedList<String> prevEng = new LinkedList<>();
        car.addTask("Build chasis", "Build the chasis of the car", new Time(500), 20.58, new LinkedList<>(), mechanic);
        prevMech.add("Build chasis");
        car.addTask("Install windows", "Install the windows of the car", new Time(20), 5, prevMech, mechanic);
        car.addTask("Install engine", "Install the engine of the car", new Time(100), 10, new LinkedList<>(), engineer);
        prevEng.add("Install engine");
        car.addTask("install navsat", "Install the navsat of the car", new Time(10), 5, prevEng, engineer);
        prevEng.add("install navsat");
        car.addTask("Brake fluid", "Regulate the brake fluid of the car", new Time(10), 5, prevEng, engineer);
        assertEquals(car.showAvailableTasks().size(), 2);
        car.startTask("Build chasis", new Time(0, 0), new Time(0, 0), mechanic);
        exception = assertThrows(UserNotAllowedToChangeTaskException.class, () -> {
            car.startTask("Build chasis", new Time(0, 0), new Time(0, 0), engineer);
        });
        exception = assertThrows(UserNotAllowedToChangeTaskException.class, () -> {
            car.startTask("Install windows", new Time(0, 0), new Time(0, 0), engineer);
        });
        exception = assertThrows(TaskNotFoundException.class, () -> {
            car.startTask("Rearrange windows", new Time(0, 0), new Time(0, 0), mechanic);
        });
        exception = assertThrows(TaskNotFoundException.class, () -> {
            car.startTask("Honda engine", new Time(0, 0), new Time(0, 0), engineer);
        });
        exception = assertThrows(IncorrectTaskStatusException.class, () -> {
            car.startTask("Build chasis", new Time(0, 0), new Time(0, 0), mechanic);
        });
        exception = assertThrows(IncorrectTaskStatusException.class, () -> {
            car.startTask("Install windows", new Time(0, 0), new Time(0, 0), mechanic);
        });
        assertEquals(car.showExecutingTasks().size(), 1);
        assertEquals(1, car.getNextStatuses("Install engine").size());
        assertEquals(car.getNextStatuses("Install engine").get(0), Status.EXECUTING);
        car.startTask("Install engine", new Time(0, 0), new Time(0, 0), engineer);
        assertEquals(2, car.getNextStatuses("Install engine").size());
        assertEquals(car.getNextStatuses("Install engine").get(0), Status.FINISHED);
        assertEquals(car.getNextStatuses("Install engine").get(1), Status.FAILED);
        assertEquals(car.showExecutingTasks().size(), 2);
        assertEquals(car.showAvailableTasks().size(), 0);
        car.endTask("Build chasis", Status.FINISHED, new Time(1, 0), new Time(2, 0), mechanic);
        assertEquals(car.showExecutingTasks().size(), 1);
        assertEquals(car.showAvailableTasks().size(), 1);
        car.endTask("Install engine", Status.FINISHED, new Time(2, 0), new Time(3, 0), engineer);
        car.startTask("install navsat", new Time(3, 0), new Time(3, 0), engineer);
        assertEquals(car.showExecutingTasks().size(), 1);
        car.endTask("install navsat", Status.FINISHED, new Time(3, 0), new Time(4, 0), engineer);
        assertEquals(car.showExecutingTasks().size(), 0);
        assertEquals(car.showAvailableTasks().size(), 2);
        assertEquals(car.getNextStatuses("Brake fluid").size(), 1);
        car.startTask("Brake fluid", new Time(4, 0), new Time(4, 0), engineer);
        assertEquals(car.getNextStatuses("Brake fluid").size(), 2);
        assertEquals(2, car.getNextStatuses("Brake fluid").size());
        assertEquals(car.getNextStatuses("Brake fluid").get(0), Status.FINISHED);
        assertEquals(car.getNextStatuses("Brake fluid").get(1), Status.FAILED);
        car.endTask("Brake fluid", Status.FINISHED, new Time(4, 0), new Time(5, 0), engineer);
        assertEquals(1, car.getNextStatuses("Install windows").size());
        assertEquals(car.getNextStatuses("Install windows").get(0), Status.EXECUTING);
        car.startTask("Install windows", new Time(5, 0), new Time(5, 0), mechanic);
        assertEquals(2, car.getNextStatuses("Install windows").size());
        assertEquals(car.getNextStatuses("Install windows").get(0), Status.FINISHED);
        assertEquals(car.getNextStatuses("Install windows").get(1), Status.FAILED);
        car.endTask("Install windows", Status.FINISHED, new Time(5, 0), new Time(6, 0), mechanic);
        assertEquals(car.getStatus(), "finished");



    }
}
