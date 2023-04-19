package Application;

import Domain.*;
import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.NonDeveloperRoleException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Separates domain from UI for the loadsystem use-case
 */
public class LoadSystemController {
    private final UserManager userManager;
    private final TaskManSystem taskManSystem;
    private final SessionWrapper session;

    public LoadSystemController(SessionWrapper session, TaskManSystem taskManSystem, UserManager userManager) {
        this.session = session;
        this.taskManSystem = taskManSystem;
        this.userManager = userManager;
    }

    private UserManager getUserManager() {
        return userManager;
    }

    private TaskManSystem getTaskManSystem() {
        return taskManSystem;
    }

    private SessionWrapper getSession() {
        return session;
    }

    public void clear() throws InvalidTimeException {
        getTaskManSystem().clear();
        for (User user : getUserManager().getUsers()) {
            user.setTask(null);
        }
    }

    /**
     * @return Whether the user is logged in as a project manager
     */
    public boolean loadSystemPreconditions() {
        return getSession().getRoles().contains(Role.PROJECTMANAGER);
    }

    /**
     * Loads in a JSON file that holds project information at the given filepath
     *
     * @param filepath String containing the filepath to the JSON holding the system information
     */
    public void LoadSystem(String filepath) throws IncorrectPermissionException, InvalidFileException {
        if (!loadSystemPreconditions()) {
            throw new IncorrectPermissionException("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");
        }
        try {
            JSONParser jsonParser = new JSONParser();
            FileReader reader = new FileReader(filepath);
            JSONObject doc = (JSONObject) jsonParser.parse(reader);
            clear();

            //load projects
            JSONArray projects = (JSONArray) doc.get("projects");
            TreeMap<Time, JSONArray> loadedProjects = new TreeMap<>();
            for (Object p : projects) {
                int startHour = (int) (long) ((JSONObject) p).get("startHour");
                int startMinute = (int) (long) ((JSONObject) p).get("startMinute");
                Time startTime = new Time(startHour, startMinute);

                if(loadedProjects.get(startTime) == null){
                    JSONArray projectArray  = new JSONArray();
                    loadedProjects.put(startTime, projectArray);
                }
                loadedProjects.get(startTime).add(p);
            }

            //load tasks
            JSONArray tasks = (JSONArray) doc.get("tasks");
            TreeMap<Time, JSONArray> startedTasks = new TreeMap<>();
            TreeMap<Time, JSONArray> endedTasks = new TreeMap<>();
            HashSet<JSONObject> remainingTasks = new HashSet<>();
            for (Object t : tasks) {
                handleTask((JSONObject) t, startedTasks, endedTasks, remainingTasks);
            }
            load(loadedProjects, startedTasks, endedTasks, remainingTasks);

            //set system time
            int systemHour = (int) (long) doc.get("systemHour");
            int systemMinute = (int) (long) doc.get("systemMinute");
            getTaskManSystem().advanceTime(new Time(systemHour, systemMinute));
        } catch (ParseException | InvalidTimeException | NewTimeBeforeSystemTimeException | UserNotFoundException |
                 ProjectNotFoundException | TaskNotFoundException | TaskNameAlreadyInUseException |
                 IncorrectTaskStatusException | UserAlreadyAssignedToTaskException |
                 SessionController.RoleNotFoundException |
                 LoopDependencyGraphException | IncorrectRoleException | NonDeveloperRoleException |
                 EndTimeBeforeStartTimeException | IncorrectUserException | ProjectNotOngoingException |
                 ProjectNameAlreadyInUseException | DueTimeBeforeCreationTimeException | DueBeforeSystemTimeException e) {

            try {
                clear();
            } catch (InvalidTimeException ex) {
                throw new RuntimeException(ex);
            }
            throw new InvalidFileException("ERROR: File logic is invalid so couldn't setup system.");

        } catch (IOException e) {
            try {
                clear();
            } catch (InvalidTimeException ex) {
                throw new RuntimeException(ex);
            }
            throw new InvalidFileException("ERROR: File path is invalid.");
        }
    }

