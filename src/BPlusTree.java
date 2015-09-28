import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * BPlusTree Class Assumptions: 
 * 1. No duplicate keys inserted 
 * 2. Order D: D<=number of keys in a node <=2*D 
 * 3. All keys are non-negative
 * TODO: Rename to BPlusTree
 */
public class BPlusTree<K extends Comparable<K>, T> {

	public Node<K,T> root;
	public static final int D = 2;
	
	/**
	 * TODO Search the value for a specific key
	 * 
	 * @param key
	 * @return value
	 */
	public T search(K key) {
		
		if(root.isLeafNode == true) {
			return findValueInLeafNode((LeafNode<K,T>)root, key);
		}
		
		else {
			IndexNode<K,T> currentPointer = (IndexNode<K,T>)root;
			LeafNode<K,T> leafNode = getLeafNodeGivenKey(currentPointer, key);
			T value = findValueInLeafNode(leafNode, key);
			return value;
		}
		
	}
	
	/**
	 * Returns the value after looking for the key in the leaf node. This method
	 * should only be called when we know the leaf node the key will be in
	 * @param leaf
	 * @param key
	 * @return
	 * 		T
	 */
	private T findValueInLeafNode( LeafNode<K,T> leaf, K key ){
		for(int i=0; i<leaf.keys.size(); i++) {
			if (key == leaf.keys.get(i))
				return leaf.values.get(i);
		}
		return null;
	}
	
	/**
	 * Given an index node and the key, it finds the leaf node where the key should.
	 * This method can be find the leaf node to search for a key or to find the 
	 * leaf node where the new key needs to be inserted
	 * @param currentPointer
	 * @param key
	 * @return
	 * 		LeafNode<K,T>
	 */
	public LeafNode<K,T> getLeafNodeGivenKey( IndexNode<K,T> currentPointer, K key) {
		
		Integer numKeys = currentPointer.keys.size();
		Integer NodeKey = 0;
		
		//Traverse the tree until you hit a leaf node
		while(currentPointer.isLeafNode != true) {
			
			//If the new key is less than the smallest key in this index node
			if(key.compareTo(currentPointer.keys.get(0))<0) 
				NodeKey = 0;

			//If the new key is more the largest key in this index node
			else if (key.compareTo(currentPointer.keys.get(numKeys-1))>0) 
				NodeKey = numKeys;
			
			//If the pointer to the next node for this key is somewhere in the middle
			else 
				for(int i=1; i<numKeys-1; i++) 
					//Finding the two keys between which this new key falls
					if (currentPointer.keys.get(i).compareTo(key) > 1 && key.compareTo(currentPointer.keys.get(i+1)) < 1) 
						NodeKey = i;
			
			//If we reach the leaf node, we break, else we case the new pointer as an index node and continue
			if (currentPointer.children.get(NodeKey).isLeafNode == true) 
				break;
			else
				currentPointer = (IndexNode<K, T>) currentPointer.children.get(NodeKey);
		}
		
		LeafNode<K,T> leafNode = (LeafNode<K, T>) currentPointer.children.get(NodeKey);
		return leafNode;
	}
	
	/**
	 * TODO Insert a key/value pair into the BPlusTree
	 * 
	 * @param key
	 * @param value
	 */
	public void insert(K key, T value) {
		
		//If this is the first time an insertion is happening on the tree
		if (root == null) {
			LeafNode<K, T> leaf = new LeafNode<K, T>(key, value);
			root = leaf;
		}
		
		//The insertion will always come to this condition once the first 
		//key has been inserted
		else {
			
			//If root is a leaf node, attempt to insert the key
			if(root.isLeafNode == true) {
				((LeafNode<K, T>) root).insertSorted(key, value);
				
				//Checking if the latest insert statement caused an overflow. If true, we need to split the node and create an index node
				if(root.isOverflowed() == true){
					System.out.println("Leaf node overflowed. Creating new index node");
					LeafNode<K,T> overflowLeafnode = (LeafNode<K,T>)root;
					
					//Splitting the leaf node on the splitting key and creating the new right leaf node
					Entry<K, Node<K, T>> slittingKeyRightLeaf = splitLeafNode(overflowLeafnode);
					
					//Creating an index node with the new key pushed up and the two leaf nodes as its children
					IndexNode<K,T> newIndexNode = new IndexNode<K,T>(slittingKeyRightLeaf.getKey(), overflowLeafnode, slittingKeyRightLeaf.getValue());
					root = newIndexNode;
				}
			}
			
			//Insertions when root is an index node
			else {	
				
				//Find the leaf node where the would be if every rule was followed and insert the key there.
				IndexNode<K,T> currentPointer = (IndexNode<K,T>)root;
				LeafNode<K,T> leafNode = getLeafNodeGivenKey( currentPointer, key);
				leafNode.insertSorted(key, value);
				
				//Checking if the latest insert statement caused an overflow. If true, we need to split the node and push the 
				//splitting key to the index node on top
				if(leafNode.isOverflowed() == true){
					System.out.println("Leaf node overflowed. Pushing splitting element to upper index node");
					
					//Splitting the leaf node on the splitting key and creating the new right leaf node
					Entry<K, Node<K, T>> slittingKeyRightLeaf = splitLeafNode(leafNode);
					
					//Adding the splitting key and new right leaf to the above index node
					currentPointer.insertSorted(slittingKeyRightLeaf, currentPointer.keys.size());
				}
				
				//Checking if the latest insert on the index node caused it to overflow
				//TODO: What to do with the children of the index that if pushed up
				if(currentPointer.isOverflowed()){
					System.out.println("Index node overflowed.");
					
					//Splitting the index node on the splitting key and creating the new right index node
					Entry<K, Node<K, T>> slittingKeyRightIndex = splitIndexNode(currentPointer);
					
					IndexNode<K,T> newIndexNode = new IndexNode<K,T>(slittingKeyRightIndex.getKey(), currentPointer, slittingKeyRightIndex.getValue());
					root = newIndexNode;
				}
			}
		}
	}

