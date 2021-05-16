package codeGeneration;
/**
 * This  class  is the  initial class where the input code file is provided as argument in the java  command or we can hard code the source  file to open in the class
 */

import java.io.*;

import parser.Parser;
import scanner.Scanner;
import symbols.SymManager;


public class Compiler {
	public static void main(String args[]) {
		//provide file to compile in args
		if (args.length==0) {
			String source = "src/codeGeneration/buggy.txt";
			String output = Compiler.outputFileName(source);
			try {
				
				//Initialise scanner with input file
				Scanner.init(new InputStreamReader(new FileInputStream(source)));
				//Initialise symbol table with  starting symbols/keywords
				SymManager.init();
				//Initialise data buffer
				Code.init();
				//Parse the  tokens scanned by scanner
				Parser.parse();
					try {
						//Write object code to output file
						Code.write(new FileOutputStream(output));
					} catch (IOException e) {
						System.out.println(output+"cannot open output file");
					}
			} catch (IOException e) {
				System.out.println(source+"cannot read input file ");
			}
		} else System.out.println();
	}
	
	public static String outputFileName(String s) {
		int i = s.lastIndexOf('.');
		//append output.txt-> gives assembly code for java  code
		if (i < 0) return s + "output.txt"; else return s.substring(0, i) + "output.txt";
	}

}