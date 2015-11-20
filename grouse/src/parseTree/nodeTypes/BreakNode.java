package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import lexicalAnalyzer.Keyword;
import lexicalAnalyzer.Lextant;
import tokens.LextantToken;
import tokens.Token;

public class BreakNode extends ParseNode {
	private ForStatementNode forStatementNodeLocation;
	private WhileStatementNode whileStatementNodeLocation;
	
	public BreakNode(Token token) {
		super(token);
		
		assert token.isLextant(Keyword.BREAK);
	}

	public BreakNode(ParseNode node) {
		super(node);
	}
	
	////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////
	
	public Lextant getOperator() {
		return lextantToken().getLextant();
	}
	
	public LextantToken lextantToken() {
		return (LextantToken)token;
	}	
	
	public void setForStatementNodeLocation(ForStatementNode node) {
		this.forStatementNodeLocation = node;
	}
	
	public ForStatementNode getForStatementNodeLocation() {
		return forStatementNodeLocation;
	}
	
	public void setWhileStatementNodeLocation(WhileStatementNode node) {
		this.whileStatementNodeLocation = node;
	}
	
	public WhileStatementNode getWhileStatementNodeLocation() {
		return whileStatementNodeLocation;
	}
	
	////////////////////////////////////////////////////////////
	// ACCEPT A VISITOR
	////////////////////////////////////////////////////////////
	
	public void accept(ParseNodeVisitor visitor) {
		visitor.visit(this);
	}
}