	/**
	 * Split a leaf node and return the new right node and the splitting
	 * key as an Entry<slitingKey, RightNode>
	 * 
	 * @param leaf, any other relevant data
	 * @return the key/node pair as an Entry
	 */
	public Entry<K, Node<K,T>> splitLeafNode(LeafNode<K,T> leaf) { //Add params
		
		//This is the key that will get pushed up to the higher level
		K newKey = leaf.keys.get(D);
		
		//Creating a new leaf node with the upper half of the current lead
		List<K> rightChildKeys = leaf.keys.subList(D, 2*D+1);
		List<T> rightChildValues = leaf.values.subList(D, 2*D+1);
		LeafNode<K,T> rightChild = new LeafNode<K,T>(rightChildKeys, rightChildValues);
		
		//Dropping the keys and values of the right leaf node from the left leaf 
		leaf.keys = new ArrayList<K> (leaf.keys.subList(0, D));
		leaf.values = new ArrayList<T> (leaf.values.subList(0, D));
		
		//Keeping all the leaves linked to each other like a doubly linked list
		rightChild.nextLeaf = leaf.nextLeaf;
		leaf.nextLeaf = rightChild;
		rightChild.previousLeaf = leaf;
		
		//Returning the entry with the splitting key and the right child
		return new AbstractMap.SimpleEntry<K, Node<K,T>>(newKey, rightChild);
	}

	
	/**
	 * TODO split an indexNode and return the new right node and the splitting
	 * key as an Entry<slitingKey, RightNode>
	 * 
	 * @param index, any other relevant data
	 * @return new key/node pair as an Entry
	 */
	public Entry<K, Node<K,T>> splitIndexNode(IndexNode<K,T> index) { //Add params
		
		K newKey = index.keys.get(D);
		
		//Creating a new leaf node with the upper half of the current lead
		List<K> rightIndexKeys = index.keys.subList(D+1, 2*D+1);
		List<Node<K,T>> rightIndexchildren = index.children.subList(D+1, 2*D+1);
		IndexNode<K,T> rightChild = new IndexNode<K,T>(rightIndexKeys, rightIndexchildren);
		
		//Dropping the keys and values of the right leaf node from the left leaf 
		index.keys = new ArrayList<K> (index.keys.subList(0, D));
		index.children = new ArrayList<Node<K,T>> (index.children.subList(0, D));
		
		//Returning the entry with the splitting key and the right child
		return new AbstractMap.SimpleEntry<K, Node<K,T>>(newKey, rightChild);
	
	}

	/**
	 * TODO Delete a key/value pair from this B+Tree
	 * 
	 * @param key
	 */
	public void delete(K key) {

	}

	/**
	 * TODO Handle LeafNode Underflow (merge or redistribution)
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
	public int handleLeafNodeUnderflow(LeafNode<K,T> left, LeafNode<K,T> right,
			IndexNode<K,T> parent) {
		return -1;

	}

	/**
	 * TODO Handle IndexNode Underflow (merge or redistribution)
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
	public int handleIndexNodeUnderflow(IndexNode<K,T> leftIndex,
			IndexNode<K,T> rightIndex, IndexNode<K,T> parent) {
		return -1;
	}
	
	public static void main(String args[]) {
		Integer primeNumbers[] = new Integer[] { 2, 4, 5, 7, 8, 9, 10, 11, 12,
				13, 14, 15, 16 };
		String primeNumberStrings[] = new String[primeNumbers.length];
		for (int i = 0; i < primeNumbers.length; i++) {
			primeNumberStrings[i] = (primeNumbers[i]).toString();
		}
		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, primeNumbers, primeNumberStrings);
		
		//Searching example
		System.out.print(Utils.outputTree(tree));
		System.out.println(tree.search(4));
		
		
		/*BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		tree.insert(1, "a");
		tree.insert(2, "b");
		tree.insert(3, "c");
		System.out.println(tree.search(3));*/
	}
}
