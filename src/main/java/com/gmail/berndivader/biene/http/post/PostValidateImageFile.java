package com.gmail.berndivader.biene.http.post;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.w3c.dom.Document;

import com.gmail.berndivader.biene.config.Config;
import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Utils;
import com.gmail.berndivader.biene.enums.Tasks;

public 
class 
PostValidateImageFile 
extends
PostTask
{
	private String name;

	public PostValidateImageFile(String url,String image_name) {
		super(url);
		
    	if(image_name.toLowerCase().contains(".jpg")) {
    		image_name=image_name.substring(0,image_name.length()-4)+".jpg";
    	}
		name=image_name;
		MultipartEntityBuilder builder=MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addPart("image_name",new StringBody(image_name,ContentType.MULTIPART_FORM_DATA));
		builder.addPart("user",new StringBody(Config.data.getShopUser(),ContentType.MULTIPART_FORM_DATA));
		builder.addPart("password",new StringBody(Config.data.getShopPassword(),ContentType.MULTIPART_FORM_DATA));
		builder.addPart("action",new StringBody(Tasks.HTTP_POST_IMAGE_VALIDATE_FILE.action(),ContentType.MULTIPART_FORM_DATA));
		
		this.post.setEntity(builder.build());
		this.start();
	}

	@Override
	public void _completed(HttpResponse response) {
		Document xml=Utils.XML.getXMLDocument(response);
		if(xml!=null) {
			Map<String,String>result=mapNodes("",xml.getChildNodes(),new HashMap<String,String>());
			if(result.get("CODE").equals("-1")) {
				Logger.$("Achtung! Keine Bild-Datei "+name+" am Server gefunden.",false,false);
			}
		} else {
			Logger.$("Validate-Image-File hat ungewöhnlich geantwortet.",false,true);
		}
	}

	@Override
	public void _failed(HttpResponse response) {
	}


}
