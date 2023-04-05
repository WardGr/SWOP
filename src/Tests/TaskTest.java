package Tests;

import Domain.*;
import Domain.TaskStates.Task;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class TaskTest {
    public TaskTest() throws IncorrectUserException, IncorrectTaskStatusException, FailTimeAfterSystemTimeException, InvalidTimeException, EndTimeBeforeStartTimeException, StartTimeBeforeAvailableException {
        testTask();
    }

    @Test
    public void testTask() throws IncorrectUserException, IncorrectTaskStatusException, FailTimeAfterSystemTimeException, InvalidTimeException, EndTimeBeforeStartTimeException, StartTimeBeforeAvailableException {
        Time estimatedDuration1 = new Time(10);
        double deviation1 = 0.1;

        Time estimatedDuration2 = new Time(20);
        double deviation2 = 0.2;

        User user = new User("Ward", "123", Role.PROJECTMANAGER);

        Domain.TaskStates.Task task1 = new Domain.TaskStates.Task("Cool task", "Cool description", estimatedDuration1, deviation1, new LinkedList<>(), user);

        List<Domain.TaskStates.Task> previousTasks = new LinkedList<>();
        previousTasks.add(task1);

        Domain.TaskStates.Task task2 = new Domain.TaskStates.Task("Cooler task", "Cooler description", estimatedDuration2, deviation2, previousTasks, user);


        assertSame("Cool task", task1.getName());
        assertSame(Status.AVAILABLE, task1.getStatus());

        assertSame("Cooler task", task2.getName());
        assertSame(Status.UNAVAILABLE, task2.getStatus()); // Must be unavailable, because task1 hasnt been completed yet

        // Check if task2 is added to task1's nexttasks as a result of constructing task2
        List<Domain.TaskStates.Task> newNextTasks = new LinkedList<>();
        newNextTasks.add(task2);

        assertEquals(task1.getNextTasks(), newNextTasks);

        // Check next available statuses

        List<Status> nextStatusesAvailable = new LinkedList<>();
        nextStatusesAvailable.add(Status.EXECUTING);
        nextStatusesAvailable.add(Status.PENDING);

        assertEquals(nextStatusesAvailable, task1.getNextStatuses());
        assertEquals(new LinkedList<>(), task2.getNextStatuses()); // Unavailable, cant change unless previous tasks completed!

        assertEquals(task1.toString(),
                """
                        Task Name:          Cool task
                        Description:        Cool description
                        Estimated Duration: 0 hours, 10 minutes
                        Accepted Deviation: 0.1
                        Status:             available

                        Replacement Task:   No replacement task
                        Replaces Task:      Replaces no tasks

                        Start Time:         Task has not started yet
                        End Time:           Task has not ended yet

                        User:               Ward
                                                
                        Next tasks:
                        1.Cooler task
                        Previous tasks:        
                        """);


        // Start task1
        Time systemTime = new Time(0);
        Time startTime1 = new Time(0);
        User wrongUser = new User("Olav", "321", Role.DEVELOPER);


        Time finalSystemTime1 = systemTime;
        Domain.TaskStates.Task finalTask = task1;
        assertThrows(IncorrectUserException.class, () -> finalTask.start(startTime1, finalSystemTime1, wrongUser));
        assertThrows(IncorrectTaskStatusException.class, () -> task2.start(startTime1, finalSystemTime1, user));

        task1.start(startTime1, systemTime, user);
        assertEquals(task1.getStatus(), Status.EXECUTING);


        // Executing next statuses
        List<Status> nextStatusesExecuting = new LinkedList<>();
        nextStatusesExecuting.add(Status.FAILED);
        nextStatusesExecuting.add(Status.FINISHED);

        assertEquals(nextStatusesExecuting, task1.getNextStatuses());

        // Ending task 1 by finishing
        Time timeBefore = new Time(10);
        Time finishSystemTime = new Time(15);

        try {
            task1.end(Status.FINISHED, timeBefore, finishSystemTime, user);
        } catch (FailTimeAfterSystemTimeException e) {
            System.out.println("This should not happen");
        }


        assertEquals(task2.getStatus(), Status.AVAILABLE);

        Domain.TaskStates.Task ffinalTask1 = new Domain.TaskStates.Task("Brew coffee", "Brew coffee for the team", new Time(10), 0.1, new LinkedList<>(), user);

        assertThrows(IncorrectTaskStatusException.class, () -> ffinalTask1.replaceTask("replacement", "Replacement of Task1", estimatedDuration1, deviation1));
        ffinalTask1.start(new Time(1300), new Time(1300), user);
        assertThrows(IncorrectTaskStatusException.class, () -> ffinalTask1.start(new Time(1300), new Time(1300), user));
        ffinalTask1.end(Status.FAILED, new Time(1500), new Time(2000), user);
        assertThrows(IncorrectTaskStatusException.class, () -> ffinalTask1.end(Status.FAILED, new Time(1500), new Time(2000), user));
        ffinalTask1.replaceTask("replacement", "Replacement of Task1", estimatedDuration1, deviation1);


        task1 = new Domain.TaskStates.Task("Cool task", "Cool description", estimatedDuration1, deviation1, new LinkedList<>(), user);
        task1.start(startTime1, systemTime, user);
        Time incorrectEndTime = new Time(20);
        Time correctEndTime = new Time(5);
        systemTime = new Time(10);

        Time finalSystemTime = systemTime;
        Domain.TaskStates.Task finalTask1 = task1;
        assertThrows(IncorrectUserException.class, () -> finalTask1.end(Status.FAILED, correctEndTime, finalSystemTime, wrongUser));
        assertThrows(IncorrectTaskStatusException.class, () -> task2.end(Status.FAILED, correctEndTime, finalSystemTime, user));
        Domain.TaskStates.Task finalTask2 = task1;
        assertThrows(FailTimeAfterSystemTimeException.class, () -> finalTask2.end(Status.FAILED, incorrectEndTime, finalSystemTime, user));
        try {
            task1.end(Status.FAILED, correctEndTime, systemTime, user);
        } catch (FailTimeAfterSystemTimeException e) {
            System.out.println("This should not happen at all");
        }

        assertEquals(Status.FAILED, task1.getStatus());

        Domain.TaskStates.Task task = new Domain.TaskStates.Task("Cool task", "Cool description", estimatedDuration1, deviation1, new LinkedList<>(), user);
        LinkedList prev = new LinkedList();
        prev.add(task);
        assertThrows(IncorrectUserException.class, () -> task.start(startTime1, new Time(30000), new User("Olav", "321", Role.DEVELOPER)));
        Domain.TaskStates.Task nextTask = new Domain.TaskStates.Task("Cooler task", "Cooler description", estimatedDuration2, deviation2, prev, user);
        assertThrows(IncorrectTaskStatusException.class, () -> nextTask.start(startTime1, new Time(30000), user));

        // Test delayed task
        Domain.TaskStates.Task delayedTask = new Domain.TaskStates.Task("Cooler task", "Cooler description", estimatedDuration2, deviation2, new LinkedList<>(), user);
        delayedTask.start(systemTime, systemTime, user);
        delayedTask.end(Status.FINISHED, new Time(10000), new Time(10000), user);
        assertEquals("""
                Task Name:          Cooler task
                Description:        Cooler description
                Estimated Duration: 0 hours, 20 minutes
                Accepted Deviation: 0.2
                Status:             finished, delayed
                                
                Replacement Task:   No replacement task
                Replaces Task:      Replaces no tasks
                                
                Start Time:         0 hours, 10 minutes
                End Time:           166 hours, 40 minutes
                                
                User:               Ward
                                
                Next tasks:
                Previous tasks:         
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), delayedTask.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));

        // Test early task

        Domain.TaskStates.Task earlyTask = new Domain.TaskStates.Task("Cooler task", "Cooler description", estimatedDuration2, deviation2, new LinkedList<>(), user);
        earlyTask.start(systemTime, systemTime, user);
        earlyTask.end(Status.FINISHED, new Time(10), new Time(10000), user);
        assertEquals("""
                Task Name:          Cooler task
                Description:        Cooler description
                Estimated Duration: 0 hours, 20 minutes
                Accepted Deviation: 0.2
                Status:             finished, early
                                
                Replacement Task:   No replacement task
                Replaces Task:      Replaces no tasks
                                
                Start Time:         0 hours, 10 minutes
                End Time:           0 hours, 10 minutes
                                
                User:               Ward
                                
                Next tasks:
                Previous tasks:         
                """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")), earlyTask.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));


        Domain.TaskStates.Task toFinish = new Domain.TaskStates.Task("toFinish", "Task will finish due to time increasing", new Time(10, 0), 0.1, new LinkedList<>(), user);
        LinkedList<Domain.TaskStates.Task> prevTasks = new LinkedList<>();
        prevTasks.add(toFinish);
        Domain.TaskStates.Task toStart = new Domain.TaskStates.Task("toStart", "Task will start due to time increasing", new Time(10, 0), 0.1, prevTasks, user);
        assertEquals(Status.UNAVAILABLE, toStart.getStatus());
        toFinish.start(new Time(0, 0), new Time(0, 0), user);
        toFinish.end(Status.FINISHED, new Time(1, 0), new Time(10, 0), user);
        assertEquals(Status.FINISHED, toFinish.getStatus());

        Domain.TaskStates.Task lastTask = new Domain.TaskStates.Task("Final Task", "Task will start due to time increasing", new Time(10, 0), 0.1, prevTasks, user);
        assertEquals(Status.AVAILABLE, lastTask.getStatus());
        lastTask.start(new Time(10, 0), new Time(10, 0), user);
        assertEquals(Status.EXECUTING, lastTask.getStatus());

        // Test Exceptions: end time before start time and start time before available
        User testUser = new User("Olav", "321", Role.DEVELOPER);
        Domain.TaskStates.Task firstTask = new Domain.TaskStates.Task("firstTask", "test", estimatedDuration1, deviation1, new LinkedList<>(), testUser);
        previousTasks = new LinkedList<>();
        previousTasks.add(firstTask);
        Domain.TaskStates.Task secondTask = new Task("secondTask", "test", estimatedDuration1, deviation1, previousTasks, testUser);
        systemTime = new Time(60);
        firstTask.start(new Time(20), systemTime, testUser);
        assertThrows(EndTimeBeforeStartTimeException.class, () -> firstTask.end(Status.FAILED, new Time(10), new Time(60), testUser));
        assertThrows(EndTimeBeforeStartTimeException.class, () -> firstTask.end(Status.FINISHED, new Time(10), new Time(60), testUser));

        firstTask.end(Status.FINISHED, new Time(40), systemTime, testUser);
        assertThrows(StartTimeBeforeAvailableException.class, () -> secondTask.start(new Time(30), new Time(60), testUser));

    }
}