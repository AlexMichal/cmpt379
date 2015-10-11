package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import lexicalAnalyzer.Keyword;
import lexicalAnalyzer.Lextant;
import tokens.LextantToken;
import tokens.Token;

public class LetStatementNode extends ParseNode {

	public LetStatementNode(Token token) {
		super(token);

		assert(token.isLextant(Keyword.LET));
	}

	public LetStatementNode(ParseNode node) {
		super(node);
	}
	
	////////////////////////////////////////////////////////////
	// attributes
	public Lextant getLetStatementType() {
		return lextantToken().getLextant();
	}
	
	public LextantToken lextantToken() {
		return (LextantToken)token;
	}	
	
	////////////////////////////////////////////////////////////
	// convenience factory
	public static LetStatementNode withChildren(Token token, ParseNode declaredName, ParseNode initializer) {
		LetStatementNode node = new LetStatementNode(token);
		node.appendChild(declaredName);
		node.appendChild(initializer);
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
