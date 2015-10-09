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
		
		result.setValue(removeDoubleQuotes(lexeme)); // TODO: Remove debugs
		
		debug.out("String token (after): " + result.getValue());
		
		return result;
	}
	
	@Override
	protected String rawString() {
		return "string, " + value;
	}
	
	// Helper function
	private static String removeDoubleQuotes(String str) {
		return str.replace("\"", "");
	}
}
