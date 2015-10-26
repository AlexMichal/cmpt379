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
	// attributes
	public Lextant getIfStatementType() {
		return lextantToken().getLextant();
	}
	
	public LextantToken lextantToken() {
		return (LextantToken)token;
	}
	
	////////////////////////////////////////////////////////////
	// convenience factory
	public static IfStatementNode withChildren(Token token, ParseNode expression, ParseNode block) {
		IfStatementNode node = new IfStatementNode(token);
		
		node.appendChild(expression);
		node.appendChild(block);
		
		return node;
	}
	
	// With an ELSE statement
	public static IfStatementNode withChildren(Token token, ParseNode expression, ParseNode ifStatementBlock, ParseNode elseStatementBlock) {
		IfStatementNode node = new IfStatementNode(token);
		
		node.appendChild(expression);
		node.appendChild(ifStatementBlock);
		node.appendChild(elseStatementBlock);
		
		return node;
	}
	
	///////////////////////////////////////////////////////////
	// boilerplate for visitors
	public void accept(ParseNodeVisitor visitor) {
		visitor.visitEnter(this);
		visitChildren(visitor);
		visitor.visitLeave(this);
	}
}
