package parser;
/**
 * After scanning tokens from input file, the tokens are checked according to syntax, if not then error is thrown,
 * The syntax  of tokens are  decided by grammar  rules which is implemented in parser.
 * Also semantic analysis which tells whether a line of code is havng any existence and  meaning or not is implemented alogn with parser rules
 */
import codeGeneration.Code;
import codeGeneration.Operand;
import scanner.Scanner;
import scanner.Token;
import symbols.Objects;
import symbols.Struct;
import symbols.SymManager;
import utils.TokenCodes;

public class Parser {
	
	private static Token t; //last token
	private static Token la; //current token
	private  static  int sym; //current token kind
	
//scanning each token from inout file using scanner
	private static void scan() {
		t = la;
		la = Scanner.next();
		la.toString();
		sym = la.kind;
	}
	
	//verify kind of token is expected or not
	private static void check (int expected) {
		if (sym == expected) scan(); // if verified,read next
		else error(TokenCodes.tokenName[expected]+"expected" );
		}
	
	//prints error
	public static void error (String message) {
		System.out.println("line " + la.line + ", col " + la.col + ": " + message);
		System.exit(1); 
		}
	public static void parse() {
		scan(); // first token scannned
	Program(); // calls the parsing methods for production rules
		check(TokenCodes.eof); 
		}
	
	//parsing methods for production rules
	
	public static void ActParams(Operand m) {
		Operand  ap;
	 if (m.kind != Operand.Meth) { error("not a method"); m.obj = SymManager.noObj; }
		int aPars = 0;
		int fPars = m.obj.nPars;
		Objects fp = m.obj.locals;
		if(sym!=TokenCodes.rpar)
		{ap=Expr();
		Code.load(ap); aPars++;
		if (fp != null) {
		if (!ap.type.assignableTo(fp.type)) error("parameter type mismatch");
		fp = fp.next;
		}
		while(sym==TokenCodes.comma) {
			scan();
			ap=Expr();
			Code.load(ap); aPars++;
			if (fp != null) {
			if (!ap.type.assignableTo(fp.type)) error("parameter type mismatch");
			fp = fp.next;
			}
		}
		}
	if (aPars > fPars)
				error("too many actual parameters");
				else if (aPars < fPars)
				error("too few actual parameters");
	check(TokenCodes.rpar);
	}
	
	public static void ArrayDeclarations() {
		check(TokenCodes.lbrack);
		check(TokenCodes.rbrack);
//  TODO:Right now syntax doesnot support direct assignment of array but this can be added
//		if(sym==TokenCodes.INT) {
//			int len=la.val;
//			scan();
//			check(TokenCodes.rbrack);
//			if(sym==TokenCodes.assign) {
//			scan();
//			
//			}
//		}
	}
	
	public static void ClassDeclarations() {
		check(TokenCodes.class_);
		check(TokenCodes.ident);
	Objects curClass=SymManager.insert(Objects.Type, t.string,new Struct(Struct.Class),false);
		if(sym==TokenCodes.extends_) {
			scan();
			Type();
			while(sym==TokenCodes.comma) {
				scan();
				Type();
			}
		}
		check(TokenCodes.lbrace);
		SymManager.openScope();
		while(sym==TokenCodes.ident||sym==TokenCodes.CHAR||sym==TokenCodes.INT) {
			VarDecl(false,true);
		}
		if(sym==TokenCodes.lbrace) {
			scan();
			while(sym==TokenCodes.ident||sym==TokenCodes.CHAR||sym==TokenCodes.INT||sym==TokenCodes.void_){
				MethodDecl();
			}
			check(TokenCodes.rbrace);
		}
		curClass.locals =SymManager.curScope.locals;
		check(TokenCodes.rbrace);
		//SymManager.dumpScope(SymManager.curScope.locals);
		SymManager.closeScope();
	}
	
	public static int Condition() {
		int op = 0; Operand x, y;
		//TODO:Inverse logic
		if(sym==TokenCodes.not) {
			scan();
		}
		
	     if(sym==TokenCodes.lpar) {
			scan();
			op=Condition();
			check(TokenCodes.rpar);
		}
		x=ConditionTerm();
	
		while(sym==TokenCodes.or||sym==TokenCodes.and) {
			if(sym==TokenCodes.or) op=Code.or;
			else if(sym==TokenCodes.and)op=Code.and;
			scan();
			y=ConditionTerm();
			Code.load(y);
			if (!x.type.compatibleWith(y.type)) error("type mismatch");
			if (x.type.isRefType() && op != Code.eq && op != Code.ne)
			error("invalid compare");
			
		}
		return op;
	}
	
