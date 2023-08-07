package com.gmail.berndivader.biene.http.post;

import java.util.HashMap;
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
import com.gmail.berndivader.biene.config.Config;
import com.gmail.berndivader.biene.enums.ActionEnum;

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
		builder.addPart("user",new StringBody(Config.data.getShopUser(),ContentType.MULTIPART_FORM_DATA));
		builder.addPart("password",new StringBody(Config.data.getShopPassword(),ContentType.MULTIPART_FORM_DATA));
		builder.addPart("action",new StringBody(ActionEnum.IMPORT_CSV_FILE.action(),ContentType.MULTIPART_FORM_DATA));
		entity=builder.build();
		post.setEntity(entity);
		start();
		
	}
	public PostImportCSV(String url, HttpEntity entity) {
		super(url, entity);
		start();
	}

	@Override
	public void _completed(HttpResponse response) {
		Document xml=Utils.getXMLDocument(response);
		if(xml!=null) {
			Map<String,String>result=mapNodes("",xml.getChildNodes(),new HashMap<String,String>());
			Logger.$("CSV-Import von "+result.get("FILE_NAME")+" "+result.get("MESSAGE"),false,true);
			if(result.get("MESSAGE")==null||!result.get("MESSAGE").equals("OK")) {
				Logger.$("CSV-Import hat mit einem Fehler geantwortet.",false,true);
				Utils.printOut("", xml.getChildNodes());
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

}
