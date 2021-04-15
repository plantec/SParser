package stree.parser;

import java.util.List;

public interface SNode {
	
	public int quote();

	public void setQuote(int q);

	public Boolean isLeaf();

	public String parsedString();

	public void setParsedString(String contents);

	public List<SNode> children();

	public void setParent(SNode parent);

	public SNode parent();

	public void addChild(SNode child);

	public int size();

	public SNode get(int no);

	public Boolean hasChildren();

	public void accept(SVisitor visitor);

}