	private static Operand ConditionTerm() {
		Operand x,y;
		int op = 0;
		x=Expr();
		Code.load(x);
		while(sym==TokenCodes.eql||sym==TokenCodes.neq||sym==TokenCodes.gtr||sym==TokenCodes.geq||sym==TokenCodes.lss||sym==TokenCodes.leq) {
			if(sym==TokenCodes.eql) {
				op=Code.eq;
			}
			else if(sym==TokenCodes.neq) {
				op=Code.ne;
			}
			else if(sym==TokenCodes.gtr) {
				op=Code.gt;
			}
			else if(sym==TokenCodes.geq) {
				op=Code.ge;
			}
			else if(sym==TokenCodes.lss) {
				op=Code.lt;
			}
			else if(sym==TokenCodes.leq) {
				op=Code.le;
			}
			scan();
			y=Expr();
		Code.load(y);
			if (!x.type.compatibleWith(y.type)) error("type mismatch");
			if (x.type.isRefType() && op != Code.eq && op != Code.ne)
			error("invalid compare");
			
		}
		return x;
	}
	
	public static void ConstDeclarations() {
		 Struct type=Type();
		check(TokenCodes.ident);
        SymManager.insert(Objects.Con, t.string, type,false);
		if(sym==TokenCodes.assign) {
			scan();
		}
		else {
			error("constant variables should be declared");
		}
		if(sym==TokenCodes.INT||sym==TokenCodes.CHAR||sym==TokenCodes.boolConstTrue||sym==TokenCodes.boolConstFalse){
			scan();
		}
		else {
			error("no value specified to identifier");
		}
		while(sym==TokenCodes.comma) {
			check(TokenCodes.ident);
			 SymManager.insert(Objects.Con, t.string, type,false);
			if(sym==TokenCodes.assign) {
				scan();
			}
			else {
				error("constant variables should be declared");
			}
			if(sym==TokenCodes.INT||sym==TokenCodes.CHAR||sym==TokenCodes.boolConstTrue||sym==TokenCodes.boolConstFalse){
				scan();
			}
			else {
				error("no value specified to identifier");
			}
		}
		check(TokenCodes.semicolon);
	}
	
	public static Operand Designator() {
		String name, fName;
		check(TokenCodes.ident);
		Objects  mainObject=SymManager.find(t.string,true,false);
		Operand x=  new  Operand(mainObject);
		while(sym==TokenCodes.period||sym==TokenCodes.lbrack) {
			if(sym==TokenCodes.period) {
				scan();
				check(TokenCodes.ident);
				 Objects fieldObj=SymManager.noObj;
				 if (x.type.kind == Struct.Class) {
						Code.load(x);
						if(sym==TokenCodes.lpar)
						{ 
							fieldObj=SymManager.findField(mainObject.name, t.string,Objects.Meth);
					}
						else {
						fieldObj=SymManager.findField(mainObject.name, t.string,Objects.Var);
						}
						x.kind = Operand.Fld;
						x.adr = fieldObj.adr;
						x.type = fieldObj.type;
						} 
					 else {
						 error(t.string + " is not an object");
					 }
			}
			else if(sym==TokenCodes.lbrack){
				Code.load(x);
				scan();
				if (x.type.kind == Struct.Arr) {
					Operand y=Expr();
					if (y.type.kind != Struct.Int) error("index must be of type int");
					Code.load(y);
					x.kind = Operand.Elem;
					x.type = x.type.elemType;
					} else error("This is not an array");
				
				check(TokenCodes.rbrack);
			}
			else {
				error("invalid syntax");
				//break;
			}
		}
		return  x;
	}
	
	public static void DesignatorStatement() {
		Operand x, y;
		x=Designator();
		if(sym==TokenCodes.assign) {
			scan();
		y=Expr();
		if (y.type.assignableTo(x.type))
			Code.assign(x, y); // x: Local | Static | Fld | Elem
			// assign must load y
			else
			error("incompatible types in assignment");
		}
		else if(sym==TokenCodes.lpar) {
			scan();
		 ActParams(x);
			Code.put(Code.call);
			Code.put2(x.adr);
			if (x.type != SymManager.noType) Code.put(Code.pop);
		}
		else if(sym==TokenCodes.increment) {
			scan();
		}
		else if(sym==TokenCodes.decrement) {
			scan();
		}
		else {
			error("invalid designation syntax");
		}
	}
	
