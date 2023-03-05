import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestLogout {
    @Test
    public void test() {
        UserInterface userInterface = new UserInterface();
        SessionController sessionController = new SessionController(userInterface);

        sessionController.login("Ward", "123");
        assertTrue(sessionController.isLoggedIn());
        sessionController.logout();
        assertEquals(sessionController.isLoggedIn(), false);

        sessionController.login("Sam", "vijf5");
        assertEquals(sessionController.isLoggedIn(), true);
        assertTrue(sessionController.logout());
        assertEquals(sessionController.isLoggedIn(), false);
        assertEquals(sessionController.logout(), false);
    }
}
