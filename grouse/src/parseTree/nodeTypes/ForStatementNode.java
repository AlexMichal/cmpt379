package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import lexicalAnalyzer.Keyword;
import lexicalAnalyzer.Lextant;
import tokens.LextantToken;
import tokens.Token;

public class ForStatementNode extends ParseNode {
	private String startLabel;
	private String endLabel;
	private String continueLabel;
	
	public ForStatementNode(Token token) {
		super(token);
		
		assert(token.isLextant(Keyword.FOR));
	}

	public ForStatementNode(ParseNode node) {
		super(node);
	}

	////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////
	
	public Lextant getIfStatementType() {
		return lextantToken().getLextant();
	}
	
	public LextantToken lextantToken() {
		return (LextantToken)token;
	}
	
	public void setStartLabel(String label){
		this.startLabel = label;
	}

	public void setEndLabel(String label){
		this.endLabel = label;
	}
	
	public void setContinueLabel(String label){
		this.continueLabel = label;
	}

	public String getStartLabel(){
		return startLabel;
	}

	public String getEndLabel(){
		return endLabel;
	}
	
	public String getContinueLabel(){
		return continueLabel;
	}
	
	////////////////////////////////////////////////////////////
	// CONVENIENCE FACTORY
	////////////////////////////////////////////////////////////
	
	// If statement without an Else statement
	public static ForStatementNode withChildren(Token token, ParseNode forControlPhrase, ParseNode forStatementblock) {
		ForStatementNode node = new ForStatementNode(token);
		
		node.appendChild(forControlPhrase);
		node.appendChild(forStatementblock);
		
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
