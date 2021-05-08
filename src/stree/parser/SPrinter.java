package stree.parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Consumer;

interface Doer {
	public void execute();
}

public class SPrinter implements SVisitor {
	protected OutputStream stream;
	Boolean withIndentation;
	int level;
	int indentSize;

	public SPrinter() {
		this(new ByteArrayOutputStream());
	}

	public SPrinter(OutputStream stream) {
		this.stream = stream;
		this.level = 0;
		this.withIndentation = false;
		this.indentSize = 2;
	}

	public void withIndentation(Boolean withIndentation) {
		this.withIndentation = withIndentation;
	}

	public OutputStream result() {
		return this.stream;
	}

	protected void forEachSepBy(List<? extends SNode> l, Consumer<SNode> cons, Doer d) {
		int count = 0;
		for (SNode e : l) {
			cons.accept(e);
			count++;
			if (count < l.size()) {
				d.execute();
			}
		}
	}

	protected void indent() {
		for (int i = 0; i < this.indentSize; i++) {
			this.write(' ');
		}
	}

	protected void space() {
		this.write(' ');
	}

	protected void write(char c) {
		try {
			this.stream.write(c);
		} catch (IOException e) {
			e.printStackTrace();
			throw new Error(e);
		}
	}

	protected void write(String s) {
		try {
			this.stream.write(s.getBytes());
		} catch (IOException e) {
			throw new Error(e);
		}
	}

	@Override
	public void visitNode(SNode node) {
		if (this.withIndentation && this.level > 0) {
			this.write('\n');
			for (int i = 0; i < level; i++) {
				this.indent();
			}
		}		
		for (int i = 0; i < node.quote(); i++)
			this.write('`');
		this.write((char)node.openTag());
		this.write(' ');
		if (node.hasChildren()) {
			this.level++;
			this.forEachSepBy(node.children(), s -> s.accept(this), this::space);
			this.level--;
		}
		this.write(' ');
		switch (node.openTag()) {
		case '(':
			this.write(')');
			break;
		case '{':
			this.write('}');
			break;
		case '[':
			this.write(']');
			break;
		}
	}

	@Override
	public void visitLeaf(SNode node) {
		for (int i = 0; i < node.quote(); i++)
			this.write('`');
		this.write(node.parsedString());
	}

}
