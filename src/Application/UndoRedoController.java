package Application;
 import Domain.Command.CommandData;
 import Domain.Command.UndoNotPossibleException;
 import Domain.CommandManager;
 import Domain.EmptyCommandStackException;
 import Domain.IncorrectUserException;
 import Domain.Tuple;

 import java.util.List;

public class UndoRedoController {
     private final SessionProxy session;
     private final CommandManager commandManager;

     public UndoRedoController(SessionProxy session, CommandManager commandManager) {
         this.session = session;
         this.commandManager = commandManager;
     }

     private SessionProxy getSession(){
         return session;
     }

     private CommandManager getCommandManager(){
         return commandManager;
     }

     public void undoLastCommand() throws IncorrectUserException, EmptyCommandStackException, UndoNotPossibleException {
         commandManager.undoLastCommand(getSession().getCurrentUser());
     }

     public void redoLastUndoneCommand() throws IncorrectUserException, EmptyCommandStackException {
         commandManager.redoLastUndoneCommand(getSession().getCurrentUser());
     }

     public List<Tuple<CommandData,String>> getPreviousCommandsList(){
         return getCommandManager().getPreviousCommandsList();
     }

     public CommandData getLastCommand(){
         return getCommandManager().getLastCommand();
     }

    public List<Tuple<CommandData,String>> getUndoneCommandsList(){
        return getCommandManager().getUndoneCommandsList();
    }

    public CommandData getLastUndoneCommand(){
         return getCommandManager().getLastUndoneCommand();
    }
}
