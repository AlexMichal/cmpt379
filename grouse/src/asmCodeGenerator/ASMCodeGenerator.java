package asmCodeGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asmCodeGenerator.codeStorage.ASMCodeChunk;
import asmCodeGenerator.codeStorage.ASMCodeFragment;
import asmCodeGenerator.codeStorage.ASMOpcode;
import asmCodeGenerator.runtime.RunTime;
import lexicalAnalyzer.Lextant;
import lexicalAnalyzer.Punctuator;
import parseTree.*;
import parseTree.nodeTypes.BinaryOperatorNode;
import parseTree.nodeTypes.BlockStatementNode;
import parseTree.nodeTypes.BooleanConstantNode;
import parseTree.nodeTypes.CharacterConstantNode;
import parseTree.nodeTypes.MainBlockNode;
import parseTree.nodeTypes.DeclarationNode;
import parseTree.nodeTypes.FloatConstantNode;
import parseTree.nodeTypes.IdentifierNode;
import parseTree.nodeTypes.IfStatementNode;
import parseTree.nodeTypes.IntegerConstantNode;
import parseTree.nodeTypes.LetStatementNode;
import parseTree.nodeTypes.NewlineNode;
import parseTree.nodeTypes.PrintStatementNode;
import parseTree.nodeTypes.ProgramNode;
import parseTree.nodeTypes.SeparatorNode;
import parseTree.nodeTypes.StringConstantNode;
import parseTree.nodeTypes.UnaryOperatorNode;
import semanticAnalyzer.types.PrimitiveType;
import semanticAnalyzer.types.Type;
import symbolTable.Binding;
import symbolTable.Scope;
import utilities.Debug;
import static asmCodeGenerator.codeStorage.ASMCodeFragment.CodeType.*;
import static asmCodeGenerator.codeStorage.ASMOpcode.*;

// do not call the code generator if any errors have occurred during analysis.
public class ASMCodeGenerator {
	private static Labeller labeller = new Labeller();
	private static Debug debug = new Debug();
	
	ParseNode root;

	public static ASMCodeFragment generate(ParseNode syntaxTree) {
		ASMCodeGenerator codeGenerator = new ASMCodeGenerator(syntaxTree);
		return codeGenerator.makeASM();
	}
	
	public ASMCodeGenerator(ParseNode root) {
		super();
		this.root = root;
	}
	
	public static Labeller getLabeller() {
		return labeller;
	}
	
	public ASMCodeFragment makeASM() {
		ASMCodeFragment code = new ASMCodeFragment(GENERATES_VOID);
		
		code.append( RunTime.getEnvironment() );
		code.append( globalVariableBlockASM() );
		code.append( programASM() );
//		code.append( MemoryManager.codeForAfterApplication() );
		
		return code;
	}
	
	private ASMCodeFragment globalVariableBlockASM() {
		assert root.hasScope();
		Scope scope = root.getScope();
		int globalBlockSize = scope.getAllocatedSize();
		
		ASMCodeFragment code = new ASMCodeFragment(GENERATES_VOID);
		code.add(DLabel, RunTime.GLOBAL_MEMORY_BLOCK);
		code.add(DataZ, globalBlockSize);
		return code;
	}
	
	private ASMCodeFragment programASM() {
		ASMCodeFragment code = new ASMCodeFragment(GENERATES_VOID);
		
		code.add(    Label, RunTime.MAIN_PROGRAM_LABEL);
		code.append( programCode());
		code.add(    Halt );
		
		return code;
	}
	
	private ASMCodeFragment programCode() {
		CodeVisitor visitor = new CodeVisitor();
		root.accept(visitor);
		return visitor.removeRootCode(root);
	}

	private class CodeVisitor extends ParseNodeVisitor.Default {
		private Map<ParseNode, ASMCodeFragment> codeMap;
		ASMCodeFragment code;
		
		public CodeVisitor() {
			codeMap = new HashMap<ParseNode, ASMCodeFragment>();
		}

		////////////////////////////////////////////////////////////////////
        // Make the field "code" refer to a new fragment of different sorts.
		////////////////////////////////////////////////////////////////////
		
