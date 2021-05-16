package scanner;

/* MicroJava Scanner Tester
   ========================
   Place this file in a subdirectory MJ
   Compile with
     javac MJ\TestScanner.java
   Run with
     java MJ.TestScanner <inputFileName>
*/


import java.io.*;

import utils.TokenCodes;

public class TestScanner {
	// Main method of the scanner tester
	public static void main(String args[]) {
		Token t;
		if (args.length == 0) {
			String source ="src/scanner/buggy.txt";
			
			try {
				Scanner.init(new InputStreamReader(new FileInputStream(source)));
				do {
					t = Scanner.next();
					System.out.print("line " + t.line + ", col " + t.col + ": " + TokenCodes.tokenName[t.kind]);
					switch (t.kind) {
						case TokenCodes.ident:   System.out.println(t.string); break;
						case TokenCodes.INT:  System.out.println(t.val); break;
						case TokenCodes.CHAR: System.out.println(t.val); break;
						default: System.out.println(); break;
					}
				} while (t.kind != TokenCodes.eof);
			} catch (IOException e) {
				System.out.println("-- cannot open input file " + source);
			}
		} else System.out.println("-- synopsis: java MJ.TestScanner");
	}

}