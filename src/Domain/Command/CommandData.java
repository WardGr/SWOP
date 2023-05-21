package Domain.Command;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface CommandData {
    default CommandData getCommandData(){
        return this;
    }

    default String getInformation()
    {
        return null;
    }

    default String getExtendedInformation(){
        return null;
    }

    default Map<String,String> getArguments(){
        return new HashMap<>();
    }

    default List<String> getArgumentNames(){
        return new LinkedList<>();
    }

    default boolean undoPossible()
    {
        return false;
    }
}
