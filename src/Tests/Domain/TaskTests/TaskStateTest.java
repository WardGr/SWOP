package Tests.Domain.TaskTests;

import Domain.Task.Status;
import Domain.Task.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TaskStateTest {

    private UnavailableState unavailableState;
    private AvailableState availableState;
    private PendingState pendingState;
    private ExecutingState executingState;
    private FinishedState finishedState;
    private FailedState failedState;

    @Before
    public void setUp() {
        this.unavailableState = new UnavailableState();
        this.availableState = new AvailableState();
        this.pendingState = new PendingState();
        this.executingState = new ExecutingState();
        this.finishedState = new FinishedState();
        this.failedState = new FailedState();
    }

    @Test
    public void testToString(){
        assertEquals("unavailable", unavailableState.toString());
        assertEquals("available", availableState.toString());
        assertEquals("pending", pendingState.toString());
        assertEquals("executing", executingState.toString());
        assertEquals("finished", finishedState.toString());
        assertEquals("failed", failedState.toString());
    }

    @Test
    public void testGetStatus() {
        assertEquals(Status.UNAVAILABLE, unavailableState.getStatus());
        assertEquals(Status.AVAILABLE, availableState.getStatus());
        assertEquals(Status.PENDING, pendingState.getStatus());
        assertEquals(Status.EXECUTING, executingState.getStatus());
        assertEquals(Status.FINISHED, finishedState.getStatus());
        assertEquals(Status.FAILED, failedState.getStatus());
    }
}
