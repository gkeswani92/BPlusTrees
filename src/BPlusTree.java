import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;

/**
 * BPlusTree Class Assumptions: 
 * 1. No duplicate keys inserted 
 * 2. Order D: D<=number of keys in a node <=2*D 
 * 3. All keys are non-negative 
 */
public class BPlusTree<K extends Comparable<K>, T> {

	public Node<K, T> root;
	public static final int D = 2;

	/**
	 * Search the value for a specific key
	 * 
	 * @param key
	 * @return value
	 */
	public T search(K key) {

		// If we reach a leaf node, search for the key in the node
		if (root.isLeafNode == true) {
			return findValueInLeafNode((LeafNode<K, T>) root, key);
		}

		// Else traverse the tree until we reach the leaf node and then search for the key
		else {
			IndexNode<K, T> currentPointer = (IndexNode<K, T>) root;
			LeafNode<K, T> leafNode = getLeafNodeGivenKey(currentPointer, key);
			T value = findValueInLeafNode(leafNode, key);
			return value;
		}
	}

	/**
	 * Returns the value after looking for the key in the leaf node. This method
	 * should only be called when we know the leaf node the key will be in
	 * 
	 * @param leaf
	 * @param key
	 * @return T
	 */
	private T findValueInLeafNode(LeafNode<K, T> leaf, K key) {
		for (int i = 0; i < leaf.keys.size(); i++) {
			if (key == leaf.keys.get(i))
				return leaf.values.get(i);
		}
		return null;
	}

	/**
	 * Given an index node and the key, it finds the leaf node where the key
	 * should. This method can be find the leaf node to search for a key or to
	 * find the leaf node where the new key needs to be inserted
	 * 
	 * @param currentPointer
	 * @param key
	 * @return LeafNode<K,T>
	 */
	public LeafNode<K, T> getLeafNodeGivenKey(Node<K, T> currentPointer, K key) {

		// Traverse the tree until you hit a leaf node
		while (currentPointer.isLeafNode != true) {

			// If the new key is less than the smallest key in this index node
			if (key.compareTo(currentPointer.keys.get(0)) < 0)
				return getLeafNodeGivenKey(((IndexNode<K, T>) currentPointer).children.get(0), key);

			// If the new key is more the largest key in this index node
			else if (key.compareTo(currentPointer.keys.get(currentPointer.keys.size() - 1)) >= 0)
				return getLeafNodeGivenKey(((IndexNode<K, T>) currentPointer).children.get(currentPointer.keys.size()),key);

			// If the pointer to the next node for this key is somewhere in the
			// middle. Finding the two keys between which this new key falls
			else
				for (int i = 0; i < currentPointer.keys.size() - 1; i++)
					if (key.compareTo(currentPointer.keys.get(i)) >= 0 && key.compareTo(currentPointer.keys.get(i + 1)) < 0)
						return getLeafNodeGivenKey(((IndexNode<K, T>) currentPointer).children.get(i + 1), key);
		}
		return ((LeafNode<K, T>) currentPointer);
	}

	/**
	 * Insert a key/value pair into the BPlusTree
	 * 
	 * @param key
	 * @param value
	 */
	public void insert(K key, T value) {

		// If this is the first time an insertion is happening on the tree
		if (root == null) {
			LeafNode<K, T> leaf = new LeafNode<K, T>(key, value);
			root = leaf;
			root.parent = null;
		}

		// If root is a leaf node, attempt to insert the key
		else if (root.isLeafNode == true) {
			((LeafNode<K, T>) root).insertSorted(key, value);
			handleOverflow(root);
		}

		// If root is an index node, find the leaf node where the would be if
		// every rule was followed and insert the key there
		else {
			IndexNode<K, T> currentPointer = (IndexNode<K, T>) root;
			LeafNode<K, T> leafNode = (LeafNode<K, T>) getLeafNodeGivenKey(currentPointer, key);
			leafNode.insertSorted(key, value);
			handleOverflow(leafNode);
		}
	}
	
