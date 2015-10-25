package parser;

import java.util.Arrays;

import logging.GrouseLogger;
import parseTree.*;
import parseTree.nodeTypes.BinaryOperatorNode;
import parseTree.nodeTypes.BooleanConstantNode;
import parseTree.nodeTypes.CharacterConstantNode;
import parseTree.nodeTypes.MainBlockNode;
import parseTree.nodeTypes.DeclarationNode;
import parseTree.nodeTypes.ErrorNode;
import parseTree.nodeTypes.FloatConstantNode;
import parseTree.nodeTypes.IdentifierNode;
import parseTree.nodeTypes.IntegerConstantNode;
import parseTree.nodeTypes.LetStatementNode;
import parseTree.nodeTypes.NewlineNode;
import parseTree.nodeTypes.PrintStatementNode;
import parseTree.nodeTypes.ProgramNode;
import parseTree.nodeTypes.SeparatorNode;
import parseTree.nodeTypes.StringConstantNode;
import semanticAnalyzer.types.PrimitiveType;
import tokens.*;
import utilities.Debug;
import lexicalAnalyzer.Keyword;
import lexicalAnalyzer.Lextant;
import lexicalAnalyzer.Punctuator;
import lexicalAnalyzer.Scanner;

public class Parser {
	private static Debug debug = new Debug();
	
	private Scanner scanner;
	private Token nowReading;
	private Token previouslyRead;
	
	// Constructor
	// Called once by GrouseCompiler
	public static ParseNode parse(Scanner scanner) {
		Parser parser = new Parser(scanner);
		
		return parser.parse();
	}
	
	public Parser(Scanner scanner) {
		super();
		this.scanner = scanner;
	}
	
	public ParseNode parse() {
		readToken();
		return parseProgram();
	}

	////////////////////////////////////////////////////////////
	// "program" is the start symbol S
	// S -> MAIN mainBlock
	private ParseNode parseProgram() {
		if (!startsProgram(nowReading)) return syntaxErrorNode("program");
		
		ParseNode program = new ProgramNode(nowReading);
		expect(Keyword.MAIN);
		
		// Parsing the main block of code
		ParseNode mainBlock = parseMainBlock();
		program.appendChild(mainBlock);
		
		if (!(nowReading instanceof NullToken)) return syntaxErrorNode("end of program");
		
		return program;
	}
	
	private boolean startsProgram(Token token) {
		return token.isLextant(Keyword.MAIN);
	}
	
	///////////////////////////////////////////////////////////
	// mainBlock
	
	// mainBlock -> { statement* }
	private ParseNode parseMainBlock() {
		if (!startsMainBlock(nowReading)) return syntaxErrorNode("mainBlock");
		
		ParseNode mainBlock = new MainBlockNode(nowReading);
		expect(Punctuator.OPEN_CURLY_BRACKET);
		
		// Parse each statement in between the opening and closing braces
		while(startsStatement(nowReading)) {
			ParseNode statement = parseStatement();
			mainBlock.appendChild(statement);
		}
		
		expect(Punctuator.CLOSE_CURLY_BRACKET);
		return mainBlock;
	}
	
	private boolean startsMainBlock(Token token) {
		return token.isLextant(Punctuator.OPEN_CURLY_BRACKET);
	}
	
	///////////////////////////////////////////////////////////
	// statements
	
	// statement-> declaration | letStmt | printStmt
	// Parses each statement of the main
	private ParseNode parseStatement() {
		if (!startsStatement(nowReading)) return syntaxErrorNode("statement");
		
		if (startsDeclaration(nowReading)) return parseDeclaration();
		
		if (startsLetStatement(nowReading)) return parseLetStatement();
		
		if (startsPrintStatement(nowReading)) return parsePrintStatement();
		
		assert false : "bad token " + nowReading + " in parseStatement()";
		return null;
	}
	
	private boolean startsStatement(Token token) {
		return startsPrintStatement(token) || startsLetStatement(token) || startsDeclaration(token);
	}
	
	// printStmt -> PRINT printExpressionList ;
	private ParseNode parsePrintStatement() {
		if (!startsPrintStatement(nowReading)) return syntaxErrorNode("print statement");
			
		PrintStatementNode result = new PrintStatementNode(nowReading);
		
		readToken();
		result = parsePrintExpressionList(result);
		
		expect(Punctuator.TERMINATOR);
		return result;
	}
	
	private boolean startsPrintStatement(Token token) {
		return token.isLextant(Keyword.PRINT);
	}	

