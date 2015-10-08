import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;

public class D3Tests {

	//D = 3 Leaf redistribution
	@Test
	public void testD3InsertionLeafRedistribute() {

		System.out.println("\n testD3Insertion");
		Character alphabet[] = new Character[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g' };
		String alphabetStrings[] = new String[alphabet.length];
		for (int i = 0; i < alphabet.length; i++) {
			alphabetStrings[i] = (alphabet[i]).toString();
		}

		BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
		Utils.bulkInsert(tree, alphabet, alphabetStrings);
		String test = Utils.outputTree(tree);
		String correct = "@d/@%%[(a,a);(b,b);(c,c);]#[(d,d);(e,e);(f,f);(g,g);]$%%";
		assertEquals(correct, test);

		tree.delete('a');
		test = Utils.outputTree(tree);
		correct = "@e/@%%[(b,b);(c,c);(d,d);]#[(e,e);(f,f);(g,g);]$%%";
		assertEquals(correct, test);
	}
	
	//D = 3 Leaf merging left heavy
	@Test
	public void testD3InsertionLeafRedistributeLeftSide() {

		System.out.println("\n testD3Insertion");
		Character alphabet[] = new Character[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g' };
		String alphabetStrings[] = new String[alphabet.length];
		for (int i = 0; i < alphabet.length; i++) {
			alphabetStrings[i] = (alphabet[i]).toString();
		}

		BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
		Utils.bulkInsert(tree, alphabet, alphabetStrings);
		String test = Utils.outputTree(tree);
		String correct = "@d/@%%[(a,a);(b,b);(c,c);]#[(d,d);(e,e);(f,f);(g,g);]$%%";
		assertEquals(correct, test);

		tree.delete('e');
		tree.delete('f');
		test = Utils.outputTree(tree);
		correct = "[(a,a);(b,b);(c,c);(d,d);(g,g);]$%%";
		assertEquals(correct, test);
	}
	
	//D = 3 Leaf Merge when non index key is deleted
	@Test
	public void testD3InsertionLeafMerge() {

		Integer primeNumbers[] = new Integer[] { 2, 4, 5, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
		String primeNumberStrings[] = new String[primeNumbers.length];
		for (int i = 0; i < primeNumbers.length; i++) {
			primeNumberStrings[i] = (primeNumbers[i]).toString();	
		}
	
		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, primeNumbers, primeNumberStrings);
		String test = Utils.outputTree(tree);
		String correct = "@7/10/13/@%%[(2,2);(4,4);(5,5);]#[(7,7);(8,8);(9,9);]#[(10,10);(11,11);(12,12);]#[(13,13);(14,14);(15,15);(16,16);]$%%";
		assertEquals(test, correct);
		
		tree.delete(2);
		test = Utils.outputTree(tree);
		Utils.printTree(tree);
		correct = "@10/13/@%%[(4,4);(5,5);(7,7);(8,8);(9,9);]#[(10,10);(11,11);(12,12);]#[(13,13);(14,14);(15,15);(16,16);]$%%";	
		assertEquals(test, correct);
	}
	
	//D = 3 Leaf Merge when index key is deleted
	@Test
	public void testD3InsertionLeafMergeIndexKeyDelete() {

		Integer primeNumbers[] = new Integer[] { 2, 4, 5, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
		String primeNumberStrings[] = new String[primeNumbers.length];
		for (int i = 0; i < primeNumbers.length; i++) {
			primeNumberStrings[i] = (primeNumbers[i]).toString();	
		}
	
		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, primeNumbers, primeNumberStrings);
		String test = Utils.outputTree(tree);
		String correct = "@7/10/13/@%%[(2,2);(4,4);(5,5);]#[(7,7);(8,8);(9,9);]#[(10,10);(11,11);(12,12);]#[(13,13);(14,14);(15,15);(16,16);]$%%";
		assertEquals(test, correct);
		
		tree.delete(7);
		test = Utils.outputTree(tree);
		Utils.printTree(tree);
		correct = "@10/13/@%%[(2,2);(4,4);(5,5);(8,8);(9,9);]#[(10,10);(11,11);(12,12);]#[(13,13);(14,14);(15,15);(16,16);]$%%";	
		assertEquals(test, correct);
	}
	
	//D = 3 Index node redistribution
	@Test
	public void testD3InsertionIndexRedistribution() {

		Integer primeNumbers[] = new Integer[] { 1, 2, 3, 4, 5, 7, 8, 9, 10, 11, 12,13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30 };
		String primeNumberStrings[] = new String[primeNumbers.length];
		for (int i = 0; i < primeNumbers.length; i++) {
			primeNumberStrings[i] = (primeNumbers[i]).toString();
		}

		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, primeNumbers, primeNumberStrings);
		String test = Utils.outputTree(tree);
		String correct = "@14/@%%@4/8/11/@@17/20/23/26/@%%[(1,1);(2,2);(3,3);]#[(4,4);(5,5);(7,7);]#[(8,8);(9,9);(10,10);]#[(11,11);(12,12);(13,13);]$[(14,14);(15,15);(16,16);]#[(17,17);(18,18);(19,19);]#[(20,20);(21,21);(22,22);]#[(23,23);(24,24);(25,25);]#[(26,26);(27,27);(28,28);(29,29);(30,30);]$%%";
		assertEquals(correct, test);
		
		tree.delete(11);
		test = Utils.outputTree(tree);
		Utils.printTree(tree);
		correct = "@17/@%%@4/8/14/@@20/23/26/@%%[(1,1);(2,2);(3,3);]#[(4,4);(5,5);(7,7);]#[(8,8);(9,9);(10,10);(12,12);(13,13);]#[(14,14);(15,15);(16,16);]$[(17,17);(18,18);(19,19);]#[(20,20);(21,21);(22,22);]#[(23,23);(24,24);(25,25);]#[(26,26);(27,27);(28,28);(29,29);(30,30);]$%%";
		assertEquals(correct, test);
	}
	
	//D = 3 Index Node Merging
	@Test
	public void testD3InsertionIndexMerge() {

		Integer primeNumbers[] = new Integer[] { 1, 2, 3, 4, 5, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30 };
		String primeNumberStrings[] = new String[primeNumbers.length];
		for (int i = 0; i < primeNumbers.length; i++) {
			primeNumberStrings[i] = (primeNumbers[i]).toString();
		}

		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, primeNumbers, primeNumberStrings);
		String test = Utils.outputTree(tree);

		tree.delete(26);
		tree.delete(23);
		test = Utils.outputTree(tree);
		Utils.printTree(tree);
		String correct = "@14/@%%@4/8/11/@@17/20/26/@%%[(1,1);(2,2);(3,3);]#[(4,4);(5,5);(7,7);]#[(8,8);(9,9);(10,10);]#[(11,11);(12,12);(13,13);]$[(14,14);(15,15);(16,16);]#[(17,17);(18,18);(19,19);]#[(20,20);(21,21);(22,22);(24,24);(25,25);]#[(27,27);(28,28);(29,29);(30,30);]$%%";
		assertEquals(test, correct);
	}
	
