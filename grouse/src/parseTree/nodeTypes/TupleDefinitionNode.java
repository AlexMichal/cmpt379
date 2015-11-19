package parseTree.nodeTypes;

import lexicalAnalyzer.Keyword;
import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import tokens.IdentifierToken;
import tokens.Token;
import utilities.Debug;

public class TupleDefinitionNode extends ParseNode {
	Debug debug = new Debug();
	
	public TupleDefinitionNode(Token token) {
		super(token);
		
		assert(token.isLextant(Keyword.TUPLE));
	}
	
	public TupleDefinitionNode(ParseNode node) {
		super(node);
	}
	
	////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////
	// CONVENIENCE FACTORY
	////////////////////////////////////////////////////////////
	
	public static TupleDefinitionNode withChildren(Token token, ParseNode declaredName, ParseNode parameterList) {
		TupleDefinitionNode node = new TupleDefinitionNode(token);

		node.appendChild(declaredName);
		node.appendChild(parameterList);
		
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