	// This adds the printExpressions it parses to the children of the given parent
	// printExpressionList -> printExpression*   (note that this is nullable)
	private PrintStatementNode parsePrintExpressionList(PrintStatementNode parent) {
		while(startsPrintExpression(nowReading)) {
			parsePrintExpression(parent);
		}
		return parent;
	}
	
	// This adds the printExpression it parses to the children of the given parent
	// printExpression -> expr? ,? nl? 
	private void parsePrintExpression(PrintStatementNode parent) {
		if(startsExpression(nowReading)) {
			ParseNode child = parseExpression();
			parent.appendChild(child);
		}
		if(nowReading.isLextant(Punctuator.SEPARATOR)) {
			readToken();
			ParseNode child = new SeparatorNode(previouslyRead);
			parent.appendChild(child);
		}
		if(nowReading.isLextant(Keyword.NEWLINE)) {
			readToken();
			ParseNode child = new NewlineNode(previouslyRead);
			parent.appendChild(child);
		}
	}
	
	private boolean startsPrintExpression(Token token) {
		return startsExpression(token) || token.isLextant(Punctuator.SEPARATOR, Keyword.NEWLINE) ;
	}
	
	// declaration -> [IMMUTABLE|VARIABLE] identifier := expression ;
	private ParseNode parseDeclaration() {
		if (!startsDeclaration(nowReading)) return syntaxErrorNode("declaration");
		
		Token declarationToken = nowReading;
		readToken();
		
		ParseNode identifier = parseIdentifier();
		expect(Punctuator.ASSIGN);
		
		ParseNode initializer = parseExpression();
		expect(Punctuator.TERMINATOR);
		
		return DeclarationNode.withChildren(declarationToken, identifier, initializer);
	}
	
	private boolean startsDeclaration(Token token) {
		return token.isLextant(Keyword.IMMUTABLE, Keyword.VARIABLE);
	}
	
	// letStatement -> identifier (variable) := expression;
	private ParseNode parseLetStatement() {
		if (!startsLetStatement(nowReading)) return syntaxErrorNode("let statement");
		
		Token letStatementToken = nowReading;
		readToken();
		
		ParseNode identifier = parseIdentifier();
		expect(Punctuator.ASSIGN);
		
		ParseNode initializer = parseExpression();
		expect(Punctuator.TERMINATOR);
		
		return LetStatementNode.withChildren(letStatementToken, identifier, initializer);
	}
	
	private boolean startsLetStatement(Token token) {
		return token.isLextant(Keyword.LET);
	}
	
	///////////////////////////////////////////////////////////
	// expressions
	// expr  -> exprBooleanComparison_Or
	// exprBooleanComparison_Or -> exprBooleanComparison_And || exprBooleanComparison_And
	// exprBooleanComparison_And -> exprComparisonOperators && exprComparisonOperators
	// exprComparisonOperators -> expr2 [(<|<=|==|!=|>|>=) expr2]?
	// expr2 -> expr3 [(+|-) expr3]*  (left-assoc)
	// expr3 -> expr4 [(*|/) expr4]*  (left-assoc)
	// 	expr4 -> expr5 : type (cast) // TODO: cast
	// expr5 -> literal OR ( expr )
	// literal -> intNumber | floatNumber | characterConstant | booleanConstant | stringConstant | identifier
	// expr -> parseExpressionInBetweenParentheses

	// expr  -> expr1
	private ParseNode parseExpression() {		
		if (!startsExpression(nowReading)) return syntaxErrorNode("expression");
		
		return parseBooleanOperator_Or();
	}
	
	// expr1 -> BooleanComparison
	private ParseNode parseBooleanOperator_Or() {
		if (!startsBooleanOperator_Or(nowReading)) return syntaxErrorNode("BooleanOperator_Or");
		
		ParseNode left = parseBooleanOperator_And();
		
		if (nowReading.isLextant(Punctuator.OR)) {
			Token compareToken = nowReading;
			
			readToken();
			
			ParseNode right = parseBooleanOperator_And();
			
			return BinaryOperatorNode.withChildren(compareToken, left, right);
		}
		
		return left;
	}
	
	// expr1 -> BooleanComparison
	private ParseNode parseBooleanOperator_And() {
		if (!startsBooleanOperator_And(nowReading)) return syntaxErrorNode("BooleanOperator_And");
		
		ParseNode left = parseComparisonOperators();
		
		if (nowReading.isLextant(Punctuator.AND)) {
			Token compareToken = nowReading;
			
			readToken();
			
			ParseNode right = parseComparisonOperators();
			
			return BinaryOperatorNode.withChildren(compareToken, left, right);
		}
		
		return left;
	}
	
