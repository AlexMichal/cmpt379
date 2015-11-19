package semanticAnalyzer.types;

import java.util.ArrayList;

public class TupleType implements Type {
	private ArrayList<Type> paramTypes = new ArrayList<Type>();
	
	public void addParameter(Type paramType){
		paramTypes.add(paramType);
	}
	
	public int getNumberOfParameters(){
		return paramTypes.size();
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
