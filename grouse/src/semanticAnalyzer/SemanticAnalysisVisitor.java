package semanticAnalyzer;

import java.util.Arrays;
import java.util.List;

import lexicalAnalyzer.Keyword;
import lexicalAnalyzer.Lextant;
import logging.GrouseLogger;
import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import parseTree.nodeTypes.BinaryOperatorNode;
import parseTree.nodeTypes.BlockStatementNode;
import parseTree.nodeTypes.BooleanConstantNode;
import parseTree.nodeTypes.BreakNode;
import parseTree.nodeTypes.CharacterConstantNode;
import parseTree.nodeTypes.ContinueNode;
import parseTree.nodeTypes.MainBlockNode;
import parseTree.nodeTypes.DeclarationNode;
import parseTree.nodeTypes.ErrorNode;
import parseTree.nodeTypes.FloatConstantNode;
import parseTree.nodeTypes.ForStatementNode;
import parseTree.nodeTypes.IdentifierNode;
import parseTree.nodeTypes.IfStatementNode;
import parseTree.nodeTypes.IntegerConstantNode;
import parseTree.nodeTypes.LetStatementNode;
import parseTree.nodeTypes.NewlineNode;
import parseTree.nodeTypes.ParameterListNode;
import parseTree.nodeTypes.ParameterNode;
import parseTree.nodeTypes.PrintStatementNode;
import parseTree.nodeTypes.ProgramNode;
import parseTree.nodeTypes.SeparatorNode;
import parseTree.nodeTypes.StaticVariableNode;
import parseTree.nodeTypes.StringConstantNode;
import parseTree.nodeTypes.TupleDefinitionNode;
import parseTree.nodeTypes.TypeNode;
import parseTree.nodeTypes.UnaryOperatorNode;
import parseTree.nodeTypes.WhileStatementNode;
import semanticAnalyzer.signatures.FunctionSignatures;
import semanticAnalyzer.types.PrimitiveType;
import semanticAnalyzer.types.TupleType;
import semanticAnalyzer.types.Type;
import symbolTable.Binding;
import symbolTable.Scope;
import tokens.LextantToken;
import tokens.Token;
import utilities.Debug;

class SemanticAnalysisVisitor extends ParseNodeVisitor.Default {
	private static Debug debug = new Debug();
	
	@Override
	public void visitLeave(ParseNode node) {
		throw new RuntimeException("Node class unimplemented in SemanticAnalysisVisitor: " + node.getClass());
	}
	
	///////////////////////////////////////////////////////////////////////////
	// CONSTRUCTS THAT ARE LARGER THAN STATEMENTS
	///////////////////////////////////////////////////////////////////////////
	
	@Override
	public void visitEnter(ProgramNode node) {
		enterProgramScope(node);
	}
	
	public void visitLeave(ProgramNode node) {
		leaveScope(node);
	}
	
	public void visitEnter(MainBlockNode node) {
	}
	
	public void visitLeave(MainBlockNode node) {
	}
	
	public void visitEnter(BlockStatementNode node) {
		enterSubscope(node);
	}
	
	public void visitLeave(BlockStatementNode node) {
		leaveScope(node);
	}
	
	public void visitEnter(TupleDefinitionNode node) {
		debug.out("IN visitEnter - TupleDefinitionNode");
		
		enterSubscope(node);
	}
	
	public void visitLeave(TupleDefinitionNode node) { // TODO: tuple def'n 
		debug.out("IN visitLeave - TupleDefinitionNode");
		// so now we have a scope for tuple
		// now we need to add a binding of this tuple to the parent node (which is program node)
		//addBindingToProgramNode(node);
		
		// also set this type to TUPLE
		
		leaveScope(node);
	}
	
	///////////////////////////////////////////////////////////////////////////
	// HELPER METHODS FOR SCOPING
	///////////////////////////////////////////////////////////////////////////
	
	private void enterProgramScope(ParseNode node) {
		Scope scope = Scope.createProgramScope();
		node.setScope(scope);
	}
	
	private void enterSubscope(ParseNode node) {
		Scope baseScope = node.getLocalScope();
		Scope scope = baseScope.createSubscope();
		node.setScope(scope);
	}
	
