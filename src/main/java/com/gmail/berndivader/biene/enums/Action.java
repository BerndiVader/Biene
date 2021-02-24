package com.gmail.berndivader.biene.enums;

public
enum
Action
{
	INSERT("insert"),
	DELETE("delete"),
	UPDATE("update");
	
	private String action;
	
	private Action(String action) {
		this.action=action;
	}
	
	public String value() {
		return action;
	}
}
