package com.gmail.berndivader.biene.http.get;

import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.w3c.dom.Document;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Utils;
import com.gmail.berndivader.biene.Utils.XML.CODES;
import com.gmail.berndivader.biene.config.Config;
import com.gmail.berndivader.biene.enums.Tasks;

public class GetInfoSync extends GetTaskSync {

	public GetInfoSync() {
		super(Config.data.http_string(),Tasks.HTTP_GET_VERSION);
		
		completable=start().thenAccept(response->{
			if(!failed) {
				_completed(response);
			} else {
				_failed(response);
			}
		}).exceptionally(e->{
			failed=true;
			Logger.$(e);
			return null;
		});

	}

	@Override
	public void _completed(HttpResponse response) {
		Document xml=Utils.XML.getXMLDocument(response);
		if(xml!=null) {
			HashMap<String,String>map=Utils.XML.map(xml);
			CODES code=CODES.from(map);
			if(Utils.XML.isError(map)) {
				Utils.XML.printError(map);
			} else if(code==CODES.VERSION) {
				Logger.$(String.format("Shop-Script-Version: %s\nShop-Script-Datum: %s",map.get("SCRIPT_VER"),map.get("SCRIPT_DATE")));
			} else {
				Logger.$(String.format("Login zur Shop-Datenbank fehlgeschlagen!\nError-Code:%s Message:%s\nDetails:%s %s",
						code,map.get("MESSAGE"),map.get("ACTION"),map.getOrDefault("ERROR","No further details.")),false,false);
			}
		} else {
			Logger.$("Keine Verbindung zur Shop-Datenbank!",false,false);
		}
	}

	@Override
	public void _failed(HttpResponse response) {
		
	}

	@Override
	protected void max_seconds(long max) {
		max_seconds=15l;
	}

}
