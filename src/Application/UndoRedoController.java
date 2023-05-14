package Application;
 import Domain.CommandManager;

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

     public void undo() throws Exception {
         commandManager.undo(session.getCurrentUser());
     }

     public void redo() throws Exception {
         commandManager.redo(session.getCurrentUser());
     }

     public List<String> possibleUndoes() {
         return commandManager.possibleUndoes(session.getCurrentUser());
     }

     public List<String> possibleRedoes() {
         return commandManager.possibleRedoes(session.getCurrentUser());
     }
}
