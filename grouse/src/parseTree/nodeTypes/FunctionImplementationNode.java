package parseTree.nodeTypes;

import lexicalAnalyzer.Keyword;
import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import tokens.IdentifierToken;
import tokens.Token;
import utilities.Debug;

public class FunctionImplementationNode extends ParseNode {
	Debug debug = new Debug();
	
	public FunctionImplementationNode(Token token) {
		super(token);
		
		debug.out("In FunctionDeclNode: " + token);
	}
	
	public FunctionImplementationNode(ParseNode node) {
		super(node);
	}
	
	////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////
	// CONVENIENCE FACTORY
	////////////////////////////////////////////////////////////
	
	public static FunctionImplementationNode withChildren(Token token, ParseNode declaredName, ParseNode parameterList) {
		FunctionImplementationNode node = new FunctionImplementationNode(token);

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
