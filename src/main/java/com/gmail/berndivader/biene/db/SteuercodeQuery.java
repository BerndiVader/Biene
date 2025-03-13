package com.gmail.berndivader.biene.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.enums.Tasks;

public class SteuercodeQuery extends SimpleResultQuery {
	
	public int code;
	private static final String QUERY="select c009 from t010 where c010=";

	public SteuercodeQuery(int tax) {
		super(QUERY+tax,Tasks.UNKOWN);
		code=1;
	}
	
	@Override
	public void completed(ResultSet result) {
		try {
			if(result!=null&&result.first()) {
				code=result.getInt(1);
				switch(code) {
					case 10:
						code=3;
						break;
					case 15:
						code=4;
						break;
				}
			}
		} catch(SQLException e) {
			Logger.$(e,false,true);
		}
	}
	
	@Override
	public void failed(ResultSet result) {
		Logger.$("Failed to get Steuercode for: "+this.code);
	}

	@Override
	protected void max_seconds(long max) {
		max_seconds=15l;
 	}

}
