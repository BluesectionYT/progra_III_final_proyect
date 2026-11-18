package co.edu.uptc.structures;

public class BinaryTreeNode<E> {
	private E value;
	private BinaryTreeNode<E> left;
	private BinaryTreeNode<E> right;
	private int height;

	public BinaryTreeNode(E value) {
		this.value = value;
		this.left = null;
		this.right = null;
		this.height = 1;
	}

	public E getValue() {
		return value;
	}

	public void setValue(E value) {
		this.value = value;
	}

	public BinaryTreeNode<E> getLeft() {
		return left;
	}

	public void setLeft(BinaryTreeNode<E> left) {
		this.left = left;
	}

	public BinaryTreeNode<E> getRight() {
		return right;
	}

	public void setRight(BinaryTreeNode<E> right) {
		this.right = right;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public String toString() {
		return "Node [value=" + value + ", left=" + left + ", right=" + right + "]";
	}
}