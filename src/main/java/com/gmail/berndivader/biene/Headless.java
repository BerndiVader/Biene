package com.gmail.berndivader.biene;

import java.io.IOException;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import com.gmail.berndivader.biene.command.Command;
import com.gmail.berndivader.biene.command.Commands;

public class Headless {
	
	public static boolean exit;
	
	public static Terminal terminal;
	public static LineReader reader;
	
	static {
		try {
			terminal=TerminalBuilder.terminal();
			terminal.enterRawMode();
			reader=LineReaderBuilder.builder().terminal(terminal).build();
			
		} catch (IOException e) {
			Logger.$(e);
			exit=true;
		}
		exit=false;
	}
	
	public Headless() throws IOException {
		
		Commands.instance=new Commands();
		
		while(!exit) {
			String input=reader.readLine(">");
			
			if(input.startsWith(".")) {
				String[]temp=input.split(" ",2);
				String cmd=temp[0];
				String args=temp.length>1?temp[1]:"";
				Command command=Commands.instance.getCommand(cmd);
				command.execute(args);
			}
			
		}
		terminal.close();
	}
	
}