	private void leaveScope(ParseNode node) {
		node.getScope().leave();
	}
	
	///////////////////////////////////////////////////////////////////////////
	// STATEMENTS, DECLARATIONS, LET STATEMENTS, IF STATEMENTS
	///////////////////////////////////////////////////////////////////////////
	
	/*******************/
	/* PRINT STATEMENT */
	/*******************/
	
	@Override
	public void visitLeave(PrintStatementNode node) {}
	
	/*************************/
	/* DECLARATION STATEMENT */
	/*************************/
	
	@Override
	public void visitLeave(DeclarationNode node) {
		IdentifierNode 	nameOfIdentifier 	= (IdentifierNode) node.child(0);;
		ParseNode 		initializer 		= node.child(1);;
		ParseNode		staticNode			= null;
		Type 			declarationType 	= initializer.getType();;
		Object			extra				= node.getToken().getLexeme(); // var, imm, etc
		
		if (node.nChildren() == 3) staticNode = node.child(2);
		
		node.setType(declarationType);

		nameOfIdentifier.setType(declarationType);
		
		addBinding(nameOfIdentifier, declarationType, extra);
	}
	
	/*****************/
	/* LET STATEMENT */
	/*****************/
	
	@Override
	public void visitLeave(LetStatementNode node) {
		IdentifierNode 	nameOfIdentifier 	= (IdentifierNode) node.child(0);
		Object			typeOfIdentifier	= nameOfIdentifier.getBinding().getExtra();
		ParseNode 		initializer 		= node.child(1);
		Type 			letStatementType 	= initializer.getType();
		
		node.setType(letStatementType);
		
		nameOfIdentifier.setType(letStatementType);
		
		addBinding(nameOfIdentifier, letStatementType, typeOfIdentifier);
	}
	
	/****************/
	/* IF STATEMENT */
	/****************/
	
	@Override
	public void visitLeave(IfStatementNode node) {}
	
	///////////////////////////////////////////////////////////////////////////
	// PARAMETERS
	///////////////////////////////////////////////////////////////////////////
	
	/******************/
	/* PARAMETER LIST */
	/******************/
	
	@Override
	public void visitEnter(ParameterListNode node) {
	}
	
	@Override
	public void visitLeave(ParameterListNode node) {
	}
	
	/*************/
	/* PARAMETER */
	/*************/
	
	@Override
	public void visitLeave(ParameterNode node) {
		TypeNode 		typeNode 		= null;
		IdentifierNode 	identifierNode 	= null;
		Type			type 			= null;
		Object			extra			= "";
		
		//debug.out("K--------visitLeave - Parameter Node--------\n" + identifierNode + "\n " + type + " \n" + extra + "\nK----------------------------------------");
		debug.out("" + node.getToken().toString().contains("(void token)"));
		 
		if (node.getToken().toString().contains("(void token)")) {
			//identifierNode = (IdentifierNode)node.child(1);
			
		} else {
			typeNode = (TypeNode)node.child(0);
			identifierNode = (IdentifierNode)node.child(1);
			type = typeNode.getType();
			
			node.setType(type);
			identifierNode.setType(type);
			
			addBindingToAboveTupleDefinition(type, identifierNode, extra);
		}
		
		//addBindingToAboveTupleDefinition(type, identifierNode, extra);
	}

	///////////////////////////////////////////////////////////////////////////
	// EXPRESSIONS
	///////////////////////////////////////////////////////////////////////////
	
	/* BINARY OPERATOR NODE */
	
	@Override
	public void visitLeave(BinaryOperatorNode node) {
		assert node.nChildren() == 2;
		
		ParseNode left  = node.child(0);
		ParseNode right = node.child(1);
		List<Type> childTypes = Arrays.asList(left.getType(), right.getType());
		
		Lextant operator = operatorFor(node);
		
		FunctionSignatures signature = FunctionSignatures.signaturesOf(operator);
		
		Type typeOfChildrenNodes = FunctionSignatures.signature(signature.getKey(), childTypes).resultType();

		//debug.out("TOKEN VISIT LEAVE: \n" + FunctionSignatures.signature(signature.getKey(), childTypes).resultType());
		//debug.out("TOKEN VISIT LEAVE: \n" + typeOfChildrenNodes);
		
		// TODO: here maybe use the getVariant() to get the variant
		
		if (signature.accepts(childTypes)) {
			node.setType(typeOfChildrenNodes);
		} else { 
			typeCheckError(node, childTypes);
			node.setType(PrimitiveType.ERROR);
		}
	}

