package Domain.Command;

import java.util.LinkedList;
import java.util.List;

public interface CommandData {
    default CommandData getCommandData(){
        return this;
    }

    default String information()
    {
        return null;
    }

    default List<String> getArguments(){
        return new LinkedList<>();
    }

    default boolean undoPossible()
    {
        return true;
    }
}
