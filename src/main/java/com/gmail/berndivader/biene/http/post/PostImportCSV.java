package com.gmail.berndivader.biene.http.post;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.w3c.dom.Document;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Utils;
import com.gmail.berndivader.biene.enums.Tasks;

public 
class
PostImportCSV
extends
PostTask
{
	public PostImportCSV(String url, String file_name) {
		super(url);
		
		MultipartEntityBuilder builder=MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addPart("file_name",new StringBody(file_name,ContentType.MULTIPART_FORM_DATA));
		builder.addPart("action",new StringBody(Tasks.HTTP_POST_IMPORT_CSV_FILE.action(),ContentType.MULTIPART_FORM_DATA));
		entity=builder.build();
		post.setEntity(entity);
		start();
		
	}
	
	public PostImportCSV(String url, HttpEntity entity) {
		super(url,entity);
		start();
	}

	@Override
	public void _completed(HttpResponse response) {
		Document xml=Utils.XML.getXMLDocument(response);
		if(xml!=null) {
			Map<String,String>result=Utils.XML.map(xml);
			Logger.$("CSV-Import von "+result.get("FILE_NAME")+" "+result.get("MESSAGE"),false,true);
			if(result.get("MESSAGE")==null||!result.get("MESSAGE").equals("OK")) {
				Logger.$("CSV-Import hat mit einem Fehler geantwortet.",false,true);
				Utils.XML.printOut("", xml.getChildNodes());
				_failed(response);
			}
		} else {
			Logger.$("CSV-Import hat ungewöhnlich geantwortet.",false,true);
			_failed(response);
		}
	}

	@Override
	public void _failed(HttpResponse response) {
		failed=true;
	}
	@Override
	protected void max_seconds(long max) {
		max_seconds=2l*60l;
	}

}
