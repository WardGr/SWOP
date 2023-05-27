package Application.TaskControllers;

public class NoCurrentTaskException extends Exception {
    public NoCurrentTaskException(String s) {
        super(s);
    }
}
