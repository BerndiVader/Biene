package com.gmail.berndivader.biene.http.post;

import org.apache.http.HttpResponse;

public 
interface 
IPostTask 
{
	void _completed(HttpResponse response);
	void _failed(HttpResponse response);

}
