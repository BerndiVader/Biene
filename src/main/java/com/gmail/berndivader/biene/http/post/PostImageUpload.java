package com.gmail.berndivader.biene.http.post;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.client.methods.ZeroCopyConsumer;
import org.apache.http.nio.client.methods.ZeroCopyPost;
import org.w3c.dom.Document;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Utils;
import com.gmail.berndivader.biene.enums.Tasks;
import com.gmail.berndivader.biene.http.Helper;

public 
class
PostImageUpload
extends
PostTask
{
	File response_file,file;
	ZeroCopyPost post;
	ZeroCopyConsumer<HttpResponse>consumer;

	public PostImageUpload(String url, File file) throws FileNotFoundException {
		super(url,null);
		
		this.file=file;
		response_file=new File("_tmp");
		response_file.deleteOnExit();
		
		String file_name=file.getName();
		String[]parse=file_name.split("\\.",-1);
		if(parse.length>1) {
			file_name=parse[0]+"."+parse[1].toLowerCase();
		} else if(parse.length==1){
			file_name=parse[0]+".jpg";
		}
		
		post=new ZeroCopyPost(url+"&action="+Tasks.HTTP_POST_IMAGE_UPLOAD.action()+"&file_name="+file_name+"&file_size="+file.length(),file,ContentType.IMAGE_JPEG) {
			
			@Override
			protected HttpEntityEnclosingRequest createRequest(final URI requestURI,final HttpEntity entity) {
				HttpEntityEnclosingRequest request=super.createRequest(requestURI,entity);
				return request;
			}
			
		};
		
		consumer=new ZeroCopyConsumer<HttpResponse>(response_file) {
			
			@Override
			protected HttpResponse process(HttpResponse response, File file, ContentType content_type) throws Exception {
				if(response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK) {
					failed=true;
					Logger.$(PostImageUpload.this.command+" failed.\nFehlermeldung: "+response.getStatusLine(),false,true);
					throw new ClientProtocolException("Upload failed:"+response.getStatusLine());
				} else {
					failed=false;
				}
				return response;
			}
		};
		
		this.start();
	}
	
	@Override
	public HttpResponse call() throws Exception {
		Future<HttpResponse>future=execute(post);
		this.took();
		return future.get(10,TimeUnit.MINUTES);
	}
	
	protected Future<HttpResponse>execute(ZeroCopyPost post) {
		
		return Helper.client.execute(post,consumer,new FutureCallback<HttpResponse>() {
			
			@Override
			public void failed(Exception e) {
				PostImageUpload.this.failed=true;
				Logger.$(e,false,true);
				Logger.$(PostImageUpload.this.command+" failed.\nFehlermeldung: "+e.getMessage(),false,false);
				_failed(null);
				latch.countDown();
			}
			@Override
			public void completed(HttpResponse respond) {
				if (failed) {
					_failed(null);
				} else {
					_completed(respond);
				}
				latch.countDown();
			}
			@Override
			public void cancelled() {
				PostImageUpload.this.failed=true;
				Logger.$(PostImageUpload.this.command+" cancelled",false,false);
				_failed(null);
				latch.countDown();
			}
		});		
	}

	@Override
	public void _completed(HttpResponse response) {
		Document xml=Utils.XML.getXMLDocument(response);
		if(xml!=null) {
			Map<String,String>result=mapNodes("",xml.getChildNodes(),new HashMap<String,String>());
			String file_name=result.get("OUTCOME");
			Logger.$("Image-Upload von "+file_name+" "+result.get("MESSAGE"),false,false);
			try {
				Files.delete(this.file.toPath());
			} catch (IOException e) {
				Logger.$(e);
			}
		} else {
			Logger.$("Image-Upload hat ungewöhnlich geantwortet.",false,true);
			_failed(response);
		}
		response_file.delete();
	}

	@Override
	public void _failed(HttpResponse response) {
		failed=true;
		response_file.delete();
	}

}
