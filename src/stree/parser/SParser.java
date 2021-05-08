package stree.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class SParser {

	private Reader input;
	private SHandler handler;
	private String pending;
	private final Character[] whites = { ' ', '\r', '\n', '\t' };
	private Deque<Character> closingStk;
	private int line;
	private int pos;
	private int level;

	public interface SNodeBuilder {
		SNode newNode(int openChar);

		SNode newLeaf(String contents);
	}

	public interface SHandler {
		List<SNode> result();

		void reset();

		void leaf(String a);

		void startNode(int openChar);

		void endNode();

		void comment(String c);

		void quote();
	}

	protected SParser.SHandler defaulHandler() {
		return new SDefaultHandler();
	};

	protected int read() throws IOException {
		int n = this.input.read();
		if ((char) n == '\n') {
			this.line++;
			this.pos = 0;
		} else {
			this.pos++;
		}
		return n;
	}

	public List<SNode> parse(SHandler handler, String src) throws IOException {
		return this.parse(handler, new StringReader(src));
	}

	public List<SNode> parse(String src) throws IOException {
		return this.parse(new StringReader(src));
	}

	public List<SNode> parse(Reader input) throws IOException {
		return this.parse(this.defaulHandler(), input);
	}

	public List<SNode> parse(SHandler handler, Reader input) throws IOException {
		this.input = input;
		this.handler = handler;
		this.closingStk = new ArrayDeque<>();
		this.pending = "";
		this.line = 1;
		this.pos = 0;
		this.level = 0;
		this.handler.reset();
		this.parse();
		return handler.result();
	}

	private void opening(int openChar) {
		switch (openChar) {
		case '(':
			this.closingStk.push(')');
			break;
		case '{':
			this.closingStk.push('}');
			break;
		case '[':
			this.closingStk.push(']');
			break;
		}
		this.flushPending();
		this.level++;
		this.handler.startNode(openChar);
	}

	private void closing(int closeChar) {
		if (closingStk.isEmpty()) {
			throw new SSyntaxError("too much closing: ", this.line, this.pos);
		}
		Character intended = closingStk.pop();
		if (closeChar != intended) {
			throw new SSyntaxError("bad closing, have '"+ (char) closeChar + "', intended '"+ intended + "'", this.line, this.pos);
		}
		this.flushPending();
		this.level--;
		if (this.level < 0)
			throw new SSyntaxError("Too much closing", this.line, this.pos);
		this.handler.endNode();
	}

	protected int parse() throws IOException {
		do {
			int n = this.read();
			switch (n) {
			case -1:
				if (this.level > 0) {
					throw new SSyntaxError("Missing closing", this.line, this.pos);
				}
				this.flushPending();
				return n;

			case ')':
			case ']':
			case '}':
				this.closing(n);
				break;

			case ';':
				this.flushPending();
				this.readComment();
				break;

			case '(':
			case '[':
			case '{':
				this.opening(n);
				break;

			case '\'':
				this.readString();
				break;

			case '`':
				this.handler.quote();
				break;

			default:
				if (this.isWhite((char) n)) {
					this.flushPending();
				} else {
					this.pending += ((char) n);
				}
			}

		} while (true);
	}

	protected void readComment() throws IOException {
		int n;
		String comment = "";
		do {
			n = this.read();
			if (n == -1) {
				return;
			}
			comment += n;
		} while (n != '\n');
		this.handler.comment(comment);
	}

	protected void readString() throws IOException {
		this.pending += ('\'');
		int l = this.line;
		int p = this.pos;
		do {
			int n = this.read();
			if ((char) n == '\'') {
				this.pending += ((char) n);
				return;
			}
			if (n == -1) {
				if (n == -1) {
					throw new SSyntaxError("Unclosed string", l, p);
				}
			}
			if ((char) n == '\\') {
				this.readEscape();
			} else {
				this.pending += ((char) n);
			}
		} while (true);
	}

	protected void readEscape() throws IOException {
		int n = this.read();
		if (n == -1) {
			throw new SSyntaxError(this.line, this.pos);
		}
		this.pending += '\\';
		this.pending += ((char) n);
	}

	protected Boolean flushPending() {
		if (!this.pending.isEmpty()) {
			this.handler.leaf(this.pending);
			this.pending = "";
			return true;
		}
		return false;
	}

	protected Boolean isWhite(char n) {
		return (Arrays.asList(this.whites).contains(n));
	}

}
