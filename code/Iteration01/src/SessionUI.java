import java.util.Scanner;

public class SessionUI { // Responsibility: Handle I/O from session-centric use-cases
    public SessionController sessionController;

    public SessionUI(Session session, UserManager userManager) {
        this.sessionController = new SessionController(session, this, userManager);
    }

    public void loginRequest(){
        sessionController.loginRequest();
    }

    public void loginPrompt() {
        Scanner scanner = new Scanner(System.in);

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

        sessionController.login(username, password);
    }

    public void logout() {
        sessionController.logout();
    }

    public void printWelcome(String name, String role) {
        System.out.println("Welcome " + name + "! Your assigned role is " + role);
    }

    public void handleLoginError() {
        System.out.println("Incorrect username/password combination, please try again");
        loginPrompt();
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
