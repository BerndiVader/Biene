package com.gmail.berndivader.biene.db;

import java.sql.ResultSet;

import com.gmail.berndivader.biene.enums.Tasks;

public 
class
SimpleResultQuery 
extends
ResultQueryTask<String>
{

	public SimpleResultQuery(String query, Tasks event_enum) {
		super(query,event_enum);
		this.execute();
	}

	@Override
	public void completed(ResultSet result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void failed(ResultSet result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setMaxTime(long max) {
		this.max_time=max;
	}

}