    /**
     * Loads in a project from the given JSONObject
     *
     * @param project JSONObject containing the details of the project to create and load
     */
    private void startProject(JSONObject project) throws ProjectNameAlreadyInUseException, InvalidTimeException, DueBeforeSystemTimeException {
        //create the project
        String name = (String) project.get("name");
        String description = (String) project.get("description");
        int endHour = (int) (long) project.get("endHour");
        int endMinute = (int) (long) project.get("endMinute");

        getTaskManSystem().createProject(name, description, new Time(endHour, endMinute));

    }


    private void load(TreeMap<Time, JSONArray> projects, TreeMap<Time, JSONArray> startedTasks, TreeMap<Time, JSONArray> endedTasks, HashSet<JSONObject> remainingTasks ) throws InvalidTimeException, NewTimeBeforeSystemTimeException, UserNotFoundException, ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, SessionController.RoleNotFoundException, LoopDependencyGraphException, IncorrectRoleException, NonDeveloperRoleException, EndTimeBeforeStartTimeException, IncorrectUserException, ProjectNotOngoingException, ProjectNameAlreadyInUseException, DueTimeBeforeCreationTimeException, DueBeforeSystemTimeException {
        while(startedTasks.size() > 0 || endedTasks.size() > 0 || projects.size() > 0){
            if(startedTasks.size() == 0){
                if (endedTasks.size() == 0) {
                    getTaskManSystem().advanceTime(projects.firstKey());
                    for(Object p : projects.firstEntry().getValue()){
                        startProject((JSONObject) p);
                    }
                    projects.pollFirstEntry();
                } else if (projects.size() == 0 || endedTasks.firstKey().before(projects.firstKey())){
                    getTaskManSystem().advanceTime(endedTasks.firstKey());
                    for(Object e : endedTasks.firstEntry().getValue()){
                        endTask((JSONObject) e);
                    }
                    endedTasks.pollFirstEntry();
                } else {
                    getTaskManSystem().advanceTime(projects.firstKey());
                    for(Object p : projects.firstEntry().getValue()){
                        startProject((JSONObject) p);
                    }
                    projects.pollFirstEntry();
                }
            } else if (endedTasks.size() == 0) {
                if(projects.size() == 0 || startedTasks.firstKey().before(projects.firstKey())){
                    getTaskManSystem().advanceTime(startedTasks.firstKey());
                    for(Object s : startedTasks.firstEntry().getValue()){
                        startTask((JSONObject) s);
                    }
                    startedTasks.pollFirstEntry();
                }else{
                    getTaskManSystem().advanceTime(projects.firstKey());
                    for(Object p : projects.firstEntry().getValue()){
                        startProject((JSONObject) p);
                    }
                    projects.pollFirstEntry();
                }
            } else if (projects.size() == 0) {
                if(startedTasks.firstKey().before(endedTasks.firstKey())){
                    getTaskManSystem().advanceTime(startedTasks.firstKey());
                    for(Object s : startedTasks.firstEntry().getValue()){
                        startTask((JSONObject) s);
                    }
                    startedTasks.pollFirstEntry();
                } else {
                    getTaskManSystem().advanceTime(endedTasks.firstKey());
                    for(Object e : endedTasks.firstEntry().getValue()){
                        endTask((JSONObject) e);
                    }
                    endedTasks.pollFirstEntry();
                }
            }else {
                if(startedTasks.firstKey().before(endedTasks.firstKey()) && startedTasks.firstKey().before(projects.firstKey())){
                    getTaskManSystem().advanceTime(startedTasks.firstKey());
                    for(Object s : startedTasks.firstEntry().getValue()){
                        startTask((JSONObject) s);
                    }
                    startedTasks.pollFirstEntry();
                } else if (endedTasks.firstKey().before(startedTasks.firstKey()) && startedTasks.firstKey().before(projects.firstKey())) {
                    getTaskManSystem().advanceTime(endedTasks.firstKey());
                    for(Object e : endedTasks.firstEntry().getValue()){
                        endTask((JSONObject) e);
                    }
                    endedTasks.pollFirstEntry();
                }else {
                    getTaskManSystem().advanceTime(projects.firstKey());
                    for(Object p : projects.firstEntry().getValue()){
                        startProject((JSONObject) p);
                    }
                    projects.pollFirstEntry();
                }

            }
        }

        for(JSONObject r : remainingTasks){
            startTask(r);
        }
    }

