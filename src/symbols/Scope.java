package symbols;

public class Scope {
	public Scope outer;		// for outer scope
	public Objects   locals;	// for local variables of current scope
	public int   nVars;     // number of variables in current scope
}
