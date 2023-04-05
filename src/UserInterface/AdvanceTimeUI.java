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

    public AdvanceTimeUI(AdvanceTimeController controller) {
        this.controller = controller;
    }

    private AdvanceTimeController getController() {
        return controller;
    }

    /**
     * Creates the initial advancetime request, checks if the user is logged in as a project manager or developer
     */
    public void advanceTime() {
        if (getController().advanceTimePreconditions()) {
            try {
                chooseNewTime();
            } catch (IncorrectPermissionException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("You must be logged in with the Project Manager role or a Developer role to call this function");
        }
    }

    /**
     * Advances the system time by the given input
     */
    public void chooseNewTime() throws IncorrectPermissionException {
        Scanner scanner = new Scanner(System.in);

        int systemHour = getController().getSystemHour();
        int systemMinute = getController().getSystemMinute();

        while(true) {
            try{
                System.out.println("Current system time is: " + systemHour + ":" + systemMinute);
                System.out.println("Type BACK to cancel advancing the system time any time");
                System.out.println("Do you want to advance to a specific time? (y/n)"); // TODO: misschien beter verwoorden
                String response = scanner.nextLine();
                if (response.equals("BACK")) {
                    return;
                }
                while (!response.equals("y") && !response.equals("n")) {
                    System.out.println("Do you want to advance to a specific time? (y/n)"); // TODO: misschien beter verwoorden
                    response = scanner.nextLine();
                    if (response.equals("BACK")) {
                        return;
                    }
                }
                if (response.equals("y")) {
                    advanceToTime(scanner);
                } else {
                    advanceDuration(scanner);
                }
                return;
            }
            catch (InvalidTimeException e) {
                System.out.println("ERROR: the chosen time is not valid");
            }
            catch (NewTimeBeforeSystemTimeException e) {
                System.out.println("ERROR: The chosen time is before the system time");
            }
        }
    }

    private void advanceToTime(Scanner scanner) throws IncorrectPermissionException, InvalidTimeException, NewTimeBeforeSystemTimeException {
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
        getController().setNewTime(newHour, newMinute);
        System.out.println("Time successfully updated");
    }

    private void advanceDuration(Scanner scanner) throws IncorrectPermissionException, InvalidTimeException, NewTimeBeforeSystemTimeException {
        System.out.println("Give amount of minutes to advance the clock with:");

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
                System.out.println("Given system minute is not an integer, please try again");
                newMinuteString = scanner.nextLine();
            }
        }
        getController().setNewTime(newMinute);
        System.out.println("Time successfully updated");
    }
}
