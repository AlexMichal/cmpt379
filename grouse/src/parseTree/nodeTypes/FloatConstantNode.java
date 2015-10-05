package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import tokens.FloatToken;
import tokens.Token;

public class FloatConstantNode extends ParseNode {
	public FloatConstantNode(Token token) {
		super(token);
		assert(token instanceof FloatToken);
	}
	public FloatConstantNode(ParseNode node) {
		super(node);
	}

	////////////////////////////////////////////////////////////
	// attributes
	public double getValue() {
		return numberToken().getValue();
	}

	public FloatToken numberToken() {
		return (FloatToken)token;
	}	

	///////////////////////////////////////////////////////////
	// accept a visitor
	public void accept(ParseNodeVisitor visitor) {
		visitor.visit(this);
	}
}
