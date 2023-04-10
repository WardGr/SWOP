package Tests;

import Domain.Status;
import Domain.TaskStates.AvailableState;
import Domain.TaskStates.Task;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class AvailableStateTest {
    @Mock
    private Task nextTask;

    @Mock
    private Task currentTask;

    @Mock
    private Task prevTask;

    private AvailableState state;

    @Before
    public void setUp() {
        this.state = new AvailableState();

    }

    @Test
    public void availableStateTest() {


        assertEquals(Status.AVAILABLE, state.getStatus());
        assertEquals("available", state.toString());


    }
}
