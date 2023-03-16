import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class TaskTest {
    public TaskTest() throws IncorrectUserException, IncorrectTaskStatusException {
        testTask();
    }
    @Test
    public void testTask() throws IncorrectUserException, IncorrectTaskStatusException {
        Time estimatedDuration1 = new Time(10);
        double deviation1 = 0.1;

        Time estimatedDuration2 = new Time(20);
        double deviation2 = 0.2;

        User user = new User("Ward", "123", Role.PROJECTMANAGER);

        Task task1 = new Task("Cool task", "Cool description", estimatedDuration1, deviation1, new LinkedList<>(), user);

        List<Task> previousTasks = new LinkedList<>();
        previousTasks.add(task1);

        Task task2 = new Task("Cooler task", "Cooler description", estimatedDuration2, deviation2, previousTasks, user);


        assertSame("Cool task", task1.getName());
        assertSame(Status.AVAILABLE, task1.getStatus());

        assertSame("Cooler task", task2.getName());
        assertSame(Status.UNAVAILABLE, task2.getStatus()); // Must be unavailable, because task1 hasnt been completed yet

        // Check if task2 is added to task1's nexttasks as a result of constructing task2
        List<Task> newNextTasks = new LinkedList<>();
        newNextTasks.add(task2);

        assertEquals(task1.getNextTasks(), newNextTasks);

        // Check next available statuses

        List<Status> nextStatusesAvailable = new LinkedList<>();
        nextStatusesAvailable.add(Status.EXECUTING);

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
        Task finalTask = task1;
        assertThrows(IncorrectUserException.class , () -> finalTask.start(startTime1, finalSystemTime1, wrongUser));
        assertThrows(IncorrectTaskStatusException.class, () -> task2.start(startTime1, finalSystemTime1, user));

        task1.start(startTime1, systemTime, user);
        assertEquals(task1.getStatus(), Status.EXECUTING);


        // Executing next statuses
        List<Status> nextStatusesExecuting = new LinkedList<>();
        nextStatusesExecuting.add(Status.FINISHED);
        nextStatusesExecuting.add(Status.FAILED);

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


        // TODO: replacement task for task 1.
        // Ending task 1 by failure


        task1 = new Task("Cool task", "Cool description", estimatedDuration1, deviation1, new LinkedList<>(), user);
        task1.start(startTime1, systemTime, user);
        Time incorrectEndTime = new Time(20);
        Time correctEndTime = new Time(5);
        systemTime = new Time(10);

        Time finalSystemTime = systemTime;
        Task finalTask1 = task1;
        assertThrows(IncorrectUserException.class, () -> finalTask1.end(Status.FAILED, correctEndTime, finalSystemTime, wrongUser));
        assertThrows(IncorrectTaskStatusException.class, () -> task2.end(Status.FAILED, correctEndTime, finalSystemTime, user));
        Task finalTask2 = task1;
        assertThrows(FailTimeAfterSystemTimeException.class, () -> finalTask2.end(Status.FAILED, incorrectEndTime, finalSystemTime, user));
        try {
            task1.end(Status.FAILED, correctEndTime, systemTime, user);
        } catch (FailTimeAfterSystemTimeException e) {
            System.out.println("This should not happen at all");
        }

        assertEquals(Status.FAILED, task1.getStatus());





    }
}
