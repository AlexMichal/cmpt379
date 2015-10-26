package symbolTable;

import inputHandler.TextLocation;
import logging.GrouseLogger;
import parseTree.nodeTypes.IdentifierNode;
import semanticAnalyzer.types.Type;
import tokens.Token;
import utilities.Debug;

public class Scope {
	private Scope baseScope;
	private MemoryAllocator allocator;
	private SymbolTable symbolTable;
	
	//////////////////////////////////////////////////////////////////////
	// FACTORIES
	//////////////////////////////////////////////////////////////////////
	
	public static Scope createProgramScope() {
		return new Scope(programScopeAllocator(), nullInstance());
	}
	
	public Scope createSubscope() {
		return new Scope(allocator, this);
	}
	
	private static MemoryAllocator programScopeAllocator() {
		return new PositiveMemoryAllocator(
				MemoryAccessMethod.DIRECT_ACCESS_BASE, 
				MemoryLocation.GLOBAL_VARIABLE_BLOCK);
	}
	
	//////////////////////////////////////////////////////////////////////
	// PRIVATE CONSTRUCTOR
	//////////////////////////////////////////////////////////////////////
	
	private Scope(MemoryAllocator allocator, Scope baseScope) {
		super();
		this.baseScope = (baseScope == null) ? this : baseScope;
		this.symbolTable = new SymbolTable();
		
		this.allocator = allocator;
		allocator.saveState();
	}
	
	///////////////////////////////////////////////////////////////////////
	// BASIC QUERIES	
	///////////////////////////////////////////////////////////////////////
	
	public Scope getBaseScope() {
		return baseScope;
	}
	
	public MemoryAllocator getAllocationStrategy() {
		return allocator;
	}
	
	public SymbolTable getSymbolTable() {
		return symbolTable;
	}
	
	///////////////////////////////////////////////////////////////////////
	// MEMORY ALLOCATION
	///////////////////////////////////////////////////////////////////////
	
	// must call leave() when destroying/leaving a scope.
	public void leave() {
		allocator.restoreState();
	}
	
	public int getAllocatedSize() {
		return allocator.getMaxAllocatedSize();
	}

	///////////////////////////////////////////////////////////////////////
	// BINDINGS
	///////////////////////////////////////////////////////////////////////
	
	public Binding createBinding(IdentifierNode identifierNode, Type type) { // TODO: LET  binding here
		Token token = identifierNode.getToken();
		
		Debug debug = new Debug();
		//debug.out("MESSAGE: " + identifierNode.toString()); // TODO: DEBUG CREATE BINDING
		//debug.out("MESSAGE: " + identifierNode.getParent().getToken().getLexeme());
		
		if (identifierNode.getParent().getToken().getLexeme() == "imm") {
			symbolTable.errorIfAlreadyDefined(token);
		}

		String lexeme = token.getLexeme();
		Binding binding = allocateNewBinding(type, token.getLocation(), lexeme);	
		symbolTable.install(lexeme, binding);

		return binding;
	}
	
	private Binding allocateNewBinding(Type type, TextLocation textLocation, String lexeme) {
		MemoryLocation memoryLocation = allocator.allocate(type.getSize());
		return new Binding(type, textLocation, memoryLocation, lexeme);
	}
	
	///////////////////////////////////////////////////////////////////////
	//toString
	///////////////////////////////////////////////////////////////////////
	
	public String toString() {
		String result = "scope: ";
		result += " hash "+ hashCode() + "\n";
		result += symbolTable;
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////
	// NULL SCOPE OBJECT - LAZY SINGLETON (Lazy Holder) IMPLEMENTATION PATTERN
	////////////////////////////////////////////////////////////////////////////////////
	
	public static Scope nullInstance() {
		return NullScope.instance;
	}
	
	private static class NullScope extends Scope {
		private static NullScope instance = new NullScope();

		private NullScope() {
			super(	new PositiveMemoryAllocator(MemoryAccessMethod.NULL_ACCESS, "", 0),
					null);
		}
		
		public String toString() {
			return "scope: the-null-scope";
		}
		
		@Override
		public Binding createBinding(IdentifierNode identifierNode, Type type) {
			unscopedIdentifierError(identifierNode.getToken());
			return super.createBinding(identifierNode, type);
		}
		
		// subscopes of null scope need their own strategy.  Assumes global block is static.
		public Scope createSubscope() {
			return new Scope(programScopeAllocator(), this);
		}
	}


	///////////////////////////////////////////////////////////////////////
	// ERROR REPORTING
	///////////////////////////////////////////////////////////////////////
	
	private static void unscopedIdentifierError(Token token) {
		GrouseLogger log = GrouseLogger.getLogger("compiler.scope");
		log.severe("variable " + token.getLexeme() + 
				" used outside of any scope at " + token.getLocation());
	}
}
