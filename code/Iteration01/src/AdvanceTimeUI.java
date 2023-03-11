import java.util.Scanner;

public class AdvanceTimeUI {
    private AdvanceTimeController controller;

    public AdvanceTimeUI (Session session, TaskManSystem taskManSystem, Time systemTime) {
        controller = new AdvanceTimeController(session, taskManSystem, this, systemTime);
    }

    public void advanceTime(){ controller.advanceTime(); }
    public void chooseNewTime(){
        Scanner scanner = new Scanner(System.in);

        System.out.println("Type BACK to cancel modifying the system time any time");
        System.out.println("Give new system time:");
        String systemTimeString = scanner.nextLine();
        if (systemTimeString.equals("BACK")) {
            return;
        }
        int systemTime;
        while (true) {
            try {
                systemTime = Integer.parseInt(systemTimeString);
                controller.setNewTime(systemTime);
                return;
            } catch (NumberFormatException e) {
                System.out.println("Given system time is not an integer, please try again");
                systemTimeString = scanner.nextLine();
                if (systemTimeString.equals("BACK")) {
                    System.out.println("Cancelled task creation");
                    return;
                }
            }
        }
    }

    public void printAccessError(Role role) {
        System.out.println("You must be logged in with the " + role.toString() + " role to call this function");
    }

    public void printNewBeforeSystemTimeError(){
        System.out.println("ERROR: The chosen time is before the system time");
        chooseNewTime();
    }
}
