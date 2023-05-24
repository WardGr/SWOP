package Application;
 import Domain.*;
 import Domain.Command.CommandData;
 import Domain.Command.UndoNotPossibleException;

 import java.util.List;

/**
 * Separates domain from UI for the undo/redo use-case
 */
public class UndoRedoController {
     private final SessionProxy session;
     private final CommandInterface commandManager;

     public UndoRedoController(SessionProxy session, CommandInterface commandManager) {
         this.session = session;
         this.commandManager = commandManager;
     }

     private SessionProxy getSession(){
         return session;
     }

     private CommandInterface getCommandManager(){
         return commandManager;
     }

    /**
     * Undoes the last command executed by the current user
     *
     * @throws IncorrectUserException       If the current user is not the one who executed the last command
     * @throws EmptyCommandStackException   If there are no commands to undo
     * @throws UndoNotPossibleException     If the last command cannot be undone
     */
     public void undoLastCommand() throws IncorrectUserException, EmptyCommandStackException, UndoNotPossibleException {
         getCommandManager().undoLastCommand(getSession().getCurrentUser());
     }

    /**
     * Redoes the last command undone by the current user
     *
     * @throws IncorrectUserException       If the current user is not the one who undid the last command
     * @throws EmptyCommandStackException   If there are no commands to redo
     */
     public void redoLastUndoneCommand() throws IncorrectUserException, EmptyCommandStackException {
         getCommandManager().redoLast(getSession().getCurrentUser());
     }

    /**
     * @return  A list of tuples containing the data of the executed commands and the username of the user who executed them, in order of execution
     */
     public List<Tuple<CommandData,String>> getExecutedCommands(){
         return getCommandManager().getExecutedCommands();
     }

    /**
     * @return  Data of the last command executed
     */
     public CommandData getLastExecutedCommand(){
         return getCommandManager().getLastExecutedCommandData();
     }

    /**
     * @return  A list of tuples containing the data of the undone commands and the username of the user who executed them, in order of undoing
     */
    public List<Tuple<CommandData,String>> getUndoneCommands(){
        return getCommandManager().getUndoneCommands();
    }

    /**
     * @return  Data of the last command undone
     */
    public CommandData getLastUndoneCommand(){
         return getCommandManager().getLastUndoneCommandData();
    }
}
