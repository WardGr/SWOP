package Tests.Commands;

import Domain.Command.CommandManager;
import Domain.Command.EmptyCommandStackException;
import Domain.Command.UpdateDependenciesCommands.AddNextTaskCommand;
import Domain.Command.ProjectCommands.CreateProjectCommand;
import Domain.Command.UndoNotPossibleException;
import Domain.DataClasses.Time;
import Domain.DataClasses.Tuple;
import Domain.Project.ProjectNameAlreadyInUseException;
import Domain.Project.ProjectNotOngoingException;
import Domain.Project.TaskNotFoundException;
import Domain.Task.IncorrectTaskStatusException;
import Domain.Task.TaskNameAlreadyInUseException;
import Domain.TaskManSystem.DueBeforeSystemTimeException;
import Domain.TaskManSystem.ProjectNotFoundException;
import Domain.TaskManSystem.TaskManSystem;
import Domain.Task.IllegalTaskRolesException;
import Domain.Task.LoopDependencyGraphException;
import Domain.User.IncorrectUserException;
import Domain.User.Role;
import Domain.User.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class CommandManagerTest {
    CommandManager commandManager;
    @Mock
    User user1;
    @Mock
    User user2;
    TaskManSystem taskManSystem;
    @Mock
    Time dueTime;
    CreateProjectCommand command;

    @Before
    public void setUp() throws ProjectNameAlreadyInUseException, DueBeforeSystemTimeException {
        taskManSystem = new TaskManSystem(dueTime);
        commandManager = new CommandManager();
        command = new CreateProjectCommand(taskManSystem, "Project", "", dueTime);
        command.execute();

        //user1 = new User("user1", "", Set.of(Role.SYSADMIN));
    }

    @Test
    public void testEmptyManager(){
        assertThrows(EmptyCommandStackException.class, () -> commandManager.undoLastCommand(user1));
        assertThrows(EmptyCommandStackException.class, () -> commandManager.redoLast(user1));
        assertNull(commandManager.getLastExecutedCommandData());
        assertNull(commandManager.getLastUndoneCommandData());

        assertEquals(0, commandManager.getExecutedCommands().size());
        assertEquals(0, commandManager.getUndoneCommands().size());
    }

    @Test
    public void testOneCommand() throws UndoNotPossibleException, IncorrectUserException, EmptyCommandStackException {
        commandManager.addExecutedCommand(command, user1);

        assertThrows(IncorrectUserException.class, () -> commandManager.undoLastCommand(user2));
        assertEquals(1, commandManager.getExecutedCommands().size());
        assertEquals(0, commandManager.getUndoneCommands().size());

        commandManager.undoLastCommand(user1);
        assertEquals(0, commandManager.getExecutedCommands().size());
        assertEquals(1, commandManager.getUndoneCommands().size());

        assertThrows(IncorrectUserException.class, () -> commandManager.redoLast(user2));
        commandManager.redoLast(user1);
        assertEquals(1, commandManager.getExecutedCommands().size());
        assertEquals(0, commandManager.getUndoneCommands().size());
    }

    @Test
    public void testMoreThanTenCommands() throws ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, UndoNotPossibleException, IncorrectUserException, EmptyCommandStackException {
        User testUser = new User("Test", "", Set.of(Role.SYSADMIN));
        CreateProjectCommand command1 = new CreateProjectCommand(taskManSystem, "Project1", "", dueTime);
        CreateProjectCommand command2 = new CreateProjectCommand(taskManSystem, "Project2", "", dueTime);
        CreateProjectCommand command3 = new CreateProjectCommand(taskManSystem, "Project3", "", dueTime);
        CreateProjectCommand command4 = new CreateProjectCommand(taskManSystem, "Project4", "", dueTime);
        CreateProjectCommand command5 = new CreateProjectCommand(taskManSystem, "Project5", "", dueTime);
        CreateProjectCommand command6 = new CreateProjectCommand(taskManSystem, "Project6", "", dueTime);
        CreateProjectCommand command7 = new CreateProjectCommand(taskManSystem, "Project7", "", dueTime);
        CreateProjectCommand command8 = new CreateProjectCommand(taskManSystem, "Project8", "", dueTime);
        CreateProjectCommand command9 = new CreateProjectCommand(taskManSystem, "Project9", "", dueTime);
        CreateProjectCommand command10 = new CreateProjectCommand(taskManSystem, "Project10", "", dueTime);
        CreateProjectCommand command11 = new CreateProjectCommand(taskManSystem, "Project11", "", dueTime);
        CreateProjectCommand command12 = new CreateProjectCommand(taskManSystem, "Project12", "", dueTime);
        command1.execute();
        command2.execute();
        command3.execute();
        command4.execute();
        command5.execute();
        command6.execute();
        command7.execute();
        command8.execute();
        command9.execute();
        command10.execute();
        command11.execute();
        command12.execute();

        commandManager.addExecutedCommand(command1, testUser);
        commandManager.addExecutedCommand(command2, testUser);

        assertEquals(command2.getCommandData(), commandManager.getLastExecutedCommandData());
        assertEquals(List.of(new Tuple<>(command1.getCommandData(), "Test"), new Tuple<>(command2.getCommandData(), "Test")), commandManager.getExecutedCommands());

        commandManager.addExecutedCommand(command3, testUser);
        commandManager.addExecutedCommand(command4, testUser);
        commandManager.addExecutedCommand(command5, testUser);
        commandManager.addExecutedCommand(command6, testUser);
        commandManager.addExecutedCommand(command7, testUser);
        commandManager.addExecutedCommand(command8, testUser);
        commandManager.addExecutedCommand(command9, testUser);
        commandManager.addExecutedCommand(command10, testUser);

        assertEquals(10, commandManager.getExecutedCommands().size());

        commandManager.addExecutedCommand(command11, testUser);
        commandManager.addExecutedCommand(command12, testUser);

        assertEquals(10, commandManager.getExecutedCommands().size());
        assertEquals(command12.getCommandData(), commandManager.getLastExecutedCommandData());

        commandManager.undoLastCommand(testUser);
        assertEquals(command12.getCommandData(), commandManager.getLastUndoneCommandData());
    }

    @Test
    public void testRunTimeExceptions() throws ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, ProjectNotFoundException, TaskNameAlreadyInUseException, TaskNotFoundException, IllegalTaskRolesException, ProjectNotOngoingException, IncorrectTaskStatusException, LoopDependencyGraphException, UndoNotPossibleException, IncorrectUserException, EmptyCommandStackException {
        commandManager.addExecutedCommand(new CreateProjectCommand(taskManSystem, "HAOHGAOGH", "", dueTime), user1);
        assertThrows(RuntimeException.class, () -> commandManager.undoLastCommand(user1));

        taskManSystem.addTaskToProject("Project", "Task1", "", dueTime, 0, List.of(Role.SYSADMIN), new HashSet<>(), new HashSet<>());
        taskManSystem.addTaskToProject("Project", "Task2", "", dueTime, 0, List.of(Role.SYSADMIN), Set.of(new Tuple<>("Project","Task1")), new HashSet<>());

        AddNextTaskCommand redo = new AddNextTaskCommand(taskManSystem, "Project", "Task2", "Project", "Task1");
        commandManager.addExecutedCommand(redo, user1);
        commandManager.undoLastCommand(user1);
        assertThrows(RuntimeException.class, () -> commandManager.redoLast(user1));
    }

}
