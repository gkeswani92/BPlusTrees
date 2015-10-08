import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestsD1 {
	
	// add some nodes, see if it comes out right, delete one, see if it's right
		@Test
		public void testSimpleHybrid() {
			System.out.println("\n testSimpleHybrid");
			Character alphabet[] = new Character[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g' };
			String alphabetStrings[] = new String[alphabet.length];
			for (int i = 0; i < alphabet.length; i++) {
				alphabetStrings[i] = (alphabet[i]).toString();
			}
			BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
			Utils.bulkInsert(tree, alphabet, alphabetStrings);

			String test = Utils.outputTree(tree);
			String correct = "@c/e/@%%@b/@@d/@@f/@%%[(a,a);]#[(b,b);]$[(c,c);]#[(d,d);]$[(e,e);]#[(f,f);(g,g);]$%%";

			assertEquals(correct, test);

			tree.delete('f');
			Utils.printTree(tree);

			test = Utils.outputTree(tree);
			correct = "@c/e/@%%@b/@@d/@@f/@%%[(a,a);]#[(b,b);]$[(c,c);]#[(d,d);]$[(e,e);]#[(g,g);]$%%";
			assertEquals(correct, test);
			
			tree.delete('e');
			Utils.printTree(tree);
			
			test = Utils.outputTree(tree);
			correct = "@c/@%%@b/@@d/e/@%%[(a,a);]#[(b,b);]$[(c,c);]#[(d,d);]#[(g,g);]$%%";
			assertEquals(correct, test);

		}
	
}
