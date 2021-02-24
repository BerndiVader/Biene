package com.gmail.berndivader.biene.db;

import java.io.File;
import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;

import com.gmail.berndivader.biene.config.Config;
import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.http.post.PostImageUpload;
import com.gmail.berndivader.biene.http.post.PostProcessImages;
import com.gmail.berndivader.biene.http.post.PostValidateImage;

public 
class 
UpdatePicturesTask 
extends
QueryBatchTask
{
	
	static String start_info="-- Starte Bilder-Update Task %s...";
	static String scheduled_info="-- Scheduled Bilder-Update Task %s...";
	static String ende_info="-- Beende Bilder-Update Task %s.";
	static String ende_error="-- Bilder-Update Task %s erfolgreich aber fehlerhaft.";
	static String ende_ok="-- Bilder-Update Task %s erfolgreich.";

	public UpdatePicturesTask(String query) {
		super(query);
		
		this.add();
		Logger.$(String.format(scheduled_info,this.uuid.toString()),false,false);
	}
	
	@Override
	public ResultSet call() throws Exception {
		Logger.$(String.format(start_info,this.uuid.toString()),false,true);
		
		boolean failed=false;
		
		File folder=new File("Bilder/");
		File[]files=folder.listFiles();
		for(File file:files) {
			PostImageUpload upload=new PostImageUpload(Config.data.getHttp_string(),file);
			upload.latch.await(5,TimeUnit.MINUTES);
			failed=upload.failed;
			
			if(!upload.failed) {
				new PostValidateImage(Config.data.getHttp_string(),file.getName());
			}
		}
		
		PostProcessImages process=new PostProcessImages(Config.data.getHttp_string());
		process.latch.await(10,TimeUnit.MINUTES);
		
		if(failed||process.failed) {
			failed();
		} else {
			completed();
		}
		
		Logger.$(String.format(ende_info,this.uuid.toString()),false,false);
		latch.countDown();
		return null;
	}

	@Override
	public void completed() {
		Logger.$(String.format(ende_ok,this.uuid.toString()),false,true);
	}

	@Override
	public void failed() {
		Logger.$(String.format(ende_error,this.uuid.toString()),false,true);
	}

}
