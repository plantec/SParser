package stree.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import stree.parser.SDefaultNode;
import stree.parser.SNode;
import stree.parser.SParser;
import stree.parser.SDefaultHandler;

class SOtherHandlerTest {

	class MySNode extends SDefaultNode {

	}

	class MySNodeBuilder implements SParser.SNodeBuilder {
		@Override
		public MySNode newNode(int openChar) {
			return new MySNode();
		}

		@Override
		public SNode newLeaf(String contents) {
			MySNode n = new MySNode();
			n.setParsedString(contents);
			return n;
		}
	}

	@Test
	void testNode() throws IOException {
		SParser reader = new SParser();
		SDefaultHandler builder = new SDefaultHandler();
		List<SNode> nodes;
		SNode n;

		builder.setNodeBuilder(new MySNodeBuilder());

		nodes = reader.parse(builder, "( X )");
		n = nodes.get(0);
		assertTrue(n != null);
		assertTrue(n instanceof MySNode);
		assertFalse(n.isLeaf());
		assertTrue(n.hasChildren());
		assertTrue(n.get(0) instanceof MySNode);

		builder.setNodeBuilder(new SParser.SNodeBuilder() {
			public MySNode newNode(int openChar) {
				return new MySNode();
			}

			@Override
			public SNode newLeaf(String contents) {
				MySNode n = new MySNode();
				n.setParsedString(contents);
				return n;
			}
		});

		nodes = reader.parse(builder, "( X )");
		n = nodes.get(0);
		assertTrue(n != null);
		assertTrue(n instanceof MySNode);
		assertFalse(n.isLeaf());
		assertTrue(n.hasChildren());
		assertTrue(n.get(0) instanceof MySNode);
	}

}
