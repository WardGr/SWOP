// TODO: Do we have to comment every class with @ tags as well? And what about postconditions and invariants?

public class Main {
    public static void main(String[] args) throws DueBeforeSystemTimeException {
        Session newSession = new Session();
        TaskManSystem taskManSystem = new TaskManSystem();

        UserInterface UI = new UserInterface(newSession, taskManSystem);
        UI.startSystem();
    }
}