		private void newAddressCode(ParseNode node) {
			code = new ASMCodeFragment(GENERATES_ADDRESS);
			codeMap.put(node, code);
		}
		
		private void newValueCode(ParseNode node) {
			code = new ASMCodeFragment(GENERATES_VALUE);
			codeMap.put(node, code);
		}
		
		private void newVoidCode(ParseNode node) {
			code = new ASMCodeFragment(GENERATES_VOID);
			codeMap.put(node, code);
		}

	    ////////////////////////////////////////////////////////////////////
        // GET CODE FROM THE MAP
		////////////////////////////////////////////////////////////////////
		
		private ASMCodeFragment getAndRemoveCode(ParseNode node) {
			ASMCodeFragment result = codeMap.get(node);
			codeMap.remove(result);
			return result;
		}
		
	    public  ASMCodeFragment removeRootCode(ParseNode tree) {
			return getAndRemoveCode(tree);
		}
	    
		private ASMCodeFragment removeValueCode(ParseNode node) {
			ASMCodeFragment frag = getAndRemoveCode(node);
			makeFragmentValueCode(frag, node);
			return frag;
		}
		
		private ASMCodeFragment removeAddressCode(ParseNode node) {
			ASMCodeFragment frag = getAndRemoveCode(node);
			assert frag.isAddress();
			return frag;
		}
		
		private ASMCodeFragment removeVoidCode(ParseNode node) {
			ASMCodeFragment frag = getAndRemoveCode(node);
			//debug.out("REMOVE VOID NODE:\n" + node);
			
			assert frag.isVoid();
			return frag;
		}
		
	    ////////////////////////////////////////////////////////////////////
        // CONVERT CODE TO VALUE-GENERATING CODE
		////////////////////////////////////////////////////////////////////
		
		private void makeFragmentValueCode(ASMCodeFragment code, ParseNode node) {
			assert !code.isVoid();
			
			if (code.isAddress()) turnAddressIntoValue(code, node);
		}
		
		private void turnAddressIntoValue(ASMCodeFragment code, ParseNode node) {
			if (node.getType() == PrimitiveType.INTEGER) {
				code.add(LoadI);
			} else if (node.getType() == PrimitiveType.BOOLEAN) {
				code.add(LoadC);
			} else if (node.getType() == PrimitiveType.FLOAT) {
				code.add(LoadF);
			} else if (node.getType() == PrimitiveType.CHARACTER) {
				code.add(LoadC);
			} else if (node.getType() == PrimitiveType.STRING) {
				code.add(LoadI);
			} else {
				assert false : "node " + node;
			}
			
			code.markAsValue();
		}
		
	    /////////////////////////////////////////////////////////////////////////
        // ENSURES ALL TYPES OF ParseNode IN GIVEN AST HAVE AT LEAST A visitLeave	
		/////////////////////////////////////////////////////////////////////////
		
		public void visitLeave(ParseNode node) {
			assert false : "node " + node + " not handled in ASMCodeGenerator";
		}
		
		///////////////////////////////////////////////////////////////////////////
		// CONSTRUCTS LARGER THAN STATEMENTS
		///////////////////////////////////////////////////////////////////////////
		
		public void visitLeave(ProgramNode node) {
			newVoidCode(node);
			
			for(ParseNode child : node.getChildren()) {
				ASMCodeFragment childCode = removeVoidCode(child);
				code.append(childCode);
			}
		}
		
		public void visitLeave(MainBlockNode node) {
			newVoidCode(node);
			
			for (ParseNode child : node.getChildren()) {
				ASMCodeFragment childCode = removeVoidCode(child);
				code.append(childCode);
			}
		}

		///////////////////////////////////////////////////////////////////////////
		// STATEMENTS
		///////////////////////////////////////////////////////////////////////////
		
		/* PRINT STATEMENT*/
		
		public void visitLeave(PrintStatementNode node) {
			newVoidCode(node);

			for(ParseNode child : node.getChildren()) {
				if(child instanceof NewlineNode || child instanceof SeparatorNode) {
					ASMCodeFragment childCode = removeVoidCode(child);
					code.append(childCode);
				}
				else {
					appendPrintCode(child);
				}
			}
		}

