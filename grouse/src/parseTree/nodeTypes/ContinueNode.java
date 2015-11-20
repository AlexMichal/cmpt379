package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import lexicalAnalyzer.Keyword;
import lexicalAnalyzer.Lextant;
import tokens.LextantToken;
import tokens.Token;

public class ContinueNode extends ParseNode {
	private ForStatementNode forStatementNodeLocation;
	
	public ContinueNode(Token token) {
		super(token);
		
		assert token.isLextant(Keyword.CONTINUE);
	}

	public ContinueNode(ParseNode node) {
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
	
	////////////////////////////////////////////////////////////
	// ACCEPT A VISITOR
	////////////////////////////////////////////////////////////
	
	public void accept(ParseNodeVisitor visitor) {
		visitor.visit(this);
	}
}
