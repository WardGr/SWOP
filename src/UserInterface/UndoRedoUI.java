package UserInterface;

import Application.UndoRedoController;

import java.util.Scanner;

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

    private UndoRedoController getController() {
        return controller;
    }

    public void undo() {
        Scanner scanner = new Scanner(System.in);
        if (!getController().undoConditions()) {
            System.out.println("You cannot undo any commands at this time.");
            return;
        }
        undoForm(scanner);
    }

    private void undoForm(Scanner scanner) {
        System.out.println("******** UNDO *******");
        System.out.println("Type 'BACK' to cancel undo");
        System.out.println("Undoable commands:");
        for (String command : getController().possibleUndoes()) {
            System.out.println(command);
        }
        System.out.println("How many commands do you want to undo?");
        String command = scanner.nextLine();
        if (command.equals("BACK")) {
            System.out.println("Cancelled undo");
            return;
        }
        try {
            for (int i = 0; i < Integer.parseInt(command); i++)
                getController().undo();
            System.out.println("Succesfully undone.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void redo(){
        Scanner scanner = new Scanner(System.in);
        if (!getController().redoConditions()) {
            System.out.println("You cannot redo any commands at this time.");
            return;
        }
        redoForm(scanner);
    }

    private void redoForm(Scanner scanner) {
        System.out.println("******** REDO *******");
        System.out.println("Type 'BACK' to cancel redo");
        System.out.println("Redoable commands:");
        for (String command : getController().possibleRedoes()) {
            System.out.println(command);
        }
        System.out.println("How many commands do you want to redo?");
        String command = scanner.nextLine();
        if (command.equals("BACK")) {
            System.out.println("Cancelled redo");
            return;
        }
        try {
            for (int i = 0; i < Integer.parseInt(command); i++)
                getController().redo();
            System.out.println("Succesfully redone.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
