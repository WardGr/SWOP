import java.util.Scanner;

public class SessionUI { // Responsibility: Handle I/O from session-centric use-cases

  public SessionController sessionController;

  public SessionUI(Session session, UserManager userManager) {
    this.sessionController = new SessionController(session, this, userManager);
  }

  public void loginRequest(Scanner scanner) {
    if (sessionController.loginRequest()) {
      loginPrompt(scanner);
    } else {

    }
  }

  public void loginPrompt(Scanner scanner) {
    while (true) {
      System.out.println("Type 'BACK' to cancel login");
      System.out.println("Enter username:");
      String username = scanner.nextLine();
      if (username.equals("BACK")) {
        break;
      }
      System.out.println("Enter password:");
      String password = scanner.nextLine();
      if (password.equals("BACK")) {
        break;
      }
      try {
        sessionController.login(username, password, scanner);
        break;
      } catch (Exception e){
        handleLoginError(scanner);
      }
    }


  }

  public void logout() {
    sessionController.logout();
  }

  public void printWelcome(String name, String role) {
    System.out.println("Welcome " + name + "! Your assigned role is " + role);
  }

  public void handleLoginError(Scanner scanner) {
    System.out.println(
      "Incorrect username/password combination, please try again"
    );
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
