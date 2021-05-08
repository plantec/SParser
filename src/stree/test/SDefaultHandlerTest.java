package stree.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import stree.parser.SNode;
import stree.parser.SParser;
import stree.parser.SPrinter;
import stree.parser.SSyntaxError;
import stree.parser.SDefaultHandler;

class SDefaultHandlerTest {

	@Test
	void testSample1() throws IOException {
		SParser reader = new SParser();
		File f = new File("resources/sample1.sexp");
		List<SNode> nodes;
		SNode n;

		nodes = reader.parse(new FileReader(f));
		n = nodes.get(0);
		
		SPrinter printer = new SPrinter();
		n.accept(printer);
		System.out.println(printer.result().toString());
		assertTrue(n.size() == 3);
		SNode n1 = n.get(0);
		assertTrue(n1.parsedString().equals("robê"));
		SNode n2 = n.get(1);
		assertTrue(n2.parsedString().equals("color:"));
		SNode n3 = n.get(2);
		assertTrue(n3.size() == 2);
		assertTrue(n3.get(0).parsedString().equals("Color"));
		assertTrue(n3.get(1).parsedString().equals("blue"));
	}

	@Test
	void testSample2() throws IOException {
		SParser reader = new SParser();
		File f = new File("resources/sample2.sexp");
		List<SNode> nodes;
		SNode n;

		nodes = reader.parse(new FileReader(f));
		n = nodes.get(0);

		SPrinter printer = new SPrinter();

		n.accept(printer);
		System.out.println(printer.result().toString());

		assertTrue(n.size() == 3);
		SNode n1 = n.get(0);
		assertTrue(n1.get(0).parsedString().equals("set"));
		assertTrue(n1.get(1).parsedString().equals("c"));
		SNode n13 = n1.get(2);
		assertTrue(n13.get(0).parsedString().equals("Console"));
		assertTrue(n13.get(1).parsedString().equals("new"));

		SNode n2 = n.get(1);
		assertTrue(n2.size() == 3);
		assertTrue(n2.get(0).parsedString().equals("c"));
		assertTrue(n2.get(1).parsedString().equals("print"));
		assertTrue(n2.get(2).parsedString().equals("'Hello World'"));

		SNode n3 = n.get(2);
		assertTrue(n3.size() == 3);
		SNode n31 = n3.get(0);
		assertTrue(n31.size() == 2);
		assertTrue(n31.get(0).parsedString().equals("Console"));
		assertTrue(n31.get(1).parsedString().equals("new"));

		assertTrue(n3.get(1).parsedString().equals("print"));
		System.out.println(n3.get(2).parsedString());
		assertTrue(n3.get(2).parsedString().equals("'He said:\\n\\'bla bla\\n\\''"));
	}

	@Test
	void testSample3() throws IOException {
		SParser reader = new SParser();
		File f = new File("resources/sample3.sexp");
		List<SNode> nodes;
		SNode n;

		nodes = reader.parse(new FileReader(f));
		n = nodes.get(0);

		SPrinter printer = new SPrinter();

		n.accept(printer);
		System.out.println(printer.result().toString());
	}

