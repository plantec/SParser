package stree.parser;

import java.util.ArrayList;
import java.util.List;

import stree.parser.SParser.SNodeBuilder;

public class SDefaultHandler implements SParser.SHandler {

	private SParser.SNodeBuilder nodeBuilder;
	
	private ArrayList<SNode> nodes;
	private SNode top;
	private int quote = 0;

	class SDefaultNodeBuilder implements SNodeBuilder {
		@Override
		public SNode newNode(int openChar) {
			SNode n = new SDefaultNode();
			n.setOpenTag(openChar);
			return n;
		}

		@Override
		public SNode newLeaf(String contents) {
			SNode n = new SDefaultNode();
			n.setParsedString(contents);
			return n;
		}
	}

	public SDefaultHandler() {
		this.nodes = new ArrayList<>();
		this.reset();
	}

	public void reset() {
		nodes.clear();
		this.top = null;
		this.quote = 0;
	}

	protected SNodeBuilder nodeBuilder() {
		return this.nodeBuilder == null ? new SDefaultNodeBuilder() : this.nodeBuilder;
	}

	public void setNodeBuilder(SNodeBuilder builder) {
		this.nodeBuilder = builder;
	}

	@Override
	public List<SNode> result() {
		return nodes;
	}

	protected void storeNode(SNode node) {
		if (this.top != null) {
			this.top.addChild((SNode) node);
		} else {
			nodes.add(node);
		}
	}

	protected SNode withQuote(SNode node) {
		node.setQuote(this.quote);
		this.quote = 0;
		return node;
	}

	protected SNode newNode(int openChar) {
		return this.withQuote(this.nodeBuilder().newNode(openChar));
	}

	protected SNode newLeaf(String contents) {
		return this.withQuote(this.nodeBuilder().newLeaf(contents));
	}

	@Override
	public void leaf(String contents) {
		this.storeNode(this.newLeaf(contents));
	}

	@Override
	public void startNode(int openChar) {
		SNode node = this.newNode(openChar);
		this.storeNode(node);
		this.top = node;
	}

	@Override
	public void endNode() {
		this.top = this.top.parent();
	}

	@Override
	public void comment(String c) {
	}

	@Override
	public void quote() {
		this.quote++;
	}

}
