package com.gmail.berndivader.biene.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.enums.EventEnum;

public class SteuercodeQuery extends SimpleResultQuery {
	
	public int code;

	public SteuercodeQuery(String query) {
		super(query, EventEnum.UNKOWN);
		code=1;
	}
	
	@Override
	public void completed() {
		if(result.isPresent()) {
			ResultSet source=result.get();
			try {
				source.first();
				code=source.getInt(1);
			} catch (SQLException e) {
				Logger.$(e,false,true);
			}
		}
	}
	
	@Override
	public void failed() {
	}

}
