package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import logging.GrouseLogger;
import symbolTable.Binding;
import symbolTable.Scope;
import tokens.IdentifierToken;
import tokens.Token;
import utilities.Debug;

public class IdentifierNode extends ParseNode {
	private static Debug debug = new Debug();
	
	private Binding binding;
	private Scope declarationScope;

	public IdentifierNode(Token token) {
		super(token);
		assert(token instanceof IdentifierToken);
		this.binding = null;
	}
	
	public IdentifierNode(ParseNode node) {
		super(node);
		
		if (node instanceof IdentifierNode) {
			this.binding = ((IdentifierNode)node).binding;
		} else {
			this.binding = null;
		}
	}
	
	////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////
	
	public IdentifierToken identifierToken() {
		return (IdentifierToken)token;
	}

	public void setBinding(Binding binding) {
		this.binding = binding;
	}
	
	public Binding getBinding() {
		return binding;
	}
	
	////////////////////////////////////////////////////////////
	// SPECIALTY FUNCTIONS
	////////////////////////////////////////////////////////////
	
	public Binding findVariableBinding() {
		String identifier = token.getLexeme();
		
		for (ParseNode current : pathToRoot()) {
			if (current.containsBindingOf(identifier)) {
				declarationScope = current.getScope();
				
				return current.bindingOf(identifier);
			}
		}
		
		useBeforeDefineError();
		
		return Binding.nullInstance();
	}

	public Scope getDeclarationScope() {
		findVariableBinding();
		return declarationScope;
	}
	
	public void useBeforeDefineError() {
		GrouseLogger log = GrouseLogger.getLogger("compiler.semanticAnalyzer.identifierNode");
		
		Token token = getToken();
		
		log.severe("identifier " + token.getLexeme() + " used before defined at " + token.getLocation());
	}
	
	///////////////////////////////////////////////////////////
	// ACCEPT A VISITOR
	///////////////////////////////////////////////////////////	
	
	public void accept(ParseNodeVisitor visitor) {
		visitor.visit(this);
	}
}
