package codeGeneration;

import parser.Parser;
import symbols.Objects;
import symbols.Struct;
import symbols.SymManager;



public class Operand {
	public static final int  // item kinds
	  Con    = 0,
	  Local  = 1,
	  Static = 2,
	  Stack  = 3,
	  Fld    = 4,
	  Elem   = 5,
	  Meth   = 6;

	public int    kind;	// Con, Local, Static, Stack, Fld, Elem, Meth
	public Struct type;	// item type
	public Objects    obj;  // Meth
	public int    val;  // Con: value
	public int    adr;  // Local, Static, Fld, Meth: address

	public Operand(Objects o) {
		type = o.type; val = o.val; adr = o.adr; kind = Stack; // default
		switch (o.kind) {
			case Objects.Con:
				kind = Con; break;
			case Objects.Var:
				if (o.level == 0) kind = Static; else kind = Local;
				break;
			case Objects.Meth:
				kind = Meth; obj = o; break;
			case Objects.Type:
				Parser.error("type identifier not allowed here"); break;
			default:
				Parser.error("wrong kind of identifier"); break;
		}
	}

	public Operand(int val) {
		kind = Con; this.val = val; type = SymManager.intType;
	}

	public Operand(int kind, int val, Struct type) {
		this.kind = kind; this.val = val; this.type = type;
	}

}