	// expr1 -> expr2 [(<|<=|==|!=|>|>=) expr2]?
	private ParseNode parseComparisonOperators() {
		if (!startsComparisonOperators(nowReading)) return syntaxErrorNode("expression<1>");
		
		ParseNode left = parseExpression2();
		
		if (nowReading.isLextant(Punctuator.GREATER) ||
				nowReading.isLextant(Punctuator.LESSER) ||
				nowReading.isLextant(Punctuator.LESSER_OR_EQUAL) ||
				nowReading.isLextant(Punctuator.EQUAL) ||
				nowReading.isLextant(Punctuator.NOT_EQUAL) ||
				nowReading.isLextant(Punctuator.GREATER) ||
				nowReading.isLextant(Punctuator.GREATER_OR_EQUAL)) {
			Token compareToken = nowReading;
			
			readToken();
			
			ParseNode right = parseExpression2();
			
			return BinaryOperatorNode.withChildren(compareToken, left, right);
		}
		
		return left;
	}
	
	// expr2 -> expr3 [(+|-) expr3]*  (left-assoc)
	private ParseNode parseExpression2() {
		if (!startsExpression2(nowReading)) return syntaxErrorNode("expression<2>");
		
		ParseNode left = parseExpression3();
		
		while(nowReading.isLextant(Punctuator.ADD) || nowReading.isLextant(Punctuator.SUBTRACT)) {
			Token token = nowReading;
			readToken();
			ParseNode right = parseExpression3();
			
			left = BinaryOperatorNode.withChildren(token, left, right);
		}
		
		return left;
	}

	// expr3 -> expr4 [(*|/) expr4]*  (left-assoc)
	private ParseNode parseExpression3() {
		if (!startsExpression3(nowReading)) return syntaxErrorNode("expression<3>");
		
		ParseNode left = parseExpression4();
		
		while (nowReading.isLextant(Punctuator.MULTIPLY) || nowReading.isLextant(Punctuator.DIVIDE)) {
			Token token = nowReading;
			readToken();
			ParseNode right = parseExpression4();
			
			left = BinaryOperatorNode.withChildren(token, left, right);
		}
		
		return left;
	}
	
	// expr4 -> ! expr (TODO: OR copy)
	private ParseNode parseExpression4() {
		if (!startsExpressionWithHighestPrecendence(nowReading)) return syntaxErrorNode("expression<4>");
		
		ParseNode left;
		
		if (nowReading.isLextant(Punctuator.NOT)) expect(Punctuator.NOT);
		
		left = parseExpression5();
				
		return left;
	}
	
	// expr5 -> literal OR ( expr )
	private ParseNode parseExpression5() {
		if (!startsExpressionWithHighestPrecendence(nowReading)) return syntaxErrorNode("expression<5>");
		
		if (nowReading.isLextant(Punctuator.OPEN_ROUND_BRACKET)) {
			return parseExpressionInBetweenParentheses();
		} else {
			return parseLiteral();
		}
	}
	
	// literal -> integerConst | floatConst | booleanConst | characterConst| stringConst | identifier 
	private ParseNode parseLiteral() {
		if (!startsLiteral(nowReading)) return syntaxErrorNode("literal");
		
		if (startsIntNumber(nowReading)) {
			return parseIntNumber();
		}
		
		if (startsFloatNumber(nowReading)) {
			return parseFloatNumber();
		}
		
		if (startsBooleanConstant(nowReading)) {
			return parseBooleanConstant();
		}

		if (startsCharacterConstant(nowReading)) {
			return parseCharacterConstant();
		}
		
		if (startsStringConstant(nowReading)) {
			return parseStringConstant();
		}
		
		if (startsIdentifier(nowReading)) {
			return parseIdentifier();
		}
		
		assert false : "bad token " + nowReading + " in parseLiteral()";
		return null;
	}

	// expr -> ( expr )
	private ParseNode parseExpressionInBetweenParentheses() {
		if (!startsExpressionInBetweenParentheses(nowReading)) return syntaxErrorNode("statement in parentheses");
		
		ParseNode left;
		
		expect(Punctuator.OPEN_ROUND_BRACKET);
		
		left = parseExpression();
		
		expect(Punctuator.CLOSE_ROUND_BRACKET);
		
		return left;
	}
	
	// starts expressions
	private boolean startsExpression(Token token) {
		return startsBooleanOperator_Or(token);
	}
	
	private boolean startsBooleanOperator_Or(Token token) {
		return startsBooleanOperator_And(token);
	}
	
