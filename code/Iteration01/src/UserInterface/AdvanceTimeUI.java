package UserInterface;

import Application.AdvanceTimeController;
import Application.IncorrectPermissionException;
import Application.Session;
import Domain.InvalidTimeException;
import Domain.NewTimeBeforeSystemTimeException;
import Domain.Role;
import Domain.TaskManSystem;

import java.util.Scanner;

/**
 * Handles user input for the advancetime use-case, requests necessary domain-level information from the Application.AdvanceTimeController
 */
public class AdvanceTimeUI {

  private final AdvanceTimeController controller;

  public AdvanceTimeUI(Session session, TaskManSystem taskManSystem) {
    controller = new AdvanceTimeController(session, taskManSystem);
  }

  private AdvanceTimeController getController() {
    return controller;
  }

  /**
   * Creates the initial advancetime request, checks if the user is logged in as a project manager
   */
  public void advanceTime() {
    if (getController().advanceTimePreconditions()) {
      try {
        chooseNewTime();
      } catch (IncorrectPermissionException e) {
        System.out.println(e.getMessage());
      }
    } else {
      System.out.println("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");
    }
  }

  /**
   * Advances the system time by the given input and updates all necessary domain-objects
   */
  public void chooseNewTime() throws IncorrectPermissionException {
    Scanner scanner = new Scanner(System.in);

    while(true) {

      int systemHour = getController().getSystemHour();
      int systemMinute = getController().getSystemMinute();

      System.out.println(
              "Current system time is: " + systemHour + ":" + systemMinute
      );

      System.out.println(
              "Type BACK to cancel advancing the system time any time"
      );
      System.out.println("Give new system hour:");
      String newHourString = scanner.nextLine();

      int newHour;
      while (true) {
        try {
          if (newHourString.equals("BACK")) {
            System.out.println("Cancelled advancing time");
            return;
          }
          newHour = Integer.parseInt(newHourString);
          break;
        } catch (NumberFormatException e) {
          System.out.println(
                  "Given system hour is not an integer, please try again"
          );
          newHourString = scanner.nextLine();
        }
      }

      System.out.println("Give new system minute:");
      String newMinuteString = scanner.nextLine();

      int newMinute;
      while (true) {
        try {
          if (newMinuteString.equals("BACK")) {
            System.out.println("Cancelled advancing time");
            return;
          }
          newMinute = Integer.parseInt(newMinuteString);
          break;
        } catch (NumberFormatException e) {
          System.out.println(
                  "Given system minute is not an integer, please try again"
          );
          newMinuteString = scanner.nextLine();
        }
      }

      try {
        getController().setNewTime(newHour, newMinute);
        System.out.println("Time successfully updated");
        return;
      } catch (InvalidTimeException e) {
        System.out.println("ERROR: the chosen time is not valid");
      } catch (NewTimeBeforeSystemTimeException e) {
        System.out.println("ERROR: The chosen time is before the system time");
      }
    }
  }
}
