package com.gmail.berndivader.biene.http.post;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
PostImageUpload
extends
PostTask
{
	private File file;
	private ZeroCopyPost post;

	public PostImageUpload(String url,File file) throws FileNotFoundException {
		super(url,null);
		
		this.file=file;
		
		String file_name=file.getName();
				
		String[]parse=file_name.split("\\.",-1);
		if(parse.length>1) {
			file_name=parse[0]+"."+parse[1].toLowerCase();
		} else if(parse.length==1){
			file_name=parse[0]+".jpg";
		}
		
		post=new ZeroCopyPost(url+Tasks.HTTP_POST_IMAGE_UPLOAD.command()+"&file_name="+file_name+"&file_size="+file.length(),file,ContentType.IMAGE_JPEG) {
			
			@Override
			protected HttpEntityEnclosingRequest createRequest(final URI requestURI,final HttpEntity entity) {
				HttpEntityEnclosingRequest request=super.createRequest(requestURI,entity);
				if(Config.data.cf_enabled()) {
					request.setHeader("CF-Access-Client-Id",Config.data.cf_client());
					request.setHeader("CF-Access-Client-Secret",Config.data.cf_secret());
				}
				request.setHeader("user",Config.data.shop_user());
				request.setHeader("password",Config.data.shop_password());
								
				return request;
			}
			
		};
		
		this.start();
	}
	
	@Override
	public HttpResponse call() throws Exception {
		Future<HttpResponse>future=execute(post);
		this.took();
		try {
			return future.get(max_seconds,TimeUnit.SECONDS);			
		} catch(TimeoutException e) {
			future.cancel(true);
		}
		return null;
	}
	
	protected Future<HttpResponse>execute(ZeroCopyPost post) {
		
		return Helper.client.execute(post,new BasicAsyncResponseConsumer(),new FutureCallback<HttpResponse>() {
			
			@Override
			public void failed(Exception e) {
				failed=true;
				Logger.$(e,false,true);
				Logger.$(command+" failed.\nFehlermeldung: "+e.getMessage(),false,false);
				latch.countDown();
			}
			@Override
			public void completed(HttpResponse respond) {
				if(!failed) {
					_completed(respond);
				}
				latch.countDown();
			}
			@Override
			public void cancelled() {
				failed=true;
				Logger.$(command+" cancelled",false,false);
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
				Utils.XML.printError(result);
				failed=true;
			} else {
				String file_name=result.get("OUTCOME");
				Logger.$("Image-Upload von "+file_name+" "+result.get("MESSAGE"),false,false);
				try {
					Files.delete(this.file.toPath());
				} catch (IOException e) {
					Logger.$(e);
				}
			}
		} else {
			this.failed=true;
			Logger.$("Image-Upload hat ungewöhnlich geantwortet.",false,true);
		}
	}

	@Override
	public void _failed(HttpResponse response) {
	}

	@Override
	protected void max_seconds(long max) {
		max_seconds=3l*60l;
	}

}
