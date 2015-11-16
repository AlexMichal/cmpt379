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
import tokens.StringToken;
import tokens.IntegerToken;
import tokens.Token;
import utilities.Debug;

import static lexicalAnalyzer.PunctuatorScanningAids.*;

import java.util.Iterator;

public class LexicalAnalyzer extends ScannerImp implements Scanner {
	private static Debug debug = new Debug();
	private final int MAX_IDENTIFIER_LENGTH = 32;
	
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
	// TOKEN-FINDING MAIN DISPATCHs (WE CREATE TOKENS HERE)
	//////////////////////////////////////////////////////////////////////////////
	
	@Override
	protected Token findNextToken() {
		LocatedChar ch = nextNonWhitespaceChar();
		
		// beginning of each potential token
		//debug.out("Potential Token spot: " + ch.toString());
		
		if (isNumberStart(ch)) {
			return scanNumber(ch);
		}
		else if (isCharacterStart(ch)) {
			return scanCharacter(ch);
		}
		else if (isStringStart(ch)) {
			return scanString(ch);
		} 
		else if (isIdentifierStart(ch)) {
			return scanIdentifier(ch);
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
	// INTEGER AND FLOAT LEXICAL ANALYSIS
	//////////////////////////////////////////////////////////////////////////////
	
	private Token scanNumber(LocatedChar firstChar) {
		StringBuffer buffer = new StringBuffer();
		NumberType numberType;
		
		buffer.append(firstChar.getCharacter());
		
		numberType = appendSubsequentDigits(buffer);
		
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
	// CHARACTER LEXICAL ANALYSIS
	//////////////////////////////////////////////////////////////////////////////
	
	private Token scanCharacter(LocatedChar lc) {
		LocatedChar c = input.next();
		
		//debug.out("SCAN CHARACTER: " + c.getCharacter().toString());
		
		if (c.isCharacter()) {
			return CharacterToken.make(lc.getLocation(), c.getCharacter().toString());
		} else {
			throw new IllegalArgumentException("found : character is not a Character");
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////
	// STRING LEXICAL ANALYSIS
	//////////////////////////////////////////////////////////////////////////////
	
	private Token scanString(LocatedChar lc) {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(lc.getCharacter());
		
		appendSubsequentStringCharacters(buffer);
		
		//debug.out("scanString: " + buffer.toString());
		
		return StringToken.make(lc.getLocation(), buffer.toString());
		
		/*} else { // TODO: fix
			lexicalError(lc);
			findNextToken();
			
			throw new IllegalArgumentException("found : value is not a string");
		}*/
	}
	
	private void appendSubsequentStringCharacters(StringBuffer buffer) {
		LocatedChar c = input.next();
		
		while (c.isStringCharacter()) {
			buffer.append(c.getCharacter());
			c = input.next();
		}
		
		buffer.append(c.getCharacter());
		c = input.next();
		
		input.pushback(c);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	// IDENTIFIER (variable names) AND KEYWORDS (imm, print, etc) LEXICAL ANALYSIS
	//////////////////////////////////////////////////////////////////////////////
	
	private Token scanIdentifier(LocatedChar firstChar) {
		String lexeme = getIdentifierName(firstChar);
		
		if (lexeme.length() > MAX_IDENTIFIER_LENGTH) tooManyCharactersLexicalError(firstChar);
		
		if (Keyword.isAKeyword(lexeme)) {
			return LextantToken.make(firstChar.getLocation(), lexeme, Keyword.forLexeme(lexeme));
		} else {
			return IdentifierToken.make(firstChar.getLocation(), lexeme);
		}
	}
	
	private String getIdentifierName(LocatedChar firstChar) {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(firstChar.getCharacter());
		appendSubsequentValidIdentifierCharacters(buffer);
		
		return buffer.toString();
	}
	
	private void appendSubsequentValidIdentifierCharacters(StringBuffer buffer) {
		LocatedChar c = input.next();
		
		while (c.isValidIdentifierCharacter()) {
			buffer.append(c.getCharacter());
			c = input.next();
		}
		
		input.pushback(c);
	}

	//////////////////////////////////////////////////////////////////////////////
	// Character-classification routines specific to Grouse scanning	
	//////////////////////////////////////////////////////////////////////////////
	
	private boolean isNumberStart(LocatedChar lc) {
		return (lc.isDigit() || isNegativeFollowedByNumber(lc));
	}
	
	private boolean isNegativeFollowedByNumber(LocatedChar lc) {
		return (lc.isNegative() && input.peek().isDigit());
	}
	
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
	
	private boolean isStringStart(LocatedChar lc) {
		char c = lc.getCharacter();

		if (c == '"') {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isIdentifierStart(LocatedChar lc) {
		char c = lc.getCharacter();
		
		if ((Character.isLetter(c)) || (c == '_')) {
			return true;
		}
		
		return false;
	}
	
	@SuppressWarnings("unused")
	private boolean isCommentStartingCharacter(char c) {
		return true;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	// ERROR-REPORTING
	//////////////////////////////////////////////////////////////////////////////
	
	private void lexicalError(LocatedChar ch) {
		GrouseLogger log = GrouseLogger.getLogger("compiler.lexicalAnalyzer");
		
		log.severe("Lexical error: invalid character " + ch);
	}
	
	private void tooManyCharactersLexicalError(LocatedChar ch) {
		GrouseLogger log = GrouseLogger.getLogger("compiler.lexicalAnalyzer");
		
		log.severe("Lexical error: " + MAX_IDENTIFIER_LENGTH + "-character limit on identifiers " + ch);
	}
}
