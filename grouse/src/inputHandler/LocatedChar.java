package inputHandler;

import utilities.Debug;

/** Value object for holding a character and its location in the input text.
 *  Contains delegates to select character operations.
 */
public class LocatedChar {
	private static Debug debug = new Debug();
	Character character;
	TextLocation location;
	
	public LocatedChar(Character character, TextLocation location) {
		super();
		this.character = character;
		this.location = location;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	// Getters
	
	public Character getCharacter() {
		return character;
	}
	
	public TextLocation getLocation() {
		return location;
	}
	
	public boolean isChar(char c) {
		return character == c;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	// toString
	
	public String toString() {
		return "(" + charString() + ", " + location + ")";
	}
	
	private String charString() {
		if(Character.isWhitespace(character)) {
			int i = character;
			return String.format("'\\%d'", i);
		}
		else {
			return character.toString();
		}
	}

	//////////////////////////////////////////////////////////////////////////////
	// Delegates
	public boolean isCharacter() {
		return isAsciiCharactersInRange(character, 32, 126);
	}
	
	public boolean isLowerCase() {
		return Character.isLowerCase(character);
	}
	
	public boolean isDigit() {
		return Character.isDigit(character);
	}
	
	public boolean isWhitespace() {
		return Character.isWhitespace(character);
	}
	
	// Helper functions
	private boolean isAsciiCharactersInRange(char character, int startOfRange, int endOfRange) {
		if (startOfRange > endOfRange) return false;
		
		for (int i = startOfRange; i <= endOfRange; i++) {
			if (character == (char)i) return true;
		}
		
		return false;
	}
}
