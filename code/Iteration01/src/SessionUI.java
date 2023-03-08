import java.util.Scanner;

public class SessionUI { // Verantwoordelijkheid: Handle I/O from session-centric use-cases
    public SessionController sessionController;

    public SessionUI(Session session) {
        this.sessionController = new SessionController(session, this);
    }

    public void loginPrompt() {
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
        loginPrompt();
    }

    public void printAlreadyLoggedInError() {
        System.out.println("You are already logged in!");
    }

    public void printLogout() {
        System.out.println("Logged out.");
    }

    public void printLogoutError() {

    }
}
