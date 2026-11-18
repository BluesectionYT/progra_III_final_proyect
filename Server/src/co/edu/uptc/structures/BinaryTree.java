package co.edu.uptc.structures;

import java.util.ArrayList;
import java.util.Comparator;

public class BinaryTree<E> {
    private BinaryTreeNode<E> root;
    private Comparator<E> comparator;

    public BinaryTree(Comparator<E> comparator) {
        this.root = null;
        this.comparator = comparator;
    }

    public boolean add(E value) {
        boolean result = false;
        if (root == null) {
            root = new BinaryTreeNode<>(value);
            result = true;
        } else if (find(value) == null) {
            root = add(value, root);
            result = true;
        }
        return result;
    }

    private BinaryTreeNode<E> add(E object, BinaryTreeNode<E> nodeToCompare) {
        if (nodeToCompare == null) {
            return new BinaryTreeNode<>(object);
        }
        int comparatorValue = comparator.compare(object, nodeToCompare.getValue());
        if (comparatorValue < 0) {
            nodeToCompare.setLeft(add(object, nodeToCompare.getLeft()));
        } else {
            nodeToCompare.setRight(add(object, nodeToCompare.getRight()));
        }
        updateHeight(nodeToCompare);
        return balance(nodeToCompare);
    }

    private int getNodeHeight(BinaryTreeNode<E> node) {
        return node == null ? 0 : node.getHeight();
    }

    private void updateHeight(BinaryTreeNode<E> node) {
        int leftHeight = getNodeHeight(node.getLeft());
        int rightHeight = getNodeHeight(node.getRight());
        if (leftHeight > rightHeight) {
            node.setHeight(leftHeight + 1);
        } else {
            node.setHeight(rightHeight + 1);
        }
    }

    private int balanceFactor(BinaryTreeNode<E> node) {
        return node == null ? 0 : getNodeHeight(node.getLeft()) - getNodeHeight(node.getRight());
    }

    private BinaryTreeNode<E> balance(BinaryTreeNode<E> node) {
        int factor = balanceFactor(node);
        if (factor > 1 && balanceFactor(node.getLeft()) >= 0) {
            return rotateRight(node);
        }
        if (factor > 1 && balanceFactor(node.getLeft()) < 0) {
            node.setLeft(rotateLeft(node.getLeft()));
            return rotateRight(node);
        }
        if (factor < -1 && balanceFactor(node.getRight()) <= 0) {
            return rotateLeft(node);
        }
        if (factor < -1 && balanceFactor(node.getRight()) > 0) {
            node.setRight(rotateRight(node.getRight()));
            return rotateLeft(node);
        }
        return node;
    }

    private BinaryTreeNode<E> rotateRight(BinaryTreeNode<E> y) {
        BinaryTreeNode<E> x = y.getLeft();
        BinaryTreeNode<E> t2 = x.getRight();
        x.setRight(y);
        y.setLeft(t2);
        updateHeight(y);
        updateHeight(x);
        return x;
    }

    private BinaryTreeNode<E> rotateLeft(BinaryTreeNode<E> x) {
        BinaryTreeNode<E> y = x.getRight();
        BinaryTreeNode<E> t2 = y.getLeft();
        y.setLeft(x);
        x.setRight(t2);
        updateHeight(x);
        updateHeight(y);
        return y;
    }

    public boolean remove(E value) {
        boolean result = false;
        if (find(value) != null) {
            root = remove(value, root);
            result = true;
        }
        return result;
    }

    private BinaryTreeNode<E> remove(E value, BinaryTreeNode<E> actualNode) {
        if (actualNode == null) {
            return null;
        }
        int comparatorValue = comparator.compare(value, actualNode.getValue());
        if (comparatorValue < 0) {
            actualNode.setLeft(remove(value, actualNode.getLeft()));
        } else if (comparatorValue > 0) {
            actualNode.setRight(remove(value, actualNode.getRight()));
        } else {
            if (actualNode.getLeft() == null) {
                return actualNode.getRight();
            } else if (actualNode.getRight() == null) {
                return actualNode.getLeft();
            } else {
                actualNode.setValue(getMax(actualNode));
            }
        }
        updateHeight(actualNode);
        return balance(actualNode);
    }

    private E getMax(BinaryTreeNode<E> node) {
        BinaryTreeNode<E> parent = node;
        BinaryTreeNode<E> current = node.getLeft();
        E result;
        while (current.getRight() != null) {
            parent = current;
            current = current.getRight();
        }
        result = current.getValue();
        if (parent == node) {
            parent.setLeft(current.getLeft());
        } else {
            parent.setRight(current.getLeft());
        }
        return result;
    }

    public ArrayList<E> inOrderData() {
        ArrayList<E> result = new ArrayList<>();
        inOrderData(result, root);
        return result;
    }

    private void inOrderData(ArrayList<E> result, BinaryTreeNode<E> actualNode) {
        if (actualNode != null) {
            inOrderData(result, actualNode.getLeft());
            result.add(actualNode.getValue());
            inOrderData(result, actualNode.getRight());
        }
    }

    public ArrayList<E> postOrderData() {
        ArrayList<E> result = new ArrayList<>();
        postOrderData(result, root);
        return result;
    }

    private void postOrderData(ArrayList<E> result, BinaryTreeNode<E> actualNode) {
        if (actualNode != null) {
            postOrderData(result, actualNode.getLeft());
            postOrderData(result, actualNode.getRight());
            result.add(actualNode.getValue());
        }
    }

    public ArrayList<E> preOrderData() {
        ArrayList<E> result = new ArrayList<>();
        preOrderData(result, root);
        return result;
    }

    private void preOrderData(ArrayList<E> result, BinaryTreeNode<E> actualNode) {
        if (actualNode != null) {
            result.add(actualNode.getValue());
            preOrderData(result, actualNode.getLeft());
            preOrderData(result, actualNode.getRight());
        }
    }

    public E find(E value) {
        return find(value, root);
    }

    private E find(E value, BinaryTreeNode<E> nodeToCompare) {
        E result = null;
        if (nodeToCompare != null) {
            int comparatorValue = comparator.compare(value, nodeToCompare.getValue());
            if (comparatorValue == 0) {
                result = nodeToCompare.getValue();
            } else if (comparatorValue < 0) {
                result = find(value, nodeToCompare.getLeft());
            } else {
                result = find(value, nodeToCompare.getRight());
            }
        }
        return result;
    }

    public int size() {
        return size(root);
    }

    private int size(BinaryTreeNode<E> actualNode) {
        int result = 0;
        if (actualNode != null) {
            result += size(actualNode.getLeft());
            result += size(actualNode.getRight());
            result++;
        }
        return result;
    }

    public int leafs() {
        return leafs(root);
    }

    private int leafs(BinaryTreeNode<E> actualNode) {
        int result = 0;
        if (actualNode != null) {
            if (actualNode.getLeft() == null && actualNode.getRight() == null) {
                result = 1;
            }
            result += leafs(actualNode.getLeft());
            result += leafs(actualNode.getRight());
        }
        return result;
    }

    public boolean isEmpty() {
        return root == null;
    }
}