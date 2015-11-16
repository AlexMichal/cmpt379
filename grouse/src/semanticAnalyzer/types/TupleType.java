package semanticAnalyzer.types;

public class TupleType implements Type {
	private Type[] paramTypes;
	
	// Trivial Tuple Types
	private TupleType(Type... type) {
		if (type.length == 0) {
			paramTypes[0] = PrimitiveType.VOID;
		} else {
			storeParamTypes(type);
		}
	}
	
	private void storeParamTypes(Type[] types) {
		paramTypes = new Type[types.length - 1];
		
		for (int i = 0; i < types.length - 1; i++) {
			paramTypes[i] = types[i];
		}
	}
	
	public boolean equals(TupleType tuple) { // TODO: equals
		return false;
	}

	@Override
	public int getSize() {
		return 0;
	}

	@Override
	public String infoString() { // TODO: string
		return "TupleType[" + "]";
	}
}
