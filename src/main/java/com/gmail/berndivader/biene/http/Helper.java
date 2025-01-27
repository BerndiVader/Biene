package com.gmail.berndivader.biene.http;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

import com.gmail.berndivader.biene.config.Config;
import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.enums.EventEnum;
import com.gmail.berndivader.biene.http.get.GetInfo;

public 
class
Helper
{
	public static CloseableHttpAsyncClient client;
	public static ThreadPoolExecutor executor;
	
	static {
		executor=(ThreadPoolExecutor)Executors.newCachedThreadPool();
		
		client=HttpAsyncClients.createDefault();
		client.start();
	}
	
	public static void init() {
		if(client.isRunning()) {
			Logger.$("HTTP Client gestartet.",false,false);
			new GetInfo(Config.data.getHttp_string(),EventEnum.HTTP_GET_VERSION);
		} else {
			Logger.$("HTTP Client konnte nicht gestartet werden.",false);
		}
	}
	
	public static void close() {
		try {
			client.close();
		} catch (IOException e) {
			Logger.$(e);
		}
		executor.shutdown();
	}
	
}
