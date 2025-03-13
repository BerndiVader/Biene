package com.gmail.berndivader.biene.http.post;

import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.w3c.dom.Document;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Utils;

public 
class 
PostPictureCreateSync 
extends
PostTaskSync
{

	public PostPictureCreateSync(String url, HttpEntity entity) {
		super(url,entity);

		completable=start().thenAccept(this::_completed).exceptionally(e->{
			failed=false;
			Logger.$(e);
			return null;
		});
		
	}

	@Override
	public void _completed(HttpResponse response) {
		if(!failed) {
			Document xml=Utils.XML.getXMLDocument(response);
			if(xml!=null) {
				HashMap<String,String>result=Utils.XML.map(xml);
				if(!Utils.XML.isError(result)) {
					Utils.XML.printOut("",xml.getChildNodes());
					Logger.$(result.get("CODE")+":"+result.get("MESSAGE")+":"+result.get("ACTION"),false,false);
				} else {
					failed=true;
					Utils.XML.printError(result);
				}
			} else {
				failed=true;
				Logger.$("Die Verarbeitung der Bilder am Server ist fehlgeschlagen.");
			}
		}
	}

	@Override
	public void _failed(HttpResponse response) {
	}

	@Override
	protected void max_seconds(long max) {
		max_seconds=60l;
	}

}
