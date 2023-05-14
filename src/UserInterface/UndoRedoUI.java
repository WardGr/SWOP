package UserInterface;

import Application.SessionController;
import Application.UndoRedoController;

public class UndoRedoUI {
    private final UndoRedoController controller;

    /**
     * Creates a new UI object
     *
     * @param controller Controller with which this UI should communicate to access the domain
     */
    public UndoRedoUI(UndoRedoController controller) {
        this.controller = controller;
    }

    /**
     * Initial login request: shows the login prompt if the user is not already logged in
     */
    public void undo() {
        //if (getController().loginPrecondition()) {
        //    loginPrompt();
        //} else {
        //    System.out.println("You are already logged in!");
        //}
    }

    public void redo(){}
}
