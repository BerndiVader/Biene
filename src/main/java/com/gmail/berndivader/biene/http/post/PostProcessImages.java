package com.gmail.berndivader.biene.http.post;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.w3c.dom.Document;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Utils;
import com.gmail.berndivader.biene.enums.ActionEnum;

public 
class
PostProcessImages
extends
PostTask
{
	public boolean more=false;
	
	public PostProcessImages(String url) {
		super(url, null);
		
		MultipartEntityBuilder builder=MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addPart("action",new StringBody(ActionEnum.UPDATE_PICTURES.action(),ContentType.MULTIPART_FORM_DATA));
		this.entity=builder.build();
		this.post.setEntity(this.entity);
		
		this.start();
	}

	@Override
	public void _completed(HttpResponse response) {
		Document xml=Utils.getXMLDocument(response);
		if(xml!=null) {
			Map<String,String>result=mapNodes("",xml.getChildNodes(),new HashMap<String,String>());
			more=!result.get("MESSAGE").equals("0");
			Logger.$(result.get("OUTCOME"));
		} else {
			Logger.$("Image-Prozess hat ungewöhnlich geantwortet.",false,true);
			_failed(response);
			more=false;
		}
	}

	@Override
	public void _failed(HttpResponse response) {
		failed=true;
		more=false;
	}

}
