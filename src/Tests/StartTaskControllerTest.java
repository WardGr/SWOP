package Tests;

import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.NonDeveloperRoleException;
import org.junit.Test;
import Domain.*;
import Application.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class StartTaskControllerTest {
    @Test
    public void test() throws InvalidTimeException, ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNameAlreadyInUseException, TaskNotFoundException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException, NonDeveloperRoleException, IncorrectPermissionException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        Set<Role> javaRole = new HashSet<>();
        javaRole.add(Role.JAVAPROGRAMMER);
        Set<Role> pythonRole = new HashSet<>();
        pythonRole.add(Role.PYTHONPROGRAMMER);
        Set<Role> sysadminRole = new HashSet<>();
        sysadminRole.add(Role.SYSADMIN);
        Set<Role> projectmanRole = new HashSet<>();
        projectmanRole.add(Role.PROJECTMANAGER);
        User java = new User("Java", "java", javaRole);
        User python = new User("Python", "python", pythonRole);
        User sysadmin = new User("Sys", "sys", sysadminRole);
        User man = new User("Pm", "pm", projectmanRole);

        Session current = new Session();
        current.login(java);
        TaskManSystem tms = new TaskManSystem(new Time(0));

        tms.createProject("Omer", "Project for the Omer brewery", new Time(2000));
        List roles = new ArrayList<>();
        roles.add(Role.JAVAPROGRAMMER);
        roles.add(Role.PYTHONPROGRAMMER);
        tms.addTaskToProject("Omer", "Hire brewer", "Get suitable brewer", new Time(10), .3, roles, new HashSet<>(), new HashSet<>());

        SessionWrapper wrapper = new SessionWrapper(current);

        assertEquals(Status.AVAILABLE, tms.getStatus("Omer", "Hire brewer"));

        StartTaskController stc = new StartTaskController(wrapper, tms);
        assertTrue(stc.startTaskPreconditions());
        assertTrue(stc.getUserRoles().contains(Role.JAVAPROGRAMMER));
        assertEquals(1, stc.getUserRoles().size());

        stc.startTask("Omer", "Hire brewer", Role.JAVAPROGRAMMER);
        assertEquals(Status.PENDING, tms.getStatus("Omer", "Hire brewer"));


        current.logout();
        current.login(python);
        assertTrue(stc.startTaskPreconditions());
        assertTrue(stc.getUserRoles().contains(Role.PYTHONPROGRAMMER));
        assertEquals(1, stc.getUserRoles().size());

        stc.startTask("Omer", "Hire brewer", Role.PYTHONPROGRAMMER);
        assertEquals(Status.EXECUTING, tms.getStatus("Omer", "Hire brewer"));



        current.logout();
        current.login(sysadmin);
        assertTrue(stc.startTaskPreconditions());
        assertTrue(stc.getUserRoles().contains(Role.SYSADMIN));
        assertEquals(1, stc.getUserRoles().size());

        current.logout();
        current.login(man);
        assertTrue(stc.getUserRoles().contains(Role.PROJECTMANAGER));
        assertEquals(1, stc.getUserRoles().size());
        assertFalse(stc.startTaskPreconditions());
        assertThrows(IncorrectPermissionException.class, () -> {
           stc.startTask("Omer", "Brew Beer", Role.PROJECTMANAGER);
        });


    }
}
