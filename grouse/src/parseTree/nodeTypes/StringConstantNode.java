package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import lexicalAnalyzer.Keyword;
import tokens.StringToken;
import tokens.Token;

public class StringConstantNode extends ParseNode {
	public StringConstantNode(Token token) {
		super(token);
		assert(token instanceof StringToken);
	}
	
	public StringConstantNode(ParseNode node) {
		super(node);
	}

	////////////////////////////////////////////////////////////
	// attributes
	public String getValue() {
		return stringToken().getValue();
	}

	public StringToken stringToken() {
		return (StringToken)token;
	}	

	///////////////////////////////////////////////////////////
	// accept a visitor
	public void accept(ParseNodeVisitor visitor) {
		visitor.visit(this);
	}
}
