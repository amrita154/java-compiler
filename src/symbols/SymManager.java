package symbols;

import parser.Parser;
import utils.TokenCodes;

public class SymManager {
	public static Scope curScope;	// current scope
	public static int   curLevel;	// nesting level of current scope

	public  static int adr=0;
	public static Struct intType;	
	public static Struct charType;
	public static  Struct boolType;
	public static Struct nullType;
	public static Struct noType;
	public static Objects chrObj;		
	public static Objects ordObj;
	public static Objects lenObj;
	public static Objects noObj;
	
	private static void error(String msg) {
		Parser.error(msg);
	}
    
	public static void openScope() {
		Scope s = new Scope();
		s.outer = curScope;
		curScope = s;
		curLevel++;
	}

	public static void closeScope() {
		curScope = curScope.outer;
		curLevel--;
	}

	
	public static Objects insert(int kind, String name, Struct type,boolean  flag) {
		Objects curObj=new Objects(kind, name, type);
		Objects  findObj=find(name,false,flag);
		if(findObj!=null&&!findObj.name.equals("???")) {
			error(findObj.name+" Object is either already declared or is a keyword");
		}
		//TODO:Addresses yet to assign
		curObj.level=curLevel;
		if(curScope.locals==null) {
			curScope.locals=curObj;
			curScope.nVars++;
		}
		else {
			Objects temp = curScope.locals;
			while(temp.next!=null) {
				temp=temp.next;
			}
			temp.next=curObj;
			curScope.nVars++;
		}
		 return curObj;
	}

	public static Objects find(String name,boolean flag,boolean  scopeFlag) {
		if(!scopeFlag)
		{for (Scope s = curScope; s != null; s = s.outer)
			for (Objects p = s.locals; p != null; p = p.next)
			{
			if (p.name.equals(name)) return p;
			
			}
		}
		else {
			for (Objects p = curScope.locals; p != null; p = p.next)
			{
			if (p.name.equals(name)) return p;
			
			}
		}
		if(flag) {
			error(name + " is undeclared");
		}
			return noObj;
	}

	// Retrieve a class field with the given name from the fields of "type"
	public static Objects findField(String className,String name, int kind) {
	Objects classObj=find(className,true,false);
	if(classObj.type!=noType) {
		for (Objects p = classObj.locals; p != null; p = p.next)
			if (p.name.equals(name)&&p.kind==kind) return p;
	}
	error(name + " is undeclared");
	return noObj;
	
	}

	public static void dumpStruct(Struct type) {
		String kind;
		switch (type.kind) {
			case Struct.Int:  kind = "Int  "; break;
			case Struct.Char: kind = "Char "; break;
			case Struct.Arr:  kind = "Arr  "; break;
			case Struct.Class:kind = "Class"; break;
			default: kind = "None";
		}
		System.out.print(kind+" ");
		if (type.kind == Struct.Arr) {
			System.out.print(type.nFields + " (");
			dumpStruct(type.elemType);
			System.out.print(")");
		}
		if (type.kind == Struct.Class) {
			System.out.println(type.nFields + "<<");
			for (Objects o = type.fields; o != null; o = o.next) dumpObj(o);
			System.out.print(">>");
		}
	}

	public static void dumpObj(Objects o) {
		String kind;
		switch (o.kind) {
			case Objects.Con:  kind = "Con "; break;
			case Objects.Var:  kind = "Var "; break;
			case Objects.Type: kind = "Type"; break;
			case Objects.Meth: kind = "Meth"; break;
			default: kind = "None";
		}
		System.out.print(kind+" "+o.name+" "+o.val+" "+o.adr+" "+o.level+" "+o.nPars+" (");
		dumpStruct(o.type);
		System.out.println(")");
	}

	public static void dumpScope(Objects head) {
		System.out.println("--------------");
		for (Objects o = head; o != null; o = o.next) dumpObj(o);
		for (Objects o = head; o != null; o = o.next)
			if (o.kind == Objects.Meth || o.kind == Objects.Prog) dumpScope(o.locals);
	}

	//declare some keywords in the  symbol table
	public static void init() {  
		curScope = new Scope();
		curScope.outer = null;
		curLevel = -1;

		
		intType = new Struct(Struct.Int);
		charType = new Struct(Struct.Char);
		boolType = new Struct(Struct.Bool);
		nullType = new Struct(Struct.Class);
		noType = new Struct(Struct.None);
		noObj = new Objects(Objects.Var, "???", noType);

		insert(Objects.Type, "int", intType,false);
		insert(Objects.Type, "char", charType,false);
		insert(Objects.Type, "boolean",boolType,false);
		insert(Objects.Con, "null", nullType,false);
		chrObj = insert(Objects.Meth, "chr", charType,false);
		chrObj.locals = new Objects(Objects.Var, "i", intType);
		chrObj.nPars = 1;
		ordObj = insert(Objects.Meth, "ord", intType,false);
		ordObj.locals = new Objects(Objects.Var, "ch", charType);
		ordObj.nPars = 1;
		lenObj = insert(Objects.Meth, "len", intType,false);
		lenObj.locals = new Objects(Objects.Var, "a", new Struct(Struct.Arr, noType));
		lenObj.nPars = 1;
	}
}
