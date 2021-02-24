package com.gmail.berndivader.biene.http.get;

import org.apache.http.HttpResponse;

public 
interface 
IGetTask 
{
	void _completed(HttpResponse response);
	void _failed(HttpResponse response);

}
