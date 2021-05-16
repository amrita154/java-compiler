
/**
 * Purpose  of scanner is to read code file and get tokens from it for lexical analysis.
 * It avoids the extra spaces
 * Right now included few keywords.
 * The keyword list will depend on the syntax of language we decide
 * Keywords list can be extended
 * 
 */
package scanner;
import java.io.*;
import java.util.HashMap;
import scanner.Token;
import utils.TokenCodes;


public class Scanner {
	
	private static final char eofCh = '\u0080';
	private static final char eol = '\n';
	public static HashMap<String, Integer> keyMapping;
	private static char ch;			// current character
	public  static int col;			// current column
	public  static int line;		// current line
	private static int pos;			// current read pointer position
	private static Reader in;  	// source file reader

	//----- ch = next input character
	private static void nextCh() {
		try {
			ch = (char)in.read(); col++; pos++;
			if (ch == eol) {line++; col = 0;}
			else if (ch == '\uffff') ch = eofCh;
		
		} catch (IOException e) {
			ch = eofCh;
		}
	}

	//--------- Initialize scanner
	public static void init(Reader r) {
		in = new BufferedReader(r);
		line = 1; col = 0;
		//All keywords are mapped with particular token codes
		keyMapping= new HashMap<String, Integer>();
		keyMapping.put("int", TokenCodes.INT);
		keyMapping.put("char", TokenCodes.CHAR);
		keyMapping.put("class",TokenCodes.class_);
		keyMapping.put("else", TokenCodes.else_);
		keyMapping.put("final", TokenCodes.final_);
		keyMapping.put("if",TokenCodes.if_);
		keyMapping.put("new", TokenCodes.new_);
		keyMapping.put("print", TokenCodes.print_);
		keyMapping.put("program", TokenCodes.program_);
		keyMapping.put("read", TokenCodes.read_);
		keyMapping.put("return", TokenCodes.return_);
		keyMapping.put("void", TokenCodes.void_);
		keyMapping.put("while", TokenCodes.while_);
		keyMapping.put("else if", TokenCodes.else_if_);
		keyMapping.put("const",TokenCodes.const_);
		keyMapping.put("true", TokenCodes.boolConstTrue);
		keyMapping.put("false", TokenCodes.boolConstFalse);
		keyMapping.put("extends", TokenCodes.extends_);
		keyMapping.put("do", TokenCodes.do_);
		keyMapping.put("break", TokenCodes.break_);
		keyMapping.put("continue", TokenCodes.continue_);
		//read file token
		nextCh();
	}
	
	public static boolean isCharAlphabet(char  ch) {
		if(ch>='A'&&ch<='Z')
			return  true;
		if(ch>='a'&&ch<='z')
			return true;
		return false;
	}
	
	public static boolean isCharNumber(char ch) {
		return (ch>='0'&&ch<='9');
	}
	
	public static void readName(Token t) {
		String name="";
		name+=ch;
		nextCh();
		while(isCharNumber(ch)||isCharAlphabet(ch)) {
			name+=ch;
			nextCh();
		}
		t.string=name;
		if(keyMapping.containsKey(name)) {
			t.kind=keyMapping.get(name);
		}
		else {
			t.kind=TokenCodes.ident;
		}
	}
	
	public static void readNumber(Token t) {
		int digit=ch-'0';
		nextCh();
		while(isCharNumber(ch)) {
			digit=digit*10+(ch-'0');
			if(digit>Integer.MAX_VALUE) {
				System.out.println("overflow!");
				return;
				//report error
			}
			else {
				nextCh();
			}
		}
		t.val=digit;
		t.kind=TokenCodes.INT;
	
	}
	
	public  static void readCharConst(Token t) {
		String characters="";
		nextCh();
		 if(ch=='\\') {
				nextCh();
				characters+=ch;
				nextCh();
			}
			else if(ch=='\''){
				System.out.println("empty char");
			}
		else {
			characters+=ch;
			t.kind=TokenCodes.CHAR;
			t.val=ch;
			nextCh();
		}
		nextCh();
		t.string=characters;
		t.kind=TokenCodes.CHAR;
	return;
	}
	
	
	
