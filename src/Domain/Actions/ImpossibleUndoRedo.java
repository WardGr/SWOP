package Domain.Actions;

public class ImpossibleUndoRedo extends Action {

        @Override
        public void undo() {
            throw new UnsupportedOperationException("Undo is not possible");
        }

        @Override
        public void redo() {
            throw new UnsupportedOperationException("Redo is not possible");
        }

        @Override
        public String information() {
            return "Undo/Redo is not possible";
        }

        @Override
        public boolean reversePossible()
        {
            return false;
        }
}
