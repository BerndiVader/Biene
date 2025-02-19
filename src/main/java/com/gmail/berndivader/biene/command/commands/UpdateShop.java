package com.gmail.berndivader.biene.command.commands;

import com.gmail.berndivader.biene.command.Command;
import com.gmail.berndivader.biene.command.CommandAnnotation;
import com.gmail.berndivader.biene.db.UpdateShopTask;

@CommandAnnotation(name=".update",usage="Überprüfe ob Aktualisierungen in die Modified MySql Datenbank eingespielt werden kann.")
public class UpdateShop extends Command {

	@Override
	public boolean execute(String args) {
		new UpdateShopTask();
		return true;
	}

}
