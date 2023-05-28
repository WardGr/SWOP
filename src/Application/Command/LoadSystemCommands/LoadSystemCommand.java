package Application.Command.LoadSystemCommands;

import Application.*;
import Application.SystemControllers.InvalidFileException;
import Application.Command.Command;
import Domain.DataClasses.EndTimeBeforeStartTimeException;
import Domain.DataClasses.InvalidTimeException;
import Domain.DataClasses.Time;
import Domain.DataClasses.Tuple;
import Domain.Project.DueTimeBeforeCreationTimeException;
import Domain.Project.ProjectNameAlreadyInUseException;
import Domain.Project.ProjectNotOngoingException;
import Domain.Project.TaskNotFoundException;
import Domain.Task.*;
import Domain.Task.IncorrectTaskStatusException;
import Domain.Task.LoopDependencyGraphException;
import Domain.Task.Status;
import Domain.TaskManSystem.DueBeforeSystemTimeException;
import Domain.TaskManSystem.NewTimeBeforeSystemTimeException;
import Domain.TaskManSystem.ProjectNotFoundException;
import Domain.TaskManSystem.TaskManSystem;
import Domain.User.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class LoadSystemCommand implements Command {

    private final String path;
    private final UserManager userManager;
    private final TaskManSystem taskManSystem;


    /**
     * Creates this commands object
     *
     * @param path              The filepath of the file that loads the system
     * @param taskManSystem     The system class to set as current system
     * @param userManager       The class managing all users in the system
     */
    public LoadSystemCommand(String path, TaskManSystem taskManSystem, UserManager userManager) {
        this.path = path;
        this.taskManSystem = taskManSystem;
        this.userManager = userManager;
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
     * Resets the taskmansystem (clearing all projects and setting the time to 0) and ends all tasks for every user
     */
    private void clear() throws InvalidTimeException {
        getTaskManSystem().reset();
        for (User user : getUserManager().getUsers()) {
            user.endTask();
        }
    }
    private String getPath(){
        return path;
    }
    @Override
    public void execute() throws IncorrectPermissionException, InvalidFileException {
        try {
            JSONParser jsonParser = new JSONParser();
            FileReader reader = new FileReader(getPath());
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
                 RoleNotFoundException |
                 LoopDependencyGraphException | IncorrectRoleException | IllegalTaskRolesException |
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

    /**
     * Loads in all the projects and tasks from the given JSONObjects, in chronological order
     *
     * @param projects          A treemap, sorted on start time, containing all projects to be loaded
     * @param startedTasks      A treemap, sorted on start time, containing all started tasks to be loaded
     * @param endedTasks        A treemap, sorted on end time, containing all ended tasks to be loaded
     * @param remainingTasks    A set containing the tasks that have not started or ended yet
     * @throws InvalidTimeException                     If any time object has negative hours or minutes, or minutes above 59
     * @throws NewTimeBeforeSystemTimeException         If creating a task before systemtime
     * @throws TaskNameAlreadyInUseException            If a two or more tasks in any of the tasks maps/set share a name
     * @throws UserAlreadyAssignedToTaskException       If a user is assigned to the same task twice
     * @throws RoleNotFoundException  If a role string does not correspond to an existing role enum
     * @throws LoopDependencyGraphException             If there is a loop in the dependency graph of the projects
     * @throws IncorrectRoleException                   If a user is assigned to a task with a role that this task does not need
     * @throws IllegalTaskRolesException                If attempting to create a task without roles, or roles containing non-developer roles
     * @throws EndTimeBeforeStartTimeException          If the end-time of any of the tasks is after its start time
     * @throws IncorrectUserException                   If attempting to finish or end a task that is not assigned to the given user
     * @throws ProjectNotOngoingException               If adding a task to a project that is already finbished
     * @throws ProjectNameAlreadyInUseException         If any project in the projects treemap share a name
     * @throws DueTimeBeforeCreationTimeException       If a tasks' due time is before its creation time
     * @throws DueBeforeSystemTimeException             If a tasks' due time is before the system time at creation
     */
    private void load(TreeMap<Time, JSONArray> projects, TreeMap<Time, JSONArray> startedTasks, TreeMap<Time, JSONArray> endedTasks, HashSet<JSONObject> remainingTasks ) throws InvalidTimeException, NewTimeBeforeSystemTimeException, UserNotFoundException, ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, RoleNotFoundException, LoopDependencyGraphException, IncorrectRoleException, IllegalTaskRolesException, EndTimeBeforeStartTimeException, IncorrectUserException, ProjectNotOngoingException, ProjectNameAlreadyInUseException, DueTimeBeforeCreationTimeException, DueBeforeSystemTimeException, InvalidFileException {
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

    /**
     * Populates the task maps and sets from the given JSONObject
     *
     * @param task       The JSONObject to extract the tasks from
     * @param started    Map of tasks to add all started tasks to
     * @param ended      Map of tasks to add all ended tasks to
     * @param remaining  Map of tasks to add all tasks not started/ended to
     * @throws InvalidTimeException if any time in the JSONObject contains a negative integer or a minute field > 59
     */
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

    /**
     * Adds a task to the task manager system from the given JSONObject
     *
     * @param task the given JSONObject containing task information
     * @throws UserNotFoundException                        If the username in the JSONObject does not correspond to an existing username
     * @throws InvalidTimeException                         If a time set in the JSONObject has negative integers or has > 59 minutes
     * @throws ProjectNotFoundException                     If the projectname in the JSONObject does not correspond to an existing project
     * @throws TaskNotFoundException                        If the taskname in the JSONObject does not correspond to an existing task
     * @throws TaskNameAlreadyInUseException                If the taskname in the JSONObject is already in use by another loaded task
     * @throws IncorrectTaskStatusException                 If the task is not AVAILABLE/UNAVAILABLE while adding, or AVAILABLE while starting
     * @throws LoopDependencyGraphException                 If adding  this task causes a loop in the dependency graph
     * @throws IllegalTaskRolesException                    If the roles in the JSONObject are empty, or contain non-developer roles
     * @throws UserAlreadyAssignedToTaskException           If the given user in the JSONObject is already assigned to this task
     * @throws IncorrectRoleException                       If the given user in the JSONObject does not have the given role, or
     * @throws RoleNotFoundException      If a role in the JSONObject could not be parsed to an existing role
     * @throws ProjectNotOngoingException                   If the project the task belongs to is not ongoing
     */
    private void startTask(JSONObject task) throws UserNotFoundException, InvalidTimeException, ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, IncorrectTaskStatusException, LoopDependencyGraphException, IllegalTaskRolesException, UserAlreadyAssignedToTaskException, IncorrectRoleException, RoleNotFoundException, ProjectNotOngoingException, InvalidFileException {
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
            ArrayList<Tuple<String, String>> prevTasks = loadPreviousTaskTuple((ArrayList<ArrayList<String>>) task.get("previousTasks"));

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

    /**
     * Ends the task depicted by the given JSONObject
     *
     * @param task the given JSONObject containing task information
     * @throws InvalidTimeException             If a time set in the JSONObject has negative integers or has > 59 minutes
     * @throws ProjectNotFoundException         If the projectname in the JSONObject does not correspond to an existing project
     * @throws EndTimeBeforeStartTimeException  If the current systemtime is before the tasks' start time
     * @throws TaskNotFoundException            If the task corresponding to the given taskname is not an existing task in the system
     * @throws IncorrectTaskStatusException     If the given task is not EXECUTING
     * @throws IncorrectUserException           If the given user is not assigned to this task
     * @throws UserNotFoundException            If the given user is not a user registered at the system
     */
    private void endTask(JSONObject task) throws InvalidTimeException, ProjectNotFoundException, EndTimeBeforeStartTimeException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectUserException, UserNotFoundException {
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

    /**
     * Parses the role field of the JSONObject
     *
     * @param   role  String to parse as a Role enum
     * @return  Role Enum corresponding to the given role
     * @throws RoleNotFoundException if the given String does not correspond to an existing role
     */
    private Role findRole(String role) throws RoleNotFoundException {
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
        throw new RoleNotFoundException();
    }

    private ArrayList<Tuple<String, String>> loadPreviousTaskTuple(ArrayList<ArrayList<String>> prev) throws InvalidFileException {
        ArrayList<Tuple<String, String>> result = new ArrayList<>();
        for(ArrayList<String> i : prev){
            if(i.size() < 2 ) throw new InvalidFileException("previousTask invalid format");
            else result.add(new Tuple<String, String>(i.get(0), i.get(1)));
        }
        return result;
    }


    public static class RoleNotFoundException extends Exception{
        public RoleNotFoundException(){super();}
    }



    @Override
    public String getName() {
        return "Load system";
    }

    @Override
    public String getDetails() {
        return "Load system at" + getPath();
    }

    @Override
    public Map<String, String> getArguments() {
        Map<String,String> arguments = new HashMap<>();
        arguments.put("path", getPath());
        return arguments;
    }

    @Override
    public List<String> getArgumentNames() {
        return new LinkedList<>(List.of("path"));
    }
}
