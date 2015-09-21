import java.util.ArrayList;

public class Node<K extends Comparable<K>, T> {
	
	protected boolean isLeafNode; //Boolean value to check if the node is a leaf node
	protected ArrayList<K> keys; //ArrayList that contains all element i.e either indexes or values

	/**
	 * Depending on the order of the bplus tree, it checks if the node is
	 * overflowed i.e. contains more than 2D elements
	 * @return
	 * 		boolean
	 */
	public boolean isOverflowed() {
		return keys.size() > 2 * BPlusTree.D;
	}

	/**
	 * Depending on the order of the bplus tree, it checks if the node is
	 * underflowed i.e. contains less than 2D elements
	 * @return
	 * 		boolean
	 */
	public boolean isUnderflowed() {
		return keys.size() < BPlusTree.D;
	}

}
