package inputHandler;

import java.util.Iterator;

import utilities.Debug;

// Locate each character and it's location
public class LocatedCharStream implements Iterator<LocatedChar> {
	private static Debug debug = new Debug();
	
	public static final char NULL_CHAR = '\0';
	public static final LocatedChar FLAG_END_OF_INPUT = new LocatedChar(NULL_CHAR, new TextLocation("null", -1, -1));
	
	private Iterator<String> inputIterator;
	private String line;
	private int index;
	private LocatedChar next;
	private InputHandler input;
	
	// Constructor (instantiate a LocatedCharStream)
	// Only gets instantiated once
	public LocatedCharStream(InputHandler input) {
		super();
		this.input = input;
		this.inputIterator = input.iterator();
		this.index = 0;
		this.line = "";
		preloadChar();
		
	}
	
	private void preloadChar() {
		ensureLineHasACharacter();
		
		next = nextCharInLine();
	}
	
	private static int lineNumberOfRestOfLineToDelete = -1;
	
	// Returns each and every character in the file
	private LocatedChar nextCharInLine() {
		if (endOfInput()) return FLAG_END_OF_INPUT;
		
		TextLocation location = new TextLocation(input.fileName(), input.lineNumber(), index);
		
		// Get next character in input stream
		char character = line.charAt(index);
		
		// Return a null character if it's a character after "//" 
		if (lineNumberOfRestOfLineToDelete == input.lineNumber()) {
			index++;
			
			return new LocatedChar(' ', location);
		}
	
		// Check if the current character is a '/'
		if (character == '/') {
			// Check if the next character in the input is also a '/'
			// If so, we have found the beginning of a comment
			// And we need to remove everything TODO: until we find a '/n'
			if (line.charAt(index + 1) == '/') {
				lineNumberOfRestOfLineToDelete = input.lineNumber();
				
				index++;
				
				return new LocatedChar(' ', location);
			}
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
