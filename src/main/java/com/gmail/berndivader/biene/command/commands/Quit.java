package com.gmail.berndivader.biene.command.commands;

import com.gmail.berndivader.biene.command.Command;
import com.gmail.berndivader.biene.command.CommandAnnotation;

@CommandAnnotation(name=".q",usage="Beendet das Programm.")
public class Quit extends Command {

	@Override
	public boolean execute(String args) {
		System.exit(0);
		return true;
	}

}
