package com.gmail.berndivader.biene.db;

public 
interface 
IQueryTask<T,U> 
{
	void execute();
	void completed(T result);
	void failed(U error);
}
