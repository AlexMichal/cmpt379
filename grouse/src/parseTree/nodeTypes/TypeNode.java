package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import semanticAnalyzer.types.PrimitiveType;
import semanticAnalyzer.types.Type;
import lexicalAnalyzer.Keyword;
import lexicalAnalyzer.Lextant;
import tokens.IdentifierToken;
import tokens.LextantToken;
import tokens.Token;

public class TypeNode extends ParseNode {
	private Type type;
	
	public TypeNode(Token token) {
		super(token);
		this.type = PrimitiveType.NO_TYPE;
		
		assert(token.isLextant(Keyword.BOOL) ||
				token.isLextant(Keyword.CHAR) ||
				token.isLextant(Keyword.STRING) ||
				token.isLextant(Keyword.INT) ||
				token.isLextant(Keyword.FLOAT) ||
				token instanceof IdentifierToken);
	}

	public TypeNode(ParseNode node) {
		super(node);
		this.type = node.getType();
	}
	
	////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////
	
	public LextantToken lextantToken() {
		return (LextantToken)token;
	}	
	
	public Type getType() {
		return type;
	}
	
	public void setType(Type newType) {
		this.type = newType;
	}

	////////////////////////////////////////////////////////////
	// BOILERPLATE FOR VISITORS
	////////////////////////////////////////////////////////////
	
	public void accept(ParseNodeVisitor visitor) {
		visitor.visit(this);
	}
}
