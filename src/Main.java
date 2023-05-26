import Application.*;
import Domain.Command.CommandInterface;
import Domain.Command.CommandManager;
import Domain.DataClasses.InvalidTimeException;
import Domain.DataClasses.Time;
import Domain.TaskManSystem.TaskManSystem;
import Domain.User.UserManager;
import UserInterface.*;

/**
 * Creates the initial objects and starts the UI
 */
public class Main {

    public static void main(String[] args) {
        try {
            TaskManSystem taskManSystem = new TaskManSystem(new Time(0, 0));
            UserManager userManager = new UserManager();
            Session session = new Session();
            SessionProxy sessionProxy = new SessionProxy(session);
            CommandInterface commandManager = new CommandManager();

            SessionController sessionController = new SessionController(session, userManager);
            AdvanceTimeController advanceTimeController = new AdvanceTimeController(sessionProxy, taskManSystem, commandManager);
            ProjectController createProjectController = new ProjectController(sessionProxy, taskManSystem, commandManager);
            ShowProjectsController showProjectsController = new ShowProjectsController(sessionProxy, taskManSystem);
            TaskController taskController = new TaskController(sessionProxy, taskManSystem, commandManager);
            LoadSystemController loadSystemController = new LoadSystemController(sessionProxy, taskManSystem, userManager, commandManager);
            StartTaskController startTaskController = new StartTaskController(sessionProxy, taskManSystem, commandManager);
            EndTaskController endTaskController = new EndTaskController(sessionProxy, taskManSystem, commandManager);
            UpdateDependenciesController updateDependenciesController = new UpdateDependenciesController(sessionProxy, taskManSystem, commandManager);
            UndoRedoController undoRedoController = new UndoRedoController(sessionProxy, commandManager);

            SessionUI sessionUI = new SessionUI(sessionController);
            AdvanceTimeUI advanceTimeUI = new AdvanceTimeUI(advanceTimeController);
            ProjectUI projectUI = new ProjectUI(createProjectController);
            ShowProjectsUI showProjectsUI = new ShowProjectsUI(showProjectsController);
            TaskUI taskUI = new TaskUI(taskController);
            LoadSystemUI loadSystemUI = new LoadSystemUI(loadSystemController);
            StartTaskUI startTaskUI = new StartTaskUI(startTaskController);
            EndTaskUI endTaskUI = new EndTaskUI(endTaskController);
            UpdateDependenciesUI updateDependenciesUI = new UpdateDependenciesUI(updateDependenciesController);
            UndoRedoUI undoRedoUI = new UndoRedoUI(undoRedoController);

            UserInterface UI = new UserInterface(
                    sessionUI,
                    advanceTimeUI,
                    projectUI,
                    showProjectsUI,
                    taskUI,
                    loadSystemUI,
                    startTaskUI,
                    endTaskUI,
                    updateDependenciesUI,
                    undoRedoUI
            );
            UI.startSystem();
        } catch (InvalidTimeException e) {
            System.out.println("Somehow the initial start time is invalid, this really should not happen.");
        }
    }
}
