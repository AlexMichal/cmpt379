package parser;

import java.util.Arrays;

import logging.GrouseLogger;
import parseTree.*;
import parseTree.nodeTypes.BinaryOperatorNode;
import parseTree.nodeTypes.BlockStatementNode;
import parseTree.nodeTypes.BooleanConstantNode;
import parseTree.nodeTypes.CastNode;
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
import parseTree.nodeTypes.TypeNode;
import parseTree.nodeTypes.UnaryOperatorNode;
import parseTree.nodeTypes.WhileStatementNode;
import semanticAnalyzer.types.PrimitiveType;
import tokens.*;
import utilities.Debug;
import lexicalAnalyzer.Keyword;
import lexicalAnalyzer.Lextant;
import lexicalAnalyzer.Punctuator;
import lexicalAnalyzer.Scanner;

// PARSER (AKA SYNTACTICAL ANALYZER)
// Input: Tokens
// Output: Abstract State Tree
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
	// "program" IS THE START SYMBOL 'S'
	////////////////////////////////////////////////////////////
	
	// S -> main { block }
	private ParseNode parseProgram() {
		if (!startsProgram(nowReading)) return syntaxErrorNode("program");
		
		ParseNode program = new ProgramNode(nowReading);
		expect(Keyword.MAIN);
		
		// Parsing the main block of code
		ParseNode mainBlock = parseMainBlock();
		
		//debug.out("" + mainBlock); // TODO: zPARSE TREE PRINT
		
		program.appendChild(mainBlock);
		
		if (!(nowReading instanceof NullToken)) return syntaxErrorNode("end of program");
		
		return program;
	}
	
	private boolean startsProgram(Token token) {
		return token.isLextant(Keyword.MAIN);
	}
	
	///////////////////////////////////////////////////////////
	// MAIN BLOCK STATEMENT
	///////////////////////////////////////////////////////////
	
	// block -> { statement* }
	private ParseNode parseMainBlock() {
		if (!startsMainBlockStatement(nowReading)) return syntaxErrorNode("main block statement");
		
		ParseNode mainBlock = new MainBlockNode(nowReading);
		
		// ... { ...
		expect(Punctuator.OPEN_CURLY_BRACKET);
		
		// Parse each statement in between the opening and closing braces
		while (startsStatement(nowReading)) {
			ParseNode statement = parseStatement();
			mainBlock.appendChild(statement);
		}
		
		// ... } ...
		expect(Punctuator.CLOSE_CURLY_BRACKET);
		
		return mainBlock;
	}
	
	private boolean startsMainBlockStatement(Token token) {
		return token.isLextant(Punctuator.OPEN_CURLY_BRACKET);
	}
	
	///////////////////////////////////////////////////////////
	// STATEMENTS
	///////////////////////////////////////////////////////////
	
	// statement -> printStmt | declaration | letStmt | if statement
	
	// Parses each statement of a block statement
	private ParseNode parseStatement() {
		if (!startsStatement(nowReading)) return syntaxErrorNode("statement");
		
		if (startsDeclaration(nowReading)) return parseDeclaration();
		
		if (startsLetStatement(nowReading)) return parseLetStatement();
		
		if (startsPrintStatement(nowReading)) return parsePrintStatement();
		
		if (startsIfStatement(nowReading)) return parseIfStatement();

		if (startsWhileStatement(nowReading)) return parseWhileStatement();
		
		if (startsBlockStatement(nowReading)) return parseBlockStatement();
		
		assert false : "bad token " + nowReading + " in parseStatement()";
		return null;
	}
	
	private boolean startsStatement(Token token) {
		return startsDeclaration(token) ||
				startsLetStatement(token) || 
				startsPrintStatement(token) || 
				startsIfStatement(token) ||
				startsWhileStatement(token) ||
				startsBlockStatement(token);
	}
	
	/*******************/
	/* PRINT STATEMENT */
	/*******************/
	
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
		while (startsPrintExpression(nowReading)) {
			parsePrintExpression(parent);
		}
		return parent;
	}
	
	// This adds the printExpression it parses to the children of the given parent
	// printExpression -> expr? ,? nl? 
	private void parsePrintExpression(PrintStatementNode parent) {
		if (startsExpression(nowReading)) {
			ParseNode child = parseExpression();
			parent.appendChild(child);
		}
		
		if (nowReading.isLextant(Punctuator.SEPARATOR)) {
			readToken();
			ParseNode child = new SeparatorNode(previouslyRead);
			parent.appendChild(child);
		}
		
		if (nowReading.isLextant(Keyword.NEWLINE)) {
			readToken();
			ParseNode child = new NewlineNode(previouslyRead);
			parent.appendChild(child);
		}
	}
	
	private boolean startsPrintExpression(Token token) {
		return startsExpression(token) || token.isLextant(Punctuator.SEPARATOR, Keyword.NEWLINE) ;
	}
	
	/*************************/
	/* DECLARATION STATEMENT */
	/*************************/
	
	// declaration -> [IMMUTABLE|VARIABLE] identifier := expression ;
	private ParseNode parseDeclaration() {
		if (!startsDeclaration(nowReading)) return syntaxErrorNode("declaration");
		
		Token declarationToken = nowReading;
		readToken();
		
		ParseNode identifierName = parseIdentifier();
		expect(Punctuator.ASSIGN);
		
		ParseNode initializer = parseExpression();
		expect(Punctuator.TERMINATOR);
		
		return DeclarationNode.withChildren(declarationToken, identifierName, initializer);
	}
	
	private boolean startsDeclaration(Token token) {
		return token.isLextant(Keyword.IMMUTABLE, Keyword.VARIABLE);
	}
	
	/*****************/
	/* LET STATEMENT */
	/*****************/
	
	// letStatement -> target := expression;
	private ParseNode parseLetStatement() {
		if (!startsLetStatement(nowReading)) return syntaxErrorNode("let statement");
		
		// let ...
		Token letStatementToken = nowReading;
		readToken();
		
		// ... (identifier OR expr[expr]) ... 
		ParseNode target = parseTarget(); // TODO: TARGET syntax
		
		// ... := ...
		expect(Punctuator.ASSIGN);
		
		// ... expr ...
		ParseNode initializer = parseExpression();
		
		// ... ;
		expect(Punctuator.TERMINATOR);
		
		return LetStatementNode.withChildren(letStatementToken, target, initializer);
	}
	
	private boolean startsLetStatement(Token token) {
		return token.isLextant(Keyword.LET);
	}
	
	/****************/
	/* IF STATEMENT */
	/****************/
	
	// ifStatement -> if (expression) block (else block)?
	private ParseNode parseIfStatement() {
		if (!startsIfStatement(nowReading)) return syntaxErrorNode("if statement");
		
		ParseNode ifStatementBlock;
		
		// if ...
		Token ifStatementToken = nowReading;
		readToken();

		// ... ( expr ) ...
		expect(Punctuator.OPEN_ROUND_BRACKET);
		ParseNode expression = parseExpression();
		expect(Punctuator.CLOSE_ROUND_BRACKET);
		
		// ... { block } ...
		ifStatementBlock = parseBlockStatement();
		
		// OPTIONAL:
		// ... else { block }
		if (startsElseStatement(nowReading)) {
			ParseNode elseStatementBlock;
			
			// ... else ...
			expect(Keyword.ELSE);
			
			// ... { block }
			elseStatementBlock = parseBlockStatement();
			
			return IfStatementNode.withChildren(ifStatementToken, expression, ifStatementBlock, elseStatementBlock);
		}
		
		return IfStatementNode.withChildren(ifStatementToken, expression, ifStatementBlock);
	}
	
	private boolean startsIfStatement(Token token) {
		return token.isLextant(Keyword.IF);
	}
	
	private boolean startsElseStatement(Token token) {
		return token.isLextant(Keyword.ELSE);
	}
	
	/*******************/
	/* WHILE STATEMENT */
	/*******************/
	
	// whileStatement -> while (expression) block
	private ParseNode parseWhileStatement() {
		if (!startsWhileStatement(nowReading)) return syntaxErrorNode("while statement");
		
		ParseNode whileStatementBlock;
		
		// while ...
		Token whileStatementToken = nowReading;
		readToken();

		// ... ( expr ) ...
		expect(Punctuator.OPEN_ROUND_BRACKET);
		ParseNode expression = parseExpression();
		expect(Punctuator.CLOSE_ROUND_BRACKET);
		
		// ... { block } ...
		whileStatementBlock = parseBlockStatement();
		
		return WhileStatementNode.withChildren(whileStatementToken, expression, whileStatementBlock);
	}
	
	private boolean startsWhileStatement(Token token) {
		return token.isLextant(Keyword.WHILE);
	}
	
	/*******************/
	/* BLOCK STATEMENT */	
	/*******************/
	
	// block -> { statement* }
	private ParseNode parseBlockStatement() {
		if (!startsBlockStatement(nowReading)) return syntaxErrorNode("block statement");
		
		ParseNode block = new BlockStatementNode(nowReading);
		
		// ... { ...
		expect(Punctuator.OPEN_CURLY_BRACKET);
		
		// Parse each statement in between the opening and closing braces
		while (startsStatement(nowReading)) {
			ParseNode statement = parseStatement();
			block.appendChild(statement);
		}
		
		// ... } ...
		expect(Punctuator.CLOSE_CURLY_BRACKET);
		
		return block;
	}
	
	private boolean startsBlockStatement(Token token) {
		return token.isLextant(Punctuator.OPEN_CURLY_BRACKET);
	}
		
	///////////////////////////////////////////////////////////
	// EXPRESSIONS
	// expr  -> exprBooleanComparison_Or
	// exprBooleanComparison_Or -> exprBooleanComparison_And || exprBooleanComparison_And
	// exprBooleanComparison_And -> exprComparisonOperators && exprComparisonOperators
	// exprComparisonOperators -> expr2 [(<|<=|==|!=|>|>=) expr2]?
	// expr2 -> expr3 [(+|-) expr3]*  (left-assoc)
	// expr3 -> exprCast [(*|/) exprCast]*  (left-assoc)
	// exprCast -> expr5 : type
	// expr5 -> literal OR ( expr )
	// literal -> intNumber | floatNumber | characterConstant | booleanConstant | stringConstant | identifier
	// expr -> parseExpressionInBetweenParentheses
	///////////////////////////////////////////////////////////
	
	// expr  -> exprBooleanComparison_Or
	private ParseNode parseExpression() {		
		if (!startsExpression(nowReading)) return syntaxErrorNode("expression");
		
		return parseBooleanOperator_Or();
	}
	
	// exprBooleanComparison_Or -> exprBooleanOperator_And
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
	
	// exprBooleanOperator_And -> BooleanComparison
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
	
	// exprComparisonOperators -> expr2 [(<|<=|==|!=|>|>=) expr2]?
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
		
		ParseNode left = parseCastExpression();
		
		while (nowReading.isLextant(Punctuator.MULTIPLY) || nowReading.isLextant(Punctuator.DIVIDE)) {
			Token token = nowReading;
			readToken();
			
			ParseNode right = parseCastExpression();
			
			left = BinaryOperatorNode.withChildren(token, left, right);
		}
		
		return left;
	}
	
	// exprCastExpression -> expr5 : type
	/*
	 * -Booleans can't be cast to any other type
	 * -Chars may be cast to integers (they yield an integer between 0 and 127)
	 * -Integers may be cast to characters (by using the bottom 7 bits of the integer as the character)
	 * ... and to floats
	 * -Floats may be cast to integer by truncation (rounding towards 0)
	 * -Integer and character may be cast to boolean (zero yields false, nonzero yields true)
	 * -Any type can be cast to itself
	 * -No other casts are allowed
	 */
	
	// TODO: CAST parser
	private ParseNode parseCastExpression() {
		if (!startsCastExpression(nowReading)) return syntaxErrorNode("expression<cast>");
		
		ParseNode left = parseExpression5();
	
		/*if (nowReading.isLextant(Punctuator.COLON)) {
			// expr : ...
			Token colonToken = nowReading;
			expect(Punctuator.COLON);
			
			debug.out("NOW READING (after COLON): " + nowReading);
			
			/// ... type
			Token typeToken = nowReading;
			
			readToken();
			
			ParseNode right = new TypeNode(typeToken);
			
			return CastNode.withChildren(colonToken, left, right);
		}*/
			
		return left;
	}
	
	// expr4 -> ! expr (TODO: OR copy)
	private ParseNode parseExpression5() {
		if (!startsExpressionWithHighestPrecendence(nowReading)) return syntaxErrorNode("expression<5>");
		
		if (nowReading.isLextant(Punctuator.NOT)) {
			Token negateToken = nowReading;
			readToken();
			
			ParseNode node = parseExpression6();
			
			return UnaryOperatorNode.withChild(negateToken, node);
		}
		
		return parseExpression6();
	}
	
	// expr5 -> literal OR ( expr )
	private ParseNode parseExpression6() {
		if (!startsExpressionWithHighestPrecendence(nowReading)) return syntaxErrorNode("expression<6>");
		
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
		return startsCastExpression(token);
	}
	
	private boolean startsCastExpression(Token token) {
		return startsExpression5(token);
	}
	
	private boolean startsExpression5(Token token) {
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
	
	/**********/
	/* TARGET */
	/**********/
	
	private ParseNode parseTarget() { // TODO: TARGET
		if (startsIdentifier(nowReading)) {
			return parseIdentifier();
		}
		
		return null;
	}
	
	/*********/
	/* OTHER */
	/*********/
	
	private void readToken() {
		previouslyRead = nowReading;
		
		//debug.out("LAST READ TOKEN: " + nowReading); // TODO: zTOKEN PRINT
		
		nowReading = scanner.next();
	}
	
	/*****************/
	/* ERROR RELATED */
	/*****************/
	
	// if the current token is one of the given lextants, read the next token.
	// otherwise, give a syntax error and read next token (to avoid endless looping).
	private void expect(Lextant ...lextants ) { // ... means we can pass in 0 or more Lextants
		if (!nowReading.isLextant(lextants)) syntaxError(nowReading, "expecting " + Arrays.toString(lextants));
		
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
