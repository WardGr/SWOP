import java.util.Scanner;

public class LoadSystemUI {
    private LoadSystemController loadSystemController;

    public LoadSystemUI(UserManager userManager,TaskManSystem taskManSystem, Session session){
        loadSystemController = new LoadSystemController(userManager, taskManSystem, session, this);
    }

    public void loadSystem(){
        loadSystemController.loadSystemForm();
    }
    public void loadSystemForm(){
        Scanner scanner = new Scanner(System.in);

        System.out.println("Type BACK to cancel system load at any time");
        System.out.println("*********** SYSTEM LOAD FORM ***********");
        System.out.println("please enter the path of the load file: ");
        String path = scanner.nextLine();
        if (path.equals("BACK")) {
            System.out.println("Cancelled system load");
            return;
        }
        loadSystemController.LoadSystem(path);
        System.out.println("system succesfully loaded");
    }
    public void printAccessError(Role role) {
        System.out.println("You must be logged in with the " + role.toString() + " role to call this function");
    }
}
