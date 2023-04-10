package UserInterface;

import Application.SessionController;
import Domain.LoginException;
import Domain.Role;

import java.util.Scanner;
import java.util.Set;

/**
 * Handles user input for the login and logout use-cases, requests necessary domain-level information from the Application.SessionController
 */
public class SessionUI {

    private final SessionController controller;

    public SessionUI(SessionController controller) {
        this.controller = controller;
    }

    /**
     * Initial login request: shows the login prompt if the user is not already logged in
     */
    public void loginRequest() {
        if (getController().loginPrecondition()) {
            loginPrompt();
        } else {
            System.out.println("You are already logged in!");
        }
    }

    private SessionController getController() {
        return controller;
    }

    /**
     * Requests user credentials via CLI and attempts to log in with these, user may type BACK at any time to return,
     * if credentials match, prints welcome message, else prints warning and requests credentials again
     *
     * @pre User is not logged in
     * @post if user typed BACK, then user is not logged in, otherwise user is logged in
     */
    private void loginPrompt() {
        Scanner scanner = new Scanner(System.in);
        while (true) {

            System.out.println("Type 'BACK' to cancel login");
            System.out.println("Enter username:");
            String username = scanner.nextLine();
            if (username.equals("BACK")) {
                return;
            }
            System.out.println("Enter password:");
            String password = scanner.nextLine();
            if (password.equals("BACK")) {
                return;
            }
            try {
                Set<Role> roles = getController().login(username, password);
                StringBuilder showRoles = new StringBuilder();
                for (Role role : roles) {
                    if (showRoles.length() > 0) {
                        showRoles.append(", ");
                    }
                    showRoles.append(role.toString());
                }
                System.out.println("Welcome " + username + "! Your assigned roles are: " + showRoles);
                return;
            } catch (LoginException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Attempts to log out
     *
     * @post User is logged out
     */
    public void logout() {
        if (getController().logout()) {
            System.out.println("Logged out.");
        } else {
            System.out.println("Already logged out.");
        }
    }
}
