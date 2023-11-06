package com.gmail.berndivader.biene.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import com.gmail.berndivader.biene.Batcher;
import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Worker;
import com.gmail.berndivader.biene.http.Helper;

public
abstract 
class
QueryBatchTask
extends
Worker
implements
Callable<ResultSet>,
IQueryTask
{
	protected UUID uuid;
	protected String query;
	public Future<ResultSet>future;
	public CountDownLatch latch;

	public QueryBatchTask(String query, int latch) {
		super();
		uuid=UUID.randomUUID();
		this.latch=new CountDownLatch(latch);
		this.query=query;
	}
	
	public QueryBatchTask(String query) {
		this(query,1);
	}
	
	public boolean batch() {
		start_time=System.currentTimeMillis();
		future=Helper.executor.submit(this);
		return true;
	}
	
	public void add() {
		Batcher.query_stack.push(this);
	}
	
	@Override
	public ResultSet call() throws Exception {
		try (Connection conn=DatabaseConnection.getNewConnection()) {
			conn.prepareStatement(this.query).execute();
			this.completed();
		} catch (SQLException ex) {
			Logger.$(ex,false,true);
			this.failed();
		}
		this.took();
		latch.countDown();
		return null;
	}
}
