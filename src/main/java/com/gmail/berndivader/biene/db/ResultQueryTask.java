package com.gmail.berndivader.biene.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Worker;
import com.gmail.berndivader.biene.enums.Tasks;
import com.gmail.berndivader.biene.Helper;

public
abstract 
class
ResultQueryTask<T>
extends
Worker
implements
Callable<ResultSet>,
IQueryTask<ResultSet,Void>
{
	
	protected final String query;
	protected final Tasks action;
	protected Future<ResultSet>future;
	
	public final CountDownLatch latch;
	public T object;

	public ResultQueryTask(String query,Tasks event_enum,int latch,T object) {
		super();
		this.object=object;
		this.query=query;
		this.action=event_enum;
		this.latch=new CountDownLatch(latch);
	}
	
	public ResultQueryTask(String query,Tasks event_enum,T object) {
		this(query,event_enum,1,object);
	}
	
	
	public ResultQueryTask(String query,Tasks event_enum) {
		this(query,event_enum,1,null);
	}
	
	@Override
	public void execute() {
		future=Helper.executor.submit(this);
		
	}
	
	@Override
	public ResultSet call() throws Exception {
		ResultSet result=null;
		try(Connection conn=DatabaseConnection.getNewConnection()) {
			try(PreparedStatement statement=conn.prepareStatement(this.query,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY)) {
				result=statement.executeQuery();
				this.completed(result);
			}
		} catch (SQLException ex) {
			Logger.$(ex,false,true);
			this.failed(null);
		}
		this.took();
		latch.countDown();
		return result;
	}
	
	protected static void parseResult(ResultSet source) {
		try {
			int columns=source.getMetaData().getColumnCount();
			source.beforeFirst();
			while(source.next()) {
				for(int i1=1;i1<=columns;i1++) {
					Logger.$(source.getString(i1),false);
				}
			}
		} catch (SQLException e) {
			Logger.$(e,false,true);
		}
	}
	
}
