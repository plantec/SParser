package stree.test;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import stree.parser.SNode;
import stree.parser.SParser;
import stree.parser.SPrinter;
import stree.parser.SDefaultHandler;

class SPrinterTest {

	@Test
	void test1() throws IOException {
		SParser reader = new SParser();
		SDefaultHandler builder = new SDefaultHandler();
		SPrinter printer;
		List<SNode> nodes;
		SNode n;

		nodes = reader.parse(builder, "()");
		n = nodes.get(0);
		printer = new SPrinter();
		n.accept(printer);
		assertTrue(printer.result().toString().equals("(  )"));

		nodes = reader.parse(builder, "( 'Hello world' )");
		n = nodes.get(0);
		printer = new SPrinter();
		n.accept(printer);
		assertTrue(printer.result().toString().equals("( 'Hello world' )"));
		
		nodes = reader.parse(builder, "(X)");
		n = nodes.get(0);
		printer = new SPrinter();
		n.accept(printer);
		assertTrue(printer.result().toString().equals("( X )"));

		nodes = reader.parse(builder, "(`X)");
		n = nodes.get(0);
		printer = new SPrinter();
		n.accept(printer);
		assertTrue(printer.result().toString().equals("( `X )"));

		nodes = reader.parse(builder, "( 	X  )");
		n = nodes.get(0);
		printer = new SPrinter();
		n.accept(printer);
		assertTrue(printer.result().toString().equals("( X )"));

		nodes = reader.parse(builder, "(X Y)");
		n = nodes.get(0);
		printer = new SPrinter();
		n.accept(printer);
		assertTrue(printer.result().toString().equals("( X Y )"));

		nodes = reader.parse(builder, "( X ( Y ) Z )");
		n = nodes.get(0);
		printer = new SPrinter();
		n.accept(printer);
		System.out.println(((ByteArrayOutputStream) printer.result()).toString());
		assertTrue(printer.result().toString().equals("( X ( Y ) Z )"));

		nodes = reader.parse(builder, "( X ``( `Y ) Z )");
		n = nodes.get(0);
		printer = new SPrinter();
		n.accept(printer);
		assertTrue(printer.result().toString().equals("( X ``( `Y ) Z )"));

		nodes = reader.parse(builder, "( X = 1 ( Y  = X ) Z )");
		n = nodes.get(0);
		printer = new SPrinter();
		n.accept(printer);
		assertTrue(printer.result().toString().equals("( X = 1 ( Y = X ) Z )"));
	}
	
	@Test
	void testSample4() throws IOException {
		SParser reader = new SParser();
		SPrinter printer;
		List<SNode> nodes;
		String src = "(\n"
				+ "	(space := Space new)\n"
				+ "	(robi := Rect new)\n"
				+ "	(space add: robi)\n"
				+ "	(x := 0)\n"
				+ "	(y := 0)\n"
				+ "	(true whileTrue: (\n"
				+ "		(x < space width)\n"
				+ "			whileTrue: ( \n"
				+ "				(x := x + 1) \n"
				+ "				(robi moveTo: (x @ y))\n"
				+ "			)\n"
				+ "		(y < space height) \n"
				+ "			whileTrue: ( \n"
				+ "				(y := y + 1) \n"
				+ "				(robi moveTo: (x @ y))\n"
				+ "			)\n"
				+ "		(x > 0) \n"
				+ "			whileTrue: ( \n"
				+ "				(x := x - 1) \n"
				+ "				(robi moveTo: (x @ y))\n"
				+ "			)\n"
				+ "		(y > 0) \n"
				+ "			whileTrue: ( \n"
				+ "				(y := y - 1) \n"
				+ "				(robi moveTo: (x @ y))\n"
				+ "			)\n"
				+ "	)\n"
				+ "))";
		nodes = reader.parse(src);
		assertTrue(nodes.size()==1);
		printer = new SPrinter();
		printer.withIndentation(true);
		nodes.get(0).accept(printer);
		System.out.println(printer.result().toString());
		
		File f = new File("resources/sample4.sexp");
		nodes = reader.parse(new FileReader(f));
		for (SNode sn : nodes) {
			printer = new SPrinter();
			printer.withIndentation(true);
			sn.accept(printer);
			System.out.println(printer.result().toString());
		}
	}

