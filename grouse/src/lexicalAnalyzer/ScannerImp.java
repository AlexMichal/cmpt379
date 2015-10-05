package lexicalAnalyzer;

import inputHandler.PushbackCharStream;
import tokens.NullToken;
import tokens.Token;
import utilities.Debug;

public abstract class ScannerImp implements Scanner {
	private Token nextToken;
	private Debug debug = new Debug();
	protected final PushbackCharStream input;
	
	protected abstract Token findNextToken();

	public ScannerImp(PushbackCharStream input) {
		super();
		
		this.input = input;
		
		nextToken = findNextToken();
		
		debug.out(nextToken.fullString());
	}

	// Iterator<Token> implementation
	@Override
	public boolean hasNext() {
		return !(nextToken instanceof NullToken);
	}

	@Override
	public Token next() {
		Token result = nextToken;
		
		//debug.out("RESULT: " + nextToken.getLocation().getLineNumber());
		
		nextToken = findNextToken();
		
		//debug.out("NEXT TOKEN: " + nextToken);`
		
		return result;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}