	/**
	 * Method which takes recursive calls on it to go from the leaf node to the 
	 * root node to check if there is an overflow
	 * @param current
	 * 		node being checked for overflow at this moment
	 */
	private void handleOverflow(Node<K, T> current) {

		if (current.isOverflowed()) {
			
			Entry<K, Node<K,T>> splittingKey; 
			IndexNode<K, T> parent = (IndexNode<K, T>) current.parent;
			
			//Splitting depending on what kind of node has been passed
			if (current.isLeafNode) {
				splittingKey = splitLeafNode((LeafNode<K, T>) current);
				current = (LeafNode<K,T>)current;
			}
			else {
				splittingKey = splitIndexNode((IndexNode<K, T>) current);
				current = (IndexNode<K,T>)current;
			}

			// If there is no parent, create a new Index Node
			if (parent == null) {
				IndexNode<K, T> newIndexNode = new IndexNode<K, T>(splittingKey.getKey(), current, splittingKey.getValue());
				root = newIndexNode;
				handleOverflow(newIndexNode);
			}
			
			// If there is a parent, which is obviously an index node, add to it
			else {
				int index = returnInsertionIndex(parent.keys, splittingKey.getKey());
				parent.insertSorted(splittingKey, index);
				splittingKey.getValue().parent = parent;
				handleOverflow(parent);
			}
		}
	}
	
	/**
	 * Split a leaf node and return the new right node and the splitting key as
	 * an Entry<slitingKey, RightNode>
	 * 
	 * @param leaf,
	 *            any other relevant data
	 * @return the key/node pair as an Entry
	 */
	public Entry<K, Node<K, T>> splitLeafNode(LeafNode<K, T> leaf) {

		// This is the key that will get pushed up to the higher level
		K newKey = leaf.keys.get(D);

		// Creating a new leaf node with the upper half of the current lead
		LeafNode<K, T> rightChild = new LeafNode<K, T>(leaf.keys.subList(D, 2 * D + 1), leaf.values.subList(D, 2 * D + 1));

		// Dropping the keys and values of the right leaf node from the left leaf
		leaf.keys = new ArrayList<K>(leaf.keys.subList(0, D));
		leaf.values = new ArrayList<T>(leaf.values.subList(0, D));

		// Keeping all the leaves linked to each other like a doubly linked list
		rightChild.nextLeaf = leaf.nextLeaf;
		leaf.nextLeaf = rightChild;
		rightChild.previousLeaf = leaf;

		// Returning the entry with the splitting key and the right child
		return new AbstractMap.SimpleEntry<K, Node<K, T>>(newKey, rightChild);
	}

	/**
	 * Split an indexNode and return the new right node and the splitting key as
	 * an Entry<slitingKey, RightNode>
	 * 
	 * @param index,
	 *            any other relevant data
	 * @return new key/node pair as an Entry
	 */
	public Entry<K, Node<K, T>> splitIndexNode(IndexNode<K, T> index) { 

		K newKey = index.keys.get(D);

		// Creating a new leaf node with the upper half of the current lead
		IndexNode<K, T> rightChild = new IndexNode<K, T>(index.keys.subList(D + 1, index.keys.size()), index.children.subList(D + 1, index.children.size()));

		// Changing the parent of the keys of this new index node to this new index node
		for (Node<K, T> leaf : rightChild.children) 
			leaf.parent = rightChild;
		
		// Dropping the keys and values of the right leaf node from the left leaf
		index.keys = new ArrayList<K>(index.keys.subList(0, D));
		index.children = new ArrayList<Node<K, T>>(index.children.subList(0, D + 1));

		// Returning the entry with the splitting key and the right child
		return new AbstractMap.SimpleEntry<K, Node<K, T>>(newKey, rightChild);
	}

