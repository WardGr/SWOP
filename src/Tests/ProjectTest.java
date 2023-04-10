package Tests;

import Domain.*;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.NonDeveloperRoleException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class ProjectTest {

    private Project project;

    @Before
    public void setUp() throws InvalidTimeException, DueBeforeSystemTimeException {
        this.project = new Project("Project 1", "", new Time(0), new Time(100));
    }

    @Test
    public void testProject() throws InvalidTimeException {

        assertEquals("Project 1", project.getName());
        assertEquals("", project.getDescription());
        assertEquals(new Time(0), project.getCreationTime());

    }
}
