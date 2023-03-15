import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class LoadSystemController {
    private UserManager userManager;
    private TaskManSystem taskManSystem;
    private Session session;

    private LoadSystemUI loadSystemUI;

    LoadSystemController( UserManager userManager, TaskManSystem taskManSystem, Session session, LoadSystemUI loadSystemUI){
        this.userManager = userManager;
        this.taskManSystem = taskManSystem;
        this.session = session;
        this.loadSystemUI = loadSystemUI;
    }
    public void loadSystemForm(){
        if (session.getRole() != Role.PROJECTMANAGER) {
            loadSystemUI.printAccessError(Role.PROJECTMANAGER);
            return;
        }
        loadSystemUI.loadSystemForm();
    }
    public void LoadSystem(String filepath){
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(filepath)){
            JSONObject doc = (JSONObject) jsonParser.parse(reader);
            //set system time
            int systemHour = (int) (long) doc.get("systemHour");
            int systemMinute = (int) (long) doc.get("systemMinute");
            taskManSystem.advanceTime(systemHour, systemMinute);
            //load projects
            JSONArray projects = (JSONArray) doc.get("projects");
            for(Object p : projects){
                loadProject((JSONObject) p, userManager, taskManSystem);
            }

        }catch (FileNotFoundException e){
            return; // nog correct afhandelen
        }catch (IOException e){
            return; // nog correct afhandelen
        }catch (ParseException e) {
            return; // nog correct afhandelen
        } catch (NewTimeBeforeSystemTimeException e) {
            throw new RuntimeException(e);
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        } catch (ProjectNameAlreadyInUseException e) {
            throw new RuntimeException(e);
        } catch (ReplacedTaskNotFailedException e) {
            throw new RuntimeException(e);
        } catch (FailTimeAfterSystemTimeException e) {
            throw new RuntimeException(e);
        } catch (ProjectNotFoundException e) {
            throw new RuntimeException(e);
        } catch (UserNotAllowedToChangeTaskException e) {
            throw new RuntimeException(e);
        } catch (TaskNotFoundException e) {
            throw new RuntimeException(e);
        } catch (TaskNameAlreadyInUseException e) {
            throw new RuntimeException(e);
        } catch (DueBeforeSystemTimeException e) {
            throw new RuntimeException(e);
        } catch (InvalidTimeException e) {
            throw new RuntimeException(e);
        } catch (IncorrectTaskStatusException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadProject(JSONObject project, UserManager userManager, TaskManSystem taskManSystem) throws UserNotFoundException, ReplacedTaskNotFailedException, DueBeforeSystemTimeException, ProjectNameAlreadyInUseException, ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, FailTimeAfterSystemTimeException, UserNotAllowedToChangeTaskException, InvalidTimeException, IncorrectTaskStatusException {
        //create the project
        String name = (String) project.get("name");
        String description = (String) project.get("description");
        int startHour = (int) (long) project.get("startHour");
        int startMinute = (int) (long) project.get("startMinute");
        int endHour = (int) (long) project.get("endHour");
        int endMinute = (int) (long) project.get("endMinute");
        taskManSystem.createProject(name, description, startHour, startMinute, endHour, endMinute);
        //load the tasks
        JSONArray tasks = (JSONArray) project.get("tasks");
        for(Object t : tasks){
            loadTask((JSONObject) t, userManager, taskManSystem, name);
        }

    }

    private void loadTask(JSONObject task, UserManager userManager, TaskManSystem taskManSystem, String projectName) throws UserNotFoundException, ReplacedTaskNotFailedException, ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, UserNotAllowedToChangeTaskException, FailTimeAfterSystemTimeException, InvalidTimeException, IncorrectTaskStatusException {
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
            taskManSystem.addAlternativeTaskToProject(projectName, name, description, new Time(dueHour,dueMinute), acceptableDeviation, replacesTask);
        }else {
            List<String> prevTasks = (List<String>) task.get("previousTasks");
            taskManSystem.addTaskToProject(projectName, name, description, new Time(dueHour,dueMinute), acceptableDeviation, prevTasks, user);
        }

        //handling the status
        String status = (String) task.get("status");
        if(status.equals("EXECUTING")){
            int startHour = (int) (long) task.get("startHour");
            int startMinute = (int) (long) task.get("startMinute");
            taskManSystem.startTask(projectName, name,startHour, startMinute, user);
        } else if (status.equals("FINISHED")) {
            int startHour = (int) (long) task.get("startHour");
            int startMinute = (int) (long) task.get("startMinute");
            int endHour = (int) (long) task.get("endHour");
            int endMinute = (int) (long) task.get("endMinute");
            taskManSystem.startTask(projectName, name, startHour, startMinute, user);
            taskManSystem.endTask(projectName, name, Status.FINISHED, endHour, endMinute, user);
        }else if (status.equals("FAILED")) {
            int startHour = (int) (long) task.get("startHour");
            int startMinute = (int) (long) task.get("startMinute");
            int endHour = (int) (long) task.get("endHour");
            int endMinute = (int) (long) task.get("endMinute");
            taskManSystem.startTask(projectName, name, startHour, startMinute, user);
            taskManSystem.endTask(projectName, name, Status.FAILED,endHour, endMinute, user);
        }
    }
}
