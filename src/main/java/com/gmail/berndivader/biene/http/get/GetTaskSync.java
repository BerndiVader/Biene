package com.gmail.berndivader.biene.http.get;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import com.gmail.berndivader.biene.Worker;
import com.gmail.berndivader.biene.config.Config;
import com.gmail.berndivader.biene.enums.Tasks;
import com.gmail.berndivader.biene.Helper;
import com.gmail.berndivader.biene.Logger;

public 
abstract 
class 
GetTaskSync
extends
Worker
implements
Callable<HttpResponse>,
IGetTask
{
	protected final String url;
	protected final Tasks command;
	protected final HttpGet request;
	protected CompletableFuture<Void>completable;
	
	public boolean failed;
	
	public GetTaskSync(String url,Tasks command) {
		super();
		
		this.url=url;
		this.command=command;
		request=new HttpGet(url.concat(command.command()));
		if(Config.data.cf_enabled()) {
			request.setHeader("CF-Access-Client-Id",Config.data.cf_client());
			request.setHeader("CF-Access-Client-Secret",Config.data.cf_secret());
		}
		request.setHeader("X-Authorization","Bearer ".concat(Config.data.bearer_token()));
		request.setHeader("user",Config.data.shop_user());
		request.setHeader("password",Config.data.shop_password());
				
	}
	
	@Override
	public HttpResponse call() throws Exception {
		failed=false;
		return Helper.syncClient.execute(request);
	}
	
	protected CompletableFuture<HttpResponse>start() {
		return CompletableFuture.supplyAsync(()->{
			try {
				return call();
			} catch (Exception e) {
				failed=true;
				Logger.$(e);
				return Helper.errorResponse(e);
			}
		},Helper.executor).orTimeout(max_seconds,TimeUnit.SECONDS);
	}
	
	public boolean done() {
		return completable.isDone();
	}
	
	public boolean cancelled() {
		return completable.isCancelled();
	}
	
	public boolean completedExceptionally() {
		return completable.isCompletedExceptionally();
	}
	
	public boolean join() {
		try {
			completable.join();
			return !(completable.isCancelled()||completable.isCompletedExceptionally());
		} catch(Exception e) {
			return false;
		}
	}

}
