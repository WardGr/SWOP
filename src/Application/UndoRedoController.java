package Application;
 import Domain.*;
 import Domain.Command.CommandData;
 import Domain.Command.UndoNotPossibleException;

 import java.util.List;

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

     public void undoLastCommand() throws IncorrectUserException, EmptyCommandStackException, UndoNotPossibleException {
         commandManager.undoLastCommand(getSession().getCurrentUser());
     }

     public void redoLastUndoneCommand() throws IncorrectUserException, EmptyCommandStackException {
         commandManager.redoLast(getSession().getCurrentUser());
     }

     public List<Tuple<CommandData,String>> getPreviousCommandsList(){
         return getCommandManager().getPreviousCommandsList();
     }

     public CommandData getLastCommand(){
         return getCommandManager().getLastExecutedCommandData();
     }

    public List<Tuple<CommandData,String>> getUndoneCommandsList(){
        return getCommandManager().getUndoneCommandsList();
    }

    public CommandData getLastUndoneCommand(){
         return getCommandManager().getLastUndoneCommandData();
    }
}
