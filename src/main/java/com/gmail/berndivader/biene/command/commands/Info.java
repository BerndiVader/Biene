package com.gmail.berndivader.biene.command.commands;

import com.gmail.berndivader.biene.command.Command;
import com.gmail.berndivader.biene.command.CommandAnnotation;
import com.gmail.berndivader.biene.http.get.GetInfoSync;

@CommandAnnotation(name=".i",usage="Zeigt Versionsinformationen.")
public class Info extends Command {

	@Override
	public boolean execute(String args) {
		return new GetInfoSync().join();
	}

}