	//Read file char by char and create token using token,tokencodes
	public static Token next() {
		while (ch <= ' ') nextCh(); // skip blanks, tabs, eols
			Token t = new Token(); 
			t.line = line; t.col = col;
			if(ch>=97&&ch<=122)
			{
				 readName(t); 
			}
			else if(ch>='A'&&ch<='Z') {
				 readName(t); 
			}
			else if(ch>='0'&&ch<='9') {
				 readNumber(t); 
			}
			else if(ch=='\''){
				readCharConst(t);
			}
			else if(ch==';') {
				 nextCh(); 
				 t.kind = TokenCodes.semicolon;
			}
			else if(ch=='.') {
				nextCh();
				t.kind = TokenCodes.period;
			}
			else if(ch=='=') {
				nextCh();
				if (ch == '=') { 
					nextCh();
					t.kind = TokenCodes.eql; 
					} 
				else t.kind = TokenCodes.assign;
			}
			else if(ch=='/') {
				 nextCh();
				 if (ch == '/') {
						do {
							nextCh();
						}
						while (ch != '\n' && ch != eofCh);
						t = next(); // call scanner recursively
				} 
				else t.kind = TokenCodes.slash;
			}
			else if(ch=='(') {
				 nextCh(); 
			t.kind=TokenCodes.lpar;
			}
			else if(ch==')') {
				 nextCh(); 
				t.kind=TokenCodes.rpar;
			}
			else if(ch=='{') {
				 nextCh(); 
				t.kind=TokenCodes.lbrace;
			}
			else if(ch=='}') {
				 nextCh(); 
				t.kind=TokenCodes.rbrace;
			}
			else if(ch=='[') {
				 nextCh(); 
				t.kind=TokenCodes.lbrack;
			}
			else if(ch==']') {
				 nextCh(); 
				t.kind=TokenCodes.rbrack;
			}
			else if(ch=='+') {
				nextCh();
				if(ch=='+') {
					nextCh();
					t.kind=TokenCodes.increment;
				}
				else {
					t.kind=TokenCodes.plus;
				}
			}
			else if(ch=='-') {
				nextCh();
				if(ch=='-') {
					nextCh();
					t.kind=TokenCodes.decrement;
				}
				else {
					t.kind=TokenCodes.minus;
				}
			}
			else if(ch=='*') {
				nextCh();
				t.kind=TokenCodes.times;
			}
			else if(ch=='/') {
				nextCh();
				t.kind=TokenCodes.slash;
			}
			else if(ch=='%') {
				nextCh();
				t.kind=TokenCodes.rem;
			}
			else if(ch=='|') {
				nextCh();
				if(ch=='|') {
					nextCh();
					t.kind=TokenCodes.or;
				}
				else {
					t.kind=TokenCodes.bitOr;
				}
			}
			else if(ch=='&') {
				nextCh();
				if(ch=='&') {
					nextCh();
					t.kind=TokenCodes.and;
				}
				else {
					t.kind=TokenCodes.bitAnd;
				}
			}
			else if(ch=='!') {
				nextCh();
				if(ch=='=') {
					nextCh();
					t.kind=TokenCodes.neq;
				}
				else {
				t.kind=TokenCodes.not;
				}
			}
			else if(ch==eofCh) {
				t.kind=TokenCodes.eof;
			}
			else if(ch==',') {
				nextCh();
				t.kind=TokenCodes.comma;
			}
			else if(ch=='<') {
				nextCh();
				if(ch=='=') {
					nextCh();
					t.kind=TokenCodes.leq;
				}
				else {
				t.kind=TokenCodes.lss;
				}
			}
			else if(ch=='>') {
				nextCh();
				if(ch=='=') {
					nextCh();
					t.kind=TokenCodes.geq;
				}
				else {
				t.kind=TokenCodes.gtr;
				}
			}
			
			else {
				 nextCh(); 
				 t.kind = TokenCodes.none;
			}
			return t;
	}
	
}






