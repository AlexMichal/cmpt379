package semanticAnalyzer.types;

public class TupleType implements Type {
	private int sizeInBytes;
	private String infoString;
	
	private TupleType(int size) {
		this.sizeInBytes = size;
		this.infoString = toString();
	}
	
	private TupleType(int size, String infoString) {
		this.sizeInBytes = size;
		this.infoString = infoString;
	}
	
	public int getSize() {
		return sizeInBytes;
	}
	
	public String infoString() {
		return infoString;
	}
	
	public boolean equals(TupleType tuple) { // TODO: equals
		return false;
	}
}
