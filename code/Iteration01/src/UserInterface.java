import java.util.Scanner;

public class UserInterface {
    private SessionController sessionController;

    public UserInterface() {
        this.sessionController = new SessionController(this);
    }

    public void startSystem() {
        while(!sessionController.isLoggedIn()) {
            login();
        }
    }

    /**
     * Prints the login prompt, receives the username and password from the user via the command line, and sends it over
     * to the SessionController.
     */
    private void login() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter username:");
        String username = scanner.nextLine();

        System.out.println("Enter password:");
        String password = scanner.nextLine();

        sessionController.login(username, password);
    }

    public void printLoginError() {
        System.out.println("Wrong password/username combination, please try again!");
    }

    public void printWelcome(String role) {
        System.out.println("Welcome! Your assigned role is " + role);
    }
}
