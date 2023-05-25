package Tests;

import Application.EndTaskController;
import Application.Session;
import Application.SessionProxy;
import Domain.*;
import Domain.Command.Command;
import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.IllegalTaskRolesException;
import UserInterface.EndTaskUI;
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
        TaskManSystem tms = new TaskManSystem(new Time(0, 0));
        CommandManager commandManager = new CommandManager();
        EndTaskController ec = new EndTaskController(sw, tms, commandManager);
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
        tms.createProject("Duvel", "Duvel moortgat brewery", new Time(117));
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(Role.PYTHONPROGRAMMER);
        tms.addTaskToProject("Duvel", "Password", "find old passwords", new Time(112), 0.12, roles, new HashSet<>(), new HashSet<>());
        tms.startTask("Duvel", "Password", um.getUser("WardGr"), Role.PYTHONPROGRAMMER);
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
        tms.createProject("Omer", "Omer vandergisten brewery", new Time(117));
        tms.addTaskToProject("Omer", "Hiring", "hire new brewer", new Time(112), 0.12, roles, new HashSet<>(), new HashSet<>());
        tms.startTask("Omer", "Hiring", um.getUser("WardGr"), Role.PYTHONPROGRAMMER);
        etui.endTask();
        assertEquals("""
                You are currently working on task: Hiring, belonging to project: Omer
                                
                Do you want to finish or fail your current task? (finish/fail)
                                
                Successfully changed current executing task status to failed
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")),  out.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();
    }
}
