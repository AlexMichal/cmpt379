package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import lexicalAnalyzer.Keyword;
import tokens.LextantToken;
import tokens.Token;

public class ImmutableIdentifierNode extends ParseNode {
	public ImmutableIdentifierNode(Token token) {
		super(token);
		assert(token.isLextant(Keyword.IMMUTABLE));
	}
	
	public ImmutableIdentifierNode(ParseNode node) {
		super(node);
	}

	////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////
	
	public boolean getValue() {
		return token.isLextant(Keyword.IMMUTABLE);
	}

	public LextantToken lextantToken() {
		return (LextantToken)token;
	}	

	///////////////////////////////////////////////////////////
	// ACCEPT A VISITOR
	///////////////////////////////////////////////////////////
	
	public void accept(ParseNodeVisitor visitor) {
		visitor.visit(this);
	}
}
