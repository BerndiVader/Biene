package com.gmail.berndivader.biene.http.post;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

public
abstract
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
	public abstract void _completed(HttpResponse response);

	@Override
	public abstract void _failed(HttpResponse response);

	@Override
	protected abstract void max_seconds(long max);


}
