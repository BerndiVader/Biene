package com.gmail.berndivader.biene.http;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
	public static ExecutorService executor;
	
	static {
		executor=Executors.newFixedThreadPool(10);
		client=HttpAsyncClients.createDefault();
		client.start();
	}
	
	public static void init() {
		if(client.isRunning()) {
			Logger.$("HTTP Client gestartet.",false,true);
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
