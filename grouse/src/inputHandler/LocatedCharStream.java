package inputHandler;

import java.util.Iterator;

import utilities.Debug;

// Locate each character and it's location
public class LocatedCharStream implements Iterator<LocatedChar> {
	private static Debug debug = new Debug();
	private static ParsingState parsingState;
	
	public static final char NULL_CHAR = '\0';
	public static final LocatedChar FLAG_END_OF_INPUT = new LocatedChar(NULL_CHAR, new TextLocation("null", -1, -1));
	
	private Iterator<String> inputIterator;
	private String line;
	private int index;
	private LocatedChar next;
	private InputHandler input;
	private enum ParsingState {
		DEFAULT,
		STRING,
		COMMENT;
	}
	
	// Constructor (instantiate a LocatedCharStream)
	// Only gets instantiated once
	public LocatedCharStream(InputHandler input) {
		super();
		this.input = input;
		this.inputIterator = input.iterator();
		this.index = 0;
		this.line = "";
		parsingState = ParsingState.DEFAULT;
		preloadChar();
	}
	
	private void preloadChar() {
		ensureLineHasACharacter();
		
		next = nextCharInLine();
	}
	
	private static int lineNumberOfRestOfLineToDelete = -1;
	
	// Returns each and every character in the file one at a time
	private LocatedChar nextCharInLine() {
		if (endOfInput()) return FLAG_END_OF_INPUT;
		
		TextLocation location = new TextLocation(input.fileName(), input.lineNumber(), index);
		
		char character = line.charAt(index); // Get next character in input stream
		
		debug.out("CURRENT STATE: " + parsingState);
		switch (parsingState) {
			case DEFAULT:
				// Check if the current character is a '/' for a Comment
				if (character == '/') {
					// Check if the next character in the input is also a '/'
					// If so, we have found the beginning of a comment
					// And we need to remove everything TODO: until we find a '/n'
					if (line.charAt(index + 1) == '/') {
						parsingState = ParsingState.COMMENT;
						
						lineNumberOfRestOfLineToDelete = input.lineNumber();
						
						index++;
						
						return new LocatedChar(' ', location);
					}
				}
				
			case COMMENT: 
				// Return a null character if it's a character after "//" 
				if (lineNumberOfRestOfLineToDelete == input.lineNumber()) {
					index++;
					
					return new LocatedChar(' ', location);
				}
				
				break;	
			case STRING:
				return null;
		}

		// Increase the index
		index++;
				
		return new LocatedChar(character, location);
	}
	
	private void ensureLineHasACharacter() {
		while(!moreCharsInLine() && inputIterator.hasNext()) {
			readNextLine();
		}
	}
	private boolean endOfInput() {
		return !moreCharsInLine() && !inputIterator.hasNext();
	}
	
	private boolean moreCharsInLine() {
		return index < line.length();
	}
	
	private void readNextLine() {
		assert(inputIterator.hasNext());
		line = inputIterator.next();
		index = 0;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	// Iterator<LocatedChar> overrides
	// next() extra-politely returns a fully-formed LocatedChar (FLAG_END_OF_INPUT)
	//         if hasNext() is false.  FLAG_END_OF_INPUT is a lightweight Null Object.
	@Override
	public boolean hasNext() {
		return next != FLAG_END_OF_INPUT;
	}
	@Override
	public LocatedChar next() {
		LocatedChar result = next;
		
		preloadChar();
		
		return result;
	}

	/**
	 * remove is an unsupported operation.  It throws an UnsupportedOperationException.
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
