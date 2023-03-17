import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

public class ShowProjectsUITest {
    @Test
    public void testShowProjectsUI() throws ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException {
        Session session = new Session();
        TaskManSystem tsm = new TaskManSystem(new Time(0));
        User manager = new User("WardGr", "minecraft123", Role.PROJECTMANAGER);
        User dev = new User("OlavBl", "753", Role.DEVELOPER);


        ShowProjectsUI ui = new ShowProjectsUI(session, tsm);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        session.login(dev);
        out.reset();
        ui.showProjects();
        assertEquals("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function\n".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        session.logout();
        session.login(manager);
        out.reset();
        System.setIn(new ByteArrayInputStream("BACK".getBytes()));
        ui.showProjects();
        assertEquals("""
                Type "BACK" to cancel
                ********* PROJECTS *********
                Type the name of a project to see more details:
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),
                out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
        tsm.createProject("Project1", "Description1", new Time(100));
        tsm.createProject("Project2", "Description2", new Time(100));
        tsm.addTaskToProject("Project1", "Task1", "Description1", new Time(100), 5, new LinkedList<>(), dev);
        LinkedList task = new LinkedList<>();
        task.add("Task1");
        tsm.addTaskToProject("Project1", "otherTask", "This is a followup task", new Time(100), 5, task, dev);
        tsm.addTaskToProject("Project2", "Task2", "Description2", new Time(100),5, new LinkedList<>(), dev);

        out.reset();
        System.setIn(new ByteArrayInputStream("BACK".getBytes()));
        ui.showProjects();
        assertEquals("""
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project2, status: ongoing
                Project1, status: ongoing
                Type the name of a project to see more details:
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        System.setIn(new ByteArrayInputStream("Project2\nBACK".getBytes()));
        ui.showProjects();
        assertEquals("""
                Type "BACK" to cancel
                ********* PROJECTS *********
                Project2, status: ongoing
                Project1, status: ongoing
                Type the name of a project to see more details:
                Project Name:  Project2
                Description:   Description2
                Creation Time: 0 hours, 0 minutes
                Due Time:      1 hours, 40 minutes
                Status:        ongoing
                Type the name of a task to see more details, or type \"BACK\" to choose another project:
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }
}
