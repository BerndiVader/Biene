package com.gmail.berndivader.biene;

import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Headless {
	
	static PrintStream console;
	static Scanner keyboard;
	
	public static boolean exit;
	
	static {
		console=System.out;
		keyboard=new Scanner(System.in);
		exit=false;
	}
	
	public Headless() {
		
		while(!exit) {
			String input=console();
			if(input!=null) {
				if(input.startsWith(".")) {
					
				} else {
					console.printf("%s",input);
				}
			}
		}
		
		keyboard.close();
	}

	private String console() {
		console.printf("%s",">");
		String input;
		try {
	        input = keyboard.nextLine().toLowerCase();
		} catch (NoSuchElementException e) {
			input="";
		}
		return input;
	}

}
