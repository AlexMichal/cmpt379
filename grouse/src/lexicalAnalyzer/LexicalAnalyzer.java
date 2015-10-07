package lexicalAnalyzer;

import logging.GrouseLogger;
import inputHandler.InputHandler;
import inputHandler.LocatedChar;
import inputHandler.LocatedCharStream;
import inputHandler.PushbackCharStream;
import inputHandler.TextLocation;
import tokens.CharacterToken;
import tokens.FloatToken;
import tokens.IdentifierToken;
import tokens.LextantToken;
import tokens.NullToken;
import tokens.IntegerToken;
import tokens.Token;
import utilities.Debug;

import static lexicalAnalyzer.PunctuatorScanningAids.*;

import java.util.Iterator;

public class LexicalAnalyzer extends ScannerImp implements Scanner {
	private static Debug debug = new Debug();
	
	private enum NumberType {
		INTEGER,
		FLOAT;
	}
	
	public static LexicalAnalyzer make(String filename) {
		InputHandler handler = InputHandler.fromFilename(filename);
		PushbackCharStream charStream = PushbackCharStream.make(handler);
		
		return new LexicalAnalyzer(charStream);
	}

	public LexicalAnalyzer(PushbackCharStream input) {
		super(input);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	// Token-finding main dispatch (We create Tokens here)
	@Override
	protected Token findNextToken() {
		LocatedChar ch = nextNonWhitespaceChar();
		
		// beginning of each potential token
		debug.out("Potential Token spot: " + ch.toString());
		
		if (ch.isDigit()) {
			// if we find a digit, keep parsing input until we find a blank space or a period 
			return scanNumber(ch);
		}
		else if (ch.isLowerCase()) {
			return scanIdentifier(ch);
		} 
		else if (isCharacterStart(ch)) {
			return scanCharacter(ch);
		} 
		else if (isPunctuatorStart(ch)) {
			return PunctuatorScanner.scan(ch, input);
		}
		else if (isEndOfInput(ch)) {
			return NullToken.make(ch.getLocation());
		}
		else {
			lexicalError(ch);
			return findNextToken();
		}
	}

	private LocatedChar nextNonWhitespaceChar() {
		LocatedChar ch = input.next();
		
		while(ch.isWhitespace()) {
			ch = input.next();
		}
		return ch;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	// Integer and Float lexical analysis	
	private Token scanNumber(LocatedChar firstChar) {
		StringBuffer buffer = new StringBuffer();
		NumberType numberType;
		
		buffer.append(firstChar.getCharacter());
		
		numberType = appendSubsequentDigits(buffer);
		
		debug.out("scanned number value: " + buffer.toString());
		
		if (numberType == NumberType.INTEGER) { // It is an Integer
			return IntegerToken.make(firstChar.getLocation(), buffer.toString());
		} else if (numberType == NumberType.FLOAT) {
			return FloatToken.make(firstChar.getLocation(), buffer.toString());
		} else {
			throw new IllegalArgumentException("found : number is not an Integer or a Float");
		}
	}
	
	// Not actually appending the digits, instead we are looking for the end of the statement
	private NumberType appendSubsequentDigits(StringBuffer buffer) {
		LocatedChar c = input.next();

		// Search input until we find a non-digit (ideally a '.' or a ';'
		while(c.isDigit()) {
			buffer.append(c.getCharacter());
			c = input.next();
		}
		
		//debug.out("Character: " + c.getCharacter());
		
		// If we found a '.', we have found a Float, otherwise it is an Integer
		if (c.getCharacter() == '.') {
			buffer.append(c.getCharacter());
			c = input.next();
			
			while(c.isDigit()) {
				buffer.append(c.getCharacter());
				c = input.next();
			}
			
			input.pushback(c);
			
			return NumberType.FLOAT;
		} else {
			input.pushback(c);
			
			return NumberType.INTEGER;
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////
	// Character lexical analysis
	private Token scanCharacter(LocatedChar lc) {
		LocatedChar c = input.next();

		// Search input until we find a non-digit (ideally a '.' or a ';'
//		while(c.isDigit()) {
//			buffer.append(c.getCharacter());
//			c = input.next();
//		}
		
		if (true) {
		return CharacterToken.make(lc.getLocation(), lc.toString());
		} else {
			throw new IllegalArgumentException("found : number is not an Integer or a Float");
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////
	// Identifier (variable names) and keyword (ex. imm, print) lexical analysis	
	private Token scanIdentifier(LocatedChar firstChar) {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(firstChar.getCharacter());
		appendSubsequentLowercase(buffer);

		String lexeme = buffer.toString();
		
		if(Keyword.isAKeyword(lexeme)) {
			return LextantToken.make(firstChar.getLocation(), lexeme, Keyword.forLexeme(lexeme));
		}
		else {
			IdentifierToken token = IdentifierToken.make(firstChar.getLocation(), lexeme);
			
			return token;
		}
	}
	
	private void appendSubsequentLowercase(StringBuffer buffer) {
		LocatedChar c = input.next();
		
		while(c.isLowerCase()) {
			buffer.append(c.getCharacter());
			c = input.next();
		}
		
		input.pushback(c);
	}

	//////////////////////////////////////////////////////////////////////////////
	// Character-classification routines specific to Grouse scanning.	
	private boolean isPunctuatorStart(LocatedChar lc) {
		char c = lc.getCharacter();
		
		return isPunctuatorStartingCharacter(c);
	}

	private boolean isEndOfInput(LocatedChar lc) {
		return lc == LocatedCharStream.FLAG_END_OF_INPUT;
	}
	
	private boolean isCharacterStart(LocatedChar lc) {
		char c = lc.getCharacter();
		
		if (c == '\'') {
			return lc.isCharacter();
		} else {
			return false;
		}
	}	
	// unused
//	private boolean isBackSlash(LocatedChar lc) {
//		char c = lc.getCharacter();
//		char nextCharacter = input.peek().getCharacter();
//		
//		debug.out("PEEKED CHAR: " + nextCharacter);
//		
//		// If next token is a "/" then it is a comment, if it is a blank then it is a Punctuator
//		// otherwise, it's an error
//		if (nextCharacter == '/') {
//			// Then we have a Comment
//			
//		} else if (nextCharacter == ' ') {
//			// Then we have a Punctuator
//			
//		} else {
//			// Throw an error
//			
//		}
//		
//		return isCommentStartingCharacter(c);
//	}
	
	// unused
	private boolean isCommentStartingCharacter(char c) {
		return true;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	// Error-reporting	

	private void lexicalError(LocatedChar ch) {
		GrouseLogger log = GrouseLogger.getLogger("compiler.lexicalAnalyzer");
		log.severe("Lexical error: invalid character " + ch);
	}

	//////////////////////////////////////////////////////////////////////////////
	// Punctuator lexical analysis	
	// old method left in to show a simple scanning method.
	// current method is the algorithm object PunctuatorScanner.java
	
	@SuppressWarnings("unused")
	private Token oldScanPunctuator(LocatedChar ch) {
		TextLocation location = ch.getLocation();
		
		switch(ch.getCharacter()) {
			case '*':
				return LextantToken.make(location, "*", Punctuator.MULTIPLY);
			case '+':
				return LextantToken.make(location, "+", Punctuator.ADD);
			case '>':
				return LextantToken.make(location, ">", Punctuator.GREATER);
			case ':':
				if(ch.getCharacter()=='=') {
					return LextantToken.make(location, ":=", Punctuator.ASSIGN);
				}
			else {
				throw new IllegalArgumentException("found : not followed by = in scanOperator");
			}
			case ',':
				return LextantToken.make(location, ",", Punctuator.SEPARATOR);
			case ';':
				return LextantToken.make(location, ";", Punctuator.TERMINATOR);
			default:
				throw new IllegalArgumentException("bad LocatedChar " + ch + "in scanOperator");
		}
	}
}
