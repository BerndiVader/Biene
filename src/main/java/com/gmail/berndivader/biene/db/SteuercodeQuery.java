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
			}
		} catch(SQLException e) {
			Logger.$(e,false,true);
		}
		if(code==10) {
			code=3;
		} else if(code==15) {
			code=4;
		}
	}
	
	@Override
	public void failed(ResultSet result) {
		Logger.$("Failed to get Steuercode for: "+this.code);
	}

}
