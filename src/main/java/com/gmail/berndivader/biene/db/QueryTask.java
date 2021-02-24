package com.gmail.berndivader.biene.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Worker;
import com.gmail.berndivader.biene.http.Helper;

public
abstract 
class
QueryTask
extends
Worker
implements
Callable<ResultSet>,
IQueryTask
{
	static String var="\\{biene_var\\}";
	String query;
	public Future<ResultSet>future;
	public CountDownLatch latch;

	public QueryTask(String query, int latch) {
		super();
		this.latch=new CountDownLatch(latch);
		this.query=query;
	}
	
	public QueryTask(String query) {
		this(query,1);
	}
	
	public void _call() {
		future=Helper.executor.submit(this);
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
	
	static String parseQuery(String source,boolean sucsess) {
		String message=""+source.split(var)[0]+" ";
		message+=sucsess?"erfolgreich ausgeführt":"fehlgeschlagen";
		Logger.$(message,false,true);
		return message;
	}
	
	static void parseQueryAction(String action) {
		
	}
	
}