	@Test
	void test2() throws IOException {
		SParser reader = new SParser();
		SPrinter printer;
		List<SNode> nodes;
		SNode n;
		
		nodes = reader.parse("((set c (Console new))\n (c print 'Hello World')\n"
				+ "	((Console new) print 'il était une fois:\\n\\'bla bla\\n\\'') \n" + ")");
		n = nodes.get(0);
		printer = new SPrinter();
		printer.withIndentation(true);
		n.accept(printer);
		System.out.println(((ByteArrayOutputStream) printer.result()).toString());
		assertTrue(printer.result().toString()
				.equals("( \n" + "  ( set c \n" + "    ( Console new ) ) \n" + "  ( c print 'Hello World' ) \n"
						+ "  ( \n" + "    ( Console new ) print 'il était une fois:\\n\\'bla bla\\n\\'' ) )"));
		
	}
	
	@Test
	void test4() throws IOException {
		SParser reader = new SParser();
		SPrinter printer;
		List<SNode> nodes;
		SNode n;
		
		nodes = reader.parse("(X) (Y)");
		assertTrue(nodes.size() == 2);
		printer = new SPrinter();
		n = nodes.get(0);
		n.accept(printer);
		assertTrue(printer.result().toString().equals("( X )"));
		printer = new SPrinter();
		n = nodes.get(1);
		n.accept(printer);
		assertTrue(printer.result().toString().equals("( Y )"));
		
	}
	
	@Test
	void test5() throws IOException {
		SParser reader = new SParser();
		SPrinter printer;
		List<SNode> nodes;
		SNode n;
		
		nodes = reader.parse("(a b | a + b)");
		assertTrue(nodes.size() == 1);
		printer = new SPrinter();
		n = nodes.get(0);
		n.accept(printer);
		System.out.println(printer.result());
	}

	@Test
	void testUtf8() throws IOException {
		SParser reader = new SParser();
		SPrinter printer;
		List<SNode> nodes;
		SNode n;
		
		nodes = reader.parse("(éàèî) (€æ‡ÒÂê∂ê∂†ê®†Ì)");
		assertTrue(nodes.size() == 2);
		printer = new SPrinter();
		n = nodes.get(0);
		n.accept(printer);
		assertTrue(printer.result().toString().equals("( éàèî )"));
		printer = new SPrinter();
		n = nodes.get(1);
		n.accept(printer);
		assertTrue(printer.result().toString().equals("( €æ‡ÒÂê∂ê∂†ê®†Ì )"));
		
	}
	@Test
	void test3() throws IOException {
		SParser reader = new SParser();
		SPrinter printer;
		List<SNode> nodes;
		SNode n;
				
		nodes = reader.parse("( space := ( Space new ) ) ( robi := ( Rect new ) ) ");
		assertTrue(nodes.size() == 2);
		printer = new SPrinter();
		n = nodes.get(0);
		n.accept(printer);
		System.out.println(printer.result().toString());
		assertTrue(printer.result().toString().equals("( space := ( Space new ) )"));
		n = nodes.get(1);
		printer = new SPrinter();
		n.accept(printer);
		System.out.println(printer.result().toString());
		assertTrue(printer.result().toString().equals("( robi := ( Rect new ) )"));
	}
	@Test
	
	void test6() throws IOException {
		SParser reader = new SParser();
		SPrinter printer;
		List<SNode> nodes;
		SNode n;
				
		nodes = reader.parse("{ space := [ Space new ] } ( robi := [ Rect new ] ) ");
		assertTrue(nodes.size() == 2);
		printer = new SPrinter();
		n = nodes.get(0);
		n.accept(printer);
		System.out.println(printer.result().toString());
		assertTrue(printer.result().toString().equals("{ space := [ Space new ] }"));
		n = nodes.get(1);
		printer = new SPrinter();
		n.accept(printer);
		System.out.println(printer.result().toString());
		assertTrue(printer.result().toString().equals("( robi := [ Rect new ] )"));
	}

}
