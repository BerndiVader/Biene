package com.gmail.berndivader.biene.command.commands;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.command.Command;
import com.gmail.berndivader.biene.command.CommandAnnotation;
import com.gmail.berndivader.biene.command.Commands;

@CommandAnnotation(name=".h",usage="Zeigt alle verfügbaren Befehle an.")
public class Help extends Command {

	@Override
	public boolean execute(String args) {
		Commands.instance.commands.forEach((name,className)->{
			try {
				Class<?>clazz=Class.forName(className);
				if(clazz!=null) {
					CommandAnnotation annotation=clazz.getDeclaredAnnotation(CommandAnnotation.class);
					if(annotation!=null) {
						Logger.$(String.format("%s - %s",annotation.name(),annotation.usage()));
					}
				}
			} catch (ClassNotFoundException e) {
 				Logger.$(e);
			}
		});
		return true;
	}

}
