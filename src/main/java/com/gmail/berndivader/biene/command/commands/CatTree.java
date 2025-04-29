package com.gmail.berndivader.biene.command.commands;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Utils;
import com.gmail.berndivader.biene.command.Command;
import com.gmail.berndivader.biene.command.CommandAnnotation;
import com.gmail.berndivader.biene.db.CatalogTree;

@CommandAnnotation(name=".cattree",usage="Zeigt Katalog an.")
public class CatTree extends Command {

	@Override
	public boolean execute(String args) {
		
		CatalogTree catTree=new CatalogTree();
		try {
			catTree.latch.await();
			Logger.$(Utils.GSON.toJson(catTree.tree));
		} catch (InterruptedException e) {
			Logger.$(e);
		}
		return true;
	}

}
