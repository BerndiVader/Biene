package com.gmail.berndivader.biene.db;

public 
interface 
IQueryTask<T> 
{
	void execute();
	void completed(T result);
	void failed(T result);
}