		/* DECLARATION NODE */
		
		public void visitLeave(DeclarationNode node) {
			newVoidCode(node);
			
			ASMCodeFragment lvalue = removeAddressCode(node.child(0));
			ASMCodeFragment rvalue = removeValueCode(node.child(1));
			
			code.append(lvalue);
			code.append(rvalue);
			
			Type type = node.getType();
			code.add(opcodeForStore(type));
		}
		
		/* LET STATEMENT */
		
		public void visitLeave(LetStatementNode node) { // TODO: let
			newVoidCode(node);
			
			ASMCodeFragment lvalue = removeAddressCode(node.child(0));	
			ASMCodeFragment rvalue = removeValueCode(node.child(1));
			
			code.append(lvalue);
			code.append(rvalue);
			
			Type type = node.getType();
			code.add(opcodeForStore(type));
		}
		
		/* IF STATEMENT */
		
		public void visitLeave(IfStatementNode node) {
			newVoidCode(node);
			
			debug.out("---If Statement \nchild 0: \n" + node.child(0) + "\nchild 1: \n" + node.child(1));
			debug.out("NODE: \n" + node);
			debug.out("SIZE OF CHILDREN: \n" + node.getChildren().size());
			
			ASMCodeFragment expression = removeValueCode(node.child(0));
			ParseNode blockStatementNode = node.child(1);
			String startLabel = labeller.newLabel("-if-arg1-", "");
			String falseLabel = labeller.newLabelSameNumber("-if-false-", "");
			String endLabel  = labeller.newLabelSameNumber("-if-end-", "");
			String strLeftChildValue = node.child(0).getToken().getLexeme();
			String strRightChildValue = node.child(1).getToken().getLexeme();

			// if (expr) ...
			code.add(Label, startLabel);
			code.append(expression);
			
			// ... If false, jump to falseLabel ...
			code.add(JumpFalse, endLabel);
			
			// ... If true, do block statement ...
			for (ParseNode child : blockStatementNode.getChildren()) {
				ASMCodeFragment childCode = removeVoidCode(child);
				code.append(childCode);
			}
			// ... Jump to End label
			code.add(Jump, endLabel);
			
			// Expression was False, so perform Else Block
			code.add(Label, falseLabel);
			
			code.add(Jump, endLabel);
			
			
			// .. after else { block }
			code.add(Label, endLabel); 
		}
		
		/* BLOCK STATEMENT NODE */
		
		public void visitLeave(BlockStatementNode node) {
			newVoidCode(node);
			
			for (ParseNode child : node.getChildren()) {
				ASMCodeFragment childCode = removeVoidCode(child);
				code.append(childCode);
			}
		}
		
		/*********/
		/* OTHER */
		/*********/
		
		/* NEWLINE NODE */
		
		public void visit(NewlineNode node) {
			newVoidCode(node);
			code.add(PushD, RunTime.NEWLINE_PRINT_FORMAT);
			code.add(Printf);
		}
		
		/* SEPARATOR NODE */
		
		public void visit(SeparatorNode node) {
			newVoidCode(node);
			code.add(PushD, RunTime.SEPARATOR_PRINT_FORMAT);
			code.add(Printf);
		}
		
		private void appendPrintCode(ParseNode node) {
			String format = printFormat(node.getType());

			code.append(removeValueCode(node));
			convertToStringIfBoolean(node);
			code.add(PushD, format);
			code.add(Printf);
		}
		
		private void convertToStringIfBoolean(ParseNode node) {
			if(node.getType() != PrimitiveType.BOOLEAN) {
				return;
			}
			
			String trueLabel = labeller.newLabel("-print-boolean-true", "");
			String endLabel = labeller.newLabelSameNumber("-print-boolean-join", "");

			code.add(JumpTrue, trueLabel);
			code.add(PushD, RunTime.BOOLEAN_FALSE_STRING);
			code.add(Jump, endLabel);
			code.add(Label, trueLabel);
			code.add(PushD, RunTime.BOOLEAN_TRUE_STRING);
			code.add(Label, endLabel);
		}
		
