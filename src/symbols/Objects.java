package symbols;

public class Objects {
	public static final int // object kinds
		Con  = 0,//constant
		Var  = 1, //variable
		Type = 2,//Type (Class/Custom)
		Meth = 3,//Method
		Prog = 4,
		Keywords=5; //Program
	public int    kind;		// Con, Var, Type, Meth, Prog
	public String name;		// object name
	public Struct type;	 	// object type
	public int    val;    // Con: value
	public int    adr;    // Var, Math: address
	public int    level;  // Var: declaration level
	public int    nPars;  // Meth: number of parameters
	public Objects    locals; // Meth: parameters and local objects
	public Objects    next;		// next local object in this scope

	public Objects(int kind, String name, Struct type) {
		this.kind = kind; this.name = name; this.type = type;
	}
}