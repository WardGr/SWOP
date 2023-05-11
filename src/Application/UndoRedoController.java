package Application;

import Application.Command.Command;
import Application.Command.CreateProjectCommand;
import Domain.Time;
import Domain.User;

public class UndoRedoController {
    private Session session;
    private Node node = null;

    public UndoRedoController(Session session) {
        this.session = session;
    }

    public void addCreateProject(ProjectController controller,
                                 String projectName,
                                 String projectDescription,
                                 Time startTime,
                                 Time dueTime) {
        Command command = new CreateProjectCommand(controller, projectName, projectDescription, startTime, dueTime);
        addNode(command);
    }

    public void undo() throws Exception {
        if (node == null) {
            throw new Exception("No actions to undo");
        }
        if (node.getUser() != session.getCurrentUser()) {
            throw new Exception("Incorrect permission: User is not allowed to undo this command");
        }
        node.getAction().undo();
        node = node.getPrev();
    }

    public void redo() throws Exception {
        if (node == null) {
            throw new Exception("No actions to redo");
        }
        if (node.getUser() != session.getCurrentUser()) {
            throw new Exception("Incorrect permission: User is not allowed to redo this command");
        }
        node.getAction().redo();
        node = node.getNext();
    }

    private void addNode(Command command) {
        Node newNode = new Node(command, session.getCurrentUser());
        if (node == null) {
            node = newNode;
        } else {
            node.setNext(newNode);
            newNode.setPrev(node);
            node = newNode;
            removeOldNodes();
        }
    }

    private void removeOldNodes() {
        Node current = node;
        User user = session.getCurrentUser();
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

        public Command getAction() {
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