		private String printFormat(Type type) {
			assert type instanceof PrimitiveType;
			
			switch((PrimitiveType)type) {
			case INTEGER:	return RunTime.INTEGER_PRINT_FORMAT;
			case BOOLEAN:	return RunTime.BOOLEAN_PRINT_FORMAT;
			case FLOAT:		return RunTime.FLOAT_PRINT_FORMAT;
			case CHARACTER:	return RunTime.CHARACTER_PRINT_FORMAT;
			case STRING:	return RunTime.STRING_PRINT_FORMAT;
			default:		
				assert false : "Type " + type + " unimplemented in ASMCodeGenerator.printFormat()";
				return "";
			}
		}

		private ASMOpcode opcodeForStore(Type type) {
			if (type == PrimitiveType.INTEGER) {
				return StoreI;
			}
			if (type == PrimitiveType.BOOLEAN) {
				return StoreC;
			}
			if (type == PrimitiveType.FLOAT) {
				return StoreF;
			}
			if (type == PrimitiveType.CHARACTER) {
				return StoreC;
			}
			if (type == PrimitiveType.STRING) {
				return StoreI;
			}
			assert false: "Type " + type + " unimplemented in opcodeForStore()";
			return null;
		}

		///////////////////////////////////////////////////////////////////////////
		// EXPRESSIONS
		///////////////////////////////////////////////////////////////////////////
		
		/* BINARY OPERATOR NODE */
		
		public void visitLeave(BinaryOperatorNode node) {
			Lextant operator = node.getOperator();

			if (isComparisonOperator(operator)) {
				visitComparisonNode(node, operator);
			} else if (isArithmeticOperator(operator)) {
				visitNormalBinaryOperatorNode(node);
			} else if (isBooleanOperator(operator)) {
				visitBooleanOperatorNode(node);
			}
		}
		
		/* UNARY OPERATOR NODE */
		
		public void visitLeave(UnaryOperatorNode node) {
			Lextant operator = node.getOperator();
			
			if (isBooleanOperator(operator)) {
				visitNormalUnaryOperatorNode(node);
			}
		}
		
		/* HELPER METHODS */
		
		public boolean isComparisonOperator(Lextant lexeme) {
			if (lexeme.equals(Punctuator.GREATER) ||
					lexeme.equals(Punctuator.GREATER_OR_EQUAL) ||
					lexeme.equals(Punctuator.EQUAL) ||
					lexeme.equals(Punctuator.NOT_EQUAL) ||
					lexeme.equals(Punctuator.LESSER) ||
					lexeme.equals(Punctuator.LESSER_OR_EQUAL)) {
				return true;
			} else {
				return false;
			}
		}
		
		public boolean isArithmeticOperator(Lextant lexeme) {
			if (lexeme.equals(Punctuator.ADD) ||
					lexeme.equals(Punctuator.SUBTRACT) ||
					lexeme.equals(Punctuator.MULTIPLY) ||
					lexeme.equals(Punctuator.DIVIDE)) {
				return true;
			} else {
				return false;
			}
		}
		
		public boolean isBooleanOperator(Lextant lexeme) {
			if (lexeme.equals(Punctuator.AND) ||
					lexeme.equals(Punctuator.OR) ||
					lexeme.equals(Punctuator.NOT)) {
				return true;
			} else {
				return false;
			}
		}
		
		/* COMPARISON NODE */
		
