package com.gmail.berndivader.biene.http.post;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import com.gmail.berndivader.biene.Logger;

public
abstract
class
PostSimpleSync 
extends
PostTaskSync
{

	public PostSimpleSync(String url,HttpEntity entity) {
		super(url,entity);
		
		completable=start().thenAccept(response->{
			if(!failed) {
				_completed(response);
			} else {
				_failed(response);
			}
		}).exceptionally(e->{
			Logger.$(e);
			return null;
		});
	}

	@Override
	public abstract void _completed(HttpResponse response);

	@Override
	public abstract void _failed(HttpResponse response);

	@Override
	protected abstract void max_seconds(long max);


}
