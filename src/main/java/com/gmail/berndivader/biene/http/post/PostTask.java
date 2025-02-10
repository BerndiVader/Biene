package com.gmail.berndivader.biene.http.post;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Worker;
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
		super();
		if(!Helper.client.isRunning()) Helper.client.start();
		failed=false;
		this.url=url;
		this.command=Tasks.VARIOUS;
		latch=new CountDownLatch(1);
		post=new HttpPost(url);
	}
	
	public PostTask(String url,HttpEntity entity) {
		super();
		if(!Helper.client.isRunning()) Helper.client.start();
		failed=false;
		this.url=url;
		this.entity=entity;
		this.command=Tasks.VARIOUS;
		latch=new CountDownLatch(1);
		post=new HttpPost(url);
		post.setEntity(this.entity);
	}

	protected void start() {
		if(Helper.client.isRunning()) {
			Helper.executor.submit(this);
		} else {
			Logger.$(this.command+" failed. http_client not running.",false,false);
			failed=true;
			latch.countDown();
		}
	}
	
	@Override
	public HttpResponse call() throws Exception {
		future=this.execute(post);
		this.took();
		return future.get(10,TimeUnit.MINUTES);
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
	
	protected static Map<String,String>mapNodes(String node_name,NodeList nodes,Map<String,String>result) {
		int size=nodes.getLength();
		for(int i1=0;i1<size;i1++) {
			Node node=nodes.item(i1);
			if(node.hasChildNodes()) {
				mapNodes(node.getNodeName(),node.getChildNodes(),result);
			} else if(node.getNodeType()==3) {
				result.put(node_name,node.getTextContent().trim());
			};
		}
		return result;
	}
	
}
