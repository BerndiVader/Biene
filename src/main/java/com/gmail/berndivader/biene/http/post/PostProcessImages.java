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
PostProcessImages
extends
PostTask
{
	public boolean more=false;
	
	public PostProcessImages(String url) {
		super(url,null);
		
		MultipartEntityBuilder builder=MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addPart("action",new StringBody(Tasks.HTTP_POST_UPDATE_PICTURES.action(),ContentType.MULTIPART_FORM_DATA));
		post.setEntity(builder.build());
		start();
	}

	@Override
	public void _completed(HttpResponse response) {
		Document xml=Utils.XML.getXMLDocument(response);
		if(xml!=null) {
			HashMap<String,String>result=Utils.XML.map(xml);
			CODES code=CODES.from(result.get("CODE"));
			if(Utils.XML.isError(result)) {
				Utils.XML.printError(result);
				failed=true;
			} else if(code.equals(CODES.OK)) {
				more=false;
				Logger.$(result.get("OUTCOME"));
			} else if(code.equals(CODES.CONTINUE)) {
				more=true;
				Logger.$(result.get("OUTCOME"));
			} else {
				more=false;
				failed=true;
				Logger.$("Image-Prozess hat ungewöhnlich geantwortet",false,true);
			}
		} else {
			Logger.$("Image-Prozess hat mit NULL geantwortet.",false,true);
			more=false;
			failed=true;
		}
		this.latch.countDown();
	}

	@Override
	public void _failed(HttpResponse response) {
		failed=true;
		more=false;
		this.latch.countDown();
	}

	@Override
	protected void max_seconds(long max) {
		this.max_seconds=60l;
	}

}