	@Test
	void test2() throws IOException {
		SParser reader = new SParser();
		List<SNode> nodes;
		SNode n;

		nodes = reader.parse(" A ");
		n = nodes.get(0);
		assertTrue(n != null);
		assertTrue(n.size() == 0);
		assertTrue(n.parsedString().equals("A"));

		nodes = reader.parse(" A ; with a comment");
		n = nodes.get(0);
		assertTrue(n != null);
		assertTrue(n.size() == 0);
		assertTrue(n.parsedString().equals("A"));

		nodes = reader.parse(" 'Hello world' ");
		n = nodes.get(0);
		assertTrue(n != null);
		assertTrue(n.size() == 0);
		assertTrue(n.parsedString().equals("'Hello world'"));

		nodes = reader.parse("( \\ )");
		n = nodes.get(0);
		assertTrue(n != null);
		assertTrue(n.size() == 1);
		assertTrue(n.get(0).parsedString().equals("\\"));

		nodes = reader.parse("( \n )");
		n = nodes.get(0);
		assertTrue(n != null);
		assertFalse(n.hasChildren());
		assertFalse(n.isLeaf());

		nodes = reader.parse("( \\n )");
		n = nodes.get(0);
		assertTrue(n != null);
		assertTrue(n.size() == 1);
		assertTrue(n.get(0).parsedString().equals("\\n"));

		nodes = reader.parse("( '' )");
		n = nodes.get(0);
		assertTrue(n != null);
		assertTrue(n.size() == 1);
		assertTrue(n.get(0).parsedString().equals("''"));

		nodes = reader.parse("( '\\\\' )");
		n = nodes.get(0);
		assertTrue(n != null);
		assertTrue(n.size() == 1);
		assertTrue(n.get(0).parsedString().equals("'\\\\'"));

		nodes = reader.parse("( '\\n\\\\' )");
		n = nodes.get(0);
		assertTrue(n != null);
		assertTrue(n.size() == 1);
		assertTrue(n.get(0).parsedString().equals("'\\n\\\\'"));

		nodes = reader.parse("( '\\'' )");
		n = nodes.get(0);
		assertTrue(n != null);
		assertTrue(n.size() == 1);
		assertTrue(n.get(0).parsedString().equals("'\\''"));

		nodes = reader.parse("( `( ) )");
		n = nodes.get(0);
		assertTrue(n != null);
		assertTrue(n.size() == 1);
		assertTrue(n.get(0).size() == 0);
	}

	@Test
	void test3() throws IOException {
		SParser reader = new SParser();
		SDefaultHandler builder = new SDefaultHandler();
		List<SNode> nodes;
		SNode n;

		nodes = reader.parse(builder, "( 'hello' )");
		n = nodes.get(0);
		assertTrue(n != null);
		assertTrue(n.size() == 1);
		assertTrue(n.get(0).parsedString().equals("'hello'"));

		nodes = reader.parse(builder, "( \\hello\\)");
		n = nodes.get(0);
		assertTrue(n != null);
		assertTrue(n.size() == 1);
		assertTrue(n.get(0).parsedString().equals("\\hello\\"));

		nodes = reader.parse(builder, "( '\\X\\'' )");
		n = nodes.get(0);
		assertTrue(n != null);
		assertTrue(n.size() == 1);
		assertTrue(n.get(0).parsedString().equals("'\\X\\''"));

		nodes = reader.parse(builder, "( 'Hello\\nworld' 'Y' )");
		n = nodes.get(0);
		assertTrue(n != null);
		assertTrue(n.size() == 2);
		assertFalse(n.isLeaf());
		assertTrue(n.get(0).parsedString().equals("'Hello\\nworld'"));
		assertTrue(n.get(1).parsedString().equals("'Y'"));

		nodes = reader.parse(builder, "( '\\'Hello world\\'' 'Y')");
		n = nodes.get(0);
		assertTrue(n != null);
		assertTrue(n.size() == 2);
		assertFalse(n.isLeaf());
		assertTrue(n.get(0).parsedString().equals("'\\'Hello world\\''"));
		assertTrue(n.get(1).parsedString().equals("'Y'"));
	}