	/**
	 * Delete a key/value pair from this B+Tree
	 * 
	 * @param key
	 */
	public void delete(K key) {

		K deletedKey = null;
		
		// If the root is a leaf node, delete the element and exit
		if (root.isLeafNode == true) {
			LeafNode<K, T> leafNode = (LeafNode<K, T>) root;
			deletedKey = removeKeyValueFromLeaf(leafNode, key);
			
			if(root.keys.size() == 0)
				root = null;
		}

		// If the root is an index node
		else {
			LeafNode<K, T> leafNode = (LeafNode<K, T>) getLeafNodeGivenKey(root, key);
			deletedKey = removeKeyValueFromLeaf(leafNode, key);
			handleUnderflow(leafNode, deletedKey);
			
			//If the root now has only one child, make that child the root
			if(((IndexNode<K,T>)root).children.size() == 1)
				root = ((IndexNode<K,T>)root).children.get(0);			
		}	
	}

	/**
	 * Method that recursively calls itself to go from the leaf to the root to
	 * check if the delete caused an underflow anywhere
	 * @param pointer
	 * 		the current node being checked for underflow
	 */
	private void handleUnderflow(Node<K, T> pointer, K deletedKey) {

		if (pointer.isUnderflowed() && pointer != root) {
			if (pointer.isLeafNode) {

				// If the pointer has a subling previous leaf, we either merge
				// with it or redistribute. We will not look at the right sibling at all
				if (((LeafNode<K, T>) pointer).previousLeaf != null && pointer.parent == ((LeafNode<K, T>) pointer).previousLeaf.parent) {
					LeafNode<K, T> leftLeafNode = ((LeafNode<K, T>) pointer).previousLeaf;
					handleLeafNodeUnderflow(leftLeafNode, ((LeafNode<K, T>) pointer), ((IndexNode<K, T>) pointer.parent), deletedKey);
					handleUnderflow(pointer.parent, deletedKey);
				}
				
				// If the pointer does not have a sibling previous leaf, we
				// either merge with or redistribute with the right sibling
				else {
					LeafNode<K, T> rightLeafNode = ((LeafNode<K, T>) pointer).nextLeaf;
					if (rightLeafNode.parent == pointer.parent) {
						handleLeafNodeUnderflow(((LeafNode<K, T>) pointer), rightLeafNode, ((IndexNode<K, T>) pointer.parent), deletedKey);
						handleUnderflow(pointer.parent, deletedKey);
					}
				}
			}

			// If the pointer that has underflown is an index node
			else {
				Entry<String, IndexNode<K, T>> sibling = getSibling(pointer);
				
				// Handling index underflow by trying to first get the left index and if not then the right
				if(sibling.getKey().equals("Left"))
					handleIndexNodeUnderflow(sibling.getValue(), (IndexNode<K, T>) pointer, (IndexNode<K, T>) pointer.parent);
				else
					handleIndexNodeUnderflow((IndexNode<K, T>) pointer, sibling.getValue(), (IndexNode<K, T>) pointer.parent);
				
				handleUnderflow(pointer.parent, deletedKey);
			}
			
			int index = root.keys.indexOf(deletedKey);
			if(index != -1 && pointer.parent == root)
				root.keys.set(index, ((IndexNode<K,T>)root).children.get(index+1).keys.get(0));
		}
	}

	/**
	 * Returns the index of the sibling we want to work with from the parent
	 * 
	 * @param pointer
	 * @return
	 */
	public Entry<String, IndexNode<K, T>> getSibling(Node<K, T> pointer) {
		IndexNode<K, T> parent = (IndexNode<K, T>) pointer.parent;

		// Get the left sibling if it exists, otherwise get the right sibling
		if (parent.children.indexOf(pointer) > 0) {
			IndexNode<K,T> leftSibling = (IndexNode<K, T>) parent.children.get(parent.children.indexOf(pointer) - 1);
			return new AbstractMap.SimpleEntry<String, IndexNode<K,T>>("Left", leftSibling);
		}
		else {
			IndexNode<K,T> rightSibling = (IndexNode<K, T>) parent.children.get(parent.children.indexOf(pointer) + 1);
			return new AbstractMap.SimpleEntry<String, IndexNode<K,T>>("Right", rightSibling);
		}
	}

