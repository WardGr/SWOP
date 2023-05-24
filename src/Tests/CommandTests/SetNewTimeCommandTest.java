package Tests.CommandTests;

import Domain.*;
import Domain.Command.SetNewTimeCommand;
import Domain.Command.UndoNotPossibleException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class SetNewTimeCommandTest {
    TaskManSystem taskManSystem;

    @Before
    public void setUp() throws InvalidTimeException, DueBeforeSystemTimeException {
        taskManSystem = new TaskManSystem(new Time(10));
    }

    @Test
    public void testCreationCommand() throws InvalidTimeException {
        SetNewTimeCommand command = new SetNewTimeCommand(taskManSystem, new Time(80));

        assertFalse(command.undoPossible());
        assertEquals("Set new time", command.getName());
        assertEquals("Set new time 1 hour(s), 20 minute(s)", command.getDetails());
        assertEquals(List.of("newTime"), command.getArgumentNames());
        assertEquals("1 hour(s), 20 minute(s)", command.getArguments().get("newTime"));
    }

    @Test
    public void testExecuteAndUndo() throws InvalidTimeException, NewTimeBeforeSystemTimeException {
        SetNewTimeCommand command = new SetNewTimeCommand(taskManSystem, new Time(80));

        assertEquals(new Time(10), taskManSystem.getSystemTime());

        command.execute();

        assertEquals(new Time(80), taskManSystem.getSystemTime());

        assertThrows(UndoNotPossibleException.class, command::undo);
    }
}
