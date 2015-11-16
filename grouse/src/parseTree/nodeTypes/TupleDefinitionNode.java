package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import tokens.Token;
import utilities.Debug;

public class TupleDefinitionNode extends ParseNode {
	Debug debug = new Debug();
	
	public TupleDefinitionNode(Token token) {
		super(token);
		
		debug.out("In TupleDefnNode: " + token);
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
	
	public static TupleDefinitionNode withChildren(Token token, ParseNode declaredName, ParseNode identifierToCopy) {
		TupleDefinitionNode node = new TupleDefinitionNode(token);

		node.appendChild(declaredName);
		node.appendChild(identifierToCopy);
		
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
