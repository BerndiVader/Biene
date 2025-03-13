package com.gmail.berndivader.biene.http.post;

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
import com.gmail.berndivader.biene.enums.Tasks;

public 
class
PostImportCSVSync
extends
PostTaskSync
{	
	public PostImportCSVSync(String url, String file_name) {
		super(url);
		
		MultipartEntityBuilder builder=MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addPart("file_name",new StringBody(file_name,ContentType.MULTIPART_FORM_DATA));
		builder.addPart("action",new StringBody(Tasks.HTTP_POST_IMPORT_CSV_FILE.action(),ContentType.MULTIPART_FORM_DATA));
		entity=builder.build();
		post.setEntity(entity);
		
		completable=start().thenAccept(this::_completed).exceptionally(e->{
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
					Logger.$("CSV-Import von "+result.get("FILE_NAME")+" "+result.get("MESSAGE"),false,true);
					if(result.get("CODE").equals(CODES.FAILED.asStr())) {
						failed=true;
						Logger.$("CSV-Import hat mit einem Fehler geantwortet.",false,true);
						Utils.XML.printOut("", xml.getChildNodes());
					}
				} else {
					failed=true;
					Utils.XML.printError(result);
				}
			} else {
				Logger.$("CSV-Import hat ungewöhnlich geantwortet.",false,true);
				failed=true;
			}
		}
	}

	@Override
	public void _failed(HttpResponse response) {
	}
	@Override
	protected void max_seconds(long max) {
		max_seconds=2l*60l;
	}

}
