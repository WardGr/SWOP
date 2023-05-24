package UserInterface;

import Application.UndoRedoController;
import Domain.Command.CommandData;
import Domain.Command.UndoNotPossibleException;
import Domain.EmptyCommandStackException;
import Domain.IncorrectUserException;
import Domain.Tuple;

import java.util.List;
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
        try {
            undoForm(scanner);
        } catch (BackException e){
            System.out.println("Cancelled undo\n");
        }
    }

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

    private void printPreviousCommandsList(List<Tuple<CommandData,String>> prevCmdList){
        System.out.println(" ***** EXECUTED ACTIONS *****");
        System.out.println(" ----- Oldest Action -----");
        for (Tuple<CommandData,String> command : prevCmdList){
            System.out.print(" - " + command.getFirst().getDetails() +
                    " --- Executed By: " + command.getSecond());
            if (!command.getFirst().undoPossible()){
                System.out.println(" --- CANNOT BE UNDONE");
            } else {
                System.out.println();
            }
        }
        System.out.println(" ----- Most Recent Action -----");
        System.out.println();
    }



    public void redo() {
        try {
            redoForm();
        } catch (BackException e){
            System.out.println("Cancelled redo\n");
        }
    }

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

    private void printUndoneCommandsList(List<Tuple<CommandData,String>> prevCmdList){
        System.out.println(" ***** UNDONE ACTIONS *****");
        System.out.println(" ----- Oldest Undone Action -----");
        for (Tuple<CommandData,String> command : prevCmdList){
            System.out.println(" - " + command.getFirst().getDetails() +
                    " --- Executed By: " + command.getSecond());
        }
        System.out.println(" ----- Most Recent Undone Action -----");
        System.out.println();
    }



    private boolean getBooleanInput(Scanner scanner, String message) throws BackException {
        System.out.println(message + " (y/n)");
        String answer = scanner.nextLine();
        if (answer.equals("BACK")) {
            throw new BackException();
        }

        while (!answer.equals("y") && !answer.equals("n")) {
            System.out.println("\nInput has to be 'y' or 'n', try again");
            System.out.println(message + " (y/n)");
            answer = scanner.nextLine();
            if (answer.equals("BACK")) {
                throw new BackException();
            }
        }

        return answer.equals("y");
    }

    private static class BackException extends Exception {
        public BackException() {super();}
    }
}
