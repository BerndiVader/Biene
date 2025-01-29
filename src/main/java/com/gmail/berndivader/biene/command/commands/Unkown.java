package com.gmail.berndivader.biene.command.commands;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.command.Command;

public class Unkown extends Command {

	@Override
	public boolean execute(String args) {
		Logger.$("Unbekannter Befehl. Verwende .h für Hilfe.");
		return true;
	}

}
