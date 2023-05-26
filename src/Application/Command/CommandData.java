package Application.Command;

import java.util.List;
import java.util.Map;

/**
 * Data class for a command, containing some information about the command needed for the UI.
 */
public interface CommandData {
    default CommandData getCommandData() {
        return this;
    }

    /**
     * @return the name of the command
     */
    String getName();

    /**
     * @return a string containing the details of the command
     */
    String getDetails();

    /**
     * @return a map containing the arguments sent to the system by executing the command
     */
    Map<String,String> getArguments();

    /**
     * @return a list containing the names of the arguments sent to the system by executing the command
     */
    List<String> getArgumentNames();

    /**
     * @return true if the command can be undone, false otherwise
     */
    default boolean undoPossible()
    {
        return false;
    }
}
