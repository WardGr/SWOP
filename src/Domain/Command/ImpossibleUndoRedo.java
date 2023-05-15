package Domain.Command;

 public class ImpossibleUndoRedo implements Command {

         @Override
         public void undo() {
             throw new UnsupportedOperationException("Undo is not possible");
         }

         @Override
         public void execute() {
             throw new UnsupportedOperationException("Redo is not possible");
         }

         @Override
         public String information() {
             return "Undo/Redo is not possible";
         }

         @Override
         public boolean undoPossible()
         {
             return false;
         }
 }