		private void visitComparisonNode(BinaryOperatorNode node, Lextant operator) { // TODO: USE A MAP INSTEAD
			ASMCodeFragment arg1 = removeValueCode(node.child(0));
			ASMCodeFragment arg2 = removeValueCode(node.child(1));
			String startLabel = labeller.newLabel("-compare-arg1-", "");
			String arg2Label  = labeller.newLabelSameNumber("-compare-arg2-", "");
			String subLabel   = labeller.newLabelSameNumber("-compare-sub-", "");
			String trueLabel  = labeller.newLabelSameNumber("-compare-true-", "");
			String falseLabel = labeller.newLabelSameNumber("-compare-false-", "");
			String joinLabel  = labeller.newLabelSameNumber("-compare-join-", "");
			String strLeftChildValue = node.child(0).getToken().getLexeme();
			String strRightChildValue = node.child(1).getToken().getLexeme();
			Double dblLeftChildValue = 0.0;
			Double dblRightChildValue = 0.0;
			char chrLeftChildValue = ' ';
			char chrRightChildValue = ' ';
			
			if ((node.child(0).getType() == PrimitiveType.INTEGER) ||
				(node.child(0).getType() == PrimitiveType.FLOAT)) {
				dblLeftChildValue = Double.parseDouble(strLeftChildValue);
				dblRightChildValue = Double.parseDouble(strRightChildValue);
			} else if (node.child(0).getType() == PrimitiveType.CHARACTER) {
				chrLeftChildValue = strLeftChildValue.charAt(0);
				chrRightChildValue = strRightChildValue.charAt(0);
			}
			
			newValueCode(node);
			// arg1
			code.add(Label, startLabel);
			code.append(arg1);
			// arg2
			code.add(Label, arg2Label);
			code.append(arg2);
			// comparison
			code.add(Label, subLabel);
			
			if (operator == Punctuator.GREATER) {
				if ((node.child(0).getType() == PrimitiveType.INTEGER &&
						node.child(1).getType() == PrimitiveType.INTEGER) ||
						(node.child(0).getType() == PrimitiveType.CHARACTER)) {
					code.add(Subtract);
					// jump to trueLabel if it's positive (greater than)
					code.add(JumpPos, trueLabel);
					// jump to falseLabel if it's negative (lesser than)
					code.add(Jump, falseLabel);
				} else if (node.child(0).getType() == PrimitiveType.FLOAT) { // float
					code.add(FSubtract);
					// jump to trueLabel if it's positive (greater than)
					code.add(JumpFPos, trueLabel);
					// jump to falseLabel if it's negative (lesser than)
					code.add(Jump, falseLabel);
				}
			} else if (operator == Punctuator.GREATER_OR_EQUAL) {
				if ((node.child(0).getType() == PrimitiveType.INTEGER &&
						node.child(1).getType() == PrimitiveType.INTEGER)) {
					code.add(Subtract);
					if ((dblLeftChildValue - dblRightChildValue) == 0) {
						// jump to trueLabel if it's 0
						code.add(JumpFalse, trueLabel);
					} else {
						// jump to trueLabel if it's positive (greater than)
						code.add(JumpPos, trueLabel);
					}
					// jump to falseLabel if it's negative (lesser than)
					code.add(Jump, falseLabel);
				} else if (node.child(0).getType() == PrimitiveType.FLOAT) { // float
					code.add(FSubtract);
					if ((dblLeftChildValue - dblRightChildValue) == 0) {
						// jump to trueLabel if it's 0
						code.add(JumpFZero, trueLabel);
					} else {
						// jump to trueLabel if it's positive (greater than)
						code.add(JumpFPos, trueLabel);
					}
					// jump to falseLabel if it's negative (lesser than)
					code.add(Jump, falseLabel);
				} else if (node.child(0).getType() == PrimitiveType.CHARACTER) {
					code.add(Subtract);
					if ((chrLeftChildValue - chrRightChildValue) == 0) {
						// jump to trueLabel if it's 0
						code.add(JumpFalse, trueLabel);
					} else {
						// jump to trueLabel if it's positive (greater than)
						code.add(JumpPos, trueLabel);
					}
					// jump to falseLabel if it's negative (lesser than)
					code.add(Jump, falseLabel);
				}
			// EQUAL ( == )
			} else if (operator == Punctuator.EQUAL) {
				// INTEGER AND CHARACTER
				if ((node.child(0).getType() == PrimitiveType.INTEGER &&
						node.child(1).getType() == PrimitiveType.INTEGER) ||
						(node.child(0).getType() == PrimitiveType.CHARACTER)) {
					code.add(Subtract);
					// jump to trueLabel if it's 0
					code.add(JumpFalse, trueLabel);
					// jump to falseLabel if it's anything else
					code.add(Jump, falseLabel);
				} else if (node.child(0).getType() == PrimitiveType.FLOAT) { // float
					code.add(FSubtract);
					// jump to trueLabel if it's 0
					code.add(JumpFZero, trueLabel);
					// jump to falseLabel if it's anything else
					code.add(Jump, falseLabel);
				// STRING
				} else if (node.child(0).getType() == PrimitiveType.STRING) { // string // TODO: doesn't fully work. need to implement "xx" == variable
					if ((strLeftChildValue.contains("\"")) || (strRightChildValue.contains("\""))) {
						code.add(Subtract);
						
						if (strLeftChildValue.equals(strRightChildValue)) {
							code.add(JumpFalse, trueLabel);
						} else {
							code.add(Jump, falseLabel);
						}
					} else {
						code.add(BEqual);
						if (strLeftChildValue.equals(strRightChildValue)) {
							code.add(JumpTrue, trueLabel);
						} else {
							code.add(Jump, falseLabel);
						}
					}
				// BOOLEAN
				} else if (node.child(0).getType() == PrimitiveType.BOOLEAN) { // boolean
					code.add(BEqual);
					code.add(JumpTrue, trueLabel);
					code.add(Jump, falseLabel);
				}
			// NOT EQUAL ( != )
			} else if (operator == Punctuator.NOT_EQUAL) {
				// INTEGER AND CHARACTER
				if ((node.child(0).getType() == PrimitiveType.INTEGER &&
						node.child(1).getType() == PrimitiveType.INTEGER) ||
						(node.child(0).getType() == PrimitiveType.CHARACTER)) {
					code.add(Subtract);
					// jump to falseLabel if it's 0)
					code.add(JumpFalse, falseLabel);
					// jump to trueLabel if it's anything else
					code.add(Jump, trueLabel);
				// FLOAT
				} else if (node.child(0).getType() == PrimitiveType.FLOAT) { // float
					code.add(FSubtract);
					// jump to falseLabel if it's 0
					code.add(JumpFZero, falseLabel);
					// jump to trueLabel if it's anything else
					code.add(Jump, trueLabel);
				// STRING
				} else if (node.child(0).getType() == PrimitiveType.STRING) { // string // TODO: doesn't fully work
					debug.out("HERE");
					
					if ((strLeftChildValue.contains("\"")) || (strRightChildValue.contains("\""))) {
						//debug.out("THERE: \"");
						
						code.add(Subtract); // TODO: doesn't work
						code.add(JumpPos, falseLabel);
						code.add(Jump, trueLabel);
					} else {
						//debug.out("THERE: NONE");
						
						code.add(Subtract);
						code.add(JumpNeg, trueLabel);
						code.add(Jump, falseLabel);
					}
				// BOOLEAN
				} else if (node.child(0).getType() == PrimitiveType.BOOLEAN) { // boolean
					code.add(BEqual);
					code.add(JumpFalse, trueLabel);
					code.add(Jump, falseLabel);
				}
			// LESSER ( < )
			} else if (operator == Punctuator.LESSER) {
				if ((node.child(0).getType() == PrimitiveType.INTEGER &&
						node.child(1).getType() == PrimitiveType.INTEGER) ||
						(node.child(0).getType() == PrimitiveType.CHARACTER)) {
					code.add(Subtract);
					// jump to trueLabel if it's negative (lesser than)
					code.add(JumpNeg, trueLabel);
					// jump to falseLabel if it's positive (greater than)
					code.add(Jump, falseLabel);
				} else if (node.child(0).getType() == PrimitiveType.FLOAT) { // float
					code.add(FSubtract);
					// jump to trueLabel if it's negative (lesser than)
					code.add(JumpFNeg, trueLabel);
					// jump to falseLabel if it's positive (greater than)
					code.add(Jump, falseLabel);
				}
			// LESSER OR EQUAL ( <= )
			} else if (operator == Punctuator.LESSER_OR_EQUAL) {
				if ((node.child(0).getType() == PrimitiveType.INTEGER &&
						node.child(1).getType() == PrimitiveType.INTEGER)) {
					code.add(Subtract);
					if ((dblLeftChildValue - dblRightChildValue) == 0) {
						// jump to trueLabel if it's 0
						code.add(JumpFalse, trueLabel);
					} else {
						// jump to trueLabel if it's negative (lesser than)
						code.add(JumpNeg, trueLabel);
					}
					// jump to falseLabel if it's positive (greater than)
					code.add(Jump, falseLabel);
				} else if (node.child(0).getType() == PrimitiveType.FLOAT) { // float
					code.add(FSubtract);
					if ((dblLeftChildValue - dblRightChildValue) == 0) {
						// jump to trueLabel if it's 0
						code.add(JumpFZero, trueLabel);
					} else {
						// jump to trueLabel if it's negative (lesser than)
						code.add(JumpFNeg, trueLabel);
					}
					// jump to falseLabel if it's positive (greater than)
					code.add(Jump, falseLabel);
				} else if (node.child(0).getType() == PrimitiveType.CHARACTER) {
					code.add(Subtract);
					if ((chrLeftChildValue - chrRightChildValue) == 0) {
						// jump to trueLabel if it's 0
						code.add(JumpFalse, trueLabel);
					} else {
						// jump to trueLabel if it's negative (lesser than)
						code.add(JumpNeg, trueLabel);
					}
					// jump to falseLabel if it's positive (greater than)
					code.add(Jump, falseLabel);
				}
			}
			// true
			code.add(Label, trueLabel);
			code.add(PushI, 1);
			code.add(Jump, joinLabel);
			// false
			code.add(Label, falseLabel);
			code.add(PushI, 0);
			code.add(Jump, joinLabel);
			// end of statement
			code.add(Label, joinLabel); 
		}
		
