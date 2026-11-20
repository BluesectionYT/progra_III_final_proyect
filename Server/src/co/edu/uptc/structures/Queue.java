package co.edu.uptc.structures;

public class Queue<E> {
    private Node<E> top;

    public Queue() {
        this.top = null;
    }

    public boolean push(E object) {
        Node<E> newNode = new Node<>(object);
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
            top = top.getNext();
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
