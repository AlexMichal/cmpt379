package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import lexicalAnalyzer.Lextant;
import tokens.LextantToken;
import tokens.Token;

public class CastNode extends ParseNode {
	public CastNode(Token token) {
		super(token);
		
		assert(token instanceof LextantToken); // TODO: CAST Possible change?
	}

	public CastNode(ParseNode node) {
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
	
	////////////////////////////////////////////////////////////
	// CONVENIENCE FACTORY
	////////////////////////////////////////////////////////////
	
	public static CastNode withChildren(Token token, ParseNode left, ParseNode right) {
		CastNode node = new CastNode(token);
		
		node.appendChild(left);
		node.appendChild(right);
		
		return node;
	}
	
	////////////////////////////////////////////////////////////
	// BOILERPLATE FOR VISITORS
	////////////////////////////////////////////////////////////
	
	public void accept(ParseNodeVisitor visitor) {
		visitor.visitEnter(this);
		visitChildren(visitor);
		visitor.visitLeave(this);
	}
}
