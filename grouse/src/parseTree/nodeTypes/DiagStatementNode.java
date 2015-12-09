package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;

import java.util.ArrayList;

import lexicalAnalyzer.Keyword;
import lexicalAnalyzer.Lextant;
import tokens.LextantToken;
import tokens.Token;

public class DiagStatementNode extends ParseNode {
	public DiagStatementNode(Token token) {
		super(token);
		
		assert(token.isLextant(Keyword.DIAG));
	}

	public DiagStatementNode(ParseNode node) {
		super(node);
	}

	////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////
	
	public Lextant getIfStatementType() {
		return lextantToken().getLextant();
	}
	
	public LextantToken lextantToken() {
		return (LextantToken)token;
	}
	
	////////////////////////////////////////////////////////////
	// CONVENIENCE FACTORY
	////////////////////////////////////////////////////////////
	
	// If statement without an Else statement
	public static DiagStatementNode withChildren(Token token, ArrayList<ParseNode> expressionList) {
		DiagStatementNode node = new DiagStatementNode(token);
		
		for (ParseNode expression : expressionList) {
			node.appendChild(expression);
		}
		
		return node;
	}
	
	// If statement with an Else statement
	
	///////////////////////////////////////////////////////////
	// BOILERPLATE FOR VISITORS
	///////////////////////////////////////////////////////////
	
	public void accept(ParseNodeVisitor visitor) {
		visitor.visitEnter(this);
		visitChildren(visitor);
		visitor.visitLeave(this);
	}
}
