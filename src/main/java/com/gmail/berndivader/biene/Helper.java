package com.gmail.berndivader.biene;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

import com.gmail.berndivader.biene.http.get.GetInfo;

public 
class
Helper
{
	public static final CloseableHttpAsyncClient client;
	public static final ThreadPoolExecutor executor;
    public static final ScheduledExecutorService scheduler;
	
	static {
		executor=(ThreadPoolExecutor)Executors.newCachedThreadPool();
		scheduler=Executors.newScheduledThreadPool(1);
		client=HttpAsyncClients.createDefault();
		client.start();
	}
	
	public static void init() {
		if(client.isRunning()) {
			Logger.$("HTTP Client gestartet.",false,false);
			new GetInfo();
		} else {
			Logger.$("HTTP Client konnte nicht gestartet werden.",false);
		}
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
