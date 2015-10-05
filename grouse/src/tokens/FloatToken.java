package tokens;

import inputHandler.TextLocation;

public class FloatToken extends TokenImp {
	protected double value;
	
	protected FloatToken(TextLocation location, String lexeme) {
		super(location, lexeme);
	}
	protected void setValue(double value) {
		this.value = value;
	}
	public double getValue() {
		return value;
	}
	
	public static FloatToken make(TextLocation location, String lexeme) {
		FloatToken result = new FloatToken(location, lexeme);
		
		result.setValue(Double.parseDouble(lexeme));
		
		return result;
	}
	
	@Override
	protected String rawString() {
		return "number, " + value;
	}
}
