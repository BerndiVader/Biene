package com.gmail.berndivader.biene.db;

import com.gmail.berndivader.biene.enums.EventEnum;

public 
class
SimpleResultQuery 
extends
ResultQueryTask<String>
{

	public SimpleResultQuery(String query, EventEnum event_enum) {
		super(query,event_enum);
		this._call();
	}

	@Override
	public void completed() {
	}

	@Override
	public void failed() {
	}

}
