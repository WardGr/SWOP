package Application;

import Domain.Actions.Action;
import Domain.Actions.CreateProjectAction;
import Domain.Time;
import Domain.User;

import java.util.ArrayList;
import java.util.List;

public class ActionController {
    private Session session;
    private Node node = null;

    public ActionController(Session session) {
        this.session = session;
    }

    public void addCreateProject(ProjectController controller,
                                 String projectName,
                                 String projectDescription,
                                 Time startTime,
                                 Time dueTime) {
        Action action = new CreateProjectAction(controller, projectName, projectDescription, startTime, dueTime);
        addNode(action);
    }

    public void undo() throws Exception {
        if (node == null) {
            throw new Exception("No actions to undo");
        }
        if (node.getUser() != session.getCurrentUser()) {
            throw new Exception("Incorrect permission: User is not allowed to undo this action");
        }
        node.getAction().undo();
        node = node.getPrev();
    }

    public void redo() throws Exception {
        if (node == null) {
            throw new Exception("No actions to redo");
        }
        if (node.getUser() != session.getCurrentUser()) {
            throw new Exception("Incorrect permission: User is not allowed to redo this action");
        }
        node.getAction().redo();
        node = node.getNext();
    }

    public List<String> possibleUndoes() {
        Node current = node;
        User user = session.getCurrentUser();
        List<String> undoes = new ArrayList<String>();
        while (current != null && current.getUser() == user) {
            undoes.add(current.getAction().information());
            current = current.getPrev();
        }
        return undoes;
    }

    public List<String> possibleRedoes() {
        Node current = node;
        User user = session.getCurrentUser();
        List<String> redoes = new ArrayList<String>();
        while (current != null && current.getUser() == user) {
            redoes.add(current.getAction().information());
            current = current.getNext();
        }
        return redoes;
    }

    private void addNode(Action action) {
        Node newNode = new Node(action, session.getCurrentUser());
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
        private Action action;
        private Node next;
        private Node prev;
        private User user;

        public Node(Action action, User user) {
            this.action = action;
            this.next = null;
            this.prev = null;
        }

        public Action getAction() {
            return action;
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
