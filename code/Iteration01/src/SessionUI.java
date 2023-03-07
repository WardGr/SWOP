import java.util.Scanner;

public class SessionUI { // Verantwoordelijkheid: Handle I/O from session-centric use-cases
    public SessionController sessionController;

    public SessionUI() {
        this.sessionController = new SessionController();
    }

    public void login() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter username:");
        String username = scanner.nextLine();
        System.out.println("Enter password:");
        String password = scanner.nextLine();

        sessionController.login(username, password);
    }

    public void logout() {
        sessionController.logout();
    }

    public void printWelcome(String name, String role) {
        System.out.println("Welcome! Your assigned role is " + role);
    }

    public void handleLoginError() {
        System.out.println("Incorrent username/password combination, please try again");
        login();
    }

    public void printAlreadyLoggedInError() {
        System.out.println("You are already logged in!");
    }

    public void printLogout(String name) {
        System.out.println("User" + name + "logged out.");
    }

    public void printLogoutError() {

    }
}
