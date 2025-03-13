package com.gmail.berndivader.biene.db;

import java.io.File;
import java.util.concurrent.TimeUnit;

import com.gmail.berndivader.biene.config.Config;
import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.http.post.PostImageUpload;
import com.gmail.berndivader.biene.http.post.PostProcessImagesSync;

public 
class 
UpdatePicturesTask 
extends
QueryBatchTask
{
	
	static String start_info="-- Starte Bilder-Update Task %s...";
	static String scheduled_info="-- Scheduled Bilder-Update Task %s...";
	static String ende_error="-- Bilder-Update Task %s abgeschlossen aber fehlerhaft.";
	static String ende_ok="-- Bilder-Update Task %s erfolgreich beendet.";

	public UpdatePicturesTask() {
		super(1);
		
		this.add();
		Logger.$(String.format(scheduled_info,this.uuid.toString()),false,false);
	}
	
	
	@Override
	public Boolean call() throws Exception {
		Logger.$(String.format(start_info,this.uuid.toString()),false,true);
		
		boolean failed=false;
		
		File folder=new File("Bilder/");
		File[]files=folder.listFiles();
		for(File file:files) {
			PostImageUpload upload=new PostImageUpload(Config.data.http_string(),file);
			upload.latch.await(upload.max_seconds,TimeUnit.SECONDS);
			failed=upload.failed;
		}
		
		boolean repeat=true;
		while(repeat) {
			PostProcessImagesSync process=new PostProcessImagesSync(Config.data.http_string());
			if(process.join()) {
				if(process.failed) break;
				repeat=process.more;
			} else {
				break;
			}
		}
		
		if(failed) {
			failed(null);
		} else {
			completed(null);
		}
		
		latch.countDown();
		return true;
	}

	@Override
	public void completed(Void result) {
		Logger.$(String.format(ende_ok,this.uuid.toString()),false,true);
	}

	@Override
	public void failed(Void result) {
		Logger.$(String.format(ende_error,this.uuid.toString()),false,true);
	}

	/**
	 * Worker timeout to 3 minutes.
	 */
	@Override
	protected void max_seconds(long max) {
		this.max_seconds=3l*60l;
	}

}
