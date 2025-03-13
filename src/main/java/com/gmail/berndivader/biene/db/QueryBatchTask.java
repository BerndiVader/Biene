package com.gmail.berndivader.biene.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import com.gmail.berndivader.biene.Batcher;
import com.gmail.berndivader.biene.Helper;
import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Worker;

public 
abstract
class
QueryBatchTask
extends
Worker
implements
Callable<Boolean>,
IQueryTask<Void>
{
	protected UUID uuid;
	protected String query;
	public Future<Boolean>future;
	public CountDownLatch latch;
	
	public QueryBatchTask(int latch) {
		this("",latch);
	}

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
		execute();
		return true;
	}
	
	protected void add() {
		Batcher.QUERY_STACK.offer(this);
	}
	
	@Override
	public Boolean call() throws Exception {
		boolean done=false;
		try (Connection conn=DatabaseConnection.getNewConnection()) {
			done=conn.prepareStatement(this.query).execute();
			this.completed(null);
		} catch (SQLException ex) {
			Logger.$(ex,false,true);
			this.failed(null);
		}
		this.took();
		latch.countDown();
		return done;
	}

	@Override
	public void execute() {
		future=Helper.executor.submit(this);
	}

}
