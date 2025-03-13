package com.gmail.berndivader.biene.http.post;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.w3c.dom.Document;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Utils;

public 
class 
PostPictureCreate 
extends
PostTask
{

	public PostPictureCreate(String url, HttpEntity entity) {
		super(url,entity);

		this.start();
	}

	@Override
	public void _completed(HttpResponse response) {
		Document xml=Utils.XML.getXMLDocument(response);
		if(xml!=null) {
			Map<String,String>result=Utils.XML.map(xml);
			Utils.XML.printOut("",xml.getChildNodes());
			Logger.$(result.get("CODE")+":"+result.get("MESSAGE")+":"+result.get("ACTION"),false,false);
		} else {
			_failed(response);
		}
	}

	@Override
	public void _failed(HttpResponse response) {
		failed=true;
		Logger.$("failed",false,false);
	}

	@Override
	protected void max_seconds(long max) {
		max_seconds=60l;
	}

}