	private Lextant operatorFor(BinaryOperatorNode node) {
		LextantToken token = (LextantToken) node.getToken();
		
		return token.getLextant();
	}
	
	/* UNARY OPERATOR NODE */
	
	public void visitLeave(UnaryOperatorNode node) {
		assert node.nChildren() == 1;
		
		ParseNode right  = node.child(0);
		
		List<Type> childType = Arrays.asList(right.getType());
		
		Lextant operator = operatorFor(node);
		
		FunctionSignatures signature = FunctionSignatures.signaturesOf(operator);
		
		Type typeOfChildNode = FunctionSignatures.signature(signature.getKey(), childType).resultType();

		//debug.out("TOKEN VISIT LEAVE: \n" + FunctionSignatures.signature(signature.getKey(), childType).resultType());
		//debug.out("TOKEN VISIT LEAVE: \n" + typeOfChildNode);

		if (signature.accepts(childType)) {
			node.setType(typeOfChildNode);
		} else { 
			typeCheckError(node, childType);
			node.setType(PrimitiveType.ERROR);
		}
	}
	
	private Lextant operatorFor(UnaryOperatorNode node) {
		LextantToken token = (LextantToken) node.getToken();
		
		return token.getLextant();
	}
	
	///////////////////////////////////////////////////////////////////////////
	// SIMPLE LEAF NODES
	///////////////////////////////////////////////////////////////////////////
	
	@Override
	public void visit(BooleanConstantNode node) {
		node.setType(PrimitiveType.BOOLEAN);
	}
	
	@Override
	public void visit(ErrorNode node) {
		node.setType(PrimitiveType.ERROR);
	}
	
	@Override
	public void visit(IntegerConstantNode node) {
		node.setType(PrimitiveType.INTEGER);
	}
	
	@Override
	public void visit(FloatConstantNode node) {
		node.setType(PrimitiveType.FLOAT);
	}
	
	@Override
	public void visit(CharacterConstantNode node) {
		node.setType(PrimitiveType.CHARACTER);
	}
	
	@Override
	public void visit(StringConstantNode node) {
		node.setType(PrimitiveType.STRING);
	}
	
	@Override
	public void visit(StaticVariableNode node) {
		//node.setType(Keyword.STATIC);
	}
	
	@Override
	public void visit(NewlineNode node) {
//		node.setType(PrimitiveType.INTEGER);
	}
	
	@Override
	public void visit(SeparatorNode node) {
//		node.setType(PrimitiveType.INTEGER);
	}
	
	@Override
	public void visit(TypeNode node) {
		String type = "" + node.getToken();
		
		switch (type) {
			case "(INT)": 
				node.setType(PrimitiveType.INTEGER); 
				break;
			case "(FLOAT)": 
				node.setType(PrimitiveType.FLOAT); 
				break;
			case "(CHAR)": 
				node.setType(PrimitiveType.CHARACTER); 
				break;
			case "(STRING)": 
				node.setType(PrimitiveType.STRING); 
				break;
			case "(BOOL)": 
				node.setType(PrimitiveType.BOOLEAN); 
				break;
			default:  
				node.setType(PrimitiveType.ERROR); 
				break;				
		}
	}
	
	@Override
	public void visit(BreakNode node) {
		ParseNode loopStatementNode = node.getParent().getParent();
		
		if (loopStatementNode instanceof ForStatementNode) {
			node.setForStatementNodeLocation((ForStatementNode) loopStatementNode);
		} else if (loopStatementNode instanceof WhileStatementNode) {
			node.setWhileStatementNodeLocation((WhileStatementNode) loopStatementNode);
		}
	}
	
	@Override
	public void visit(ContinueNode node) {
		ParseNode loopStatementNode = node.getParent().getParent();
		
		if (loopStatementNode instanceof ForStatementNode) {
			node.setForStatementNodeLocation((ForStatementNode) loopStatementNode);
		} else if (loopStatementNode instanceof WhileStatementNode) {
			node.setWhileStatementNodeLocation((WhileStatementNode) loopStatementNode);
		}
	}

