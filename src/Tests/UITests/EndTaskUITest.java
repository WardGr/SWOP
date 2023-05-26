package Tests.UITests;

import Application.TaskControllers.EndTaskController;
import Application.Session.Session;
import Application.Session.SessionProxy;
import Domain.Command.CommandManager;
import Domain.DataClasses.InvalidTimeException;
import Domain.DataClasses.Time;
import Domain.Project.ProjectNameAlreadyInUseException;
import Domain.Project.ProjectNotOngoingException;
import Domain.Project.TaskNotFoundException;
import Domain.Task.*;
import Domain.Task.IncorrectTaskStatusException;
import Domain.Task.LoopDependencyGraphException;
import Domain.TaskManSystem.DueBeforeSystemTimeException;
import Domain.TaskManSystem.ProjectNotFoundException;
import Domain.TaskManSystem.TaskManSystem;
import Domain.User.Role;
import Domain.User.UserAlreadyAssignedToTaskException;
import Domain.User.UserManager;
import Domain.User.UserNotFoundException;
import UserInterface.TaskUIs.EndTaskUI;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class EndTaskUITest {
    @Test
    public void test() throws InvalidTimeException, UserNotFoundException, ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNameAlreadyInUseException, TaskNotFoundException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException, IllegalTaskRolesException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        //create EndTaskUi
        Session s = new Session();
        SessionProxy sw = new SessionProxy(s);
        TaskManSystem taskManSystem = new TaskManSystem(new Time(0, 0));
        CommandManager commandManager = new CommandManager();
        EndTaskController ec = new EndTaskController(sw, taskManSystem, commandManager);
        EndTaskUI etui = new EndTaskUI(ec);
        UserManager um = new UserManager();

        //inloggen met PROJECTMANAGER
        s.login(um.getUser("DieterVH"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        etui.endTask();
        assertEquals("ERROR: You need a developer role to call this function.\n".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),  out.toString());
        out.reset();
        s.logout();

        //inloggen als developer maar zonder executing task
        s.login(um.getUser("WardGr"));
        System.setOut(new PrintStream(out));
        etui.endTask();
        assertEquals("ERROR: You are currently not working on an executing task.\n".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),  out.toString());
        out.reset();

        //Back ingeven
        System.setOut(new PrintStream(out));
        taskManSystem.createProject("Duvel", "Duvel moortgat brewery", new Time(117));
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(Role.PYTHONPROGRAMMER);
        taskManSystem.addTaskToProject("Duvel", "Password", "find old passwords", new Time(112), 0.12, roles, new HashSet<>(), new HashSet<>());
        taskManSystem.startTask("Duvel", "Password", um.getUser("WardGr"), Role.PYTHONPROGRAMMER);
        System.setIn(new ByteArrayInputStream("BACK\n".getBytes()));
        etui.endTask();
        assertEquals("""
                        You are currently working on task: Password, belonging to project: Duvel
                                                
                        Do you want to finish or fail your current task? (finish/fail)
                        Cancelled ending task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),  out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        //foute input gevolgd door BACK
        System.setOut(new PrintStream(out));
        System.setIn(new ByteArrayInputStream("BCK\nBACK\n".getBytes()));
        etui.endTask();
        assertEquals("""
                You are currently working on task: Password, belonging to project: Duvel
                                       
                Do you want to finish or fail your current task? (finish/fail)
                Do you want to finish or fail your current task? (finish/fail)
                Cancelled ending task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),  out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        //finish task
        System.setOut(new PrintStream(out));
        System.setIn(new ByteArrayInputStream("finish\n".getBytes()));
        etui.endTask();
        assertEquals("""
                You are currently working on task: Password, belonging to project: Duvel
                                
                Do you want to finish or fail your current task? (finish/fail)
                                
                Successfully finished the current executing task
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),  out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        //finish task
        System.setOut(new PrintStream(out));
        System.setIn(new ByteArrayInputStream("fail\n".getBytes()));
        taskManSystem.createProject("Omer", "Omer vandergisten brewery", new Time(117));
        taskManSystem.addTaskToProject("Omer", "Hiring", "hire new brewer", new Time(112), 0.12, roles, new HashSet<>(), new HashSet<>());
        taskManSystem.startTask("Omer", "Hiring", um.getUser("WardGr"), Role.PYTHONPROGRAMMER);
        etui.endTask();
        assertEquals("""
                You are currently working on task: Hiring, belonging to project: Omer
                                
                Do you want to finish or fail your current task? (finish/fail)
                                
                Successfully changed current executing task status to failed
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),  out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }
}