    private void handleTask(JSONObject task, Map<Time, JSONArray> started, Map<Time, JSONArray> ended, HashSet<JSONObject> remaining) throws InvalidTimeException {
        if(task.get("startHour") == null | task.get("startMinute") == null){
            remaining.add(task);
            return;
        }
        else {
            Time startTime = new Time((int) (long) task.get("startHour"), (int) (long) task.get("startMinute"));
            if(started.get(startTime) == null){
                JSONArray taskArray  = new JSONArray();
                started.put(startTime, taskArray);
            }
            started.get(startTime).add(task);

        }
        if(task.get("endHour") != null && task.get("endMinute") != null){
            Time endTime = new Time((int) (long) task.get("endHour"), (int) (long) task.get("endMinute"));
            if(ended.get(endTime) == null){
                JSONArray taskArray  = new JSONArray();
                ended.put(endTime, taskArray);
            }
            ended.get(endTime).add(task);

        }
    }

    private void startTask(JSONObject task) throws UserNotFoundException, InvalidTimeException, ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, IncorrectTaskStatusException, LoopDependencyGraphException, NonDeveloperRoleException, UserAlreadyAssignedToTaskException, IncorrectRoleException, SessionController.RoleNotFoundException, ProjectNotOngoingException {
        //standard task fields
        String name = (String) task.get("name");
        String description = (String) task.get("description");
        int dueHour = (int) (long) task.get("dueHour");
        int dueMinute = (int) (long) task.get("dueMinute");
        Time dueTime = new Time(dueHour, dueMinute);
        double acceptableDeviation = (double) task.get("acceptableDeviation");
        String projectName = (String) task.get("project");

        //find required roles
        ArrayList<Role> roles = new ArrayList<>();
        for(Object role : (JSONArray) task.get("roles")){
            roles.add(findRole((String) role));
        }
        //add task to project
        String replaces = (String) task.get("replaces");
        if(replaces != null){
            getTaskManSystem().replaceTaskInProject(projectName, name, description, dueTime, acceptableDeviation, replaces);
        }else{
            ArrayList<String> prevTasks = (ArrayList<String>) task.get("previousTasks");
            getTaskManSystem().addTaskToProject(projectName, name, description, dueTime, acceptableDeviation, roles, new HashSet<>(prevTasks), new HashSet<>());
        }
        //start the task
        if(getTaskManSystem().getTaskData(projectName, name).getStatus() == Status.AVAILABLE || getTaskManSystem().getTaskData(projectName, name).getStatus() == Status.PENDING){
            JSONArray users = (JSONArray) task.get("users");
            for(Object user : users){
                getTaskManSystem().startTask(projectName, name, getUserManager().getUser((String) ((JSONObject) user).get("user")), findRole((String) ((JSONObject) user).get("role")));
            }
        }
    }

    public void endTask(JSONObject task) throws InvalidTimeException, ProjectNotFoundException, EndTimeBeforeStartTimeException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectUserException, UserNotFoundException {
        //standard task fields
        String name = (String) task.get("name");
        int dueHour = (int) (long) task.get("dueHour");
        int dueMinute = (int) (long) task.get("dueMinute");
        Time dueTime = new Time(dueHour, dueMinute);
        String projectName = (String) task.get("project");

        //get a user from the task (which one doesn't matter)
        JSONArray users = (JSONArray) task.get("users");
        JSONObject user = (JSONObject) users.get(0);

        //check if task is failed or finished
        if(dueTime.before(getTaskManSystem().getSystemTime())){
            getTaskManSystem().failTask(projectName, name, getUserManager().getUser((String) user.get("user")));
        }
        else {
            getTaskManSystem().finishTask(projectName, name, getUserManager().getUser((String) user.get("user")));
        }
    }

    public Role findRole(String role) throws SessionController.RoleNotFoundException {
        switch (role) {
            case "SYSADMIN" -> {
                return Role.SYSADMIN;
            }
            case "JAVAPROGRAMMER" -> {
                return Role.JAVAPROGRAMMER;
            }
            case "PYTHONPROGRAMMER" -> {
                return Role.PYTHONPROGRAMMER;
            }
        }
        throw new SessionController.RoleNotFoundException();
    }
}


