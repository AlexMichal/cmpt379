package lexicalAnalyzer;

import tokens.LextantToken;
import tokens.Token;

public enum Punctuator implements Lextant {
	// Arithmetic operator:
	ADD("+"),
	SUBTRACT("-"),
	MULTIPLY("*"),
	DIVIDE("/"),
	// Comparison operator:
	LESSER("<"),
	LESSER_OR_EQUAL("<="),
	EQUAL("=="),
	NOT_EQUAL("!="),
	GREATER(">"),
	GREATER_OR_EQUAL(">="),
	// Boolean operator:
	AND("&&"),
	OR("||"),
	NOT("!"),
	// Punctuation:
	TERMINATOR(";"),
	SEPARATOR(","),
	OPEN_CURLY_BRACKET("{"),
	CLOSE_CURLY_BRACKET("}"),
	OPEN_ROUND_BRACKET("("),
	CLOSE_ROUND_BRACKET(")"),
	OPEN_SQUARE_BRACKET("["),
	CLOSE_SQUARE_BRACKET("]"),
	AMPERSAND("&"),
	COLON(":"),
	ASSIGN(":="),
	ARROW("->"),
	// Other:
	NULL_PUNCTUATOR("");

	private String lexeme;
	private Token prototype;
	
	private Punctuator(String lexeme) {
		this.lexeme = lexeme;
		this.prototype = LextantToken.make(null, lexeme, this);
	}
	
	public String getLexeme() {
		return lexeme;
	}
	
	public Token prototype() {
		return prototype;
	}
	
	// TODO: unused. Delete?
	public boolean isComparisonOperator(String lexeme) {
		if (lexeme.equals(Punctuator.GREATER) ||
				lexeme.equals(Punctuator.GREATER_OR_EQUAL) ||
				lexeme.equals(Punctuator.EQUAL) ||
				lexeme.equals(Punctuator.NOT_EQUAL) ||
				lexeme.equals(Punctuator.LESSER) ||
				lexeme.equals(Punctuator.LESSER_OR_EQUAL)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isArithmeticOperator(String lexeme) {
		if (lexeme.equals(Punctuator.ADD) ||
				lexeme.equals(Punctuator.SUBTRACT) ||
				lexeme.equals(Punctuator.MULTIPLY) ||
				lexeme.equals(Punctuator.DIVIDE)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static Punctuator forLexeme(String lexeme) {
		for(Punctuator punctuator: values()) {
			if(punctuator.lexeme.equals(lexeme)) return punctuator;
		}
		return NULL_PUNCTUATOR;
	}
	
/*
	//   the following hashtable lookup can replace the implementation of forLexeme above. It is faster but less clear. 
	private static LexemeMap<Punctuator> lexemeToPunctuator = new LexemeMap<Punctuator>(values(), NULL_PUNCTUATOR);
	public static Punctuator forLexeme(String lexeme) {
		   return lexemeToPunctuator.forLexeme(lexeme);
	}
*/
}
