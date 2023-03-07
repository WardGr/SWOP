import org.junit.Test;

import static org.junit.Assert.*;

public class TestLogout {
    @Test
    public void test() {
        UserInterface userInterface = new UserInterface();
        Controller sessionController = new Controller(userInterface);

        sessionController.login("Ward", "123");
        assertTrue(sessionController.isLoggedIn());
        sessionController.logout();
        assertFalse(sessionController.isLoggedIn());

        sessionController.login("Sam", "vijf5");
        assertTrue(sessionController.isLoggedIn());
        assertTrue(sessionController.logout());
        assertFalse(sessionController.isLoggedIn());
        assertFalse(sessionController.logout());
    }
}
