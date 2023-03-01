import java.util.Scanner;

public class UserInterface {
    private Session userSession = null;


    public void startInterface() {
        this.login();
    }


    private void login() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter username:");
        String username = scanner.nextLine();

        System.out.println("Enter password:");
        String password = scanner.nextLine();

        this.userSession = SessionController.login(username, password);
    }
}
