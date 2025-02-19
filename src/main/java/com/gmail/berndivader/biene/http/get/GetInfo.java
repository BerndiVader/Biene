package com.gmail.berndivader.biene.http.get;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.w3c.dom.Document;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Utils;
import com.gmail.berndivader.biene.config.Config;
import com.gmail.berndivader.biene.enums.Tasks;

public 
class
GetInfo
extends
GetTask
{

	public GetInfo() {
		super(Config.data.http_string(),Tasks.HTTP_GET_VERSION);
	}
	
	@Override
	public HttpResponse call() throws Exception {
		Future<HttpResponse>future=this.execute(request);
		boolean completed=latch.await(10,TimeUnit.SECONDS);
		HttpResponse response=future.get();
		
		if(!completed||this.failed) {
			_failed(response);
		} else {
			_completed(response);
		}
		
		this.took();
		return response;
	}

	@Override
	public void _completed(HttpResponse response) {
		Document xml=Utils.XML.getXMLDocument(response);
		if(xml!=null) {
			int code=Integer.parseInt(xml.getElementsByTagName("CODE").item(0).getFirstChild().getNodeValue());
			if(code==111) {
				Logger.$("Shop-Script-Version: "+xml.getElementsByTagName("SCRIPT_VER").item(0).getFirstChild().getNodeValue(),false,true);
				Logger.$("Shop-Script-Datum: "+xml.getElementsByTagName("SCRIPT_DATE").item(0).getFirstChild().getNodeValue(),false,true);
			} else {
				Logger.$("Login zur Shop-Datenbank fehlgeschlagen! Error-Code: "+code,false);
				Logger.$("Message: "+xml.getElementsByTagName("MESSAGE").item(0).getFirstChild().getNodeValue(),false,false);
			}
		} else {
			Logger.$("Keine Verbindung zur Shop-Datenbank!",false,false);
		}
	}

	@Override
	public void _failed(HttpResponse response) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void max_minutes(long max) {
		this.max_minutes=max;
	}
}
