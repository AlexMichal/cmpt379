package semanticAnalyzer;

import java.util.Arrays;
import java.util.List;

import com.sun.org.apache.xpath.internal.ExpressionNode;

import lexicalAnalyzer.Lextant;
import logging.GrouseLogger;
import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import parseTree.nodeTypes.BinaryOperatorNode;
import parseTree.nodeTypes.BlockStatementNode;
import parseTree.nodeTypes.BooleanConstantNode;
import parseTree.nodeTypes.CharacterConstantNode;
import parseTree.nodeTypes.MainBlockNode;
import parseTree.nodeTypes.DeclarationNode;
import parseTree.nodeTypes.ErrorNode;
import parseTree.nodeTypes.FloatConstantNode;
import parseTree.nodeTypes.IdentifierNode;
import parseTree.nodeTypes.IfStatementNode;
import parseTree.nodeTypes.IntegerConstantNode;
import parseTree.nodeTypes.LetStatementNode;
import parseTree.nodeTypes.NewlineNode;
import parseTree.nodeTypes.PrintStatementNode;
import parseTree.nodeTypes.ProgramNode;
import parseTree.nodeTypes.SeparatorNode;
import parseTree.nodeTypes.StringConstantNode;
import parseTree.nodeTypes.UnaryOperatorNode;
import semanticAnalyzer.signatures.FunctionSignature;
import semanticAnalyzer.signatures.FunctionSignatures;
import semanticAnalyzer.types.PrimitiveType;
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
	// CONSTRUCTS THAT ARE  LARGER THAN STATEMENTS
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
	
	///////////////////////////////////////////////////////////////////////////
	// HELPER METHODS FOR SCOPING
	///////////////////////////////////////////////////////////////////////////
	
	private void enterProgramScope(ParseNode node) {
		debug.out("ENTERED PROGRAM SCOPE");
		
		Scope scope = Scope.createProgramScope();
		node.setScope(scope);
	}
	
	private void enterSubscope(ParseNode node) {
		debug.out("ENTERED SUB - SCOPE");
		
		Scope baseScope = node.getLocalScope();
		Scope scope = baseScope.createSubscope();
		node.setScope(scope);
	}
	
	private void leaveScope(ParseNode node) {
		debug.out("LEFT SCOPE");
		
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
		IdentifierNode 	nameOfIdentifier 	= (IdentifierNode) node.child(0);
		ParseNode 		initializer 		= node.child(1);
		Type 			declarationType 	= initializer.getType();
		Object			extra				= node.getToken().getLexeme(); // var, imm, etc
		
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
		
		
		debug.out("---------------------------");
		debug.out(" BINDING EXTRA: \n TOKEN: " + typeOfIdentifier);
		debug.out("---------------------------");
		
		addBinding(nameOfIdentifier, letStatementType, typeOfIdentifier);
	}
	
	/****************/
	/* IF STATEMENT */
	/****************/
	
	@Override
	public void visitLeave(IfStatementNode node) {
		/*ParseNode expression = (ParseNode) node.child(0);
		ParseNode block = node.child(1);

		debug.out("VISIT LEAVE GET TYPE: " + expression.getType());
		
		Type ifStatementType = expression.getType();
		node.setType(ifStatementType);
		
		expression.setType(ifStatementType);
		addBinding(expression, ifStatementType);*/
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
	public void visit(NewlineNode node) {
//		node.setType(PrimitiveType.INTEGER);
	}
	
	@Override
	public void visit(SeparatorNode node) {
//		node.setType(PrimitiveType.INTEGER);
	}
	
	///////////////////////////////////////////////////////////////////////////
	// IDENTIFIER NODES, WITH HELPER METHODS
	///////////////////////////////////////////////////////////////////////////
	
	@Override
	public void visit(IdentifierNode node) {
		if (!isBeingDeclared(node)) {
			Binding binding = node.findVariableBinding();

			node.setType(binding.getType());
			node.setBinding(binding);
									
			debug.out("---------------------------");
			debug.out(" BINDING EXTRA: "  + binding + "\n TOKEN: " + node.getParent().getToken());
			debug.out("---------------------------");
		}
		// else parent DeclarationNode does the processing.
	}
	
	private boolean isBeingDeclared(IdentifierNode node) {
		ParseNode parent = node.getParent();
		
		return (parent instanceof DeclarationNode) && (node == parent.child(0));
	}
	
	private void addBinding(IdentifierNode identifierNode, Type type, Object extra) {
		Scope scope = identifierNode.getLocalScope();
		Binding binding = scope.createBinding(identifierNode, type, extra);
		
		identifierNode.setBinding(binding);
	}
	
	///////////////////////////////////////////////////////////////////////////
	// ERROR LOGGING/PRINTING
	///////////////////////////////////////////////////////////////////////////
	
	private void typeCheckError(ParseNode node, List<Type> operandTypes) {
		Token token = node.getToken();
		
		logError("operator " + token.getLexeme() + " not defined for types " 
				 + operandTypes  + " at " + token.getLocation());	
	}
	
	private void logError(String message) {
		GrouseLogger log = GrouseLogger.getLogger("compiler.semanticAnalyzer");
		log.severe(message);
	}
}
