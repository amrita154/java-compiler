
package parser;

import java.io.*;

import scanner.Scanner;
import scanner.Token;
import symbols.SymManager;

public class TestParser {

	// Main method of the parser tester
	public static void main(String args[]) {
		Token t;
		if (args.length == 0) {
			String source ="src/scanner/buggy.txt";
			try {
				Scanner.init(new InputStreamReader(new FileInputStream(source)));
				SymManager.init();
				Parser.parse();
//				System.out.println(Parser.errors + " errors detected");
			} catch (IOException e) {
				System.out.println("-- cannot open input file " + source);
			}
		} else System.out.println("-- synopsis: java MJ.TestParser <inputfileName>");
	}

}