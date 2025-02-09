package com.gmail.berndivader.biene.http.get;

import java.io.File;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.w3c.dom.Document;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Utils;
import com.gmail.berndivader.biene.enums.Tasks;
import com.gmail.berndivader.biene.http.Helper;

public 
class
GetProducts
extends
GetTask
{

	public GetProducts(String url, Tasks command) {
		super(url, command);
	}
	
	@Override
	public HttpResponse call() throws Exception {
		Future<HttpResponse>future=this.execute(request);
		latch.await(10,TimeUnit.MINUTES);
		this.took();
		return future.get();
	}
	
	@Override
	protected Future<HttpResponse>execute(HttpGet request){
		return Helper.client.execute(request,new FutureCallback<HttpResponse>() {
			
			@Override
			public void failed(Exception e) {
				GetProducts.this.failed=true;
				Logger.$(GetProducts.this.command+" failed",false,false);
				latch.countDown();
			}
			
			@Override
			public void completed(HttpResponse respond) {
				
				boolean error=false;
				GetProducts.this.failed=false;
				Document xml=Utils.XML.getXMLDocument(((HttpResponse)respond));
				Transformer transformer=null;
				
				try {
					transformer=TransformerFactory.newInstance().newTransformer();
				} catch (TransformerConfigurationException e) {
					Logger.$(GetProducts.this.command+" ERROR:"+e.getMessage(),false,true);
					Logger.$(e);
					error=true;
				} catch (TransformerFactoryConfigurationError e) {
					Logger.$(GetProducts.this.command+" ERROR:"+e.getMessage(),false,true);
					Logger.$(e.getException());
					error=true;
				}
				
				Result output=new StreamResult(new File(Utils.working_dir+"/products.xml"));
				Source input=new DOMSource(xml);
				
				if(transformer!=null) {
					try {
						transformer.transform(input,output);
					} catch (TransformerException e) {
						Logger.$(GetProducts.this.command+" ERROR:"+e.getMessage(),false,true);
						Logger.$(e);
						error=true;
					}
				}
				Logger.$(GetProducts.this.command+(error?" failed":" completed"),false,false);
				latch.countDown();
			}
			
			@Override
			public void cancelled() {
				Logger.$(GetProducts.this.command+" cancelled",false,false);
				GetProducts.this.failed=false;
				latch.countDown();
			}
		});
	}

	@Override
	public void _completed(HttpResponse response) {
		Document xml=Utils.XML.getXMLDocument(((HttpResponse)response));
		Utils.XML.printOut("",xml.getChildNodes());
	}

	@Override
	public void _failed(HttpResponse response) {
		Logger.$("failed",false);
	}
}
