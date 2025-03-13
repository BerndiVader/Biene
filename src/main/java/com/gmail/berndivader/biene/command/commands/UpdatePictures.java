package com.gmail.berndivader.biene.command.commands;

import com.gmail.berndivader.biene.command.Command;
import com.gmail.berndivader.biene.command.CommandAnnotation;
import com.gmail.berndivader.biene.db.UpdatePicturesTask;

@CommandAnnotation(name=".pictures",usage="Upload und Verarbeitung von neu bereitgestellten Bildern.")
public class UpdatePictures extends Command {

	@Override
	public boolean execute(String args) {
		new UpdatePicturesTask();
		return true;
		
	}

}
