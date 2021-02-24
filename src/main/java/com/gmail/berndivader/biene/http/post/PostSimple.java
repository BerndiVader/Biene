package com.gmail.berndivader.biene.http.post;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

public 
class 
PostSimple 
extends
PostTask
{

	public PostSimple(String url, HttpEntity entity) {
		super(url, entity);
		this.start();
	}

	@Override
	public void _completed(HttpResponse response) {
		
	}

	@Override
	public void _failed(HttpResponse response) {
	}


}
