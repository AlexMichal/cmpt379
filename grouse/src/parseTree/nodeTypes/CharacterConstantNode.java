package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import lexicalAnalyzer.Keyword;
import tokens.CharacterToken;
import tokens.IntegerToken;
import tokens.LextantToken;
import tokens.Token;

public class CharacterConstantNode extends ParseNode {
	public CharacterConstantNode(Token token) {
		super(token);
		assert(token instanceof CharacterToken);
	}
	
	public CharacterConstantNode(ParseNode node) {
		super(node);
	}

	////////////////////////////////////////////////////////////
	// attributes
	public char getValue() {
		return characterToken().getValue();
	}

	public CharacterToken characterToken() {
		return (CharacterToken)token;
	}	

	///////////////////////////////////////////////////////////
	// accept a visitor
	public void accept(ParseNodeVisitor visitor) {
		visitor.visit(this);
	}
}
