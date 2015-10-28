package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import lexicalAnalyzer.Keyword;
import lexicalAnalyzer.Lextant;
import tokens.LextantToken;
import tokens.Token;

public class IfStatementNode extends ParseNode {
	public IfStatementNode(Token token) {
		super(token);
		
		assert(token.isLextant(Keyword.IF));
	}

	public IfStatementNode(ParseNode node) {
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
	public static IfStatementNode withChildren(Token token, ParseNode expression, ParseNode ifStatementBlock) {
		IfStatementNode node = new IfStatementNode(token);
		
		node.appendChild(expression);
		node.appendChild(ifStatementBlock);
		
		return node;
	}
	
	// If statement with an Else statement
	public static IfStatementNode withChildren(Token token, ParseNode expression, ParseNode ifStatementBlock, ParseNode elseStatementBlock) {
		IfStatementNode node = new IfStatementNode(token);
		
		node.appendChild(expression);
		node.appendChild(ifStatementBlock);
		node.appendChild(elseStatementBlock);
		
		return node;
	}
	
	///////////////////////////////////////////////////////////
	// BOILERPLATE FOR VISITORS
	///////////////////////////////////////////////////////////
	
	public void accept(ParseNodeVisitor visitor) {
		visitor.visitEnter(this);
		visitChildren(visitor);
		visitor.visitLeave(this);
	}
}
