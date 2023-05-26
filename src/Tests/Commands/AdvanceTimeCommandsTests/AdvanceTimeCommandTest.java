package Tests.Commands.AdvanceTimeCommandsTests;

import Application.Command.AdvanceTimeCommands.AdvanceTimeCommand;
import Application.Command.UndoNotPossibleException;
import Domain.DataClasses.InvalidTimeException;
import Domain.DataClasses.Time;
import Domain.TaskManSystem.DueBeforeSystemTimeException;
import Domain.TaskManSystem.NewTimeBeforeSystemTimeException;
import Domain.TaskManSystem.TaskManSystem;
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

