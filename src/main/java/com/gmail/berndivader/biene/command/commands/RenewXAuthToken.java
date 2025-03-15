package com.gmail.berndivader.biene.command.commands;

import com.gmail.berndivader.biene.command.Command;
import com.gmail.berndivader.biene.command.CommandAnnotation;
import com.gmail.berndivader.biene.http.post.PostXAuthTokenRequestSync;

@CommandAnnotation(name=".xauth",usage="Holt neuen gültigen XAuth Token vom Server und speichert ihn in der Configuration.")
public class RenewXAuthToken extends Command {

	@Override
	public boolean execute(String args) {
		return new PostXAuthTokenRequestSync().join();
	}

}
