package com.gmail.berndivader.biene.http.post;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Worker;
import com.gmail.berndivader.biene.config.Config;
import com.gmail.berndivader.biene.enums.Tasks;
import com.gmail.berndivader.biene.Helper;

public
abstract 
class 
PostTask
extends
Worker
implements
Callable<HttpResponse>,
IPostTask
{
	
	protected String url;
	protected HttpEntity entity;
	public final CountDownLatch latch;
	public Future<HttpResponse>future;
	protected final HttpPost post;
	protected final Tasks command;
	
	public boolean failed;
	
	public PostTask(String url) {
		this(url,null,1);
	}
	
	public PostTask(String url,HttpEntity entity) {
		this(url,entity,1);
	}
	
	public PostTask(String url,HttpEntity entity,int latchCount) {
		super();
		if(!Helper.client.isRunning()) Helper.client.start();
		failed=false;
		this.url=url;
		this.entity=entity;
		this.command=Tasks.VARIOUS;
		latch=new CountDownLatch(latchCount);
		post=new HttpPost(url);
		if(Config.data.cf_enabled()) {
			post.setHeader("CF-Access-Client-Id",Config.data.cf_client());
			post.setHeader("CF-Access-Client-Secret",Config.data.cf_secret());
		}
		post.setHeader("X-Authorization",Config.data.bearer_token());
		if(entity!=null) post.setEntity(this.entity);
	}

	protected void start() {
		if(Helper.client.isRunning()) {
			future=Helper.executor.submit(this);
		} else {
			Logger.$(this.command+" failed. http_client not running.",false,false);
			failed=true;
			latch.countDown();
		}
	}
	
	@Override
	public HttpResponse call() throws Exception {
		Future<HttpResponse>future=this.execute(post);
		HttpResponse response=null;
		
		try {
			response=future.get(max_seconds,TimeUnit.SECONDS);
		} catch(TimeoutException e) {
			future.cancel(true);
		}
		
		this.took();
		return response;
	}
	
	protected Future<HttpResponse> execute(HttpPost post) {
		
		return Helper.client.execute(post,new FutureCallback<HttpResponse>() {
			@Override
			public void failed(Exception e) {
				PostTask.this.failed=true;
				Logger.$(e,false,false);
				Logger.$(PostTask.this.command+" failed.\nFehlermeldung: "+e.getMessage(),false,false);
				_failed(null);
				latch.countDown();
			}
			@Override
			public void completed(HttpResponse respond) {
				PostTask.this.failed=false;
				_completed(respond);
				latch.countDown();
			}
			@Override
			public void cancelled() {
				PostTask.this.failed=true;
				Logger.$(PostTask.this.command+" cancelled",false,false);
				_failed(null);
				latch.countDown();
			}
		});		
	}
	
}
