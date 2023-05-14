package Domain.Command;

public interface CommandData {
    default CommandData getCommandData(){
        return this;
    }

    default String information()
    {
        return null;
    }

    default boolean undoPossible()
    {
        return true;
    }
}
