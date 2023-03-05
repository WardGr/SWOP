import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestLogin {
    @Test
    public void test() {
        UserInterface userInterface = new UserInterface();
        SessionController sessionController = new SessionController(userInterface);

        assertEquals(sessionController.isLoggedIn(), false);

        System.out.println("Trying username Ward with password 123");
        sessionController.login("Ward", "123");

        assertEquals(sessionController.isLoggedIn(), true);

        System.out.println("Trying username Sam with password vijf5");
        sessionController.login("Sam", "vijf5");

        System.out.println("Trying username hierbas with password 123");
        sessionController.login("hierbas", "123");
    }
}
