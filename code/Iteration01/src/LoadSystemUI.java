import java.util.Scanner;

/**
 * Handles user input for the loadsystem use-case, requests necessary domain-level information from the LoadSystemController
 */
public class LoadSystemUI {
    private final LoadSystemController loadSystemController;


    public LoadSystemUI(UserManager userManager,TaskManSystem taskManSystem, Session session){
        loadSystemController = new LoadSystemController(userManager, taskManSystem, session);
    }

    private LoadSystemController getController() {
        return loadSystemController;
    }

    public void loadSystem(){
        if (loadSystemController.loadSystemPreconditions()){
            try{
                loadSystemForm();
            } catch (IncorrectPermissionException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");
        }
    }
    public void loadSystemForm() throws IncorrectPermissionException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Type BACK to cancel system load at any time");
        System.out.println("*********** SYSTEM LOAD FORM ***********");
        System.out.println("please enter the path of the load file: ");
        String path = scanner.nextLine();
        if (path.equals("BACK")) {
            System.out.println("Cancelled system load");
            return;
        }
        getController().LoadSystem(path);
        System.out.println("system succesfully loaded");
    }
}
