import java.util.Scanner;

public class SessionUI { // Responsibility: Handle I/O from session-centric use-cases

  public SessionController controller;

  public SessionUI(Session session, UserManager userManager) {
    this.controller = new SessionController(session, userManager);
  }

  public void loginRequest(Scanner scanner) {
    if (controller.loginPrecondition()) {
      loginPrompt(scanner);
    } else {
      System.out.println("You are already logged in!");
    }
  }

  public void loginPrompt(Scanner scanner) {
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
        Role newRole = controller.login(username, password);
        System.out.println("Welcome " + username + "! Your assigned role is " + newRole.toString());
        return;
      } catch (LoginException e) {
        System.out.println(e.getMessage());
      }
    }
  }

  public void logout() {
    if (controller.logout()) {
      System.out.println("Logged out.");
    } else {
      System.out.println("Already logged out.");
    }
  }
}
