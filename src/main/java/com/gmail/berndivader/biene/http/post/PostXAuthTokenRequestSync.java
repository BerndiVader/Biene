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
import com.gmail.berndivader.biene.config.Config;

public 
class 
PostXAuthTokenRequestSync
extends
PostTaskSync
{

	public PostXAuthTokenRequestSync() {
		super(Config.data.http_string());
		
		post.removeHeaders("X-Authorization");
		post.setHeader("X-User",Config.data.shop_user());
		post.setHeader("X-Password",Config.data.shop_password());
		post.setEntity(
			MultipartEntityBuilder.create()
					.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
					.addPart("action",new StringBody(Tasks.HTTP_POST_XAUTH_TOKEN_REQUEST.action(),ContentType.MULTIPART_FORM_DATA))
					.build()
		);
		
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
				} else if(result.get("CODE").equals(CODES.OK.asStr())) {
					String token=result.get("TOKEN");
					Config.data.bearer_token(token);
					Config.saveConfig();
					Logger.$("XAuth Token erfolgfreich aktualisiert.");
				}
			} else {
				Logger.$("XAuth Token Request hat ungewöhnlich geantwortet.",false,true);
			}
		}
	}

	@Override
	public void _failed(HttpResponse response) {
	}

	@Override
	protected void max_seconds(long max) {
		this.max_seconds=30l;
	}


}