	@Test
	void test4() throws IOException {
		SParser reader = new SParser();
		SDefaultHandler builder = new SDefaultHandler();
		List<SNode> nodes;
		SNode n;

		nodes = reader.parse(builder, "( `A )");
		n = nodes.get(0);
		assertTrue(n != null);
		assertTrue(n.size() == 1);
		assertTrue(n.get(0).parsedString().equals("A"));
		assertTrue(n.get(0).quote() == 1);

		nodes = reader.parse(builder, "( ``A )");
		n = nodes.get(0);
		assertTrue(n != null);
		assertTrue(n.size() == 1);
		assertTrue(n.get(0).parsedString().equals("A"));
		assertTrue(n.get(0).quote() == 2);

		nodes = reader.parse(builder, "( A )");
		n = nodes.get(0);
		assertTrue(n != null);
		assertTrue(n.size() == 1);
		assertTrue(n.quote() == 0);
		assertTrue(n.get(0).parsedString().equals("A"));

		nodes = reader.parse(builder, "( `( A ) )");
		n = nodes.get(0);
		assertTrue(n != null);
		assertTrue(n.size() == 1);
		assertTrue(n.get(0).size() == 1);
		assertTrue(n.get(0).get(0).parsedString().equals("A"));
		assertTrue(n.get(0).quote() == 1);

		nodes = reader.parse(builder, "( ```( `A B C ) )");
		n = nodes.get(0);
		assertTrue(n != null);
		assertTrue(n.size() == 1);
		assertTrue(n.get(0).size() == 3);
		assertTrue(n.get(0).get(0).parsedString().equals("A"));
		assertTrue(n.get(0).get(0).quote() == 1);
		assertTrue(n.get(0).quote() == 3);
		
		nodes = reader.parse(builder, "( (X) (Y) )");
		assertTrue(nodes.size() == 1);
		n = nodes.get(0);
		assertTrue(n.size() == 2);
		assertTrue(n.get(0).get(0).parsedString().equals("X"));
		assertTrue(n.get(1).get(0).parsedString().equals("Y"));
	}

	@Test
	void testSeveralNodes() throws IOException {
		SParser reader = new SParser();
		SDefaultHandler builder = new SDefaultHandler();
		List<SNode> nodes;
		SNode n;
		nodes = reader.parse(builder, "X Y");
		assertTrue(nodes.size() == 2);
		n = nodes.get(0);
		assertTrue(n.parsedString().equals("X"));
		n = nodes.get(1);
		assertTrue(n.parsedString().equals("Y"));
		
		nodes = reader.parse(builder, "0 (X) () (Y) 9");
		assertTrue(nodes.size() == 5);
		n = nodes.get(0);
		assertTrue(n.isLeaf());
		assertTrue(n.parsedString().equals("0"));
		n = nodes.get(1);
		assertFalse(n.isLeaf());
		assertTrue(n.size()==1);
		assertTrue(n.get(0).parsedString().equals("X"));
		n = nodes.get(2);
		assertFalse(n.isLeaf());
		assertTrue(n.size()==0);
		n = nodes.get(3);
		assertTrue(n.size()==1);
		assertTrue(n.get(0).parsedString().equals("Y"));
		n = nodes.get(4);
		assertTrue(n.isLeaf());
		assertTrue(n.parsedString().equals("9"));
	}
	
	@Test
	void testSyntaxErrors() throws IOException {
		SParser reader = new SParser();
		SDefaultHandler builder = new SDefaultHandler();
		try {
			reader.parse(builder, "( 'not ended string )");
			assertTrue(false);
		} catch (SSyntaxError e) {
			// e.printStackTrace();
			assertTrue(true);
		}
		try {
			reader.parse(builder, "( not ended s expression");
			assertTrue(false);
		} catch (SSyntaxError e) {
			// e.printStackTrace();
			assertTrue(true);
		}
		try {
			reader.parse(builder, "( not ( ended (s sub) expression)");
			assertTrue(false);
		} catch (SSyntaxError e) {
			// e.printStackTrace();
			assertTrue(true);
		}

		try {
			reader.parse(builder, "X Y)");
			assertFalse(true);
		} catch (SSyntaxError e) {
			assertTrue(true);
		}
		try {
			reader.parse(builder, "(X Y");
			assertFalse(true);
		} catch (SSyntaxError e) {
			assertTrue(true);
		}
		try {
			reader.parse(builder, ")");
			assertFalse(true);
		} catch (SSyntaxError e) {
			assertTrue(true);
		}
		try {
			reader.parse(builder, "(");
			assertFalse(true);
		} catch (SSyntaxError e) {
			assertTrue(true);
		}
	}

	@Test
	void testRobiScript() throws IOException {
		SParser reader = new SParser();
		reader.parse("(space color black) (robi color yellow)");
	}

}
