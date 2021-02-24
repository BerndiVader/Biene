package com.gmail.berndivader.biene.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Worker;
import com.gmail.berndivader.biene.enums.EventEnum;
import com.gmail.berndivader.biene.http.Helper;

public
abstract 
class
ResultQueryTask<T>
extends
Worker
implements
Callable<ResultSet>,
IQueryTask
{
	
	final String query;
	final EventEnum event_enum;
	public final CountDownLatch latch;
	public Optional<ResultSet>result;
	public Future<ResultSet>future;
	public T object;

	public ResultQueryTask(String query,EventEnum event_enum,int latch,T object) {
		super();
		this.object=object;
		this.result=Optional.empty();
		this.query=query;
		this.event_enum=event_enum;
		this.latch=new CountDownLatch(latch);
	}
	
	public ResultQueryTask(String query,EventEnum event_enum,T object) {
		this(query,event_enum,1,object);
	}
	
	
	public ResultQueryTask(String query,EventEnum event_enum) {
		this(query,event_enum,1,null);
	}
	
	public void _call() {
		future=Helper.executor.submit(this);
	}
	
	@Override
	public ResultSet call() throws Exception {
		try (Connection conn=DatabaseConnection.getNewConnection()) {
			PreparedStatement statement=conn.prepareStatement(this.query,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			result=Optional.ofNullable(statement.executeQuery());
			this.completed();
		} catch (SQLException ex) {
			Logger.$(ex,false,true);
			this.failed();
		}
		this.took();
		latch.countDown();
		return result.orElse(null);
	}
	
	public static void parseResult(ResultSet source) {
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