	///////////////////////////////////////////////////////////////////////////
	// IDENTIFIER NODES, WITH HELPER METHODS
	///////////////////////////////////////////////////////////////////////////
	
	@Override
	public void visit(IdentifierNode node) {
		if (isChildOfTupleDefinitionNode(node)) {
			TupleType tupleType = new TupleType();

			node.setType(tupleType);

			addBindingToParentsParentNode(node, node.getType(),"tuple");
		} else if (isChildOfParameterNode(node)) {
			/*IdentifierNode 	identifier 			= node;
			Type 			type 				= node.getType();
			Object 			typeOfIdentifier 	= "imm";

			debug.out("IN visit - IdentifierNode - isParameterNode - type: " + node.getParent());*/
			
			
			//addBindingToAboveTupleDefinition(identifier, type, typeOfIdentifier);
		} else if (!isBeingDeclared(node)) {
			Binding binding = node.findVariableBinding();

			node.setType(binding.getType());
			node.setBinding(binding);
		} else {
			// Parent DeclarationNode does the processing
		}
	}
	
	private boolean isChildOfTupleDefinitionNode(IdentifierNode node){
		ParseNode parent = node.getParent();
		
		return parent instanceof TupleDefinitionNode;
	}
	
	private boolean isChildOfParameterNode(IdentifierNode node){
		ParseNode parent = node.getParent();
		
		return parent instanceof ParameterNode;
	}
	
	private boolean isBeingDeclared(IdentifierNode node) {
		ParseNode parent = node.getParent();
		
		return (parent instanceof DeclarationNode) && (node == parent.child(0));
	}
	
	private void addBinding(IdentifierNode identifierNode, Type type, Object extra) {
		Scope scope = identifierNode.getLocalScope();
		
		//debug.out("\naddBinding:\n---SCOPE---------\n" + scope.toString() + "\nNAME: " + identifierNode.getToken() + "\n-----------------");
		
		Binding binding = scope.createBinding(identifierNode, type, extra);
		
		identifierNode.setBinding(binding);
	}
	
	private void addBindingToParentsParentNode(IdentifierNode node, Type type, Object extra) {
		ParseNode 	parseNode 	= node.getParent().getParent(); // change to traverse to root instead of this ghetto way
		Scope 		scope 		= parseNode.getScope();
		
		//debug.out("\naddBindingToProgramNode:\n---SCOPE---------\n" + scope.toString() + "\nNAME: " + node.getToken() + "\n-----------------");

		Binding binding = scope.createBinding(node, type, extra);
		
		node.setBinding(binding);
	}
	
	private void addBindingToAboveTupleDefinition(Type type, IdentifierNode node, Object extra) {
		Scope scope = findParentTupleDefinitionNodesScope(node);

		//debug.out("\nY-addBindingToAboveTupleDefinition:\n---SCOPE---------\n" + scope.toString() + "\nNAME: " + node.getToken() + "\nY-----------------------------------------------------");

		Binding binding = scope.createBinding(node, type, extra);
		
		node.setBinding(binding);
	}
	
	private Scope findParentTupleDefinitionNodesScope(ParseNode node) {
		for (ParseNode current : node.pathToRoot()) {
			if (current.getToken().getLexeme().contains("tuple")) {
				return current.getScope();
			}
		}
		
		return null;
	}
	
	///////////////////////////////////////////////////////////////////////////
	// ERROR LOGGING/PRINTING
	///////////////////////////////////////////////////////////////////////////
	
	private void typeCheckError(ParseNode node, List<Type> operandTypes) {
		Token token = node.getToken();
		
		logError("operator " + token.getLexeme() + " not defined for types " 
				 + operandTypes  + " at " + token.getLocation());	
	}
	
	private void unknownDeclarationTypeError(ParseNode node) {
		Token token = node.getToken();
		
		logError("unknown declaration node type - " + token.getLexeme() + " at " + token.getLocation());
	}
	
	private void logError(String message) {
		GrouseLogger log = GrouseLogger.getLogger("compiler.semanticAnalyzer");
		log.severe(message);
	}
}
