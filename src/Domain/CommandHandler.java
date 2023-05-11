package Domain;

import Application.CommandController;
import Application.CreateTaskController;
import Application.ProjectController;
import Domain.Command.Command;
import Domain.Command.CreateProjectCommand;
import Domain.Command.CreateTaskCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CommandHandler {
    private Node node;
    private User user;

    public CommandHandler(User user) {
        this.user = user;
        node = null;
    }

    public void addCreateProject(ProjectController controller,
                                 String projectName,
                                 String projectDescription,
                                 Time startTime,
                                 Time dueTime) {
        Command command = new CreateProjectCommand(controller, projectName, projectDescription, startTime, dueTime);
        addNode(command);
    }

    public void addCreateTask(CreateTaskController controller,
                              String projectName,
                              String taskName,
                              String description,
                              Time durationTime,
                              double deviation,
                              List<Role> roles,
                              Set<String> previousTasks,
                              Set<String> nextTasks) {
        Command command = new CreateTaskCommand(controller, projectName, taskName, description, durationTime, deviation, roles, previousTasks, nextTasks);
        addNode(command);
    }
    public void undo() throws Exception {
        if (node == null) {
            throw new Exception("No commands to undo");
        }
        if (node.getUser() != user) {
            throw new Exception("Incorrect permission: User is not allowed to undo this command");
        }
        node.getcommand().undo();
        node = node.getPrev();
    }

    public void redo() throws Exception {
        if (node == null) {
            throw new Exception("No commands to redo");
        }
        if (node.getUser() != user) {
            throw new Exception("Incorrect permission: User is not allowed to redo this command");
        }
        node.getcommand().redo();
        node = node.getNext();
    }

    public List<String> possibleUndoes() {
        Node current = node;
        List<String> undoes = new ArrayList<String>();
        while (current != null && current.getUser() == user) {
            undoes.add(current.getcommand().information());
            current = current.getPrev();
        }
        return undoes;
    }

    public List<String> possibleRedoes() {
        Node current = node;
        List<String> redoes = new ArrayList<String>();
        while (current != null && current.getUser() == user) {
            redoes.add(current.getcommand().information());
            current = current.getNext();
        }
        return redoes;
    }

    private void addNode(Command command) {
        Node newNode = new Node(command, user);
        if (node == null) {
            node = newNode;
        } else {
            node.setNext(newNode);
            newNode.setPrev(node);
            newNode.setNext(null);
            node = newNode;
            removeOldNodes();
        }
    }

    private void removeOldNodes() {
        Node current = node;
        while (current.getPrev() != null) {
            current = current.getPrev();
            if (current.getUser() == user) {
                current.setNext(null);
                break;
            }
        }

    }

    private class Node {
        private Command command;
        private Node next;
        private Node prev;
        private User user;

        public Node(Command command, User user) {
            this.command = command;
            this.next = null;
            this.prev = null;
        }

        public Command getcommand() {
            return command;
        }

        public Node getNext() {
            return next;
        }

        public Node getPrev() {
            return prev;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }

        public User getUser() {
            return user;
        }
    }
}