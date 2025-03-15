package com.gmail.berndivader.biene.http.post;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.client.methods.ZeroCopyPost;
import org.apache.http.nio.protocol.BasicAsyncResponseConsumer;
import org.w3c.dom.Document;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Utils;
import com.gmail.berndivader.biene.config.Config;
import com.gmail.berndivader.biene.enums.Tasks;
import com.gmail.berndivader.biene.Helper;

public 
class
PostUploadCSV
extends
PostTask
{
	
	ZeroCopyPost post;

	public PostUploadCSV(String url, File file) throws FileNotFoundException {
		super(url,null);
		
		post=new ZeroCopyPost(url+Tasks.HTTP_POST_UPLOAD_CSV_FILE.get()+"&file_name="+file.getName()+"&file_size="+file.length(),file,ContentType.TEXT_PLAIN) {
			
			@Override
			protected HttpEntityEnclosingRequest createRequest(final URI requestURI,final HttpEntity entity) {
				HttpEntityEnclosingRequest request=super.createRequest(requestURI,entity);
				if(Config.data.cf_enabled()) {
					request.setHeader("CF-Access-Client-Id",Config.data.cf_client());
					request.setHeader("CF-Access-Client-Secret",Config.data.cf_secret());
				}
				request.setHeader("X-Authorization",Config.data.bearer_token());
				return request;
			}
			
		};
		
		start();
	}
	
	@Override
	public HttpResponse call() throws Exception {
		Future<HttpResponse>future=execute(post);
		this.took();
		return future.get(5,TimeUnit.MINUTES);
	}
	
	protected Future<HttpResponse>execute(ZeroCopyPost post) {
		
		return Helper.client.execute(post,new BasicAsyncResponseConsumer(),new FutureCallback<HttpResponse>() {
			
			@Override
			public void failed(Exception e) {
				failed=true;
				Logger.$(e,false,true);
				Logger.$(command+" failed.\nFehlermeldung: "+e.getMessage(),false,true);
				latch.countDown();
			}
			@Override
			public void completed(HttpResponse respond) {
				if (!failed) {
					_completed(respond);
				}
				latch.countDown();
			}
			@Override
			public void cancelled() {
				failed=true;
				Logger.$(command+" cancelled",false,true);
				latch.countDown();
			}
		});		
	}

	@Override
	public void _completed(HttpResponse response) {
		Document xml=Utils.XML.getXMLDocument(response);
		if(xml!=null) {
			HashMap<String,String>result=Utils.XML.map(xml);
			if(Utils.XML.isError(result)) {
				failed=true;
				Utils.XML.printError(result);
			} else {
				String file_name=result.get("OUTCOME");
				Logger.$("CSV-Upload von "+file_name+" "+result.get("MESSAGE"),false,false);
			}
		} else {
			failed=true;
			Logger.$("CSV-Upload hat ungewöhnlich geantwortet.",false,true);
		}
	}

	@Override
	public void _failed(HttpResponse response) {
	}

	@Override
	protected void max_seconds(long max) {
		this.max_seconds=2l*60l;
		
	}

}
