package com.gmail.berndivader.biene.command.commands;

import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.w3c.dom.Document;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Utils;
import com.gmail.berndivader.biene.Utils.XML.CODES;
import com.gmail.berndivader.biene.command.Command;
import com.gmail.berndivader.biene.command.CommandAnnotation;
import com.gmail.berndivader.biene.config.Config;
import com.gmail.berndivader.biene.http.post.PostSimpleSync;

@CommandAnnotation(name=".export",usage="Export Produkte.")
public class Export extends Command {

	@Override
	public boolean execute(String args) {
		
		MultipartEntityBuilder builder=MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addPart("action",new StringBody("csv_export",ContentType.MULTIPART_FORM_DATA));
		
		return new PostSimpleSync(Config.data.http_string(),builder.build()) {
			
			@Override
			protected void max_seconds(long max) {
				max_seconds=5l;
			}
			
			@Override
			public void _failed(HttpResponse response) {
				
			}
			
			@Override
			public void _completed(HttpResponse response) {
				Document xml=Utils.XML.getXMLDocument(response);
				if(xml!=null) {
					HashMap<String,String>map=Utils.XML.map(xml);
					CODES code=CODES.from(map);
					if(Utils.XML.isError(map)) {
						Utils.XML.printError(map);
					} else if(code==CODES.OK) {
						Logger.$("Artikelexport erfolgreich abgeschlossen.");
					} else {
						Logger.$("Artikelexport fehlerhaft.");
					}
				} else {
					Logger.$("Keine Verbindung zur Shop-Datenbank!",false,false);
				}
			}
		}.join();
		
	}

}
