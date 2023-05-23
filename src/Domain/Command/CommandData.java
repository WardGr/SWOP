package Domain.Command;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface CommandData {
    default CommandData getCommandData() {
        return this;
    }

    String getInformation();

    String getExtendedInformation();

    Map<String,String> getArguments();

    List<String> getArgumentNames();

    default boolean undoPossible()
    {
        return false;
    }
}
