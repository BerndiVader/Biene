package com.gmail.berndivader.biene.http.post;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Worker;
import com.gmail.berndivader.biene.config.Config;
import com.gmail.berndivader.biene.enums.Tasks;
import com.gmail.berndivader.biene.Helper;

public
abstract 
class 
PostTaskSync
extends
Worker
implements
Callable<HttpResponse>,
IPostTask
{
	
	protected String url;
	protected HttpEntity entity;
	
	protected CompletableFuture<Void>completable;
	protected final HttpPost post;
	protected final Tasks command;
	
	public boolean failed;
	
	public PostTaskSync(String url) {
		this(url,null);
	}
	
	public PostTaskSync(String url,HttpEntity entity) {
		super();
		failed=false;
		this.url=url;
		this.entity=entity;
		this.command=Tasks.VARIOUS;
		post=new HttpPost(url);
		if(Config.data.cf_enabled()) {
			post.setHeader("CF-Access-Client-Id",Config.data.cf_client());
			post.setHeader("CF-Access-Client-Secret",Config.data.cf_secret());
		}
		post.setHeader("X-Authorization",Config.data.bearer_token());
		if(entity!=null) post.setEntity(this.entity);
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
		
	@Override
	public HttpResponse call() throws Exception {
		HttpResponse response=Helper.syncClient.execute(post);
		int raw=response.getStatusLine().getStatusCode();
		if(raw>199&&300>raw) {
			return response;
		} else {
			throw new RuntimeException(String.format("%s : %d - %s",command.action(),raw,response.getStatusLine().getReasonPhrase()));
		}
	}
	
	public boolean isDone() {
		return completable.isDone();
	}
	
	public boolean isCancelled() {
		return completable.isCancelled();
	}
	
	public boolean isDoneExceptionally()  {
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
