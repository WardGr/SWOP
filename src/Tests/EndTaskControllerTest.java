package Tests;

import Application.EndTaskController;
import Application.IncorrectPermissionException;
import Application.Session;
import Application.SessionWrapper;
import Domain.*;
import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.NonDeveloperRoleException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class EndTaskControllerTest {
    @Test
    public void test() throws ProjectNotFoundException, TaskNotFoundException, InvalidTimeException, ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, TaskNameAlreadyInUseException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException, NonDeveloperRoleException, UserAlreadyAssignedToTaskException, IncorrectRoleException, NewTimeBeforeSystemTimeException, EndTimeBeforeStartTimeException, IncorrectPermissionException, IncorrectUserException {
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
        tms.addTaskToProject("Omer", "Buy ingredients", "Get ingredients for the beer", new Time(2), .3, roles, new HashSet<>(), new HashSet<>());


        SessionWrapper wrapper = new SessionWrapper(current);

        EndTaskController etc = new EndTaskController(wrapper, tms);
        assertEquals(Status.AVAILABLE, tms.getTaskData("Omer", "Hire brewer").getStatus());
        assertTrue(etc.endTaskPreconditions());
        tms.startTask("Omer", "Hire brewer", java, Role.JAVAPROGRAMMER);
        assertEquals(Status.PENDING, tms.getTaskData("Omer", "Hire brewer").getStatus());
        tms.startTask("Omer", "Hire brewer", python, Role.PYTHONPROGRAMMER);
        assertEquals(Status.EXECUTING, tms.getTaskData("Omer", "Hire brewer").getStatus());

        tms.advanceTime(10);
        etc.finishCurrentTask();
        assertEquals(Status.FINISHED, tms.getTaskData("Omer", "Hire brewer").getStatus());

        assertEquals(Status.AVAILABLE, tms.getTaskData("Omer", "Buy ingredients").getStatus());
        tms.startTask("Omer", "Buy ingredients", java, Role.JAVAPROGRAMMER);
        assertEquals(Status.PENDING, tms.getTaskData("Omer", "Buy ingredients").getStatus());
        tms.startTask("Omer", "Buy ingredients", python, Role.PYTHONPROGRAMMER);
        assertEquals(Status.EXECUTING, tms.getTaskData("Omer", "Buy ingredients").getStatus());
        current.logout();
        current.login(man);
        assertThrows(IncorrectPermissionException.class, () -> etc.finishCurrentTask());
        current.logout();
        current.login(java);
        etc.failCurrentTask();
        assertEquals(Status.FAILED, tms.getTaskData("Omer", "Buy ingredients").getStatus());
        assertThrows(IncorrectPermissionException.class, etc::finishCurrentTask);
        assertThrows(IncorrectPermissionException.class, etc::failCurrentTask);

        Set prev = new HashSet();
        Set next = new HashSet();
        tms.addTaskToProject("Omer", "Make beer", "Make the beer", new Time(10), .3, roles, new HashSet<>(), new HashSet<>());
        tms.addTaskToProject("Omer", "Sell beer", "Sell the beer", new Time(10), .3, roles, new HashSet<>(), new HashSet<>());
        assertEquals(Status.AVAILABLE, tms.getTaskData("Omer", "Make beer").getStatus());
        assertEquals(Status.AVAILABLE, tms.getTaskData("Omer", "Sell beer").getStatus());
        prev.add("Make beer");
        next.add("Sell beer");
        tms.addTaskToProject("Omer", "Clean up", "Clean up the brewery", new Time(10), .3, roles, prev, next);
        assertEquals(Status.UNAVAILABLE, tms.getTaskData("Omer", "Clean up").getStatus());
        assertEquals(Status.AVAILABLE, tms.getTaskData("Omer", "Make beer").getStatus());
        assertEquals(Status.UNAVAILABLE, tms.getTaskData("Omer", "Sell beer").getStatus());

        tms.startTask("Omer", "Make beer", java, Role.JAVAPROGRAMMER);
        tms.startTask("Omer", "Make beer", python, Role.PYTHONPROGRAMMER);

        tms.advanceTime(10);
        etc.finishCurrentTask();
        assertEquals(Status.FINISHED, tms.getTaskData("Omer", "Make beer").getStatus());
        assertEquals(Status.AVAILABLE, tms.getTaskData("Omer", "Clean up").getStatus());
        assertThrows(IncorrectPermissionException.class, etc::finishCurrentTask);
        assertThrows(IncorrectPermissionException.class, etc::failCurrentTask);

        tms.startTask("Omer", "Clean up", java, Role.JAVAPROGRAMMER);
        assertEquals(Status.PENDING, tms.getTaskData("Omer", "Clean up").getStatus());
        tms.startTask("Omer", "Clean up", python, Role.PYTHONPROGRAMMER);
        tms.advanceTime(1);
        current.logout();
        current.login(man);
        assertThrows(IncorrectPermissionException.class, () -> etc.failCurrentTask());
        current.logout();
        current.login(java);
        etc.failCurrentTask();
        assertThrows(IncorrectPermissionException.class, etc::finishCurrentTask);
        assertThrows(IncorrectPermissionException.class, etc::failCurrentTask);

        assertEquals(Status.FAILED, tms.getTaskData("Omer", "Clean up").getStatus());
        assertEquals(Status.UNAVAILABLE, tms.getTaskData("Omer", "Sell beer").getStatus());

        tms.replaceTaskInProject("Omer", "Hire cleanup", "Hire someone to do the cleanup", new Time(2), .4, "Clean up");
        assertEquals(Status.AVAILABLE, tms.getTaskData("Omer", "Hire cleanup").getStatus());
        tms.startTask("Omer", "Hire cleanup", java, Role.JAVAPROGRAMMER);
        assertEquals(Status.PENDING, tms.getTaskData("Omer", "Hire cleanup").getStatus());
        assertEquals(Status.FAILED, tms.getTaskData("Omer", "Clean up").getStatus());
        assertEquals(Status.UNAVAILABLE, tms.getTaskData("Omer", "Sell beer").getStatus());
        tms.startTask("Omer", "Hire cleanup", python, Role.PYTHONPROGRAMMER);
        tms.advanceTime(2);
        current.logout();
        current.login(man);
        assertThrows(IncorrectPermissionException.class, () -> etc.finishCurrentTask());
        current.logout();
        current.login(java);
        etc.finishCurrentTask();
        assertEquals(Status.FINISHED, tms.getTaskData("Omer", "Hire cleanup").getStatus());
        assertEquals(Status.FAILED, tms.getTaskData("Omer", "Clean up").getStatus());
        assertEquals(Status.AVAILABLE, tms.getTaskData("Omer", "Sell beer").getStatus());




        current.logout();
        current.login(python);
        assertTrue(etc.endTaskPreconditions());

        current.logout();
        current.login(sysadmin);
        assertTrue(etc.endTaskPreconditions());
        assertEquals(etc.getTaskData("Omer", "Hire brewer").getStatus(), Status.FINISHED);
        assertEquals(etc.getTaskData("Omer", "Buy ingredients").getStatus(), Status.FAILED);
        assertEquals(etc.getTaskData("Omer", "Make beer").getStatus(), Status.FINISHED);
        assertEquals(etc.getTaskData("Omer", "Clean up").getStatus(), Status.FAILED);
        assertEquals(etc.getTaskData("Omer", "Sell beer").getStatus(), Status.AVAILABLE);
        assertEquals(etc.getTaskData("Omer", "Hire cleanup").getStatus(), Status.FINISHED);
        assertEquals(etc.getTaskData("Omer", "Hire brewer").getEndTime(), new Time(10));
        assertEquals(etc.getTaskData("Omer", "Buy ingredients").getEndTime(), new Time(10));
        assertEquals(etc.getTaskData("Omer", "Make beer").getEndTime(), new Time(20));
        assertEquals(etc.getTaskData("Omer", "Clean up").getEndTime(), new Time(21));
        assertNull(etc.getTaskData("Omer", "Sell beer").getEndTime());

        assertEquals(ProjectStatus.ONGOING, etc.getProjectData("Omer").getStatus());
        assertEquals(new Time(0), etc.getProjectData("Omer").getCreationTime());
        assertEquals(new Time(2000), etc.getProjectData("Omer").getDueTime());
        assertEquals("Project for the Omer brewery", etc.getProjectData("Omer").getDescription());

        tms.createProject("Test", "just a test", new Time(30));
        assertEquals(new Time(23), etc.getProjectData("Test").getCreationTime());
        assertEquals("just a test", etc.getProjectData("Test").getDescription());


        assertEquals(new Time(23), etc.getTaskManSystemData().getSystemTime());
        assertEquals(2, etc.getTaskManSystemData().getProjectNames().size());

        current.logout();
        current.login(man);
        assertFalse(etc.endTaskPreconditions());
        assertThrows(IncorrectPermissionException.class, etc::getTaskManSystemData);
        assertThrows(IncorrectPermissionException.class, () -> etc.getProjectData("Omer"));
        assertThrows(IncorrectPermissionException.class, () -> etc.getTaskData("Omer", "Hire brewer"));
        assertThrows(IncorrectPermissionException.class, () -> etc.getTaskData("Omer", "Buy ingredients"));
        assertThrows(IncorrectPermissionException.class, () -> etc.getTaskData("Omer", "Make beer"));
        assertThrows(IncorrectPermissionException.class, () -> etc.getProjectData("Test"));

        assertEquals(1, etc.getUserRoles().size());
        Set twoRolesSet = new HashSet();
        twoRolesSet.add(Role.PYTHONPROGRAMMER);
        twoRolesSet.add(Role.JAVAPROGRAMMER);
        User twoRoles = new User("2Roles", "2Roles", twoRolesSet);
        current.logout();
        current.login(twoRoles);
        assertEquals(2, etc.getUserRoles().size());

        current.logout();
        assertEquals(0, etc.getUserRoles().size());
    }
}
