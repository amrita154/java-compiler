package symbols;

public class Struct {
	public static final int // structure kinds
	None  = 0,
	Int   = 1,
	Char  = 2,
	Arr   = 3,
	Class = 4,
	Program=5,
	Void=6,
	Bool=7;
public int    kind;		  // None, Int, Char, Arr, Class
public Struct elemType; // Arr: element type
public int    nFields;  // Class: number of fields
public Objects    fields;   // Class: fields

public Struct(int kind) {
	this.kind = kind;
}

public Struct(int kind, Struct elemType) {
	this.kind = kind; this.elemType = elemType;
}

// Checks if this is a reference type
public boolean isRefType() {
	return this.kind == Class || this.kind == Arr;
}

// Checks if two types are equal
public boolean equals(Struct other) {
	if (this.kind == Arr)
		return other.kind == Arr && other.elemType == this.elemType;
		else
		return other.kind == this.kind;
}

// Checks if two types are compatible (e.g. in a comparison)
public boolean compatibleWith(Struct other) {
	return this.equals(other)
		||	this == SymManager.nullType && other.isRefType()
		||	other ==SymManager.nullType && this.isRefType();
}

// Checks if an object with type "this" can be assigned to an object with type "dest"
public boolean assignableTo(Struct dest) {
	return this.equals(dest)
		||	this == SymManager.nullType && dest.isRefType()
		||  this.kind == Arr && dest.kind == Arr && dest.elemType == SymManager.noType;
}
}
