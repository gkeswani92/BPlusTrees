import java.util.ArrayList;

public class Node<K extends Comparable<K>, T> {
	protected boolean isLeafNode;
	protected ArrayList<K> keys;
	protected Node<K,T> parent;

	public boolean isOverflowed() {
		return keys.size() > 2 * BPlusTree.D;
	}

	public boolean isUnderflowed() {
		return keys.size() < BPlusTree.D;
	}
}