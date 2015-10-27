package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import lexicalAnalyzer.Keyword;
import tokens.LextantToken;
import tokens.Token;

public class VariableIdentifierNode extends ParseNode {
	public VariableIdentifierNode(Token token) {
		super(token);
		assert(token.isLextant(Keyword.VARIABLE));
	}
	
	public VariableIdentifierNode(ParseNode node) {
		super(node);
	}

	////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////
	
	public boolean getValue() {
		return token.isLextant(Keyword.VARIABLE);
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
