package com.gmail.berndivader.biene.db;

import com.gmail.berndivader.biene.enums.Tasks;

public abstract 
class
SimpleResultQuery 
extends
ResultQueryTask<String>
{

	public SimpleResultQuery(String query, Tasks event_enum) {
		super(query,event_enum);
		this.execute();
	}
	
	public SimpleResultQuery(String query, Tasks event_enum,long max_time) {
		super(query,event_enum);
		max_minutes(max_time);
		this.execute();
	}
	
	@Override
	protected void max_minutes(long max) {
		this.max_minutes=max;
	}

}