	private boolean startsBooleanOperator_And(Token token) {
		return startsComparisonOperators(token);
	}
	
	private boolean startsComparisonOperators(Token token) {
		return startsExpression2(token);
	}
	
	private boolean startsExpression2(Token token) {
		return startsExpression3(token);
	}	
	
	private boolean startsExpression3(Token token) {
		return startsExpression4(token);
	}
	
	private boolean startsExpression4(Token token) {
		return startsExpressionWithHighestPrecendence(token);
	}
	
	private boolean startsExpressionWithHighestPrecendence(Token token) {
		if (isValidExpression()) {
			return true;
		} else {
			return startsLiteral(token);
		}
	}
	
	private boolean startsLiteral(Token token) {
		return startsIntNumber(token) || 
				startsFloatNumber(token) || 
				startsCharacterConstant(token) || 
				startsStringConstant(token) || 
				startsIdentifier(token) || 
				startsBooleanConstant(token);
	}

	private boolean startsExpressionInBetweenParentheses(Token token) {
		if (token.isLextant(Punctuator.OPEN_ROUND_BRACKET)) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isValidExpression() {
		if (nowReading.isLextant(Punctuator.OPEN_ROUND_BRACKET) ||
				nowReading.isLextant(Punctuator.NOT)) {
			return true;
		} else {
			return false;
		}
	}
	
	// number (terminal)
	private ParseNode parseIntNumber() {
		if (!startsIntNumber(nowReading)) return syntaxErrorNode("integer constant"); 
		
		readToken();
		
		return new IntegerConstantNode(previouslyRead);
	}
	
	private ParseNode parseFloatNumber() {
		if (!startsFloatNumber(nowReading)) return syntaxErrorNode("float constant");
		
		readToken();
		
		return new FloatConstantNode(previouslyRead);
	}
	
	private boolean startsIntNumber(Token token) {
		return token instanceof IntegerToken;
	}
	
	private boolean startsFloatNumber(Token token) {
		return token instanceof FloatToken;
	}

	// character (terminal)
	private ParseNode parseCharacterConstant() {
		if (!startsCharacterConstant(nowReading)) return syntaxErrorNode("character constant");
		
		readToken();
		
		return new CharacterConstantNode(previouslyRead);
	}
	
	private boolean startsCharacterConstant(Token token) {
		return token instanceof CharacterToken;
	}
	
	// string (terminal)
	private ParseNode parseStringConstant() {
		if (!startsStringConstant(nowReading)) return syntaxErrorNode("string constant");
		
		readToken();
		
		return new StringConstantNode(previouslyRead);
	}
	
	private boolean startsStringConstant(Token token) {
		return token instanceof StringToken;
	}
		
	// identifier (terminal)
	private ParseNode parseIdentifier() {
		if (!startsIdentifier(nowReading)) return syntaxErrorNode("identifier");
		
		readToken();
		return new IdentifierNode(previouslyRead);
	}
	
	private boolean startsIdentifier(Token token) {
		return token instanceof IdentifierToken;
	}

	// boolean constant (terminal)
	private ParseNode parseBooleanConstant() {
		if(!startsBooleanConstant(nowReading)) return syntaxErrorNode("boolean constant");
	
		readToken();
		return new BooleanConstantNode(previouslyRead);
	}
	
	private boolean startsBooleanConstant(Token token) {
		return token.isLextant(Keyword.TRUE, Keyword.FALSE);
	}

	private void readToken() {
		previouslyRead = nowReading;
		
		//debug.out("LAST READ TOKEN: " + nowReading); // TODO: TOKEN: DEBUG OUT
		
		nowReading = scanner.next();
	}	
	
	/** ERROR RELATED **/
	// if the current token is one of the given lextants, read the next token.
	// otherwise, give a syntax error and read next token (to avoid endless looping).
	private void expect(Lextant ...lextants ) { // ... means we can pass in 0 or more Lextants
		if (!nowReading.isLextant(lextants)) {
			syntaxError(nowReading, "expecting " + Arrays.toString(lextants));
		}
		readToken();
	}	
	
	private ErrorNode syntaxErrorNode(String expectedSymbol) {
		syntaxError(nowReading, "expecting " + expectedSymbol);
		ErrorNode errorNode = new ErrorNode(nowReading);
		readToken();
		return errorNode;
	}
	
	private void syntaxError(Token token, String errorDescription) {
		String message = "" + token.getLocation() + " " + errorDescription;
		error(message);
	}
	
	private void error(String message) {
		GrouseLogger log = GrouseLogger.getLogger("compiler.Parser");
		log.severe("syntax error: " + message);
	}	
}
