package Application.SystemControllers;

import Application.IncorrectPermissionException;
import Application.Session.SessionProxy;
import Domain.Command.CommandInterface;
import Domain.Command.LoadSystemCommands.LoadSystemCommand;
import Domain.DataClasses.InvalidTimeException;
import Domain.TaskManSystem.TaskManSystem;
import Domain.User.Role;
import Domain.User.User;
import Domain.User.UserManager;

/**
 * Separates domain from UI for the loadsystem use-case
 */
public class LoadSystemController {
    private final UserManager userManager;
    private final TaskManSystem taskManSystem;
    private final SessionProxy session;
    private final CommandInterface commandManager;

    /**
     * Creates this controller object
     *
     * @param session           The current session to set as active session
     * @param taskManSystem     The system class to set as current system
     * @param userManager       The class managing all users in the system
     */
    public LoadSystemController(SessionProxy session, TaskManSystem taskManSystem, UserManager userManager, CommandInterface commandManager) {
        this.session = session;
        this.taskManSystem = taskManSystem;
        this.userManager = userManager;
        this.commandManager = commandManager;
    }

    /**
     * @return  The current user manager object
     */
    private UserManager getUserManager() {
        return userManager;
    }

    /**
     * @return  The object containing the current taskmanager system
     */
    private TaskManSystem getTaskManSystem() {
        return taskManSystem;
    }

    /**
     * @return  The session data object with currently logged-in user
     */
    private SessionProxy getSession() {
        return session;
    }

    /**
     * @return  The object containing the current command manager
     */
    private CommandInterface getCommandManager(){
        return commandManager;
    }

    /**
     * Resets the taskmansystem (clearing all projects and setting the time to 0) and ends all tasks for every user
     */
    private void clear() throws InvalidTimeException {
        getTaskManSystem().reset();
        for (User user : getUserManager().getUsers()) {
            user.endTask();
        }
    }

    /**
     * @return Whether the user is logged in as a project manager
     */
    public boolean loadSystemPreconditions() {
        return getSession().getRoles().contains(Role.PROJECTMANAGER);
    }

    /**
     * Loads in a JSON file that holds project information (tasks, projects, systemtime, ...) at the given filepath
     *
     * @param filepath String containing the filepath to the JSON holding the system information
     */
    public void LoadSystem(String filepath) throws IncorrectPermissionException, InvalidFileException {
        if (!loadSystemPreconditions()) {
            throw new IncorrectPermissionException("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");
        }
        LoadSystemCommand cmd = new LoadSystemCommand(filepath, getSession(), getTaskManSystem(), getUserManager());
        cmd.execute();
        getCommandManager().addExecutedCommand(cmd, getSession().getCurrentUser());
    }

}


