package stree.parser;

import java.util.ArrayList;
import java.util.List;

public class SDefaultNode implements SNode {

	private SNode parent;
	private Integer quote;
	private String parsedString;
	private List<SNode> children;
	private int openTag;
	
	protected List<SNode> newChildrenList() {
		return new ArrayList<>();
	}

	public SDefaultNode() {
		this.parsedString = null;
		this.children = this.newChildrenList();
		this.parent = null;
		this.quote = 0;
	}

	public int openTag() {
		return openTag;
	}
	
	public void setOpenTag(int openTag) {
		this.openTag = openTag;
	}
	
	public int quote() {
		return this.quote;
	}
	public void setQuote(int q) {
		this.quote = q;
	}
	
	public Boolean isLeaf() {
		return this.parsedString != null;
	}

	public String parsedString() {
		return this.parsedString;
	}

	public void setParsedString(String contents) {
		this.parsedString = contents;
	}

	public void addToContents(Character c) {
		if (this.parsedString == null)
			this.parsedString = "";
		this.parsedString += c;
	}

	public List<SNode> children() {
		return this.children;
	}

	public void setParent(SNode parent) {
		this.parent = parent;
	}
	
	public SNode parent() {
		return this.parent;
	}

	public void addChild(SNode child) {
		this.children.add(child);
		child.setParent(this);
	}
	
	public int size() {
		return this.children.size();
	}
	
	public SNode get(int no) {
		return this.children.get(no);
	}
	
	public Boolean hasChildren() {
		return this.children.size() > 0;
	}
	
	public void accept(SVisitor visitor) {
		if (this.isLeaf()) {
			visitor.visitLeaf(this);
		} else {
			visitor.visitNode(this);
		}
	}

}
