package co.edu.uptc.structures;

public class Node<E> {
    private E value;
    private Node<E> next;

    public Node(E value) {
        this.value = value;
    }

    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        this.value = value;
    }

    public Node<E> getNext() {
        return next;
    }

    public void setNext(Node<E> next) {
        this.next = next;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(value + " " + next);
        return result.toString();
    }
}