		/* BINARY OPERATOR NODE */
		
		private void visitNormalBinaryOperatorNode(BinaryOperatorNode node) {
			newValueCode(node);
			
			ASMCodeFragment arg1 = removeValueCode(node.child(0));
			ASMCodeFragment arg2 = removeValueCode(node.child(1));
			Type leftChildType = node.child(0).getType();
			Type rightChildType = node.child(1).getType();
			double rightChildValue;

			try {  
			    rightChildValue = Double.parseDouble(node.child(1).getToken().getLexeme());  
		    } catch(NumberFormatException nfe) {
		    	rightChildValue = -1.0;
			}  
			
			code.append(arg1);
			code.append(arg2);

			// Divide by 0 error
			if ((node.getToken().getLexeme() == Punctuator.DIVIDE.getLexeme()) && (rightChildValue == 0)) {
				code.add(Jump, RunTime.NUMBER_DIVIDE_BY_ZERO_RUNTIME_ERROR);
			}
			
			ASMOpcode opcode = opcodeForOperator(node.getOperator(), leftChildType, rightChildType);
									
			code.add(opcode);
		}
		
		/* BOOLEAN OPERATOR NODE */
		
		private void visitBooleanOperatorNode(BinaryOperatorNode node) {
			ASMCodeFragment arg1 = removeValueCode(node.child(0));
			ASMCodeFragment arg2 = removeValueCode(node.child(1));
			String startLabel = labeller.newLabel("-compare-arg1-bool-", "");
			String arg2Label  = labeller.newLabelSameNumber("-compare-arg2-bool-", "");
			String subLabel   = labeller.newLabelSameNumber("-compare-sub-bool-", "");
			String trueLabel  = labeller.newLabelSameNumber("-compare-true-bool-", "");
			String falseLabel = labeller.newLabelSameNumber("-compare-false-bool-", "");
			String joinLabel  = labeller.newLabelSameNumber("-compare-join-bool-", "");
			Lextant operator = node.getOperator();
			HashMap<Lextant, ASMOpcode> lextantHashMap = new HashMap<Lextant, ASMOpcode>();
			
			lextantHashMap.put(Punctuator.AND, And);
			lextantHashMap.put(Punctuator.OR, Or);
			
			newValueCode(node);
			// arg1
			code.add(Label, startLabel);
			code.append(arg1);
			// arg2
			code.add(Label, arg2Label);
			code.append(arg2);
			// comparison
			code.add(Label, subLabel);
			
			// operate on children
			code.add(lextantHashMap.get(operator));
			code.add(JumpTrue, trueLabel);
			code.add(Jump, falseLabel);
			
			// true
			code.add(Label, trueLabel);
			code.add(PushI, 1);
			code.add(Jump, joinLabel);
			// false
			code.add(Label, falseLabel);
			code.add(PushI, 0);
			code.add(Jump, joinLabel);
			// end of statement
			code.add(Label, joinLabel); 
		}
		
