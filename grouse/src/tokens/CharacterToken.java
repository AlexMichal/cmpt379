package tokens;

import inputHandler.TextLocation;
import utilities.Debug;

public class CharacterToken extends TokenImp {
	protected char value;
	private static Debug debug = new Debug(); // TODO: REMOVE AFTER DONE TESTING
	
	protected CharacterToken(TextLocation location, String lexeme) {
		super(location, lexeme);
	}
	protected void setValue(char value) {
		this.value = value;
	}
	public char getValue() {
		return value;
	}
	
	public static CharacterToken make(TextLocation location, String lexeme) {
		CharacterToken result = new CharacterToken(location, lexeme);
		
		debug.out("Character token: " + lexeme.charAt(0));
		
		result.setValue(lexeme.charAt(0)); // TODO: character related HERE
		
		debug.out("Character token: " + result.getValue());
		
		return result;
	}
	
	@Override
	protected String rawString() {
		return "number, " + value;
	}
}
