import java.util.Scanner;

public class SessionUI { // Responsibility: Handle I/O from session-centric use-cases
    public SessionController sessionController;

    public SessionUI(Session session, UserManager userManager) {
        this.sessionController = new SessionController(session, this, userManager);
    }

    public void loginRequest(Scanner scanner){
        sessionController.loginRequest(scanner);
    }

    public void loginPrompt(Scanner scanner) {
        System.out.println("Type 'BACK' to cancel login");
        System.out.println("Enter username:");
        String username = scanner.nextLine();
        if (username.equals("BACK")){
            return;
        }
        System.out.println("Enter password:");
        String password = scanner.nextLine();
        if (password.equals("BACK")){
            return;
        }

        sessionController.login(username, password, scanner);
    }

    public void logout() {
        sessionController.logout();
    }

    public void printWelcome(String name, String role) {
        System.out.println("Welcome " + name + "! Your assigned role is " + role);
    }

    public void handleLoginError(Scanner scanner) {
        System.out.println("Incorrect username/password combination, please try again");
        loginPrompt(scanner);
    }

    public void printAlreadyLoggedInError() {
        System.out.println("You are already logged in!");
    }

    public void printLogout() {
        System.out.println("Logged out.");
    }

    public void printLogoutError() {
        System.out.println("Already logged out.");
    }
}
