package pro.jarvenpaa.wundernuts;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import pro.jarvenpaa.wundernuts.HeftiestWordPairFinder;

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * Copyright (c) 2015 J. Järvenpää <jarvenja@gmail.com>
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
final public class App {
	static private final int DIFFERENT_LETTERS_MAX = 20;
	static private final String ALPHABET = "abcdefghijklmnopqrstuvwxyzåäö";
	static private void error(String msg) {
		System.err.format("\033[31mError: %s!\033[0m\n", msg == null ? "<no message>" : msg);
	}

	private long startTime = System.currentTimeMillis();
	private App(String path) {
		try {
			Scanner s = new Scanner(new File(path), "ISO-8859-1");
			String text = s.useDelimiter("\\A").next();
			HeftiestWordPairFinder finder = new HeftiestWordPairFinder(DIFFERENT_LETTERS_MAX, ALPHABET);
			finder.evaluate(text);
			System.out.format("\033[32mSolved in %s ms.\033[0m\n", System.currentTimeMillis() - startTime); 
			System.out.println(finder);
		} catch (IllegalArgumentException iae) {
			iae.printStackTrace();
		} catch (FileNotFoundException fnfe) {
			error(fnfe.getMessage());
		}
	}

	static public void main(String[] args) {
		switch (args.length) {
			case 0 : 
				error("Missing file path (argument) to text file");
				break;
			case 1 :
				new App(args[0]);
				break;
			default :
				error("Too many argument(s)");
				break;
		}
	}
}

