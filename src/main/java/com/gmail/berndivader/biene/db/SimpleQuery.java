package com.gmail.berndivader.biene.db;

public 
class 
SimpleQuery 
extends
QueryTask
{

	public SimpleQuery(String query) {
		super(query);
		this._call();
	}

	@Override
	public void completed() {
		parseQuery(query,true);
		parseQueryAction(query);
	}

	@Override
	public void failed() {
		parseQuery(query,false);
	}

}
