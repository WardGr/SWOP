package Tests.Commands.AdvanceTimeCommandsTests;

import Domain.*;
import Domain.Command.AdvanceTimeCommand;
import Domain.Command.UndoNotPossibleException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class AdvanceTimeCommandTest {
    TaskManSystem taskManSystem;

    @Before
    public void setUp() throws InvalidTimeException, DueBeforeSystemTimeException {
        taskManSystem = new TaskManSystem(new Time(10));
    }

    @Test
    public void testCreationCommand(){
        AdvanceTimeCommand command = new AdvanceTimeCommand(taskManSystem, 80);

        assertFalse(command.undoPossible());
        assertEquals("Advance time", command.getName());
        assertEquals("Advance time with 80 minutes", command.getDetails());
        assertEquals(List.of("minutes"), command.getArgumentNames());
        assertEquals("80", command.getArguments().get("minutes"));
    }

    @Test
    public void testExecuteAndUndo() throws InvalidTimeException, NewTimeBeforeSystemTimeException {
        AdvanceTimeCommand command = new AdvanceTimeCommand(taskManSystem, 80);

        assertEquals(new Time(10), taskManSystem.getSystemTime());

        command.execute();

        assertEquals(new Time(90), taskManSystem.getSystemTime());

        assertThrows(UndoNotPossibleException.class, command::undo);
    }
}

