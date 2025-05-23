package com.gmail.berndivader.biene.http.post;

import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.w3c.dom.Document;

import com.gmail.berndivader.biene.enums.Tasks;
import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Utils;

public 
class 
PostValidateImage 
extends
PostTask
{
	private final String file_name;

	public PostValidateImage(String url,String file_name) {
		super(url);
		
		this.file_name=file_name;
		MultipartEntityBuilder builder=MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addPart("image_name",new StringBody(file_name,ContentType.MULTIPART_FORM_DATA));
		builder.addPart("action",new StringBody(Tasks.HTTP_POST_IMAGE_VALIDATE.action(),ContentType.MULTIPART_FORM_DATA));
		
		post.setEntity(builder.build());
		start();
	}

	@Override
	public void _completed(HttpResponse response) {
		Document xml=Utils.XML.getXMLDocument(response);
		if(xml!=null) {
			Map<String,String>result=Utils.XML.map(xml);
			if(result.get("CODE").equals("-1")) {
				Logger.$("Achtung! Bild "+file_name+" kann keinem Artikel zugeordnet werden.",true,false);
			}
		} else {
			Logger.$("Validate-Image hat ungewöhnlich geantwortet.",false,true);
		}
	}

	@Override
	public void _failed(HttpResponse response) {
	}

	@Override
	protected void max_seconds(long max) {
		this.max_seconds=60l;
		
	}


}
