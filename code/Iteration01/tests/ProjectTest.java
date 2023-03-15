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
        minecraft.addAlternativeTask("Purchase Render", "Purchase a render of the game", renderDuration, 300, "Make Render");
        exception = assertThrows(TaskNameAlreadyInUseException.class, () -> {
            minecraft.addAlternativeTask("Purchase Render", "Purchase a render of the game", renderDuration, 300, "Make Render");
        });
        exception = assertThrows(TaskNotFoundException.class, () -> {
            minecraft.addAlternativeTask("Make Mobs", "Adds creepers and skeletons to the game", renderDuration, 300, "Add Mobs");
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
        System.out.println(minecraft.getTask("Purchase Render").getNextTasks());
/*
        // start puchrase render en make mobs
        minecraft.getTask("Purchase Render").start(new Time(0, 2), new Time(0, 2), ward);
        minecraft.getTask("Make Mobs").start(new Time(0, 2), new Time(0, 2), manager);

        // beeindig ze
        minecraft.getTask("Purchase Render").end(Status.FINISHED, new Time(0, 15), new Time(0, 15), ward);
        assertEquals(minecraft.getStatus(), "ongoing");
        minecraft.getTask("Make Mobs").end(Status.FINISHED, new Time(0, 15), new Time(0, 15), manager);
        assertEquals(minecraft.getStatus(), "finished");

        assertEquals(0, car.getTasks().size());
        assertEquals(0, house.getTasks().size()); */


    }
}
