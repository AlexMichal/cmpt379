package parseTree.nodeTypes;

import parseTree.ParseNode;
import parseTree.ParseNodeVisitor;
import lexicalAnalyzer.Keyword;
import lexicalAnalyzer.Lextant;
import tokens.LextantToken;
import tokens.Token;

public class WhileStatementNode extends ParseNode {
	private String startLabel;
	private String endLabel;
	private String continueLabel;
	
	public WhileStatementNode(Token token) {
		super(token);
		
		assert(token.isLextant(Keyword.WHILE));
	}

	public WhileStatementNode(ParseNode node) {
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
	public static WhileStatementNode withChildren(Token token, ParseNode expression, ParseNode ifStatementBlock) {
		WhileStatementNode node = new WhileStatementNode(token);
		
		node.appendChild(expression);
		node.appendChild(ifStatementBlock);
		
		return node;
	}
	
	// If statement with an Else statement
	public static WhileStatementNode withChildren(Token token, ParseNode expression, ParseNode ifStatementBlock, ParseNode elseStatementBlock) {
		WhileStatementNode node = new WhileStatementNode(token);
		
		node.appendChild(expression);
		node.appendChild(ifStatementBlock);
		node.appendChild(elseStatementBlock);
		
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
