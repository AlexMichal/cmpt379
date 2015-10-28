package symbolTable;

import asmCodeGenerator.codeStorage.ASMCodeFragment;
import inputHandler.TextLocation;
import semanticAnalyzer.types.PrimitiveType;
import semanticAnalyzer.types.Type;

public class Binding {
	private Type type;
	private TextLocation textLocation;
	private MemoryLocation memoryLocation;
	private String lexeme;
	private Object extra;
	
	public Binding(Type type, TextLocation location, MemoryLocation memoryLocation, String lexeme, Object extra) {
		super();
		this.type = type;
		this.textLocation = location;
		this.memoryLocation = memoryLocation;
		this.lexeme = lexeme;
		this.extra = extra;
	}
	
	public String toString() {
		return "[" + lexeme +
				" " + type +  // " " + textLocation +	
				" " + memoryLocation +
				" " + extra +
				"]";
	}	
	
	public String getLexeme() {
		return lexeme;
	}
	
	public Type getType() {
		return type;
	}
	
	public Object getExtra() {
		return extra;
	}
	
	public TextLocation getLocation() {
		return textLocation;
	}
	
	public MemoryLocation getMemoryLocation() {
		return memoryLocation;
	}
	
	public void generateAddress(ASMCodeFragment code) {
		memoryLocation.generateAddress(code, "%% " + lexeme);
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	// NULL BINDING OBJECT
	////////////////////////////////////////////////////////////////////////////////////

	public static Binding nullInstance() {
		return NullBinding.getInstance();
	}
	
	private static class NullBinding extends Binding {
		private static NullBinding instance = null;
		
		private NullBinding() {
			super(PrimitiveType.ERROR,
					TextLocation.nullInstance(),
					MemoryLocation.nullInstance(),
					"the-null-binding",
					null);
		}
		
		public static NullBinding getInstance() {
			if (instance == null) {
				instance = new NullBinding();
			}
			
			return instance;
		}
	}
}
