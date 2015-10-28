package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import lexicalAnalyzer.Keyword;
import lexicalAnalyzer.Lextant;
import tokens.LextantToken;
import tokens.Token;

public class DeclarationNode extends ParseNode {
	enum validTypes {
		IMMUTABLE,
		VARIABLE,
		NONE;
	}
	
	private validTypes typeOfIdentifer;
	
	public DeclarationNode(Token token) {
		super(token);
		assert(token.isLextant(Keyword.IMMUTABLE) || token.isLextant(Keyword.VARIABLE) || token.isLextant(Keyword.LET));
	}

	public DeclarationNode(ParseNode node) {
		super(node);
	}
	
	////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////
	
	public Lextant getDeclarationType() {
		return lextantToken().getLextant();
	}
	
	public LextantToken lextantToken() {
		return (LextantToken)token;
	}	
	
	// TODO: DELETE
	/*public void setTypeOfIdentifier(String typeOfIdentifier) {
		switch (typeOfIdentifier) {
			case "var" : 
				this.typeOfIdentifer = validTypes.VARIABLE;
				break;
			case "imm" :
				this.typeOfIdentifer = validTypes.IMMUTABLE;
				break;
			default :
				this.typeOfIdentifer = validTypes.NONE;
				break;
		}
	}
	
	public validTypes getTypeOfIdentifier() {
		return typeOfIdentifer;
	}*/
	
	////////////////////////////////////////////////////////////
	// CONVENIENCE FACTORY
	////////////////////////////////////////////////////////////
	
	public static DeclarationNode withChildren(Token token, ParseNode declaredName, ParseNode initializer) {
		DeclarationNode node = new DeclarationNode(token);
		node.appendChild(declaredName);
		node.appendChild(initializer);
		return node;
	}
	
	///////////////////////////////////////////////////////////
	// BOILERPLATE FOR VISITORS
	///////////////////////////////////////////////////////////
	
	public void accept(ParseNodeVisitor visitor) {
		visitor.visitEnter(this);
		visitChildren(visitor);
		visitor.visitLeave(this);
	}
}
