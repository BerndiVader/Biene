package com.gmail.berndivader.biene.http.post;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
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
PostUploadCSV
extends
PostTask
{
	
	File response_file;
	ZeroCopyPost post;
	ZeroCopyConsumer<HttpResponse>consumer;

	public PostUploadCSV(String url, File file) throws FileNotFoundException {
		super(url,null);
		
		response_file=new File("_tmp");
		response_file.deleteOnExit();
		post=new ZeroCopyPost(url+"&action="+Tasks.HTTP_POST_UPLOAD_CSV_FILE.action()+"&file_name="+file.getName()+"&file_size="+file.length(),file,ContentType.TEXT_PLAIN) {
			
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
					Logger.$(PostUploadCSV.this.command+" failed.\nFehlermeldung: "+response.getStatusLine(),false,true);
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
		return future.get(5,TimeUnit.MINUTES);
	}
	
	protected Future<HttpResponse>execute(ZeroCopyPost post) {
		
		return Helper.client.execute(post,consumer,new FutureCallback<HttpResponse>() {
			
			@Override
			public void failed(Exception e) {
				PostUploadCSV.this.failed=true;
				Logger.$(e, false,true);
				Logger.$(PostUploadCSV.this.command+" failed.\nFehlermeldung: "+e.getMessage(),false,true);
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
				PostUploadCSV.this.failed=true;
				Logger.$(PostUploadCSV.this.command+" cancelled",false,true);
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
			Logger.$("CSV-Upload von "+file_name+" "+result.get("MESSAGE"),false,false);
		} else {
			Logger.$("CSV-Upload hat ungewöhnlich geantwortet.",false,true);
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
