package Tests;

import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.IllegalTaskRolesException;
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
    public void test() throws InvalidTimeException, ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNameAlreadyInUseException, TaskNotFoundException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException, IllegalTaskRolesException, IncorrectPermissionException, UserAlreadyAssignedToTaskException, IncorrectRoleException, NewTimeBeforeSystemTimeException {
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

        assertEquals(Status.AVAILABLE, tms.getTaskData("Omer", "Hire brewer").getStatus());

        StartTaskController stc = new StartTaskController(wrapper, tms);
        assertTrue(stc.startTaskPreconditions());
        assertTrue(stc.getUserRoles().contains(Role.JAVAPROGRAMMER));
        assertEquals(1, stc.getUserRoles().size());

        stc.startTask("Omer", "Hire brewer", Role.JAVAPROGRAMMER);
        assertEquals("Hire brewer", stc.getUserTaskData().getName());

        assertEquals(Status.PENDING, tms.getTaskData("Omer", "Hire brewer").getStatus());


        assertEquals("Omer", stc.getProjectData("Omer").getName());
        assertEquals(new Time(0), stc.getProjectData("Omer").getCreationTime());
        assertEquals(new Time(2000), stc.getProjectData("Omer").getDueTime());
        current.logout();
        current.login(python);
        assertTrue(stc.startTaskPreconditions());
        assertTrue(stc.getUserRoles().contains(Role.PYTHONPROGRAMMER));
        assertEquals(1, stc.getUserRoles().size());
        stc.startTask("Omer", "Hire brewer", Role.PYTHONPROGRAMMER);
        assertEquals("Hire brewer", stc.getUserTaskData().getName());

        assertEquals(Status.EXECUTING, tms.getTaskData("Omer", "Hire brewer").getStatus());
        assertEquals(new Time(0), stc.getTaskManSystemData().getSystemTime());

        tms.advanceTime(32);
        assertEquals(new Time(32), stc.getTaskManSystemData().getSystemTime());

        assertEquals(1, stc.getTaskManSystemData().getProjectNames().size());
        assertTrue(stc.getTaskManSystemData().getProjectNames().contains("Omer"));

        tms.createProject("Test", "Brew Beer", new Time(2030));
        assertTrue(stc.getTaskManSystemData().getProjectNames().contains("Test"));
        assertEquals(2, stc.getTaskManSystemData().getProjectNames().size());
        assertEquals(new Time(32), stc.getProjectData("Test").getCreationTime());
        assertEquals(new Time(2030), stc.getProjectData("Test").getDueTime());


        current.logout();
        current.login(sysadmin);
        assertTrue(stc.startTaskPreconditions());
        assertTrue(stc.getUserRoles().contains(Role.SYSADMIN));
        assertEquals(1, stc.getUserRoles().size());
        assertEquals("Hire brewer", stc.getTaskData("Omer", "Hire brewer").getName());
        assertEquals(2, stc.getTaskData("Omer", "Hire brewer").getUserNamesWithRole().size());
        assertTrue(stc.getTaskData("Omer", "Hire brewer").getUserNamesWithRole().containsKey("Java"));
        assertTrue(stc.getTaskData("Omer", "Hire brewer").getUserNamesWithRole().containsKey("Python"));
        current.logout();
        current.login(man);
        assertTrue(stc.getUserRoles().contains(Role.PROJECTMANAGER));
        assertEquals(1, stc.getUserRoles().size());
        assertFalse(stc.startTaskPreconditions());
        assertThrows(IncorrectPermissionException.class, () -> {
           stc.startTask("Omer", "Brew Beer", Role.PROJECTMANAGER);
        });
        assertThrows(IncorrectPermissionException.class, () -> {
            stc.getProjectData("Omer");
        });
        assertThrows(IncorrectPermissionException.class, () -> {
            stc.getProjectData("Test");
        });
        assertThrows(IncorrectPermissionException.class, () -> {
            stc.getTaskManSystemData();
        });
        assertThrows(IncorrectPermissionException.class, () -> {
            stc.getTaskData("Omer", "Brew Beer");
        });
        assertThrows(IncorrectPermissionException.class, () -> {
            stc.getProjectData("Omer");
        });
        assertThrows(IncorrectPermissionException.class, () -> {
            stc.getProjectData("Test");
        });


        current.logout();
        assertThrows(IncorrectPermissionException.class, () -> {
            stc.getProjectData("Omer");
        });
        assertThrows(IncorrectPermissionException.class, () -> {
            stc.getProjectData("Test");
        });
        assertThrows(IncorrectPermissionException.class, () -> {
            stc.getTaskManSystemData();
        });
        assertThrows(IncorrectPermissionException.class, () -> {
            stc.getTaskData("Omer", "Brew Beer");
        });
        assertThrows(IncorrectPermissionException.class, () -> {
            stc.getProjectData("Omer");
        });
        assertThrows(IncorrectPermissionException.class, () -> {
            stc.getProjectData("Test");
        });

    }
}
