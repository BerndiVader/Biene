package com.gmail.berndivader.biene.db;

public 
class 
SimpleQuery 
extends
QueryTask
{

	public SimpleQuery(String query) {
		super(query);
		this.execute();
	}

	@Override
	protected void max_seconds(long max) {
		this.max_seconds=max;
	}

	@Override
	public void completed(Void result) {
	}

	@Override
	public void failed(Void result) {
	}

}