	//Tree's root becomes null when all elements are deleted
	@Test
	public void testDeleteAll() {
		
		Character alphabet[] = new Character[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g' };
		String alphabetStrings[] = new String[alphabet.length];
		for (int i = 0; i < alphabet.length; i++) {
			alphabetStrings[i] = (alphabet[i]).toString();
		}
		BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
		Utils.bulkInsert(tree, alphabet, alphabetStrings);
		
		tree.delete('a');
		tree.delete('b');
		tree.delete('c');
		tree.delete('d');
		tree.delete('e');
		tree.delete('f');
		tree.delete('g');
		assertEquals(null, tree.root);
	}
	
	// Testing appropriate depth and node invariants on a big tree
	@Test
	public void testLargeTree() {
		BPlusTree<Integer, Integer> tree = new BPlusTree<Integer, Integer>();
		ArrayList<Integer> numbers = new ArrayList<Integer>(100000);
		for (int i = 0; i < 100000; i++) {
			numbers.add(i);
		}
		Collections.shuffle(numbers);
		for (int i = 0; i < 100000; i++) {
			tree.insert(numbers.get(i), numbers.get(i));
		}
		testTreeInvariants(tree);
		assertTrue(treeDepth(tree.root) < 11);
	}

	public <K extends Comparable<K>, T> void testTreeInvariants(BPlusTree<K, T> tree) {
		for (Node<K, T> child : ((IndexNode<K, T>) (tree.root)).children)
			testNodeInvariants(child);
	}

	public <K extends Comparable<K>, T> void testNodeInvariants(Node<K, T> node) {
		assertFalse(node.keys.size() > 2 * BPlusTree.D);
		assertFalse(node.keys.size() < BPlusTree.D);
		if (!(node.isLeafNode))
			for (Node<K, T> child : ((IndexNode<K, T>) node).children)
				testNodeInvariants(child);
	}

	public <K extends Comparable<K>, T> int treeDepth(Node<K, T> node) {
		if (node.isLeafNode)
			return 1;
		int childDepth = 0;
		int maxDepth = 0;
		for (Node<K, T> child : ((IndexNode<K, T>) node).children) {
			childDepth = treeDepth(child);
			if (childDepth > maxDepth)
				maxDepth = childDepth;
		}
		return (1 + maxDepth);
	}
	
	@Test
	public void test() {
        Integer primeNumbers[] = new Integer[] { 2, 4, 5, 7, 8, 9, 10, 11, 12,
                13, 14, 15, 16,17,18,19,20,21,22,1 };
        String primeNumberStrings[] = new String[primeNumbers.length];
        for (int i = 0; i < primeNumbers.length; i++) {
            primeNumberStrings[i] = (primeNumbers[i]).toString();
        }
        BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
        Utils.bulkInsert(tree, primeNumbers, primeNumberStrings);
        Utils.printTree(tree);
        String correct = "@10/16/@%%@5/8/@@12/14/@@18/20/@%%[(1,1);(2,2);(4,4);]#[(5,5);(7,7);]#[(8,8);(9,9);]$[(10,10);(11,11);]#[(12,12);(13,13);]#[(14,14);(15,15);]$[(16,16);(17,17);]#[(18,18);(19,19);]#[(20,20);(21,21);(22,22);]$%%";
       // System.out.println("correct \n" +correct);
        //System.out.println( tree.search(22));
        tree.delete(2);
        assertEquals("@7/10/13/16/19/@%%[(1,1);(4,4);(5,5);]#[(7,7);(8,8);(9,9);]#[(10,10);(11,11);(12,12);]#[(13,13);(14,14);(15,15);]#[(16,16);(17,17);(18,18);]#[(19,19);(20,20);(21,21);(22,22);]$%%",Utils.outputTree(tree));
        tree.delete(1);
        //Utils.printTree(tree);
        assertEquals("@10/13/16/19/@%%[(4,4);(5,5);(7,7);(8,8);(9,9);]#[(10,10);(11,11);(12,12);]#[(13,13);(14,14);(15,15);]#[(16,16);(17,17);(18,18);]#[(19,19);(20,20);(21,21);(22,22);]$%%",Utils.outputTree(tree));
        tree.delete(8);
       // Utils.printTree(tree);
        assertEquals("@10/13/16/19/@%%[(4,4);(5,5);(7,7);(9,9);]#[(10,10);(11,11);(12,12);]#[(13,13);(14,14);(15,15);]#[(16,16);(17,17);(18,18);]#[(19,19);(20,20);(21,21);(22,22);]$%%",Utils.outputTree(tree));
        tree.delete(19);
        //Utils.printTree(tree);
        assertEquals("@10/13/16/19/@%%[(4,4);(5,5);(7,7);(9,9);]#[(10,10);(11,11);(12,12);]#[(13,13);(14,14);(15,15);]#[(16,16);(17,17);(18,18);]#[(20,20);(21,21);(22,22);]$%%",Utils.outputTree(tree));
        tree.delete(10);
        //Utils.printTree(tree);//ok
        System.out.println("Answer=");
        System.out.println(Utils.outputTree(tree));
        assertEquals("@9/13/16/19/@%%[(4,4);(5,5);(7,7);]#[(9,9);(11,11);(12,12);]#[(13,13);(14,14);(15,15);]#[(16,16);(17,17);(18,18);]#[(20,20);(21,21);(22,22);]$%%",Utils.outputTree(tree));
        tree.delete(4);
        
        assertEquals("@13/16/19/@%%[(5,5);(7,7);(9,9);(11,11);(12,12);]#[(13,13);(14,14);(15,15);]#[(16,16);(17,17);(18,18);]#[(20,20);(21,21);(22,22);]$%%",Utils.outputTree(tree));
     
        tree.delete(22);
        
        assertEquals("@13/16/@%%[(5,5);(7,7);(9,9);(11,11);(12,12);]#[(13,13);(14,14);(15,15);]#[(16,16);(17,17);(18,18);(20,20);(21,21);]$%%",Utils.outputTree(tree));
        
        
        tree.delete(18);
        assertEquals("@13/16/@%%[(5,5);(7,7);(9,9);(11,11);(12,12);]#[(13,13);(14,14);(15,15);]#[(16,16);(17,17);(20,20);(21,21);]$%%", Utils.outputTree(tree));

        tree.delete(7);
        assertEquals("@13/16/@%%[(5,5);(9,9);(11,11);(12,12);]#[(13,13);(14,14);(15,15);]#[(16,16);(17,17);(20,20);(21,21);]$%%",Utils.outputTree(tree) );

        tree.delete(14);
        tree.delete(11);
        assertEquals("@16/@%%[(5,5);(9,9);(12,12);(13,13);(15,15);]#[(16,16);(17,17);(20,20);(21,21);]$%%", Utils.outputTree(tree));

        tree.delete(5);
        assertEquals("@16/@%%[(9,9);(12,12);(13,13);(15,15);]#[(16,16);(17,17);(20,20);(21,21);]$%%", Utils.outputTree(tree));

        tree.delete(15);
        tree.delete(20);
        assertEquals("@16/@%%[(9,9);(12,12);(13,13);]#[(16,16);(17,17);(21,21);]$%%", Utils.outputTree(tree));

        tree.delete(16);
        tree.delete(17);
        assertEquals("[(9,9);(12,12);(13,13);(21,21);]$%%", Utils.outputTree(tree));

        tree.delete(13);
        assertEquals("[(9,9);(12,12);(21,21);]$%%", Utils.outputTree(tree));

        tree.delete(9);
        assertEquals("[(12,12);(21,21);]$%%", Utils.outputTree(tree));

        tree.delete(12);
        tree.delete(21);
        assertEquals(null, tree.root);
	}

}
