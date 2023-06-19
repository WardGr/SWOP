package UserInterface.SystemUIs;

import Application.Controllers.SystemControllers.UndoRedoController;
import Application.Command.CommandData;
import Application.Command.UndoNotPossibleException;
import Application.Command.EmptyCommandStackException;
import Domain.User.IncorrectUserException;
import Domain.User.Role;
import Domain.DataClasses.Tuple;

import java.util.List;
import java.util.Scanner;

/**
 * Separates domain from UI for the undo/redo use-case
 */
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

    /**
     * Initial method called to start the UI and show the menu
     */
    public void undo() {
        if (getController().undoRedoPreconditions()) {
            Scanner scanner = new Scanner(System.in);
            try {
                undoForm(scanner);
            } catch (BackException e){
                System.out.println("Cancelled undo\n");
            }
        } else {
            System.out.println("You must be logged in with the " + Role.PROJECTMANAGER + " role or a Developer role to call this function");
        }
    }

    /**
     * Prints the undo form, allows the user to undo the last action
     *
     * @param scanner           Scanner object used to get user input
     * @throws BackException    if the user types "BACK" at any time
     */
    private void undoForm(Scanner scanner) throws BackException {
        List<Tuple<CommandData,String>> previousCommands = getController().getExecutedCommands();
        if (previousCommands.size() == 0){
            System.out.println("There are no actions that can be undone; Cancelled undo\n");
        } else {
            printPreviousCommandsList(previousCommands);
            CommandData lastExecutedCommand = getController().getLastExecutedCommand();
            boolean confirmation = getBooleanInput(scanner,"Are you sure that you want to undo the last action? " + lastExecutedCommand.getDetails());

            if (confirmation){
                try{
                    getController().undoLastCommand();
                    System.out.println("Last action successfully undone\n");
                } catch (IncorrectUserException | EmptyCommandStackException e) {
                    System.out.println("ERROR: " + e.getMessage() + '\n');
                } catch (UndoNotPossibleException e) {
                    System.out.println("Last executed action can't be undone\n");
                }
            } else {
                System.out.println("Cancelled undo\n");
            }
        }
    }

    /**
     * Prints the commands of the previously executed commands
     * @param prevCmdList   List of previously executed commands along with the user who executed them
     */
    private void printPreviousCommandsList(List<Tuple<CommandData,String>> prevCmdList){
        System.out.println(" ***** EXECUTED ACTIONS *****");
        System.out.println(" ----- Oldest Action -----");
        for (Tuple<CommandData,String> command : prevCmdList){
            System.out.print(" - " + command.first().getDetails() +
                    " --- Executed By: " + command.second());
            if (!command.first().undoPossible()){
                System.out.println(" --- CANNOT BE UNDONE\n");
            } else {
                System.out.println();
            }
        }
        System.out.println(" ----- Most Recent Action -----");
        System.out.println();
    }

    /**
     * Initial method called to start the UI and show the redo menu
     */
    public void redo() {
        if (getController().undoRedoPreconditions()) {
            try {
                redoForm();
            } catch (BackException e){
                System.out.println("Cancelled redo\n");
            }
        } else {
            System.out.println("You must be logged in with the " + Role.PROJECTMANAGER + " role or a Developer role to call this function");
        }
    }

    /**
     * Prints the redo form, allows the user to redo the last undone action
     * @throws BackException  if the user types "BACK" at any time
     */
    private void redoForm() throws BackException {
        Scanner scanner = new Scanner(System.in);

        List<Tuple<CommandData,String>> undoneCommands = getController().getUndoneCommands();
        if (undoneCommands.size() == 0){
            System.out.println("There are no undone actions that can be redone; Cancelled redo\n");
        } else {
            printUndoneCommandsList(undoneCommands);
            CommandData latestCommand = getController().getLastUndoneCommand();
            boolean confirmation = getBooleanInput(scanner,"Confirm that you want to redo the last undone action: " + latestCommand.getDetails());

            if (confirmation){
                try{
                    getController().redoLastUndoneCommand();
                    System.out.println("Last undone action successfully redone\n");
                } catch (IncorrectUserException | EmptyCommandStackException e) {
                    System.out.println("ERROR: " + e.getMessage() + '\n');
                }
            } else {
                System.out.println("Cancelled redo\n");
            }
        }
    }

    /**
     * Prints the commands of all commands that have been undone
     * @param prevCmdList   List of previously undone commands along with the user who executed them
     */
    private void printUndoneCommandsList(List<Tuple<CommandData,String>> prevCmdList){
        System.out.println(" ***** UNDONE ACTIONS *****");
        System.out.println(" ----- Oldest Undone Action -----\n");
        for (Tuple<CommandData,String> command : prevCmdList){
            System.out.println(" - " + command.first().getDetails() +
                    " --- Executed By: " + command.second() + '\n');
        }
        System.out.println(" ----- Most Recent Undone Action -----");
        System.out.println(); // the fuck doet dit hier?
    }


    /**
     * Gets a boolean input from the user
     * @param scanner   Scanner object used to get user input
     * @param message   Message to be displayed to the user
     * @return          True if the user inputs "y", false if the user inputs "n"
     * @throws BackException    if the user types "BACK" at any time
     */
    private boolean getBooleanInput(Scanner scanner, String message) throws BackException {
        System.out.println(message + " (yes/no), but yes is no and no is no but crocodile is yes.");
        String answer = scanner.nextLine();
        if (answer.equals("BACK")) {
            throw new BackException();
        }

        while (!answer.equals("yes") && !answer.equals("no") && !answer.equals("crocodile")) {
            System.out.println("\nInput has to be 'yes', 'crocodile' or 'no', try again");
            System.out.println(message + " (yes/no/crocodile)");
            answer = scanner.nextLine();
            if (answer.equals("BACK")) {
                throw new BackException();
            }
        }

        return answer.equals("crocodile");
    }

    private static class BackException extends Exception {
        public BackException() {super();}
    }
}
