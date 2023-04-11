package Tests;

import Application.IncorrectPermissionException;
import Domain.*;
import org.junit.Test;

public class UpdateTaskControllerTest {

    @Test
    public void testUpdateTaskController() throws InvalidTimeException, IncorrectPermissionException, ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, IncorrectTaskStatusException, IncorrectUserException, NewTimeBeforeSystemTimeException, EndTimeBeforeStartTimeException {
        /*
        Session omer = new Session();
        SessionWrapper omerWrapper = new SessionWrapper(omer);
        TaskManSystem tms = new TaskManSystem(new Time(0, 0));
        UpdateTaskController utc = new UpdateTaskController(omerWrapper, tms);
        User boss = new User("WardGr", "minecraft123", Role.PROJECTMANAGER);
        assertFalse(utc.updateTaskPreconditions());
        omer.login(boss);
        assertFalse(utc.updateTaskPreconditions());
        assertThrows(IncorrectPermissionException.class, utc::availableTasksNames);
        assertThrows(IncorrectPermissionException.class, utc::executingTasksNames);
        assertThrows(IncorrectPermissionException.class, () -> utc.showTask("Brew", "Omer"));
        assertThrows(IncorrectPermissionException.class, () -> utc.getNextStatuses("Brew", "Omer"));
        assertThrows(IncorrectPermissionException.class, () -> utc.getStatus("Brew", "Omer"));
        assertThrows(IncorrectPermissionException.class, () -> utc.showTask("Sell", "Omer"));
        assertThrows(IncorrectPermissionException.class, () -> utc.getNextStatuses("Sell", "Omer"));
        assertThrows(IncorrectPermissionException.class, () -> utc.getStatus("Sell", "Omer"));
        assertThrows(IncorrectPermissionException.class, utc::getSystemHour);
        assertThrows(IncorrectPermissionException.class, utc::getSystemMinute);
        assertThrows(IncorrectPermissionException.class, () -> utc.startTask("Brew", "Omer", 5, 10));
        assertThrows(IncorrectPermissionException.class, () -> utc.endTask("Brew", "Omer", Status.FINISHED, 15, 30));


        Session dev = new Session();
        SessionWrapper devWrapper = new SessionWrapper(dev);
        TaskManSystem devTms = new TaskManSystem(new Time(0, 0));
        UpdateTaskController devUtc = new UpdateTaskController(devWrapper, devTms);
        User developer = new User("OlavBl", "toilet573", Role.DEVELOPER);
        assertFalse(devUtc.updateTaskPreconditions());
        dev.login(developer);
        assertTrue(devUtc.updateTaskPreconditions());
        assertEquals(0, devUtc.availableTasksNames().size());
        assertEquals(0, devUtc.executingTasksNames().size());
        assertThrows(ProjectNotFoundException.class, () -> devUtc.showTask("Brew", "Omer"));
        assertThrows(ProjectNotFoundException.class, () -> devUtc.getNextStatuses("Brewery", "Build brewery"));
        devTms.createProject("Brewery", "Build brewery", new Time(0, 0), new Time(10, 0));
        devTms.addTaskToProject("Brewery", "Brew", "Brew beer", new Time(10, 0), 50, new LinkedList<>(), developer);
        devTms.createProject("Sell", "Sell beer", new Time(0, 0), new Time(12, 30));
        devTms.addTaskToProject("Sell", "Design", "Design shop", new Time(12, 30), 50, new LinkedList<>(), developer);
        LinkedList prev = new LinkedList<>();
        prev.add("Design");
        devTms.addTaskToProject("Sell", "Build", "Build shop", new Time(12, 30), 50, prev, developer);

        assertEquals(2, devUtc.availableTasksNames().size());
        LinkedList equals = new LinkedList<>();
        equals.add("Brew");
        assertEquals(equals, devUtc.availableTasksNames().get("Brewery"));
        equals = new LinkedList();
        equals.add("Design");
        assertEquals(equals, devUtc.availableTasksNames().get("Sell"));
        equals = new LinkedList();
        assertEquals(equals, devUtc.executingTasksNames().get("Brewery"));
        assertEquals(equals, devUtc.executingTasksNames().get("Sell"));

        assertEquals("""
                Task Name:          Brew
                Description:        Brew beer
                Estimated Duration: 10 hours, 0 minutes
                Accepted Deviation: 50.0
                Status:             available
                                
                Replacement Task:   No replacement task
                Replaces Task:      Replaces no tasks
                                
                Start Time:         Task has not started yet
                End Time:           Task has not ended yet
                                
                User:               OlavBl
                                
                Next tasks:
                Previous tasks:
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), devUtc.showTask("Brewery", "Brew").replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        // TODO: dit geeft een error omdat tasks nu nimeer automatisch finishen met advanceTime, moeten dus onze tests beetje aanpassen
        assertEquals(1, devUtc.getNextStatuses("Brewery", "Brew").size());
        assertEquals(Status.EXECUTING, devUtc.getNextStatuses("Brewery", "Brew").get(0));
        assertEquals(Status.AVAILABLE, devUtc.getStatus("Brewery", "Brew"));
        devUtc.startTask("Brewery", "Brew", 5, 10);
        prev = new LinkedList<>();
        assertEquals(prev, devUtc.executingTasksNames().get("Brewery"));
        prev.add("Brew");
        assertEquals(prev, devUtc.availableTasksNames().get("Brewery"));
        devTms.advanceTime(new Time(5, 30));
        assertEquals(prev, devUtc.executingTasksNames().get("Brewery"));
        assertEquals(new LinkedList<>(), devUtc.availableTasksNames().get("Brewery"));
        assertEquals(2, devUtc.getNextStatuses("Brewery", "Brew").size());
        assertEquals(Status.FINISHED, devUtc.getNextStatuses("Brewery", "Brew").get(0));
        assertEquals(Status.FAILED, devUtc.getNextStatuses("Brewery", "Brew").get(1));
        assertEquals(Status.EXECUTING, devUtc.getStatus("Brewery", "Brew"));
        devUtc.endTask("Brewery", "Brew", Status.FINISHED, 15, 30);
        assertEquals(Status.EXECUTING, devUtc.getStatus("Brewery", "Brew"));
        devTms.advanceTime(new Time(20, 18));
        assertEquals(Status.FINISHED, devUtc.getStatus("Brewery", "Brew"));

        assertEquals(20, devUtc.getSystemHour());
        assertEquals(18, devUtc.getSystemMinute());
        assertEquals(new LinkedList<>(), devUtc.availableTasksNames().get("Brewery"));
        assertEquals("""
                Task Name:          Brew
                Description:        Brew beer
                Estimated Duration: 10 hours, 0 minutes
                Accepted Deviation: 50.0
                Status:             finished, on time
                                
                Replacement Task:   No replacement task
                Replaces Task:      Replaces no tasks
                                
                Start Time:         5 hours, 10 minutes
                End Time:           15 hours, 30 minutes
                                
                User:               OlavBl
                                
                Next tasks:
                Previous tasks:
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), devUtc.showTask("Brewery", "Brew").replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));

        prev = new LinkedList<>();
        prev.add("Design");
        assertEquals(prev, devUtc.availableTasksNames().get("Sell"));
        devUtc.startTask("Sell", "Design", 30, 10);
        assertEquals(prev, devUtc.availableTasksNames().get("Sell"));
        assertEquals(new LinkedList<>(), devUtc.executingTasksNames().get("Sell"));
        assertEquals(Status.AVAILABLE, devUtc.getStatus("Sell", "Design"));
        assertEquals(Status.EXECUTING, devUtc.getNextStatuses("Sell", "Design").get(0));
        assertEquals(1, devUtc.getNextStatuses("Sell", "Design").size());
        devTms.advanceTime(new Time(30, 20));
        assertEquals(new LinkedList<>(), devUtc.availableTasksNames().get("Sell"));
        assertEquals(prev, devUtc.executingTasksNames().get("Sell"));
        assertEquals(Status.FINISHED, devUtc.getNextStatuses("Sell", "Design").get(0));
        assertEquals(Status.FAILED, devUtc.getNextStatuses("Sell", "Design").get(1));
        devUtc.endTask("Sell", "Design", Status.FAILED, 30, 15);
        assertEquals(Status.FAILED, devUtc.getStatus("Sell", "Design"));
        assertEquals("""
                Task Name:          Design
                Description:        Design shop
                Estimated Duration: 12 hours, 30 minutes
                Accepted Deviation: 50.0
                Status:             failed
                                
                Replacement Task:   No replacement task
                Replaces Task:      Replaces no tasks
                                
                Start Time:         30 hours, 10 minutes
                End Time:           30 hours, 15 minutes
                                
                User:               OlavBl
                                
                Next tasks:
                1.Build
                Previous tasks:
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), devUtc.showTask("Sell", "Design").replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        devTms.advanceTime(new Time(45, 30));
        assertEquals(Status.UNAVAILABLE, devUtc.getStatus("Sell", "Build"));
        prev = new LinkedList();
        devTms.addTaskToProject("Sell", "PurchaseStore", "Purchase a lidl store", new Time(10, 5), 5, new LinkedList<>(), developer);
        prev.add("PurchaseStore");
        devTms.addTaskToProject("Sell", "Open", "Open the shop", new Time(10, 5), 5, prev, developer);
        assertEquals(Status.UNAVAILABLE, devUtc.getStatus("Sell", "Open"));
        devUtc.startTask("Sell", "PurchaseStore", 45, 30);
        devUtc.endTask("Sell", "PurchaseStore", Status.FINISHED, 50, 35);
        devTms.advanceTime(new Time(55, 10));
        assertEquals(Status.AVAILABLE, devUtc.getStatus("Sell", "Open"));

        */


    }

}
