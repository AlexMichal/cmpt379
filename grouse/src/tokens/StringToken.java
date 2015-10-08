package tokens;

import inputHandler.TextLocation;
import utilities.Debug;

public class StringToken extends TokenImp {
	protected String value;
	private static Debug debug = new Debug(); // TODO: REMOVE AFTER DONE TESTING
	
	protected StringToken(TextLocation location, String lexeme) {
		super(location, lexeme);
	}
	protected void setValue(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	
	public static StringToken make(TextLocation location, String lexeme) {
		StringToken result = new StringToken(location, lexeme);
		
		debug.out("String token: " + lexeme);
		
		result.setValue(lexeme); // TODO: character related HERE
		
		debug.out("String token: " + result.getValue());
		
		return result;
	}
	
	@Override
	protected String rawString() {
		return "string, " + value;
	}
}