		/* UNARY OPERATOR NODE */
		
		private void visitNormalUnaryOperatorNode(UnaryOperatorNode node) {
			newValueCode(node);
			
			ASMCodeFragment arg = removeValueCode(node.child(0));
			String lexeme = node.child(0).getToken().getLexeme();
			String str = arg.toString();
			
			if (lexeme.equals("true") || lexeme.equals("false")) {
				if (lexeme.equals("true")) {
					code.add(PushI, 0);
				} else if (lexeme.equals("false")) {
					code.add(PushI, 1);
				}
			} else {
				String[] arguments = str.split(System.getProperty("line.separator"));

				code.add(PushD, "$global-memory-block");
				
				// negate the boolean value
				for (int i = 0; i < arguments.length; i++) {
					if (arguments[i].contains("  1  ")) {
						code.add(PushI, 0);
					} else if (arguments[i].contains("  0  ")) {
						code.add(PushI, 1);
					}
				}
				
				code.add(Add);
				code.add(LoadC);
			}
		}
		
		/* HELPER FUNCTIONS */
		
		private ASMOpcode opcodeForOperator(Lextant lextant, Type leftChildType, Type rightChildType) {
			assert(lextant instanceof Punctuator);
			Punctuator punctuator = (Punctuator)lextant;
			
			if ((leftChildType == PrimitiveType.INTEGER) && (rightChildType == PrimitiveType.INTEGER))  {
				switch(punctuator) {
					case ADD: 	   	return Add;
					case SUBTRACT: 	return Subtract;
					case MULTIPLY: 	return Multiply;
					case DIVIDE: 	return Divide;
					default:		assert false : "integer - unimplemented operator in opcodeForOperator";
				}
			} else if ((leftChildType == PrimitiveType.FLOAT) && (rightChildType == PrimitiveType.FLOAT)) {
				switch(punctuator) {
					case ADD: 	   	return FAdd;
					case SUBTRACT: 	return FSubtract;
					case MULTIPLY: 	return FMultiply;
					case DIVIDE: 	return FDivide;
					default:		assert false : "float - unimplemented operator in opcodeForOperator";
				}
			}
			
			return null;
		}

