import java.util.Scanner;

public class UserInterface {
    private final SessionController sessionController;

    public UserInterface() {
        this.sessionController = new SessionController(this);
    }

    /**
     * Starts the system by sequentially handling commands input by the user
     */
    public void startSystem() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("At your order! Enter 'help' for a list of commands.");

        String nextCommand = scanner.nextLine();
        while(!nextCommand.equals("shutdown")) {
            handleCommand(nextCommand);
            nextCommand = scanner.nextLine();
        }
    }

    /**
     * Calls the appropriate function depending on the user input command
     * @param command String representation of the command given by the user via CLI
     */
    private void handleCommand(String command) {
        switch (command) {
            case "login" -> login();
            case "logout" -> logout();
            case "help" -> printHelp();
            default -> System.out.println("Unknown command, please try again!");
        }
    }

    /**
     * Prints all available commands
     */
    public void printHelp() {
        System.out.println("Available commands:");
        System.out.println("help: Prints this message");
        System.out.println("logout: Logs you out");
        System.out.println("shutdown: Exits the system");
        System.out.println("login: Logs you in");
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

    /**
     * Removes the current session
     */
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

}
