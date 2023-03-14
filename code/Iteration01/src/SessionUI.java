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
      alreadyLoggedInError();
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
        welcomeMessage(username, newRole);
        return;
      } catch (IncorrectPermissionException e){
        alreadyLoggedInError();
      } catch (UserNotFoundException e) {
        handleLoginError();
      }
    }
  }

  public void logout() {
    if (controller.logout()) {
      logoutMessage();
    } else {
      alreadyLoggedOutError();
    }
  }

  private void welcomeMessage(String name, Role role) {
    System.out.println("Welcome " + name + "! Your assigned role is " + role.toString());
  }

  private void handleLoginError() {
    System.out.println(
      "Incorrect username/password combination, please try again"
    );
  }

  private void alreadyLoggedInError() {
    System.out.println("You are already logged in!");
  }

  private void logoutMessage() {
    System.out.println("Logged out.");
  }

  private void alreadyLoggedOutError() {
    System.out.println("Already logged out.");
  }
}
