package co.edu.uptc.structures;

public class Queue<E> {
    private Node<E> top;

    public Queue() {
        top = null;
    }

    public boolean push(E object) {
        Node<E> newNode = new Node<E>(object);
        if (top == null) {
            top = newNode;
        } else {
            Node<E> actual = top;
            while (actual.getNext() != null) {
                actual = actual.getNext();
            }
            actual.setNext(newNode);
        }
        return true;
    }

    public E pull() {
        E result = peek();
        if (top != null) {
            if (top.getNext() != null) {
                top = top.getNext();
            } else {
                top = null;
            }
        }
        return result;
    }

    public E peek() {
        E result = null;
        if (top != null) {
            result = top.getValue();
        }
        return result;
    }

    public boolean isEmpty() {
        return top == null;
    }
}
