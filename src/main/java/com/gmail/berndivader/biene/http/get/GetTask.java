package com.gmail.berndivader.biene.http.get;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Worker;
import com.gmail.berndivader.biene.config.Config;
import com.gmail.berndivader.biene.enums.Tasks;
import com.gmail.berndivader.biene.Helper;

public 
abstract 
class 
GetTask
extends
Worker
implements
Callable<HttpResponse>,
IGetTask
{
	protected final String url;
	protected final Tasks command;
	protected final HttpGet request;
	
	public final CountDownLatch latch;
	public Future<HttpResponse>future;
	public boolean failed;
	
	public GetTask(String url,Tasks command) {
		super();
		if(!Helper.client.isRunning()) Helper.client.start();
		this.url=url;
		this.command=command;
		latch=new CountDownLatch(1);
		request=new HttpGet(url.concat(command.get()));
		if(Config.data.cf_enabled()) {
			request.setHeader("CF-Access-Client-Id",Config.data.cf_client());
			request.setHeader("CF-Access-Client-Secret",Config.data.cf_secret());
		}
		request.setHeader("X-Authorization",Config.data.bearer_token());
		if(Helper.client.isRunning()) {
			future=Helper.executor.submit(this);
		} else {
			latch.countDown();
			Logger.$(request.getRequestLine()+" failed. http_client not running.",false,false);
		}
	}
	
	@Override
	public HttpResponse call() throws Exception {
		Future<HttpResponse>future=this.execute(request);
		try {
			return future.get(15,TimeUnit.SECONDS);
		} catch(TimeoutException e) {
			future.cancel(true);
			Logger.$("Task timeout.");
		}
		return null;
	}
	
	protected Future<HttpResponse>execute(HttpGet request){
		return Helper.client.execute(request,new FutureCallback<HttpResponse>() {
			@Override
			public void failed(Exception e) {
				GetTask.this.failed=true;
				Logger.$(e,false,false);
				Logger.$(command.action().concat(" failed."),false,false);
				latch.countDown();
			}
			@Override
			public void completed(HttpResponse respond) {
				GetTask.this.failed=false;
				latch.countDown();
			}
			@Override
			public void cancelled() {
				GetTask.this.failed=true;
				Logger.$(command.action().concat(" cancelled."),false,false);
				latch.countDown();
			}
		});
	}
	
}