		private ASMOpcode opcodeForOperator(Lextant lextant, Type childType) {
			assert(lextant instanceof Punctuator);
			Punctuator punctuator = (Punctuator)lextant;
			
			if (childType == PrimitiveType.BOOLEAN)  {
				switch(punctuator) {
					case NOT: 		return Negate;
					default:		assert false : "boolean - unimplemented operator in opcodeForOperator";
				}
			}
			
			return null;
		}
		
		///////////////////////////////////////////////////////////////////////////
		// LEAF NODES (ErrorNode NOT NECESSARY)
		///////////////////////////////////////////////////////////////////////////
		
		public void visit(BooleanConstantNode node) {
			newValueCode(node);
			code.add(PushI, node.getValue() ? 1 : 0);
		}
		
		public void visit(IdentifierNode node) {
			newAddressCode(node);
			Binding binding = node.getBinding();
			
			binding.generateAddress(code);
		}
		
		public void visit(IntegerConstantNode node) {
			newValueCode(node);
			
			code.add(PushI, node.getValue());
		}
		
		public void visit(FloatConstantNode node) {
			newValueCode(node);
			
			code.add(PushF, node.getValue());
		}
		
		public void visit(CharacterConstantNode node) {
			newValueCode(node);
			
			code.add(PushI, node.getValue());
		}

		public void visit(StringConstantNode node) {
			String label = labeller.newLabel("-str-constant-", "");
			
			newValueCode(node);
			
			code.add(DLabel, label);
			code.add(DataS, node.getValue());
			code.add(PushD, label);
		}
	}
}
