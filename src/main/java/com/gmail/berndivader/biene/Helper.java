package com.gmail.berndivader.biene;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicHttpResponse;

import com.gmail.berndivader.biene.Utils.XML.CODES;
import com.gmail.berndivader.biene.enums.Tasks;
import com.gmail.berndivader.biene.http.get.GetInfoSync;

public 
class
Helper
{
	public static final CloseableHttpAsyncClient client;
	public static final CloseableHttpClient syncClient;
	
	public static final ThreadPoolExecutor executor;
    public static final ScheduledExecutorService scheduler;
    
    public static LinkedHashMap<String,Object>catTree;
	
	static {
		executor=(ThreadPoolExecutor)Executors.newCachedThreadPool();
		scheduler=Executors.newScheduledThreadPool(1);
		
		syncClient=initSyncClient();
		
		client=initHttpClient();
		client.start();
		
		catTree=new LinkedHashMap<String,Object>();
	}
	
	private static CloseableHttpAsyncClient initHttpClient() {
		CloseableHttpAsyncClient client;
		try {
			IOReactorConfig reactConfig=IOReactorConfig.custom()
					.setIoThreadCount(Runtime.getRuntime().availableProcessors())
					.setSoTimeout(300000)
					.build();
			PoolingNHttpClientConnectionManager manager=
					new PoolingNHttpClientConnectionManager(new DefaultConnectingIOReactor(reactConfig));
			manager.setDefaultMaxPerRoute(10);
			manager.setMaxTotal(50);
			RequestConfig requestConfig=RequestConfig.custom()
					.setRedirectsEnabled(false)
					.setSocketTimeout(180000)
					.setCookieSpec(CookieSpecs.IGNORE_COOKIES)
					.build();
			client=HttpAsyncClients.custom()
					.setConnectionManager(manager)
					.setDefaultRequestConfig(requestConfig)
					.build();
		} catch (Exception e) {
			Logger.$(e);
			client=HttpAsyncClients.createDefault();
		}
		
		return client;
	}
	
	private static CloseableHttpClient initSyncClient() {
		CloseableHttpClient client;

		try {
			PoolingHttpClientConnectionManager manager=new PoolingHttpClientConnectionManager();
			manager.setDefaultMaxPerRoute(10);
			manager.setMaxTotal(50);
			
			RequestConfig config=RequestConfig.custom()
					.setRedirectsEnabled(false)
					.setSocketTimeout(180000)
					.setCookieSpec(CookieSpecs.IGNORE_COOKIES)
					.build();
			client=HttpClients.custom()
					.setConnectionManager(manager)
					.setDefaultRequestConfig(config)
					.build();
		} catch(Exception e) {
			client=HttpClients.createDefault();
		}
		
		return client;
	}
	
	public static void init() {
		
		if(client.isRunning()) {
			Logger.$("HTTP Client gestartet.",false,false);
			new GetInfoSync();
		} else {
			Logger.$("HTTP Client konnte nicht gestartet werden.",false);
		}
	}
	
	public static HttpResponse emptyResponse() {
		HttpResponse empty=new BasicHttpResponse(new ProtocolVersion("HTTP",1,1),HttpStatus.SC_NO_CONTENT,"No Content");
		empty.setHeader("Content-type","text/xml");
		String content=String.format(Utils.XML.ERR_TEMPLATE
			,Charset.defaultCharset().name()
			,CODES.JAVA_ERROR.asStr()
			,Tasks.UNKOWN.action()
			,"Default created empty response."
			,CODES.JAVA_ERROR.asStr()
			,"No response from php server."
		);
		StringEntity entity=new StringEntity(content,ContentType.TEXT_XML);
		empty.setEntity(entity);
		return empty;
	}
	
	public static HttpResponse errorResponse(Throwable e) {
		HttpResponse error=new BasicHttpResponse(new ProtocolVersion("HTTP",1,1),HttpStatus.SC_EXPECTATION_FAILED,e.getMessage());
		error.setHeader("Content-type","text/xml");
		
		String content=String.format(Utils.XML.ERR_TEMPLATE
			,Charset.defaultCharset().name()
			,CODES.JAVA_ERROR.asStr()
			,Tasks.UNKOWN.action()
			,"Error occured while performing php server request."
			,CODES.JAVA_ERROR.asStr()
			,e.getMessage()
		);
		StringEntity entity=new StringEntity(content,ContentType.TEXT_XML);
		error.setEntity(entity);
		return error;
	}
	
	public static void refreshCatTree() {
		
	}
		
	public static void close() {
		try {
			if(client!=null) client.close();
		} catch (IOException e) {
			Logger.$(e);
		}
		if(executor!=null) executor.shutdown();
		if(scheduler!=null) schedulerClose();
	}
	
    public static void schedulerClose() {
    	scheduler.shutdown();
    	try {
    		if(!scheduler.awaitTermination(1,TimeUnit.SECONDS)) scheduler.shutdownNow();
    	} catch(InterruptedException e) {
    		scheduler.shutdownNow();
    	}
    }
	
}
