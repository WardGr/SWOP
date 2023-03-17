package UserInterface;

import Application.Session;
import Application.SessionController;
import Domain.LoginException;
import Domain.Role;
import Domain.UserManager;

import java.util.Scanner;

/**
 * Handles user input for the login and logout use-cases, requests necessary domain-level information from the Application.SessionController
 */
public class SessionUI {

  private SessionController controller;

  public SessionUI(Session session, UserManager userManager) {
    this.controller = new SessionController(session, userManager);
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
   * @pre Domain.User is not logged in
   * @post if user typed BACK, then user is not logged in, otherwise user is logged in
   */
  private void loginPrompt() {
    while (true) {
      Scanner scanner = new Scanner(System.in);

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
        Role newRole = getController().login(username, password);
        System.out.println("Welcome " + username + "! Your assigned role is " + newRole.toString());
        return;
      } catch (LoginException e) {
        System.out.println(e.getMessage());
      }
    }
  }

  /**
   * Attempts to log out
   *
   * @post Domain.User is logged out
   */
  public void logout() {
    if (getController().logout()) {
      System.out.println("Logged out.");
    } else {
      System.out.println("Already logged out.");
    }
  }
}
