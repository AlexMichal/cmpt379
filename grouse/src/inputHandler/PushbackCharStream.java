package inputHandler;

import java.util.Stack;

import utilities.Debug;

public class PushbackCharStream extends LocatedCharStream {
	private Stack<LocatedChar> pushedBack;
	private static Debug debug = new Debug();
	
	// Constructor
	// Gets called only once
	public PushbackCharStream(InputHandler handler) {
		super(handler);
		
		// Create an empty PushedBack stack
		this.pushedBack = new Stack<LocatedChar>();
	}

	@Override
	public boolean hasNext() {
		return !pushedBack.empty() || super.hasNext();
	}

	@Override
	public LocatedChar next() {
		//debug.out("NEXT");
		
		if(pushedBack.empty()) {
			return super.next();
		}
		else {
			//debug.out("POP: " + pushedBack.get(pushedBack.ind));
			return pushedBack.pop(); // Pop non-Punctuators
		}
	}
	
	public LocatedChar peek() {
		LocatedChar result = next(); // Pop off the result
		debug.out("PEEK");
		pushback(result); // Push back the result
		
		return result; // Return the result
	}

	public void pushback(LocatedChar locatedChar) {
		pushedBack.push(locatedChar);
	}

	/**
	 * remove is an unsupported operation.  It throws an UnsupportedOperationException.
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	public static PushbackCharStream make(InputHandler handler) {
		return new PushbackCharStream(handler);
	}
}