	public static void DoWhileStatements() {
	int op;
	check(TokenCodes.while_);
	int top=Code.pc;
		check(TokenCodes.lpar);
		op=Condition();
		check(TokenCodes.rpar);
		Code.putFalseJump(op, 0);
		int adr = Code.pc - 2; 
		if(sym==TokenCodes.lbrace) {
			scan();
		Statement();
		while(sym!=TokenCodes.rbrace) {
			scan();
			Statement();
		}
		check(TokenCodes.rbrace);
		Code.putJump(top);
		Code.fixup(adr); 
		}
		else {
			check(TokenCodes.semicolon);
			Code.putJump(top);
			Code.fixup(adr); 
		}
	}
	
	public static Operand Expr() {
		Operand x, y; int op = 0; 
		if(sym==TokenCodes.minus) {
			scan();
			 x=Term();
			  if (x.type != SymManager.intType) error("operand must be of type int");
				 if (x.kind == Operand.Con) x.val = -x.val;
				 else {
				 Code.load(x); Code.put(Code.neg);
		}
		}
		else {
			x=Term();
		}
	
		while(sym==TokenCodes.plus||sym==TokenCodes.minus) {
			if(sym==TokenCodes.plus) {
				op=Code.add;
			}
			else if(sym==TokenCodes.minus) {
				op=Code.sub;
			}
			Code.load(x);
			scan();
			y=Term();
			Code.load(y);
			if (x.type.kind != Struct.Int || y.type.kind != Struct.Int)
				error("operands must be of type int");
				Code.put(op);
		}
		return x;
	}
	
	public static Operand Factor() {
		Operand x=new Operand(Operand.Stack);
		if(sym==TokenCodes.ident) {
			x=Designator();
			if(sym==TokenCodes.lpar) {
				scan();
			ActParams(x);
				if (x.type == SymManager.noType) error("procedure called as a function");
				if (x.obj == SymManager.ordObj || x.obj == SymManager.chrObj) ; // nothing
				else if (x.obj == SymManager.lenObj)
				Code.put(Code.arraylength);
				else {
				Code.put(Code.call);
				Code.put2(x.adr);
				}
				x.kind = Operand.Stack;
			}
			
		}
		else if(sym==TokenCodes.INT) {
			x = new Operand(la.val);
			scan();
			
		}
		else if(sym==TokenCodes.CHAR) {
			x = new Operand(la.val);
			x.type =SymManager.charType; 
			scan();
			
		}
		else if(sym==TokenCodes.new_) {
			scan();
			Struct type=Type();
			if(sym==TokenCodes.lbrack) {
				scan();
 		 x=Expr();
check(TokenCodes.rbrack);
				 if (x.type != SymManager.intType) error("array size must be of type int");
				 Code.load(x);
				 Code.put(Code.newarray);
				if (type == SymManager.charType) Code.put(0); else Code.put(1);
				type = new Struct(Struct.Arr, type);
			}
			else {
				if (type.kind != Struct.Class)
					error("class type expected");
					Code.put(Code.new_); Code.put2(type.nFields);
					x = new Operand(Operand.Stack); 
					x.type = type;
			}
			
		}
		else if(sym==TokenCodes.lpar) {
			scan();
			x=Expr();
			check(TokenCodes.rpar);
			
		}
		else if(sym==TokenCodes.boolConstTrue||sym==TokenCodes.boolConstFalse) {
			x=new Operand(sym==TokenCodes.boolConstTrue?1:0);
			x.type =SymManager.boolType;
			scan();
		}
		else {
			error("invalid syntax");
		}
		return x;
	}
	
	public static void IfElseStatements() {
		int op;
		check(TokenCodes.if_);
		check(TokenCodes.lpar);
		op=Condition();
		check(TokenCodes.rpar);
		Code.putFalseJump(op, 0);
		int adr = Code.pc - 2; 
		Statement();
// TODO: Not supporting else if statemnets can be added further		
//		while(sym==TokenCodes.else_if_) {
//			int op2;
//			scan();
//			check(TokenCodes.lpar);
//			op2=Condition();
//			check(TokenCodes.rpar);
//			Code.putFalseJump(op2, 0);
//			int adr2 = Code.pc - 2; 
//			Statement();
//		}
		if(sym==TokenCodes.else_) {
			Code.putJump(0);
			int adr2 = Code.pc - 2;
			Code.fixup(adr); 	
			scan();
			Statement();
			Code.fixup(adr2);
		}
		else {
			Code.fixup(adr);
		}
	}
	
