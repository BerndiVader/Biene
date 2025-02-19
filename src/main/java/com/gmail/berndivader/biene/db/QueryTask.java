package com.gmail.berndivader.biene.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Worker;
import com.gmail.berndivader.biene.Helper;

public
abstract 
class
QueryTask
extends
Worker
implements
Callable<Boolean>,
IQueryTask<Void>
{
	protected final static String VAR="\\{biene_var\\}";
	private final String query;
	
	public Future<Boolean>future;
	public final CountDownLatch latch;

	public QueryTask(String query, int latch) {
		super();
		this.latch=new CountDownLatch(latch);
		this.query=query;
	}
	
	public QueryTask(String query) {
		this(query,1);
	}
	
	@Override
	public void execute() {
		future=Helper.executor.submit(this);
	}
	
	@Override
	public Boolean call() throws Exception {
		boolean done=false;
		try(Connection conn=DatabaseConnection.getNewConnection()) {
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
	
	protected static String parseQuery(String source,boolean sucsess) {
		String message=""+source.split(VAR)[0]+" ";
		message+=sucsess?"erfolgreich ausgeführt":"fehlgeschlagen";
		Logger.$(message,false,true);
		return message;
	}
	
}
