package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import lexicalAnalyzer.Keyword;
import lexicalAnalyzer.Lextant;
import tokens.LextantToken;
import tokens.Token;

public class TypeNode extends ParseNode {
	public TypeNode(Token token) {
		super(token);
		
		assert(token.isLextant(Keyword.BOOL) ||
				token.isLextant(Keyword.CHAR) ||
				token.isLextant(Keyword.STRING) ||
				token.isLextant(Keyword.INT) ||
				token.isLextant(Keyword.FLOAT));
	}

	public TypeNode(ParseNode node) {
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
	
	public static TypeNode withChildren(Token token, ParseNode left, ParseNode right) {
		TypeNode node = new TypeNode(token);
		
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
