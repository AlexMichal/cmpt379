package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import lexicalAnalyzer.Keyword;
import lexicalAnalyzer.Lextant;
import tokens.LextantToken;
import tokens.Token;

public class DiagStatementNode extends ParseNode {
	public DiagStatementNode(Token token) {
		super(token);
		
		assert(token.isLextant(Keyword.IF));
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
	public static DiagStatementNode withChildren(Token token, ParseNode expression, ParseNode ifStatementBlock) {
		DiagStatementNode node = new DiagStatementNode(token);
		
		node.appendChild(expression);
		node.appendChild(ifStatementBlock);
		
		return node;
	}
	
	// If statement with an Else statement
	public static DiagStatementNode withChildren(Token token, ParseNode expression, ParseNode ifStatementBlock, ParseNode elseStatementBlock) {
		DiagStatementNode node = new DiagStatementNode(token);
		
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
