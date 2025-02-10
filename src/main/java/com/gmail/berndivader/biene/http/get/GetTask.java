package com.gmail.berndivader.biene.http.get;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Worker;
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
	final String url;
	final Tasks command;
	final CountDownLatch latch;
	final HttpGet request;
	
	public Future<HttpResponse>future;
	public boolean failed;
	
	public GetTask(String url,Tasks command) {
		super();
		if(!Helper.client.isRunning()) Helper.client.start();
		this.url=url;
		this.command=command;
		latch=new CountDownLatch(1);
		request=new HttpGet(url+command.command());
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
		return future.get(15,TimeUnit.SECONDS);
	}
	
	protected Future<HttpResponse>execute(HttpGet request){
		return Helper.client.execute(request,new FutureCallback<HttpResponse>() {
			@Override
			public void failed(Exception e) {
				GetTask.this.failed=true;
				Logger.$(e,false,false);
				Logger.$(request.getRequestLine()+" failed",false,false);
				latch.countDown();
			}
			@Override
			public void completed(HttpResponse respond) {
				GetTask.this.failed=false;
				latch.countDown();
			}
			@Override
			public void cancelled() {
				Logger.$(request.getRequestLine()+" cancelled",false,false);
				GetTask.this.failed=false;
				latch.countDown();
			}
		});
	}
	
}