	/*
	 * Given a leaf node and the key to be deleted, this finds the position of
	 * the key to delete it and its corresponding value
	 */
	private K removeKeyValueFromLeaf(LeafNode<K, T> leaf, K key) {
		K deletedKey = null;
		for (int i = 0; i < leaf.keys.size(); i++) {
			if (key.compareTo(leaf.keys.get(i)) == 0) {
				deletedKey = leaf.keys.remove(i);
				leaf.values.remove(i);
			}
		}
		return deletedKey;
	}

	/**
	 * Handle LeafNode Underflow (merge or redistribution)
	 * 
	 * @param left
	 *            : the smaller node
	 * @param right
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 */
	public void handleLeafNodeUnderflow(LeafNode<K, T> left, LeafNode<K, T> right, IndexNode<K, T> parent, K deletedKey) {

		// Redistribution is possible
		if (left.keys.size() + right.keys.size() >= 2 * D) 
			redistributeLeafNodes(left, right, parent, deletedKey);
		
		//Else we merge the leaf node
		else 
			mergeLeafNodes(left, right, parent, deletedKey);
	}

	private K redistributeLeafNodes(LeafNode<K, T> left, LeafNode<K, T> right, IndexNode<K, T> parent, K deletedKey) {
		
		//Creating a collection of the keys and values of the left and right leaf node
		List<K> keys = new ArrayList<K>();
		keys.addAll(left.keys);
		keys.addAll(right.keys);
		
		List<T> values = new ArrayList<T>();
		values.addAll(left.values);
		values.addAll(right.values);
		
		//Figuring out where to split and the index that needs to be replaced
		int split = keys.size()/2;		
		K splittingKey = right.keys.get(0);
		int splittingKeyIndex = parent.keys.indexOf(splittingKey);
		
		// Adding the first D elements to the left leaf node
		left.keys = new ArrayList<K>(keys.subList(0, split));
		left.values = new ArrayList<T>(values.subList(0, split));

		// Adding the remaining D or D+1 elements to the right leaf node
		right.keys = new ArrayList<K>(keys.subList(split, keys.size()));
		right.values = new ArrayList<T>(values.subList(split, values.size()));

		// Moving the new first key of the right node to the index node
		if(parent.keys.contains(deletedKey)) {
			parent.keys.set(parent.keys.indexOf(deletedKey), right.keys.get(0));
		}
		else if(parent.keys.contains(splittingKey)){
			parent.keys.remove(splittingKey);
			parent.keys.add(splittingKeyIndex, right.keys.get(0));
		}
		else {
			int index = parent.children.indexOf(left);
			parent.keys.set(index, right.keys.get(0));
		}
		return null;
	}

	/**
	 * Merges the left and the right leaf node and adjusts the respective key
	 * and pointers in the parent index node
	 * 
	 * @param left
	 * @param right
	 * @param parent
	 */
	public K mergeLeafNodes(LeafNode<K, T> left, LeafNode<K, T> right, IndexNode<K, T> parent, K deletedKey) {

		K splittingKey = right.keys.get(0);
		
		// Adding the keys and values of the right into the left
		left.keys.addAll(right.keys);
		left.values.addAll(right.values);
		
		// Next leaf needs to point to the next leaf of the removed right node
		left.nextLeaf = right.nextLeaf;
		if(right.nextLeaf != null)
			right.nextLeaf.previousLeaf = left;

		// Removing the deleted key from the parent if it was an index key as well
		// Otherwise removing the original key from the parent that was pointing 
		// to the right index
		if(parent.keys.contains(deletedKey)) {
			parent.children.remove(parent.keys.indexOf(deletedKey) + 1);
			parent.keys.remove(deletedKey);
			return deletedKey;
		}
		else if(parent.keys.contains(splittingKey)) {
			parent.children.remove(parent.keys.indexOf(splittingKey) + 1);
			parent.keys.remove(splittingKey);
			return splittingKey;
		}
		else {
			int index = parent.children.indexOf(left);
			parent.children.remove(right);
			parent.keys.remove(index);
			return deletedKey;
		}
	}

