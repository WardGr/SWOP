package UserInterface;

import Application.AdvanceTimeController;
import Application.IncorrectPermissionException;
import Domain.InvalidTimeException;
import Domain.NewTimeBeforeSystemTimeException;
import Domain.Role;
import Domain.Time;

import java.util.Scanner;

/**
 * Handles user input for the advancetime use-case, requests necessary domain-level information from the Application.AdvanceTimeController
 */
public class AdvanceTimeUI {

    private final AdvanceTimeController controller;

    /**
     * Creates a new UI object
     *
     * @param controller Controller with which this UI should communicate to access the domain
     */
    public AdvanceTimeUI(AdvanceTimeController controller) {
        this.controller = controller;
    }

    /**
     * @return This UI's controller
     */
    private AdvanceTimeController getController() {
        return controller;
    }

    /**
     * Creates the initial advancetime request, checks if the user is logged in as a project manager or with a developer role
     */
    public void advanceTime() {
        if (getController().advanceTimePreconditions()) {
            try {
                chooseAdvanceMethod();
            } catch (IncorrectPermissionException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("You must be logged in with the " + Role.PROJECTMANAGER + " role or a Developer role to call this function");
        }
    }

    /**
     * Requests the user to choose how to advance the time, giving two options:
     *      1) Advance time by a certain amount of minutes
     *      2) Advance time to a certain timestamp
     * And calls the respective functions to facilitate this request
     */
    public void chooseAdvanceMethod() throws IncorrectPermissionException {
        Scanner scanner = new Scanner(System.in);

        Time systemTime = getController().getSystemTime();

        while (true) {
            try {
                System.out.println("Current system time is: " + systemTime.toString());
                System.out.println("Type BACK to cancel advancing the system time any time");
                System.out.println("Do you want to advance the clock with a certain amount of minutes or choose a new timestamp");
                System.out.println("advance/new");
                String response = scanner.nextLine();
                if (response.equals("BACK")) {
                    return;
                }
                while (!response.equals("advance") && !response.equals("new")) {
                    System.out.println("Do you want to advance the time with a certain amount of minutes or choose a new timestamp");
                    System.out.println("advance/new");
                    response = scanner.nextLine();
                    if (response.equals("BACK")) {
                        return;
                    }
                }
                if (response.equals("advance")) {
                    advanceDuration(scanner);
                } else {
                    newTime(scanner);
                }
                return;
            } catch (InvalidTimeException e) {
                System.out.println("ERROR: the chosen time is not valid");
            } catch (NewTimeBeforeSystemTimeException e) {
                System.out.println("ERROR: The chosen time is before the system time");
            }
        }
    }

    /**
     * Advances the time by a certain amount of minutes, given by the user
     *
     * @param scanner Scanner object to retrieve user input
     * @throws IncorrectPermissionException      If the currently logged-in user does not have the project manager role or any developer role
     * @throws NewTimeBeforeSystemTimeException  If the given minutes is negative
     */
    private void advanceDuration(Scanner scanner) throws IncorrectPermissionException, InvalidTimeException, NewTimeBeforeSystemTimeException {
        System.out.println("Give amount of minutes to advance the clock with:");

        String amountMinutesString = scanner.nextLine();
        int amountMinutes;
        while (true) {
            try {
                if (amountMinutesString.equals("BACK")) {
                    System.out.println("Cancelled advancing time");
                    return;
                }
                amountMinutes = Integer.parseInt(amountMinutesString);
                break;
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Given system minute is not an integer, please try again");
                amountMinutesString = scanner.nextLine();
            }
        }
        try {
            getController().advanceTime(amountMinutes);
        }
        catch (InvalidTimeException e) {
            throw new RuntimeException("Time was somehow not converted properly, this is an internal bug");
        }
        System.out.println("Time successfully updated");
    }

    /**
     * Advances the time to a new timestamp
     *
     * @param scanner Scanner object to retrieve user input
     * @throws IncorrectPermissionException     if the current user is not logged in as project manager and does not have a developer role
     * @throws InvalidTimeException             if the given minute is > 59 or < 0
     * @throws NewTimeBeforeSystemTimeException if the given time is before the current system time
     */
    private void newTime(Scanner scanner) throws IncorrectPermissionException, InvalidTimeException, NewTimeBeforeSystemTimeException {
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
        getController().setNewTime(new Time(newHour, newMinute));
        System.out.println("Time successfully updated");
    }
}
