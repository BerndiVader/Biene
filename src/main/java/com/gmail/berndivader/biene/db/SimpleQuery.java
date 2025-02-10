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
	public void completed(Boolean result) {
	}

	@Override
	public void failed(Boolean result) {
	}

	@Override
	protected void setMaxTime(long max) {
		this.max_time=max;
	}

}
