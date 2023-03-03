import java.util.Scanner;

public class UserInterface {
    private SessionController sessionController;

    public UserInterface() {
        this.sessionController = new SessionController(this);
    }

    public void startInterface() {
        login();
        // hier komen misschien nog wat dingen? Anders doet deze methode echt niks
    }

    private void login() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter username:");
        String username = scanner.nextLine();

        System.out.println("Enter password:");
        String password = scanner.nextLine();

        sessionController.login(username, password);
    }


    public void loginerror() {
        System.out.println("Wrong password/username combination, please try again!");
        // login(); (kunnen we doen, als we meteen weer de prompt willen geven, maar voor de test-cases werkt da nu ni haha)
    }

    public void welcome(String role) {
        System.out.println("Welcome! Your assigned role is " + role);
    }
}
