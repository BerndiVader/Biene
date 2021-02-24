package com.gmail.berndivader.biene.http.post;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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
import com.gmail.berndivader.biene.enums.ActionEnum;
import com.gmail.berndivader.biene.enums.EventEnum;
import com.gmail.berndivader.biene.http.Helper;

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
	
	String url;
	HttpEntity entity;
	public final CountDownLatch latch;
	public Future<HttpResponse>future;
	final HttpPost post;
	final EventEnum command;
	
	public boolean failed;
	
	public PostTask(String url) {
		super();
		if(!Helper.client.isRunning()) Helper.client.start();
		failed=false;
		this.url=url;
		this.command=EventEnum.HTTP_POST_VARIOUS;
		latch=new CountDownLatch(1);
		post=new HttpPost(url);
	}
	
	public PostTask(String url,HttpEntity entity) {
		super();
		if(!Helper.client.isRunning()) Helper.client.start();
		failed=false;
		this.url=url;
		this.entity=entity;
		this.command=EventEnum.HTTP_POST_VARIOUS;
		latch=new CountDownLatch(1);
		post=new HttpPost(url);
		post.setEntity(this.entity);
	}

	public void start() {
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
		this.took();
		return future.get(10,TimeUnit.MINUTES);
	}
	
	protected Future<HttpResponse> execute(HttpPost post) {
		
		return Helper.client.execute(post,new FutureCallback<HttpResponse>() {
			@Override
			public void failed(Exception e) {
				PostTask.this.failed=true;
				Logger.$(e,false,true);
				Logger.$(PostTask.this.command+" failed.\nFehlermeldung: "+e.getMessage(),false,true);
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
				Logger.$(PostTask.this.command+" cancelled",false,true);
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
	
	public static boolean isValid(Map<String,String>result) {
		if(result.containsKey("ACTION")) {
			ActionEnum action=ActionEnum.ERROR;
			try {
				action=ActionEnum.valueOfIgnoreCase(result.get("ACTION"));
			} catch (IllegalArgumentException ex) {
				Logger.$(ex.getMessage(),false,true);
			}
			return action!=ActionEnum.ERROR;
		}
		return true;
	}
	
	protected static void parseError(Map<String,String>result) {
		Iterator<Map.Entry<String,String>>iter=result.entrySet().iterator();
		while(iter.hasNext()) {
			Entry<String,String>entry=iter.next();
			String key=entry.getKey();
			String value=entry.getValue();
			if(!value.isEmpty()) Logger.$(key+":"+value,false,true);
		}
	}

}
