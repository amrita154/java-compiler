package scanner;

/**
 * 
 * While scanning the file character by  character. The  tokens are  created  with followign properties
 *
 */

public class Token {
	public int kind;		// token kind->integer value from TokenCodes
	public int line;		// line number
	public int col;			// column number
	public int val;			//  value (for number and charConst)
	public String string;	// token label/name
}
