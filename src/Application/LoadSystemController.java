package Application;

import Domain.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Separates domain from UI for the loadsystem use-case
 */
public class LoadSystemController {
    private final UserManager userManager;
    private final TaskManSystem taskManSystem;
    private final Session session;

    public LoadSystemController(UserManager userManager, TaskManSystem taskManSystem, Session session) {
        this.userManager = userManager;
        this.taskManSystem = taskManSystem;
        this.session = session;
    }

    private UserManager getUserManager() {
        return userManager;
    }

    private TaskManSystem getTaskManSystem() {
        return taskManSystem;
    }

    private Session getSession() {
        return session;
    }

    /**
     * @return Whether the user is logged in as a project manager
     */
    public boolean loadSystemPreconditions() {
        return getSession().getRole() == Role.PROJECTMANAGER;
    }

    /**
     * Loads in a JSON file that holds project information at the given filepath
     *
     * @param filepath String containing the filepath to the JSON holding the system information
     */
    public void LoadSystem(String filepath) throws IncorrectPermissionException, IOException, InvalidTimeException, ParseException, UserNotFoundException, ProjectNameAlreadyInUseException, ReplacedTaskNotFailedException, FailTimeAfterSystemTimeException, ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, DueBeforeSystemTimeException, IncorrectTaskStatusException, IncorrectUserException, NewTimeBeforeSystemTimeException, EndTimeBeforeStartTimeException, StartTimeBeforeAvailableException {
        if (!loadSystemPreconditions()) {
            throw new IncorrectPermissionException("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");
        }
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader(filepath);
        JSONObject doc = (JSONObject) jsonParser.parse(reader);
        getTaskManSystem().clear();
        //set system time
        int systemHour = (int) (long) doc.get("systemHour");
        int systemMinute = (int) (long) doc.get("systemMinute");
        getTaskManSystem().advanceTime(new Time(systemHour, systemMinute));
        //load projects
        JSONArray projects = (JSONArray) doc.get("projects");
        for (Object p : projects) {
            loadProject((JSONObject) p);
        }

    }

    /**
     * Loads in a project from the given JSONObject
     *
     * @param project JSONObject containing the details of the project to create and load
     */
    private void loadProject(JSONObject project) throws UserNotFoundException, ReplacedTaskNotFailedException, DueBeforeSystemTimeException, ProjectNameAlreadyInUseException, ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, FailTimeAfterSystemTimeException, IncorrectUserException, InvalidTimeException, IncorrectTaskStatusException, EndTimeBeforeStartTimeException, StartTimeBeforeAvailableException {
        //create the project
        String name = (String) project.get("name");
        String description = (String) project.get("description");
        int startHour = (int) (long) project.get("startHour");
        int startMinute = (int) (long) project.get("startMinute");
        int endHour = (int) (long) project.get("endHour");
        int endMinute = (int) (long) project.get("endMinute");
        getTaskManSystem().createProject(name, description, new Time(startHour, startMinute), new Time(endHour, endMinute));
        //load the tasks
        JSONArray tasks = (JSONArray) project.get("tasks");
        for (Object t : tasks) {
            loadTask((JSONObject) t, name);
        }

    }

    /**
     * Loads a task from a JSONObject and adds it to the given project
     *
     * @param task        JSONobject holding the new task information
     * @param projectName Name of the project which to add the new task to
     */
    private void loadTask(JSONObject task, String projectName) throws UserNotFoundException, ReplacedTaskNotFailedException, ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, IncorrectUserException, FailTimeAfterSystemTimeException, InvalidTimeException, IncorrectTaskStatusException, StartTimeBeforeAvailableException, EndTimeBeforeStartTimeException {
        //standard task fields
        String name = (String) task.get("name");
        String description = (String) task.get("description");
        int dueHour = (int) (long) task.get("dueHour");
        int dueMinute = (int) (long) task.get("dueMinute");
        double acceptableDeviation = (double) task.get("acceptableDeviation");

        //determening what kind of taks it is
        User user = getUserManager().getDeveloper((String) task.get("user"));
        String replacesTask = (String) task.get("replaces");
        if (replacesTask != null) {
            getTaskManSystem().replaceTaskInProject(projectName, name, description, new Time(dueHour, dueMinute), acceptableDeviation, replacesTask);
        } else {
            List<String> prevTasks = (List<String>) task.get("previousTasks");
            getTaskManSystem().addTaskToProject(projectName, name, description, new Time(dueHour, dueMinute), acceptableDeviation, prevTasks, user);
        }

        //handling the status
        String status = (String) task.get("status");
        if (status.equals("EXECUTING")) {
            int startHour = (int) (long) task.get("startHour");
            int startMinute = (int) (long) task.get("startMinute");
            getTaskManSystem().startTask(projectName, name, new Time(startHour, startMinute), user);
        } else if (status.equals("FINISHED")) {
            int startHour = (int) (long) task.get("startHour");
            int startMinute = (int) (long) task.get("startMinute");
            int endHour = (int) (long) task.get("endHour");
            int endMinute = (int) (long) task.get("endMinute");
            getTaskManSystem().startTask(projectName, name, new Time(startHour, startMinute), user);
            getTaskManSystem().endTask(projectName, name, Status.FINISHED, new Time(endHour, endMinute), user);
        } else if (status.equals("FAILED")) {
            int startHour = (int) (long) task.get("startHour");
            int startMinute = (int) (long) task.get("startMinute");
            int endHour = (int) (long) task.get("endHour");
            int endMinute = (int) (long) task.get("endMinute");
            getTaskManSystem().startTask(projectName, name, new Time(startHour, startMinute), user);
            getTaskManSystem().endTask(projectName, name, Status.FAILED, new Time(endHour, endMinute), user);
        }
    }
}
