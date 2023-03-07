import org.junit.Test;
import static org.junit.Assert.*;
public class TestCreateTask {
    @Test
    public void test() {
        UserInterface userInterface = new UserInterface();
        Controller sessionController = new Controller(userInterface);

        // Login with valid project manager credentials
        sessionController.login("Ward", "123");
        assertTrue(sessionController.isLoggedIn());

        // Create a new task
        sessionController.createTask("Project x", "Task 3", "Description 3", "3", "3");
        System.out.println(sessionController.getProjectDetails("Project x"));
        assertEquals(sessionController.getTaskDetails("Project x", "Task 3"), """
                Task Name:          Task 3
                Description:        Description 3
                Estimated Duration: 3
                Accepted Deviation: 3
                """);

        assertEquals(sessionController.getProjectDetails("Project x"), """
                Project Name:  Project x
                Description:   Cool project
                Creation Time: 0
                Due time:      1000

                Tasks:
                1.Task 1
                2.Task 2
                3.Task 3
                """);
    }
}
