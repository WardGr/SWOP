import java.util.Scanner;

public class UserInterface {
    private final SessionController sessionController;

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

    private void logout() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Are you sure you want to log out? (y/n)");
        String answer = scanner.nextLine();

        if (answer.equals("y")) {
            if (sessionController.logout()) {
                System.out.println("Logged out!");
            } else {
                System.out.println("Error: you are not logged in!");
            }
        }
    }

    public void printLoginError() {
        System.out.println("Wrong password/username combination, please try again!");
    }

    public void printWelcome(String role) {
        System.out.println("Welcome! Your assigned role is " + role);
    }

    public void idle() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("At your order! Enter 'Help' for a list of commands.");
        String answer = scanner.nextLine();
        handleCmd(answer, scanner);
    }

    public void printHelp() {
        System.out.println("Available commands:");
        System.out.println("Help: Prints this message");
        System.out.println("Logout: Logs you out");
        System.out.println("Exit: Exits the system");
        System.out.println("Login: Logs you in");
    }

    public void handleCmd(String cmd, Scanner scanner) {
        switch (cmd) {
            case "Help" -> printHelp();
            case "Logout" -> logout();
            case "Login" -> login();
            default -> System.out.println("Unknown command, please try again!");
        }
        cmd = scanner.nextLine();
        handleCmd(cmd, scanner);
    }
}
