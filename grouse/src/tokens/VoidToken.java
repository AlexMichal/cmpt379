package tokens;

import inputHandler.TextLocation;

public class VoidToken extends TokenImp {

	protected VoidToken(TextLocation location, String lexeme) {
		super(location, lexeme);
	}

	@Override
	protected String rawString() {
		return "void token";
	}
	
	public static VoidToken make(TextLocation location) {
		VoidToken result = new VoidToken(location, "");
		return result;
	}
}