	public static void MethodDecl() {
		Struct type; String name; int n=0; 
		  type=ReturnType();
		check(TokenCodes.ident);
		Objects curMethod=SymManager.insert(Objects.Meth,t.string,type,false);
		name=t.string;
		SymManager.openScope();
		check(TokenCodes.lpar);
		if(sym!=TokenCodes.rpar)
		 n=MethodParams();
		check(TokenCodes.rpar);
		curMethod.nPars = n;
		if (name.equals("main")) {
		Code.mainPc = Code.pc;
		if (curMethod.type != SymManager.noType) error("method main must be void");
		if (curMethod.nPars != 0) error("main must not have parameters");
		}
		
		while(sym==TokenCodes.ident||sym==TokenCodes.CHAR||sym==TokenCodes.INT) {
			VarDecl(false,true);
		}
		check(TokenCodes.lbrace);
		curMethod.locals =SymManager.curScope.locals;
		curMethod.adr = Code.pc;
		Code.put(Code.enter);
		Code.put(curMethod.nPars);
		Code.put(SymManager.curScope.nVars); 
		while(sym!=TokenCodes.rbrace) {
			if(sym==TokenCodes.return_)
			Statement(curMethod);
			else
				Statement();
	}
		check(TokenCodes.rbrace);
		if (curMethod.type == SymManager.noType) {
			Code.put(Code.exit); Code.put(Code.return_);
			} else { // end of function reached without a return statement
			Code.put(Code.trap); Code.put(1);
			}
		//SymManager.dumpScope(SymManager.curScope.locals);
		SymManager.closeScope();
	}
	
	public static int MethodParams() {
		return VarDecl(true,true);
	}
	
	public static void Program() {
		check(TokenCodes.program_);
		check(TokenCodes.ident);
		Objects curObject=SymManager.insert(Objects.Prog, t.string,SymManager.noType,false);
		while(sym!=TokenCodes.lbrace) {
		if(sym==TokenCodes.const_||sym==TokenCodes.final_) {
			scan();
			ConstDeclarations();
		}
		else if(sym==TokenCodes.ident||sym==TokenCodes.CHAR||sym==TokenCodes.INT) {
			VarDecl(false,false);
		}
		else if(sym==TokenCodes.class_) {
			ClassDeclarations();
		}
		else {
			error("unrecognised token");
		}
		}
		check(TokenCodes.lbrace);
		SymManager.openScope();
		while(sym==TokenCodes.ident||sym==TokenCodes.CHAR||sym==TokenCodes.INT||sym==TokenCodes.void_){
			MethodDecl();
		}
		check(TokenCodes.rbrace);
		curObject.locals=SymManager.curScope.locals;
		//SymManager.dumpScope(SymManager.curScope.locals);
		SymManager.closeScope();
	}
	
	public static Struct ReturnType() {
		if(sym==TokenCodes.ident||sym==TokenCodes.CHAR||sym==TokenCodes.INT||sym==TokenCodes.void_) {
			if(sym==TokenCodes.CHAR) {
				scan();
				return new Struct(Struct.Char);
			}
			if(sym==TokenCodes.INT) {
				scan();
				return new Struct(Struct.Int);
			}
			if(sym==TokenCodes.ident) {
				Objects obj=SymManager.find(la.string,true,false);
				
				scan();
				return obj.type;
			}
			if(sym==TokenCodes.void_) {
				scan();
				return SymManager.noType;
			}
			return SymManager.noType;
		}
		else {
			error("invalid return type");
			return SymManager.noType;
		}
	}
	
