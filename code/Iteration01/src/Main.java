// TODO: Do we have to comment every class with @ tags as well? And what about postconditions and invariants?

public class Main {
    public static void main(String[] args) throws NotValidTimeException {
        Session newSession = new Session();
        TaskManSystem taskManSystem = new TaskManSystem(new Time(0,0)); // exception thrown by the new Time
        UserManager userManager = new UserManager();

        UserInterface UI = new UserInterface(newSession, taskManSystem, userManager);
        UI.startSystem();
    }
}
