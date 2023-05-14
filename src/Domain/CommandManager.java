package Domain;

 import Domain.Command.Command;

 import java.util.ArrayList;
 import java.util.List;

 public class CommandManager {
     private Node node;

     public CommandManager() {
         node = null;
     }

     public void undo(User user) throws Exception {
         if (node == null) {
             throw new Exception("No commands to undo");
         }
         if (node.getUser() != user) {
             throw new Exception("Incorrect permission: User is not allowed to undo this command");
         }
         node.getcommand().undo();
         node = node.getPrev();
     }

     public void redo(User user) throws Exception {
         if (node == null) {
             throw new Exception("No commands to redo");
         }
         if (node.getUser() != user) {
             throw new Exception("Incorrect permission: User is not allowed to redo this command");
         }
         node.getcommand().execute();
         node = node.getNext();
     }

     public List<String> possibleUndoes(User user) {
         Node current = node;
         List<String> undoes = new ArrayList<String>();
         while (current != null && current.getUser() == user) {
             undoes.add(current.getcommand().information());
             current = current.getPrev();
         }
         return undoes;
     }

     public List<String> possibleRedoes(User user) {
         Node current = node;
         List<String> redoes = new ArrayList<String>();
         while (current != null && current.getUser() == user) {
             redoes.add(current.getcommand().information());
             current = current.getNext();
         }
         return redoes;
     }

     public void addExecutedCommand(Command command, User executingUser) {
         Node newNode = new Node(command, executingUser);
         if (node == null) {
             node = newNode;
         } else {
             node.setNext(newNode);
             newNode.setPrev(node);
             newNode.setNext(null);
             node = newNode;
             removeOldNodes(executingUser); // TODO deze zou niet meer moeten, maar dat doen we later wel
         }
     }

     private void removeOldNodes(User user) {
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