	//for return statements
	public static void Statement(Objects curMethod) {
		Operand x,y;
	 if(sym==TokenCodes.return_) {
			scan();
			if(sym==TokenCodes.ident||sym==TokenCodes.INT||sym==TokenCodes.CHAR||sym==TokenCodes.new_||sym==TokenCodes.lpar||sym==TokenCodes.boolConstFalse||sym==TokenCodes.boolConstTrue) {
			x=Expr();
			Code.load(x);
			if (curMethod.type == SymManager.noType)
			error("void method must not return a value");
			else if (!x.type.assignableTo(curMethod.type))
			error("type of return value must match method type");
			}
			else {
			if (curMethod.type != SymManager.noType) error("return value expected");	
			}
			Code.put(Code.exit);
			Code.put(Code.return_);
			check(TokenCodes.semicolon);
		}
	 else {
		 Statement();
	 }
	}
	
	
	public static void Statement() {
		Operand x,y;
		if(sym==TokenCodes.ident) {
			DesignatorStatement();
			check(TokenCodes.semicolon);
		}
		else if(sym==TokenCodes.if_) {
			IfElseStatements();
		}
		else if(sym==TokenCodes.while_) {
			DoWhileStatements();
		}
		else if(sym==TokenCodes.break_) {
			scan();
			check(TokenCodes.semicolon);
		}
		else if(sym==TokenCodes.continue_) {
			scan();
			check(TokenCodes.semicolon);
		}
		else if(sym==TokenCodes.read_) {
			scan();
			check(TokenCodes.lpar);
			x=Designator();
			check(TokenCodes.rpar);
			check(TokenCodes.semicolon);
			if(x.type.kind!=Struct.Char&&x.type.kind!=Struct.Int) {
				error("can only read int or char variables");
			}
			Code.read(x);
			}
		else if(sym==TokenCodes.print_) {
			scan();
			check(TokenCodes.lpar);
			x=Expr();
			if(x.type!=SymManager.charType&&x.type!=SymManager.intType) {
				error("can only print int or char variables");
			}
			Code.print(x);
			if(sym==TokenCodes.comma) {
				scan();
				check(TokenCodes.INT);
			}
			check(TokenCodes.rpar);
			check(TokenCodes.semicolon);
			
		}
		else if(sym==TokenCodes.lbrace) {
			SymManager.openScope();
			scan();
			while(sym!=TokenCodes.rbrace) {
				Statement();
			}
			check(TokenCodes.rbrace);
			//SymManager.dumpScope(SymManager.curScope.locals);
			SymManager.closeScope();
		}
		else {
			error("invalid statements syntax");
		}
	}
	
	public static Operand Term() {
		Operand x,y;
		int op=0;
		x=Factor();
		while(sym==TokenCodes.times||sym==TokenCodes.slash||sym==TokenCodes.rem) {
			if(sym==TokenCodes.times) {
				op=Code.mul;
			}
			else if(sym==TokenCodes.slash) {
				op=Code.div;
			}
			else if(sym==TokenCodes.rem) {
				op=Code.rem;	
			}
			Code.load(x);
			scan();
			y=Factor();
			Code.load(y);
			if (x.type !=SymManager.intType || y.type != SymManager.intType)
				error("operands must be of type int");
				Code.put(op);
		}
		return x;
	}
	
	
	public static int VarDecl(boolean AreParams,boolean   scopeFlag) {
		int n=0;
		Struct type=Type();
		if(sym==TokenCodes.lbrack) {
			scan();
			check(TokenCodes.rbrack);
			check(TokenCodes.ident);
			SymManager.insert(Objects.Var,t.string,new Struct(Struct.Arr),scopeFlag);
			n++;
		}
		else {
			check(TokenCodes.ident);
			if(sym==TokenCodes.lbrack) {
				SymManager.insert(Objects.Var,t.string,new Struct(Struct.Arr),scopeFlag);
				ArrayDeclarations();
			}
			else {
				SymManager.insert(Objects.Var,t.string, type,scopeFlag);
			}
			n++;
		}
		while(sym==TokenCodes.comma) {
			scan();
			Struct type2 = SymManager.noType;
			if(AreParams) {
				type2=Type();
			}
			check(TokenCodes.ident);
		
			if(sym==TokenCodes.lbrack) {
				    SymManager.insert(Objects.Var,t.string,new Struct(Struct.Arr),scopeFlag );
				ArrayDeclarations();
			}
			else {
				if(AreParams)
				SymManager.insert(Objects.Var,t.string, type2,scopeFlag);
			   else
				    SymManager.insert(Objects.Var,t.string, type,scopeFlag);
				
			}
			n++;
		}
		if(!AreParams)
		check(TokenCodes.semicolon);
		return n;
	}
	
	public static Struct Type() {
		if(sym==TokenCodes.ident||sym==TokenCodes.CHAR||sym==TokenCodes.INT) {
			if(sym==TokenCodes.CHAR) {
				scan();
				return new Struct(Struct.Char);
			}
			if(sym==TokenCodes.INT) {
				scan();
				return new Struct(Struct.Int);
			}
			if(sym==TokenCodes.ident) {
				Objects obj=SymManager.find(la.string,true,false);
			 if (obj.kind != Objects.Type) error("type expected");
				scan();
				return obj.type;
			}
			return SymManager.noType;
		}
		else {
			error("invalid type declarations");
			return SymManager.noType;
		}
	}
	
	
	
}
