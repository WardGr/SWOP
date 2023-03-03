import org.junit.Test;

public class testLogin {
    @Test
    public void test() {
        UserInterface userInterface = new UserInterface();
        SessionController sessionController = new SessionController(userInterface);

        System.out.println("Trying username Ward with password 123");
        sessionController.login("Ward", "123");

        System.out.println("Trying username Sam with password vijf5");
        sessionController.login("Sam", "vijf5");

        System.out.println("Trying username hierbas with password 123");
        sessionController.login("hierbas", "123");
    }
}
