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
PostValidateImageFileSync 
extends
PostTaskSync
{
	private String name;

	public PostValidateImageFileSync(String url,String image_name) {
		super(url);
		
    	if(image_name.toLowerCase().contains(".jpg")) {
    		image_name=image_name.substring(0,image_name.length()-4)+".jpg";
    	}
		name=image_name;
		MultipartEntityBuilder builder=MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addPart("image_name",new StringBody(image_name,ContentType.MULTIPART_FORM_DATA));
		builder.addPart("action",new StringBody(Tasks.HTTP_POST_IMAGE_VALIDATE_FILE.action(),ContentType.MULTIPART_FORM_DATA));
		
		post.setEntity(builder.build());
		
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
					CODES code=CODES.from(result.get("CODE"));
					if(code.equals(CODES.FAILED)) {
						Logger.$("Achtung! Keine Bild-Datei "+name+" am Server gefunden.",false,false);
					}
				} else {
					failed=true;
					Utils.XML.printError(result);
				}
			} else {
				Logger.$("Validate-Image-File hat ungewöhnlich geantwortet.",false,true);
			}
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
