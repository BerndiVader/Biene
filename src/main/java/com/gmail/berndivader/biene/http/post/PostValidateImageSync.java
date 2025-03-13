package com.gmail.berndivader.biene.http.post;

import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.w3c.dom.Document;

import com.gmail.berndivader.biene.enums.Tasks;
import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Utils;
import com.gmail.berndivader.biene.Utils.XML.CODES;

public 
class 
PostValidateImageSync 
extends
PostTaskSync
{
	private final String file_name;

	public PostValidateImageSync(String url,String file_name) {
		super(url);
		
		this.file_name=file_name;
		MultipartEntityBuilder builder=MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addPart("image_name",new StringBody(file_name,ContentType.MULTIPART_FORM_DATA));
		builder.addPart("action",new StringBody(Tasks.HTTP_POST_IMAGE_VALIDATE.action(),ContentType.MULTIPART_FORM_DATA));
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
				if(Utils.XML.isError(result)) {
					failed=true;
					Utils.XML.printError(result);
				} else if(result.get("CODE").equals(CODES.FAILED.asStr())) {
					Logger.$("Achtung! Bild "+file_name+" kann keinem Artikel zugeordnet werden.",false,false);
				}
			} else {
				Logger.$("Validate-Image hat ungewöhnlich geantwortet.",false,true);
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
