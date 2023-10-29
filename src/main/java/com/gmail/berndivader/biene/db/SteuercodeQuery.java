package com.gmail.berndivader.biene.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.enums.EventEnum;

public class SteuercodeQuery extends SimpleResultQuery {
	
	public int code;
	private static String query="select c009 from t010 where c010 = ";

	public SteuercodeQuery(int tax) {
		super(query+tax,EventEnum.UNKOWN);
		code=1;
	}
	
	@Override
	public void completed() {
		if(result.isPresent()) {
			ResultSet source=result.get();
			try {
				source.first();
				code=source.getInt(1);
				source.close();
			} catch (SQLException e) {
				Logger.$(e,false,true);
			}
		}
		if(code==10) {
			code=3;
		} else if(code==15) {
			code=4;
		}
	}
	
	@Override
	public void failed() {
		if(result.isPresent())
			try {
				result.get().close();
			} catch (SQLException e) {
				Logger.$(e,false,true);
			}
		Logger.$("Failed to get Steuercode for: "+this.code);
	}

}
