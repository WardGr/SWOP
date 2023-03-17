// TODO: Do we have to comment every class with @ tags as well? And what about postconditions and invariants?

import Application.Session;
import UserInterface.UserInterface;
import Domain.InvalidTimeException;
import Domain.TaskManSystem;
import Domain.Time;
import Domain.UserManager;

/**
 * Creates the initial objects and starts the UI
 */
public class Main {

  public static void main(String[] args) {
    Session newSession = new Session();
    try {
      TaskManSystem taskManSystem = new TaskManSystem(new Time(0, 0)); // exception thrown by the new Domain.Time
      UserManager userManager = new UserManager();

      UserInterface UI = new UserInterface(
              newSession,
              taskManSystem,
              userManager
      );
      UI.startSystem();
    }
    catch (InvalidTimeException e) {
      System.out.println("Somehow the initial start time is invalid, this really should not happen.");
    }
  }
}
