
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class LoadSystemController {
    private final UserManager userManager;
    private final TaskManSystem taskManSystem;
    private final Session session;

    private UserManager getUserManager() {
        return userManager;
    }

    private TaskManSystem getTaskManSystem() {
        return taskManSystem;
    }

    private Session getSession() {
        return session;
    }

    LoadSystemController( UserManager userManager, TaskManSystem taskManSystem, Session session){
        this.userManager = userManager;
        this.taskManSystem = taskManSystem;
        this.session = session;
    }

    public boolean loadSystemPreconditions(){
        return getSession().getRole() == Role.PROJECTMANAGER;
    }

    /**
     * Creates the initial
     */
    public void LoadSystem(String filepath) throws IncorrectPermissionException, IOException, InvalidTimeException, ParseException, UserNotFoundException, ProjectNameAlreadyInUseException, ReplacedTaskNotFailedException, FailTimeAfterSystemTimeException, ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, DueBeforeSystemTimeException, IncorrectTaskStatusException, IncorrectUserException, NewTimeBeforeSystemTimeException {
        if (getSession().getRole() != Role.PROJECTMANAGER) {
            throw new IncorrectPermissionException("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");
        }
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader(filepath);
        JSONObject doc = (JSONObject) jsonParser.parse(reader);
        //set system time
        int systemHour = (int) (long) doc.get("systemHour");
        int systemMinute = (int) (long) doc.get("systemMinute");
        getTaskManSystem().advanceTime(new Time(systemHour, systemMinute));
        //load projects
        JSONArray projects = (JSONArray) doc.get("projects");
        for(Object p : projects){
            loadProject((JSONObject) p, getUserManager(), taskManSystem);
        }

    }

    private void loadProject(JSONObject project, UserManager userManager, TaskManSystem taskManSystem) throws UserNotFoundException, ReplacedTaskNotFailedException, DueBeforeSystemTimeException, ProjectNameAlreadyInUseException, ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, FailTimeAfterSystemTimeException, IncorrectUserException, InvalidTimeException, IncorrectTaskStatusException {
        //create the project
        String name = (String) project.get("name");
        String description = (String) project.get("description");
        int startHour = (int) (long) project.get("startHour");
        int startMinute = (int) (long) project.get("startMinute");
        int endHour = (int) (long) project.get("endHour");
        int endMinute = (int) (long) project.get("endMinute");
        taskManSystem.createProject(name, description, new Time(startHour, startMinute), new Time (endHour, endMinute));
        //load the tasks
        JSONArray tasks = (JSONArray) project.get("tasks");
        for(Object t : tasks){
            loadTask((JSONObject) t, userManager, taskManSystem, name);
        }

    }

    private void loadTask(JSONObject task, UserManager userManager, TaskManSystem taskManSystem, String projectName) throws UserNotFoundException, ReplacedTaskNotFailedException, ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, IncorrectUserException, FailTimeAfterSystemTimeException, InvalidTimeException, IncorrectTaskStatusException {
        //standard task fields
        String name = (String) task.get("name");
        String description = (String) task.get("description");
        int dueHour = (int) (long) task.get("dueHour");
        int dueMinute = (int) (long) task.get("dueMinute");
        double acceptableDeviation = (double) task.get("acceptableDeviation");

        //determening what kind of taks it is
        User user = userManager.getDeveloper((String) task.get("user"));
        String replacesTask = (String) task.get("replaces");
        if(replacesTask != null){
            taskManSystem.replaceTaskInProject(projectName, name, description, new Time(dueHour,dueMinute), acceptableDeviation, replacesTask);
        }else {
            List<String> prevTasks = (List<String>) task.get("previousTasks");
            taskManSystem.addTaskToProject(projectName, name, description, new Time(dueHour,dueMinute), acceptableDeviation, prevTasks, user);
        }

        //handling the status
        String status = (String) task.get("status");
        if(status.equals("EXECUTING")){
            int startHour = (int) (long) task.get("startHour");
            int startMinute = (int) (long) task.get("startMinute");
            taskManSystem.startTask(projectName, name, new Time(startHour, startMinute), user);
        } else if (status.equals("FINISHED")) {
            int startHour = (int) (long) task.get("startHour");
            int startMinute = (int) (long) task.get("startMinute");
            int endHour = (int) (long) task.get("endHour");
            int endMinute = (int) (long) task.get("endMinute");
            taskManSystem.startTask(projectName, name, new Time(startHour, startMinute), user);
            taskManSystem.endTask(projectName, name, Status.FINISHED, new Time(endHour, endMinute), user);
        }else if (status.equals("FAILED")) {
            int startHour = (int) (long) task.get("startHour");
            int startMinute = (int) (long) task.get("startMinute");
            int endHour = (int) (long) task.get("endHour");
            int endMinute = (int) (long) task.get("endMinute");
            taskManSystem.startTask(projectName, name, new Time(startHour, startMinute), user);
            taskManSystem.endTask(projectName, name, Status.FAILED, new Time(endHour, endMinute), user);
        }
    }
}
