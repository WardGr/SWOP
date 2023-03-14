import java.util.Scanner;

public class AdvanceTimeUI {

  private final AdvanceTimeController controller;

  public AdvanceTimeUI(Session session, TaskManSystem taskManSystem) {
    controller = new AdvanceTimeController(session, taskManSystem);
  }

  public void advanceTime() {
    if (controller.advanceTimePreconditions()) {
      chooseNewTime();
    } else {
      permissionError();
    }
  }

  public void chooseNewTime() {
    Scanner scanner = new Scanner(System.in);

    int systemHour = controller.getSystemHour();
    int systemMinute = controller.getSystemMinute();

    while(true) {

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
        controller.setNewTime(newHour, newMinute);
        return;
      } catch (IncorrectPermissionException e) {
        permissionError();
      } catch (NotValidTimeException e) {
        notValidTimeError();
      } catch (NewTimeBeforeSystemTimeException e) {
        newBeforeSystemTimeError();
      }
    }
  }

  private void permissionError() {
    System.out.println(
      "You must be logged in with the " +
      Role.PROJECTMANAGER +
      " role to call this function"
    );
  }

  private void notValidTimeError() {
    System.out.println("ERROR: the chosen time is not valid");
  }

  private void newBeforeSystemTimeError() {
    System.out.println("ERROR: The chosen time is before the system time");
  }
}