	/**
	 * Handle IndexNode Underflow (merge or redistribution)
	 * 
	 * @param left
	 *            : the smaller node
	 * @param right
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 */
	public void handleIndexNodeUnderflow(IndexNode<K, T> leftIndex, IndexNode<K, T> rightIndex, IndexNode<K, T> parent) {

		int splittingIndex = -1;

		//Getting the index of the key that needs to be removed from the parent
		for (int i = 0; i <= parent.keys.size() - 1; i++) 
			if (parent.children.get(i) == leftIndex && parent.children.get(i + 1) == rightIndex) {
				splittingIndex = i;
				break;
			}
		
		//If redistribution is being performed
		if (leftIndex.keys.size() + rightIndex.keys.size() >= 2 * D) 
			redistributeIndexNodes(leftIndex, rightIndex, parent, splittingIndex);
		
		//If merging is being performed
		else 
			mergeIndexNodes(leftIndex, rightIndex, parent, splittingIndex);
	}

	private void redistributeIndexNodes(IndexNode<K, T> leftIndex, IndexNode<K, T> rightIndex, IndexNode<K, T> parent,
			int splittingIndex) {
		
		//Creating a collection of the keys of the left index, parent key and right index
		List<K> keys = new ArrayList<K>(leftIndex.keys);
		keys.add(parent.keys.get(splittingIndex));
		keys.addAll(rightIndex.keys);
		
		//Creating a collection of the children of the right and left index
		List<Node<K,T>>children = new ArrayList<Node<K,T>>(leftIndex.children);
		children.addAll(rightIndex.children);
		
		//Distributing the keys into the left index, parent and right index
		int split = (keys.size() % 2 == 0)? keys.size()/2 - 1 : keys.size()/2;
	
		leftIndex.keys = new ArrayList<K>(keys.subList(0, split));
		leftIndex.children = new ArrayList<Node<K,T>>(children.subList(0, split+1));
		
		rightIndex.keys = new ArrayList<K>(keys.subList(split+1, keys.size()));
		rightIndex.children = new ArrayList<Node<K,T>>(children.subList(split+1, children.size()));
		
		//Making the children point to their own parents
		for (Node<K, T> leaf : leftIndex.children) 
			leaf.parent = leftIndex;
		
		for (Node<K, T> leaf : rightIndex.children) 
			leaf.parent = rightIndex;
		
		//Moving the new first key of the right index to the parent
		parent.keys.set(splittingIndex, keys.get(split));
	}

	private void mergeIndexNodes(IndexNode<K, T> leftIndex, IndexNode<K, T> rightIndex, IndexNode<K, T> parent,
			int splittingIndex) {
		
		// Changing the parent of the keys of the right index node to the left since they are going to be merged
		for (Node<K, T> leaf : rightIndex.children) 
			leaf.parent = leftIndex;
		
		//Moving the parent key and everything from the right index to the left one
		leftIndex.keys.add(parent.keys.get(splittingIndex));
		leftIndex.keys.addAll(rightIndex.keys);
		leftIndex.children.addAll(rightIndex.children);

		//Removing the right index node from the parent
		parent.children.remove(rightIndex);
		parent.keys.remove(splittingIndex);
	}
	
	private int returnInsertionIndex(ArrayList<K> keys, K splittingKey) {
		ListIterator<K> iterator = keys.listIterator();
		while (iterator.hasNext()) {
			if (iterator.next().compareTo(splittingKey) > 0) {
				return iterator.previousIndex();
			}
		}
		return keys.size();
	}
}